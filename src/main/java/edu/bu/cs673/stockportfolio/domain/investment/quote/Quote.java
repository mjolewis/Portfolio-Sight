package edu.bu.cs673.stockportfolio.domain.investment.quote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sun.istack.NotNull;
import edu.bu.cs673.stockportfolio.domain.account.AccountLine;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**********************************************************************************************************************
 * Data object representing investment products. Each quote is for a specific symbol and can be associated with an
 * Account.
/*********************************************************************************************************************/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "companyName",
        "symbol",
        "latestPrice",
        "marketCap"
})
@Entity
@Check(constraints = "CHECK (LENGTH(TRIM(company_name)) > 0) &&"
        + " CHECK (LENGTH(TRIM(symbol)) > 0) &&"
        + " CHECK (latest_price >= 0) &&"
        + " CHECK (market_cap >= 0) &&"
        + " CONSTRAINT UNIQUE (symbol)")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("companyName")
    @NotNull
    private String companyName;

    @JsonProperty("symbol")
    @NotNull
    private String symbol;

    @JsonProperty("latestPrice")
    @NotNull
    private BigDecimal latestPrice;

    @JsonProperty("marketCap")
    @NotNull
    private Long marketCap;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<AccountLine> accountLines = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * No args constructor for use in serialization.
     */
    public Quote() {
    }

    /**
     * A parameterized constructor for use in creating a quote.
     * @param companyName The registered name of the company.
     * @param symbol The ticker symbol associated with this quote.
     * @param latestPrice Either the realtime price if the market is open. Otherwise, the closing price.
     * @param marketCap The market capitalization of the company represented by the symbol.
     * @param accountLines A collection of all the different owned lots within an account.
     */
    public Quote(String companyName, String symbol, BigDecimal latestPrice,
                 Long marketCap, List<AccountLine> accountLines, Company company) {
        super();
        this.companyName = companyName;
        this.symbol = symbol;
        this.latestPrice = latestPrice;
        this.marketCap = marketCap;
        this.accountLines = accountLines;
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @JsonProperty("latestPrice")
    public BigDecimal getLatestPrice() {
        return latestPrice;
    }

    @JsonProperty("latestPrice")
    public void setLatestPrice(BigDecimal latestPrice) {
        this.latestPrice = latestPrice;
    }

    @JsonProperty("marketCap")
    public Long getMarketCap() {
        return marketCap;
    }

    @JsonProperty("marketCap")
    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    public List<AccountLine> getAccountLines() {
        return accountLines;
    }

    public void setAccountLines(List<AccountLine> accounts) {
        this.accountLines = accounts;
    }

}