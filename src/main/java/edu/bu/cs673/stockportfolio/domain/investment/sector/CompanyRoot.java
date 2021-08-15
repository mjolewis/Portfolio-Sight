package edu.bu.cs673.stockportfolio.domain.investment.sector;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompanyRoot {
    private Map<String, StockSector> companies = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, StockSector> getCompanies() {
        return this.companies;
    }

    @JsonAnySetter
    public void addCompany(String ticker, StockSector sector) {
        this.companies.put(ticker, sector);
    }
}