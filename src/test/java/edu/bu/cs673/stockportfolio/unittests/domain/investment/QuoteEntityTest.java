package edu.bu.cs673.stockportfolio.unittests.domain.investment;

import edu.bu.cs673.stockportfolio.domain.account.AccountLine;
import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class QuoteEntityTest {

    @InjectMocks
    private Quote quote;

    @Test
    public void createQuoteWith() {
        AccountLine accountLine = new AccountLine();
        Company company = new Company();
        BigDecimal latestPrice = new BigDecimal("345.53");
        quote = new Quote("Goldman Sachs, Inc.", "GS",
                latestPrice, 127370100000L, List.of(accountLine), company);

        Assertions.assertAll(
                () -> Assertions.assertEquals("Goldman Sachs, Inc.", quote.getCompanyName()),
                () -> Assertions.assertEquals("GS", quote.getSymbol()),
                () -> Assertions.assertEquals(latestPrice, quote.getLatestPrice()),
                () -> Assertions.assertEquals(127370100000L, quote.getMarketCap()),
                () -> Assertions.assertEquals(accountLine, quote.getAccountLines().get(0)),
                () -> Assertions.assertEquals(company, quote.getCompany())
        );
    }

    @Test
    public void createQuoteWithNoArgConstructorAndSetAttributesAfterward() {
        AccountLine accountLine = new AccountLine();
        Company company = new Company();
        BigDecimal latestPrice = new BigDecimal("345.53");

        quote = new Quote();
        quote.setId(1L);
        quote.setCompanyName("Goldman Sachs, Inc.");
        quote.setSymbol("GS");
        quote.setLatestPrice(new BigDecimal("345.53"));
        quote.setMarketCap(127370100000L);
        quote.setAccountLines(List.of(accountLine));
        quote.setCompany(company);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, quote.getId()),
                () -> Assertions.assertEquals("Goldman Sachs, Inc.", quote.getCompanyName()),
                () -> Assertions.assertEquals("GS", quote.getSymbol()),
                () -> Assertions.assertEquals(latestPrice, quote.getLatestPrice()),
                () -> Assertions.assertEquals(127370100000L, quote.getMarketCap()),
                () -> Assertions.assertEquals(accountLine, quote.getAccountLines().get(0)),
                () -> Assertions.assertEquals(company, quote.getCompany())
        );
    }
}
