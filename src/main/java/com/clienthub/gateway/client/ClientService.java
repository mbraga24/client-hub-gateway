package com.clienthub.gateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClientService {
    
    private final WebClient webClient;

    public ClientService(WebClient.Builder webClientBuilder, @Value("${client-hub-api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Long addClient(ClientRequest request) {
        log.info("addClient :: forwarding request to client management api (disabled)");
        // TODO: re-enable when Client Management API is available
        // ClientResponse response = webClient.post()
        //         .uri("/api/v1/clients")
        //         .bodyValue(request)
        //         .retrieve()
        //         .bodyToMono(ClientResponse.class)
        //         .block();
        // return response.id();
        return -1L;
    }


}
