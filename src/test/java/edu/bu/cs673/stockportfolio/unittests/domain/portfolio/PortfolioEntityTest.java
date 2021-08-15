package edu.bu.cs673.stockportfolio.unittests.domain.portfolio;

import edu.bu.cs673.stockportfolio.domain.account.Account;
import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import edu.bu.cs673.stockportfolio.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PortfolioEntityTest {

    @InjectMocks
    private Portfolio portfolio;

    @Test
    public void createPortfolioWithIdAndUserAndAccount() {
        User user = new User(1L, "username", "password",
                "2337dk", "user@gmail.com", portfolio);

        Account account = new Account(portfolio, "12345678");

        Portfolio portfolio = new Portfolio(1L, user, List.of(account));

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, portfolio.getId()),
                () -> Assertions.assertEquals(user, portfolio.getUser()),
                () -> Assertions.assertEquals(account, portfolio.getAccounts().get(0))
        );
    }

    @Test
    public void createPortfolioWithUserOnlyAndSetAccountsAfter() {
        User user = new User(1L, "username", "password",
                "2337dk", "user@gmail.com", portfolio);

        Portfolio portfolio = new Portfolio(user);
        portfolio.setId(2L);

        Account account = new Account(portfolio, "12345678");
        portfolio.setAccounts(List.of(account));

        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, portfolio.getId()),
                () -> Assertions.assertEquals(user, portfolio.getUser()),
                () -> Assertions.assertEquals(account, portfolio.getAccounts().get(0))
        );
    }

    @Test
    public void createPortfolioWithUserOnlyAndAddAccountAfter() {
        User user = new User(1L, "username", "password",
                "2337dk", "user@gmail.com", portfolio);

        Portfolio portfolio = new Portfolio(user);
        portfolio.setId(2L);

        Account account = new Account(portfolio, "12345678");
        portfolio.addAccount(account);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, portfolio.getId()),
                () -> Assertions.assertEquals(user, portfolio.getUser()),
                () -> Assertions.assertEquals(account, portfolio.getAccounts().get(0))
        );
    }

    @Test
    public void createPortfolioWithUserOnlySetAccountsToNullAndAddAccountAfterToEnsureAccountListIsCreated() {
        User user = new User(1L, "username", "password",
                "2337dk", "user@gmail.com", portfolio);

        Portfolio portfolio = new Portfolio(user);
        portfolio.setId(2L);

        portfolio.setAccounts(null);

        Account account = new Account(portfolio, "12345678");
        portfolio.addAccount(account);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, portfolio.getId()),
                () -> Assertions.assertEquals(user, portfolio.getUser()),
                () -> Assertions.assertEquals(account, portfolio.getAccounts().get(0))
        );
    }

    @Test
    public void createPortfolioWithUserAndListOfAccounts() {
        User user = new User(1L, "username", "password",
                "2337dk", "user@gmail.com", portfolio);

        Account account = new Account(portfolio, "12345678");

        Portfolio portfolio = new Portfolio(user, List.of(account));
        portfolio.setId(2L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2L, portfolio.getId()),
                () -> Assertions.assertEquals(user, portfolio.getUser()),
                () -> Assertions.assertEquals(account, portfolio.getAccounts().get(0))
        );
    }
}
