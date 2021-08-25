package edu.bu.cs673.stockportfolio.marketdata.model.company.persistence;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompanyRoot {
    private final Map<String, CompanyContainer> companyContainer = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, CompanyContainer> getCompanyContainer() {
        return this.companyContainer;
    }

    @JsonAnySetter
    public void addCompanyContainer(String ticker, CompanyContainer sector) {
        this.companyContainer.put(ticker, sector);
    }
}