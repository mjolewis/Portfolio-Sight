package edu.bu.cs673.stockportfolio.unittests.service.company;

import static org.mockito.Mockito.when;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRepository;
import edu.bu.cs673.stockportfolio.service.company.CompanyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/*
 * https://rieckpil.de/difference-between-mock-and-mockbean-spring-boot-applications/
 */
@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    private Company company;

    private Quote quote;

    @BeforeEach
    public void setup() {
        company = new Company();
        company.setId(1L);
        company.setCompanyName("Goldman Sachs Group, Inc.");
        company.setSymbol("GS");
        company.setLatestPrice("374.88");

        quote = new Quote();
        quote.setId(1L);
        quote.setCompanyName("Goldman Sachs, Inc.");
        quote.setSymbol("GS");
        quote.setLatestPrice(new BigDecimal("345.53"));
        quote.setMarketCap(127370100000L);
    }

    @Test
    public void searchForExistingSymbolInCompanyRepositoryShouldReturnTrue() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        companyRepository.findAll();

        Assertions.assertTrue(companyService.contains(company.getSymbol()));
    }

    @Test
    public void searchForNonExistingSymbolInCompanyRepositoryShouldReturnNull() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        companyRepository.findAll();

        Assertions.assertFalse(companyService.contains("missing symbol"));
    }

    @Test
    public void searchForExistingCompanyBySymbolInCompanyRepositoryShouldReturnCompany() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        companyRepository.findAll();

        Company result = companyService.getBySymbol(company.getSymbol());

        Assertions.assertEquals(company, result);
    }

    @Test
    public void searchForNonExistingCompanyCompanyBySymbolInCompanyRepositoryShouldReturnNull() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        companyRepository.findAll();

        Company result = companyService.getBySymbol("missing symbol");

        Assertions.assertNull(result);
    }

    @Test
    public void addingCompanyToDatabaseAndSearchingForCompanyInDatabaseShouldReturnSavedCompany() {
        when(companyRepository.save(company)).thenReturn(company);
        when(companyRepository.findById(company.getId())).thenReturn(Optional.ofNullable(company));

        companyService.add(company);

        Optional<Company> result = companyRepository.findById(company.getId());

        Assertions.assertEquals(company, result.get());
    }

    @Test
    public void linkQuotesAndCompaniesBasedOnTheTickerSymbol() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        companyService.doLinkQuotes(List.of(quote));

        Assertions.assertEquals(company, quote.getCompany());
        Assertions.assertEquals(quote, company.getQuotes().get(0));
    }
}
