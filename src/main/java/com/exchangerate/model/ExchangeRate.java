package com.exchangerate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("exchange_rate")
public class ExchangeRate {

    @Id
    private int id;

    @Column("currency_code")
    private String currencyCode;

    @Column("cost")
    private BigDecimal cost;
}
