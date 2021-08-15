package edu.bu.cs673.stockportfolio.unittests.domain.account;

import edu.bu.cs673.stockportfolio.domain.account.Account;
import edu.bu.cs673.stockportfolio.domain.account.AccountLine;
import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AccountEntityTest {

    @InjectMocks
    private Account account;

    @Test
    public void createAccountWithId() {
        Portfolio portfolio = new Portfolio();

        AccountLine accountLine = new AccountLine();

        account = new Account(1L, portfolio, "12345678", List.of(accountLine));

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, account.getId()),
                () -> Assertions.assertEquals(portfolio, account.getPortfolio()),
                () -> Assertions.assertEquals("12345678", account.getAccountNumber()),
                () -> Assertions.assertEquals(accountLine, account.getAccountLines().get(0))
        );
    }

    @Test
    public void createAccountWithoutIdAndThenSetIdAfterward() {
        Portfolio portfolio = new Portfolio();

        AccountLine accountLine = new AccountLine();

        account = new Account(portfolio, "12345678", List.of(accountLine));
        account.setId(1L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, account.getId()),
                () -> Assertions.assertEquals(portfolio, account.getPortfolio()),
                () -> Assertions.assertEquals("12345678", account.getAccountNumber()),
                () -> Assertions.assertEquals(accountLine, account.getAccountLines().get(0))
        );
    }

    @Test
    public void createAccountWithAPortfolioAndAccountNumberOnlyAndSetAccountIdAfterward() {
        Portfolio portfolio = new Portfolio();

        account = new Account(portfolio, "12345678");
        account.setId(1L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, account.getId()),
                () -> Assertions.assertEquals(portfolio, account.getPortfolio()),
                () -> Assertions.assertEquals("12345678", account.getAccountNumber())
        );
    }

    @Test
    public void createAnEmptyAccountAndSetAllAttributesAfterward() {
        Portfolio portfolio = new Portfolio();

        AccountLine accountLine = new AccountLine();

        account = new Account();
        account.setId(1L);
        account.setPortfolio(portfolio);
        account.setAccountNumber("12345678");
        account.setAccountLines(List.of(accountLine));

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, account.getId()),
                () -> Assertions.assertEquals(portfolio, account.getPortfolio()),
                () -> Assertions.assertEquals("12345678", account.getAccountNumber()),
                () -> Assertions.assertEquals(accountLine, account.getAccountLines().get(0))
        );
    }
}
