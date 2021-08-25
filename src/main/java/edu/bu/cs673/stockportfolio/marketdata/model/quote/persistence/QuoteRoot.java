package edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuoteRoot {
    private final Map<String, QuoteContainer> quoteContainer = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, QuoteContainer> getQuoteContainer() {
        return this.quoteContainer;
    }

    @JsonAnySetter
    public void addQuoteContainer(String name, QuoteContainer value) {
        this.quoteContainer.put(name, value);
    }
}