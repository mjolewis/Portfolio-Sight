package edu.bu.cs673.stockportfolio.domain.investment.sector;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompanyRoot {
    private final Map<String, CompanySector> companySectors = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, CompanySector> getCompanySectors() {
        return this.companySectors;
    }

    @JsonAnySetter
    public void addCompanySector(String ticker, CompanySector sector) {
        this.companySectors.put(ticker, sector);
    }
}