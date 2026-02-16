package com.example.demo.services;

import com.example.demo.dto.GraphListResponse;
import com.example.demo.dto.OnlineMeeting;
import com.example.demo.dto.Transcript;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TranscriptService {

    private final WebClient graph;

    public TranscriptService(WebClient graphWebClient) {
        this.graph = graphWebClient;
    }

    // 1) retrouver le meeting ID depuis un joinUrl (POC pratique)
    public OnlineMeeting findMyMeetingByJoinUrl(String joinUrl,
                                                @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {

        String encoded = UriUtils.encodePath(joinUrl, StandardCharsets.UTF_8);

        // Graph OData filter: JoinWebUrl eq '...'
        // Attention: les quotes dans OData -> on échappe en doublant si besoin
        String odata = "$filter=" + "JoinWebUrl eq '" + joinUrl.replace("'", "''") + "'";

        return graph.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/onlineMeetings")
                        .query(odata)
                        .build()
                )
                .attributes(org.springframework.security.oauth2.client.web.reactive.function.client
                        .ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(GraphListResponse.class)
                .map(resp -> {
                    var list = (List<java.util.LinkedHashMap<String, Object>>) resp.value();
                    if (list == null || list.isEmpty()) return null;
                    var m = list.get(0);
                    return new OnlineMeeting((String) m.get("id"), (String) m.get("joinWebUrl"), (String) m.get("subject"));
                })
                .block();
    }

    // 2) lister les transcripts d’un meeting (user scope)
    public List<Transcript> listTranscripts(String userId, String meetingId,
                                            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {

        return graph.get()
                .uri("/users/{userId}/onlineMeetings/{meetingId}/transcripts", userId, meetingId)
                .attributes(org.springframework.security.oauth2.client.web.reactive.function.client
                        .ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<GraphListResponse<Transcript>>() {})
                .map(GraphListResponse::value)
                .block();
    }

    // 3) télécharger le contenu
    public String downloadTranscriptContent(String userId, String meetingId, String transcriptId,
                                            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {

        return graph.get()
                .uri("/users/{userId}/onlineMeetings/{meetingId}/transcripts/{transcriptId}/content",
                        userId, meetingId, transcriptId)
                .attributes(org.springframework.security.oauth2.client.web.reactive.function.client
                        .ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}