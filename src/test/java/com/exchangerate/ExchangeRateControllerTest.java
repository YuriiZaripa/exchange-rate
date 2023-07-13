package com.exchangerate;

import com.exchangerate.model.ExchangeRate;
import com.exchangerate.repository.ExchangeRateRepository;
import com.exchangerate.rest.ExchangeRateController;
import com.exchangerate.service.ExchangeRateService;
import com.exchangerate.service.OpenExchangeRatesWebClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ExchangeRateController.class)
@Import(ExchangeRateService.class)
public class ExchangeRateControllerTest {

    @MockBean
    private ExchangeRateRepository repository;

    @MockBean
    private OpenExchangeRatesWebClient openExchangeRatesWebClient;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testFindAllExchangeRates_ShouldBySuccess() {
        List<ExchangeRate> rates = getExchangeRateMockData();

        Mockito.when(repository.findAll()).thenReturn(Flux.fromIterable(rates));

        webClient.get()
                .uri("/api/v1/exchange-rates")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExchangeRate.class)
                .value(response -> {
                    List<String> expectedCosts = rates.stream()
                            .map(rate -> rate.getCost().stripTrailingZeros().toPlainString())
                            .collect(Collectors.toList());
                    List<String> actualCosts = response.stream()
                            .map(rate -> rate.getCost().stripTrailingZeros().toPlainString())
                            .collect(Collectors.toList());
                    Assertions.assertEquals(expectedCosts, actualCosts);
                })
                .value(response -> {
                    List<String> expected = rates.stream()
                            .map(ExchangeRate::getCurrencyCode)
                            .toList();
                    List<String> actual = response.stream()
                            .map(ExchangeRate::getCurrencyCode)
                            .toList();
                    Assertions.assertEquals(expected, actual);
                });

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetExchangeRateByCurrencyCode_ShouldBySuccess() {
        ExchangeRate uahRate = getUAHExchangeRateMockData();
        List<ExchangeRate> mockRates = getExchangeRateMockData();

        List<ExchangeRate> expectedResponseRates = List.of(
                ExchangeRate.builder()
                        .id(1)
                        .currencyCode("USD")
                        .cost(BigDecimal.valueOf(0.02713704))
                        .build(),
                ExchangeRate.builder()
                        .id(2)
                        .currencyCode("EUR")
                        .cost(BigDecimal.valueOf(0.02306649))
                        .build(),
                ExchangeRate.builder()
                        .id(3)
                        .currencyCode("UAH")
                        .cost(BigDecimal.valueOf(1.00000000))
                        .build()
        );

        Mockito.when(repository.findAll()).thenReturn(Flux.fromIterable(mockRates));
        Mockito.when(repository.findByCurrencyCode("UAH")).thenReturn(Mono.just(uahRate));

        webClient.get()
                .uri("/api/v1/exchange-rates/{currencyCode}", "UAH")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExchangeRate.class)
                .value(response -> {
                    List<String> expectedCosts = expectedResponseRates.stream()
                            .map(rate -> rate.getCost().stripTrailingZeros().toPlainString())
                            .collect(Collectors.toList());
                    List<String> actualCosts = response.stream()
                            .map(rate -> rate.getCost().stripTrailingZeros().toPlainString())
                            .collect(Collectors.toList());
                    Assertions.assertEquals(expectedCosts, actualCosts);
                })
                .value(response -> {
                    List<String> expected = expectedResponseRates.stream()
                            .map(ExchangeRate::getCurrencyCode)
                            .toList();
                    List<String> actual = response.stream()
                            .map(ExchangeRate::getCurrencyCode)
                            .toList();
                    Assertions.assertEquals(expected, actual);
                });
    }

    @Test
    public void testExchangeRateFromTo_ShouldBySuccess() {
        ExchangeRate eurRate = getUEURExchangeRateMockData();
        String from = eurRate.getCurrencyCode();
        ExchangeRate uahRate = getUAHExchangeRateMockData();
        String to = uahRate.getCurrencyCode();
        BigDecimal exchangeRate = uahRate.getCost().divide(eurRate.getCost(), 8, RoundingMode.HALF_UP);

        Mockito.when(repository.findByCurrencyCode("UAH")).thenReturn(Mono.just(uahRate));
        Mockito.when(repository.findByCurrencyCode("EUR")).thenReturn(Mono.just(eurRate));

        webClient.get()
                .uri("/api/v1/exchange-rates/change?from={from}&to={to}", from, to)
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .isEqualTo(exchangeRate);
    }

    private List<ExchangeRate> getExchangeRateMockData() {
        return List.of(
                getUSDExchangeRateMockData(),
                getUEURExchangeRateMockData(),
                getUAHExchangeRateMockData()
        );
    }

    private ExchangeRate getUSDExchangeRateMockData() {
        return ExchangeRate.builder()
                .id(1)
                .currencyCode("USD")
                .cost(BigDecimal.valueOf(1.00000000))
                .build();
    }

    private ExchangeRate getUEURExchangeRateMockData() {
        return ExchangeRate.builder()
                .id(2)
                .currencyCode("EUR")
                .cost(BigDecimal.valueOf(0.8500000))
                .build();
    }

    private ExchangeRate getUAHExchangeRateMockData() {
        return ExchangeRate.builder()
                .id(3)
                .currencyCode("UAH")
                .cost(BigDecimal.valueOf(36.8500000))
                .build();
    }
}
