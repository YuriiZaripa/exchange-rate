package com.exchangerate.service;

import com.exchangerate.model.RateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenExchangeRatesWebClient {

    @Value("${rate-source.api-key}")
    private String API_KEY;

    private static final String LATEST_DATA_IN_JSON_FORMAT = "/latest.json";
    private static final String APP_ID = "app_id";

    private final WebClient client;

    public OpenExchangeRatesWebClient(@Value("${rate-source.url}") String baseUrl) {
        client = WebClient.create(baseUrl);
    }

    public Mono<RateResponse> getRates() {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(LATEST_DATA_IN_JSON_FORMAT)
                        .queryParam(APP_ID, API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(RateResponse.class);
    }
}

