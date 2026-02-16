package com.example.demo.controllers;

import com.example.demo.dto.OnlineMeeting;
import com.example.demo.dto.Transcript;
import com.example.demo.services.TranscriptService;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TranscriptController {

    private final TranscriptService service;

    public TranscriptController(TranscriptService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public String health() { return "ok"; }

    // 1) trouver meetingId depuis joinUrl (pratique)
    @GetMapping("/poc/meeting/by-join-url")
    public OnlineMeeting meetingByJoinUrl(@RequestParam String joinUrl,
                                          @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {
        return service.findMyMeetingByJoinUrl(joinUrl, client);
    }

    // 2) lister transcripts
    @GetMapping("/poc/transcripts")
    public List<Transcript> list(@RequestParam String userId,
                                 @RequestParam String meetingId,
                                 @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {
        return service.listTranscripts(userId, meetingId, client);
    }

    // 3) télécharger content
    @GetMapping(value = "/poc/transcripts/content", produces = MediaType.TEXT_PLAIN_VALUE)
    public String content(@RequestParam String userId,
                          @RequestParam String meetingId,
                          @RequestParam String transcriptId,
                          @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient client) {
        return service.downloadTranscriptContent(userId, meetingId, transcriptId, client);
    }
}