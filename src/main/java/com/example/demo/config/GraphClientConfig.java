package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GraphClientConfig {

    @Bean
    WebClient graphWebClient(ClientRegistrationRepository registrations,
                             OAuth2AuthorizedClientRepository clients) {

        var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(registrations, clients);
        oauth.setDefaultClientRegistrationId("azure");

        return WebClient.builder()
                .baseUrl("https://graph.microsoft.com/v1.0")
                .apply(oauth.oauth2Configuration())
                .build();
    }
}