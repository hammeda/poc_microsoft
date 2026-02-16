package com.example.demo.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/poc")
public class PocController {

    private final WebClient graph;

    public PocController(WebClient graphWebClient) {
        this.graph = graphWebClient;
    }

    @GetMapping("/me")
    public String me(@RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {
        return graph.get()
                .uri("/me")
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/meetings")
    public String meetings(@RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {
        // Tu peux ajouter $top, $filter dates plus tard
        return graph.get()
                .uri("/me/onlineMeetings")
                .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/meetings/{meetingId}/transcripts")
    public ResponseEntity<String> transcripts(@PathVariable String meetingId,
                                              @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {

        try {
            String body = graph.get()
                    .uri("/me/onlineMeetings/{meetingId}/transcripts", meetingId)
                    .attributes(ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(body);

        } catch (WebClientResponseException.Forbidden e) {
            // Ici on "prouve" que c'est une question d'autorisation admin
            String msg = """
        {
          "error": "TRANSCRIPT_NOT_AUTHORIZED",
          "details": "Microsoft Graph returned 403 Forbidden. Admin consent is required for OnlineMeetingTranscript.Read.All in this tenant."
        }
        """;
            return ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON).body(msg);
        }
    }
}