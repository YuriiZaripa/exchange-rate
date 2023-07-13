package com.exchangerate.repository;

import com.exchangerate.model.ExchangeRate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ExchangeRateRepository extends ReactiveCrudRepository<ExchangeRate, String> {

    Mono<ExchangeRate> findByCurrencyCode(String currencyCode);
}
