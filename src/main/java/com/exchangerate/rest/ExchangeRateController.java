package com.exchangerate.rest;

import com.exchangerate.model.ExchangeRate;
import com.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public Flux<ExchangeRate> findAll() {
        return exchangeRateService.findAll();
    }

    @GetMapping("/{currencyCode}")
    public Flux<ExchangeRate> findAllByCurrencyCode(@PathVariable String currencyCode) {
        return exchangeRateService.findAllByCurrencyCode(currencyCode);
    }

    @GetMapping("/exchange")
    public Mono<BigDecimal> findPairExchangeRate(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to) {
        return exchangeRateService.exchangeRate(from, to);
    }
}
