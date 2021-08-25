package edu.bu.cs673.stockportfolio.marketdata.model.company.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "company"
})
public class CompanyContainer {

    @JsonProperty("company")
    private Company company;

    public CompanyContainer() {
    }

    public CompanyContainer(Company company) {
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