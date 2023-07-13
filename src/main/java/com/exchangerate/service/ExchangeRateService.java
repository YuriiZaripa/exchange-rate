package com.exchangerate.service;

import com.exchangerate.exception.OpenExchangeRatesNullBodySoursResponseException;
import com.exchangerate.model.ExchangeRate;
import com.exchangerate.repository.ExchangeRateRepository;
import com.exchangerate.exception.InvalidCurrencyCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final OpenExchangeRatesWebClient client;
    private final ExchangeRateRepository exchangeRateRepository;

    private final static String USD = "USD";
    private final static int BIG_DECIMAL_PRECISION = 8;

    public Flux<ExchangeRate> findAll() {
        return exchangeRateRepository.findAll();
    }

    public Flux<ExchangeRate> findAllByCurrencyCode(String currencyCode) {
        if (currencyCode.equals(USD)) {
            return exchangeRateRepository.findAll();
        }

        return exchangeRateRepository.findByCurrencyCode(currencyCode)
                .switchIfEmpty(Mono.error(() -> new InvalidCurrencyCodeException(currencyCode)))
                .flatMapMany(fulcrumExchangeRate ->
                        exchangeRateRepository.findAll()
                                .map(exchangeRate ->
                                {
                                    exchangeRate.setCost(calculateExchangeRate(exchangeRate.getCost(), fulcrumExchangeRate.getCost()));
                                    return exchangeRate;
                                })
                );
    }

    public Mono<BigDecimal> exchangeRate(String from, String to) {
        if (from.equals(USD))
            return exchangeRateRepository.findByCurrencyCode(to)
                    .switchIfEmpty(Mono.error(() -> new InvalidCurrencyCodeException(to)))
                    .map(exchangeRate -> exchangeRate.getCost());

        return exchangeRateRepository.findByCurrencyCode(from)
                .switchIfEmpty(Mono.error(() -> new InvalidCurrencyCodeException(from)))
                .flatMap(exchangeRateFrom ->
                        exchangeRateRepository.findByCurrencyCode(to)
                                .switchIfEmpty(Mono.error(() -> new InvalidCurrencyCodeException(to)))
                                .map(exchangeRateTo ->
                                        calculateExchangeRate(exchangeRateTo.getCost(), exchangeRateFrom.getCost()))
                );
    }

    @Scheduled(fixedDelayString = "${rate-source.delay}")
    public void updateExchangeRates() {
        client.getRates()
                .flatMapIterable(rateResponse -> rateResponse.getRates().entrySet())
                .switchIfEmpty(Mono.error(OpenExchangeRatesNullBodySoursResponseException::new))
                .map(entry -> ExchangeRate.builder()
                        .currencyCode(entry.getKey())
                        .cost(entry.getValue())
                        .build())
                .flatMap(exchangeRate -> {
                    return exchangeRateRepository.findByCurrencyCode(exchangeRate.getCurrencyCode())
                            .flatMap(existingExchangeRate -> {
                                existingExchangeRate.setCost(exchangeRate.getCost());
                                return exchangeRateRepository.save(existingExchangeRate);
                            })
                            .switchIfEmpty(exchangeRateRepository.save(exchangeRate));
                }).subscribe();
    }

    private BigDecimal calculateExchangeRate(BigDecimal currentRate, BigDecimal fulcrum) {
        return currentRate.divide(fulcrum, BIG_DECIMAL_PRECISION, RoundingMode.HALF_UP);
    }
}
