package edu.bu.cs673.stockportfolio.unittests.domain.account;

import edu.bu.cs673.stockportfolio.domain.account.Account;
import edu.bu.cs673.stockportfolio.domain.account.AccountLine;
import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountLineEntityTest {

    @InjectMocks
    private AccountLine accountLine;

    @Test
    public void createAccountLineWithAccountAndQuoteAndUser() {
        Account account = new Account();
        account.setId(2L);

        Quote quote = new Quote();
        quote.setId(3L);

        accountLine = new AccountLine(account, quote, 100);
        accountLine.setId(1L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, accountLine.getId()),
                () -> Assertions.assertEquals(account, accountLine.getAccount()),
                () -> Assertions.assertEquals(quote, accountLine.getQuote()),
                () -> Assertions.assertEquals(100, accountLine.getQuantity())
        );
    }

    @Test
    public void createAccountLineWithNoArgConstructorAndSetTheAccountAndQuoteAndQuantity() {
        Account account = new Account();
        account.setId(2L);

        Quote quote = new Quote();
        quote.setId(3L);

        accountLine = new AccountLine();
        accountLine.setId(1L);
        accountLine.setAccount(account);
        accountLine.setQuote(quote);
        accountLine.setQuantity(100);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, accountLine.getId()),
                () -> Assertions.assertEquals(account, accountLine.getAccount()),
                () -> Assertions.assertEquals(quote, accountLine.getQuote()),
                () -> Assertions.assertEquals(100, accountLine.getQuantity())
        );
    }
}
