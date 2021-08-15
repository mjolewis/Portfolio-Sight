package edu.bu.cs673.stockportfolio.service.company;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /**
     * Returns if the CompanyRepository holds the input parameter symbol
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

    /**
     * If the CompanyRepository doesn't have this company, add it.
     *
     * @param company Company to add if not already in repository.
     */
    public void add(Company company) {
        if (!this.contains(company.getSymbol())) {
            companyRepository.save(company);
        }
    }
}
