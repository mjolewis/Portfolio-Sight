package edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "quote"
})
public class QuoteContainer {

    @JsonProperty("quote")
    private Quote quote;

    public QuoteContainer() {
    }

    public QuoteContainer(Quote quote) {
        this.quote = quote;
    }

    @JsonProperty("quote")
    public Quote getQuote() {
        return quote;
    }

    @JsonProperty("quote")
    public void setQuote(Quote quote) {
        this.quote = quote;
    }
}
