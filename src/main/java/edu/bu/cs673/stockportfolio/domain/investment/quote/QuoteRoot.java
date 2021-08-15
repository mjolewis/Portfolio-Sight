package edu.bu.cs673.stockportfolio.domain.investment.quote;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuoteRoot {
    private Map<String, StockQuote> stocks = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, StockQuote> getStocks() {
        return this.stocks;
    }

    @JsonAnySetter
    public void addStock(String name, StockQuote value) {
        this.stocks.put(name, value);
    }
}