package edu.bu.cs673.stockportfolio.service.company;

import java.util.*;

import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRoot;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanySector;
import org.springframework.stereotype.Service;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Filter Companies by symbol to discover only the Companies that don't exist in the database. This is used as an
     * optimization before sending requests to IEX Clouds company endpoint.
     *
     * @param symbols A set of all symbols within the portfolio being uploaded.
     * @return A Set of symbols that represent companies not yet in the database.
     */
    public Set<String> filterCompaniesBySymbolNotInDb(Set<String> symbols) {
        Set<String> companySymbolsNotInDb = new HashSet<>();
        for (String symbol : symbols) {
            if (!contains(symbol)) {
                companySymbolsNotInDb.add(symbol);
            }
        }

        return companySymbolsNotInDb;
    }

    /**
     * Returns true if the CompanyRepository holds the input parameter symbol
     *
     * @param symbol The ticker symbol of a company.
     * @return True if the repository already has this symbol
     */
    public boolean contains(String symbol) {
        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            if (company.getSymbol().equals(symbol)) return true;
        }

        return false;
    }

    public List<Company> processCompanyRootRestTemplate(CompanyRoot companyRoot) {

        List<Company> companies = new ArrayList<>();
        if (companyRoot != null) {
            Map<String, CompanySector> companyData = companyRoot.getCompanySectors();
            companyData.forEach((key, value) -> companies.add(value.getCompany()));
        }

        return companies;
    }

    /**
     * If the CompanyRepository doesn't have this company, add it.
     *
     * @param company Company to add if not already in repository.
     */
    public void save(Company company) {
        if (!this.contains(company.getSymbol())) {
            companyRepository.save(company);
        }
    }

    /**
     * Link a list of quotes to the associated company and vice versa.
     *
     * @param quotes List of quotes to link.
     */
    public void doLinkQuotes(List<Quote> quotes) {
        Company company;
        for (Quote quote : quotes) {
            company = getBySymbol(quote.getSymbol());
            if (company != null) {
                quote.setCompany(company);
                company.getQuotes().add(quote);
            }
        }
    }

    /**
     * Returns the company by the symbol.
     *
     * @param symbol The ticker symbol of a Company.
     * @return Company handle with the parameter symbol.
     */
    public Company getBySymbol(String symbol) {
        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            if (company.getSymbol().equals(symbol)) {
                return company;
            }
        }

        return null;
    }
}
