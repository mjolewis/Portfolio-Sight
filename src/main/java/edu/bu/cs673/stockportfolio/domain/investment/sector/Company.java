package edu.bu.cs673.stockportfolio.domain.investment.sector;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import org.hibernate.annotations.Check;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "symbol",
        "sector",
        "companyName"
})
@Entity
@Check(constraints = "CHECK (LENGTH(TRIM(symbol)) > 0) &&"
        + " CHECK (LENGTH(TRIM(sector)) > 0) &&"
        + " CHECK (LENGTH(TRIM(company_name)) > 0)")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("sector")
    private String sector;

    @JsonProperty("companyName")
    private String companyName;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Quote> quotes = new ArrayList<Quote>();

    /**
     * No args constructor for use in serialization
     */
    public Company() {
    }

    /**
     *
     * @param symbol
     * @param sector
     */
    public Company(String symbol, String sector, String companyName, List<Quote> quotes) {
        super();
        this.symbol = symbol;
        this.sector = sector;
        this.companyName = companyName;
        this.quotes = quotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    @JsonProperty("companyName")
    public String getCompanyName() {
        return companyName;
    }

    @JsonProperty("companyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("sector")
    public String getSector() {
        return sector;
    }

    @JsonProperty("sector")
    public void setLatestPrice(String sector) {
        this.sector = sector;
    }
}