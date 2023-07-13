package com.exchangerate.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class RateResponse {

    private Map<String, BigDecimal> rates;
}
