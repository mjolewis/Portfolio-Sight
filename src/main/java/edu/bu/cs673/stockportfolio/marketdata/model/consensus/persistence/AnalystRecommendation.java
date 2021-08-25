package edu.bu.cs673.stockportfolio.marketdata.model.consensus.persistence;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.Quote;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "symbol",
        "marketConsensus",
        "marketConsensusTargetPrice"
})
@Entity(name = "analyst_recommendation")
@Check(constraints = "(LENGTH(TRIM(symbol)) > 0)),"
        + " CHECK (market_consensus_target_price >= 0),"
        + " CONSTRAINT UNIQUE (symbol")
public class AnalystRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("marketConsensus")
    private Float marketConsensus;
    @JsonProperty("marketConsensusTargetPrice")
    private BigDecimal marketConsensusTargetPrice;

    @OneToMany(mappedBy = "analystRecommendation", fetch = FetchType.LAZY)
    private List<Quote> quotes = new ArrayList<>();

    public AnalystRecommendation() {}

    public AnalystRecommendation(String symbol,
                                 Float marketConsensus,
                                 BigDecimal marketConsensusTargetPrice,
                                 List<Quote> quotes) {
        super();
        this.symbol = symbol;
        this.marketConsensus = marketConsensus;
        this.marketConsensusTargetPrice = marketConsensusTargetPrice;
        this.quotes = quotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("marketConsensus")
    public Float getMarketConsensus() {
        return marketConsensus;
    }

    @JsonProperty("marketConsensus")
    public void setMarketConsensus(Float marketConsensus) {
        this.marketConsensus = marketConsensus;
    }

    @JsonProperty("marketConsensusTargetPrice")
    public BigDecimal getMarketConsensusTargetPrice() {
        return marketConsensusTargetPrice;
    }

    @JsonProperty("marketConsensusTargetPrice")
    public void setMarketConsensusTargetPrice(BigDecimal marketConsensusTargetPrice) {
        this.marketConsensusTargetPrice = marketConsensusTargetPrice;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}