package edu.bu.cs673.stockportfolio.domain.investment.sector;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;

@JsonPropertyOrder({
        "company"
})
public class StockSector {

    @JsonProperty("company")
    private Company company;

    public StockSector() {
    }

    public StockSector(Company company) {
        this.company = company;
    }

    @JsonProperty("company")
    public Company getCompany() {
        return company;
    }

    @JsonProperty("company")
    public void setCompany(Company company) {
        this.company = company;
    }
}