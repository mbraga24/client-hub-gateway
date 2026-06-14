package com.clienthub.gateway.ipapi;

import com.clienthub.gateway.exception.custom.IPApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class IPApiService {

    private final WebClient.Builder webClientBuilder;

    public IPApiResponse ipAPICall(String userIp) {
        String apiUrl = "http://ip-api.com/json/" + userIp;

        try {
            IPApiResponse apiResponse = webClientBuilder.build()
                    .get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(IPApiResponse.class)
                    .block();

            log.info("RECEIVE API :: [{}]", apiResponse.toString());
            return apiResponse;
        } catch (Exception error) {
            log.error("IP API Request Error :: {}", error.getMessage(), error);
            throw new IPApiException("We're experiencing trouble connecting to our services. Please try again in a few minutes.");
        }
    }

}
