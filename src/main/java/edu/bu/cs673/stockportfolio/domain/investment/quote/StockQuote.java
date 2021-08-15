package edu.bu.cs673.stockportfolio.domain.investment.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "quote"
})
public class StockQuote {

    @JsonProperty("quote")
    private Quote quote;

    public StockQuote() {
    }

    public StockQuote(Quote quote) {
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
