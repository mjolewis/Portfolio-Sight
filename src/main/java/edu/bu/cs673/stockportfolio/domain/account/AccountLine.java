package edu.bu.cs673.stockportfolio.domain.account;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;

import javax.persistence.*;

/**********************************************************************************************************************
 * An AccountLine represents an associative entity between an Account and a Quote. An Account can have multiple
 * investments, including different lots of the same symbol, and the AccountLine represents each of these investments.
 *********************************************************************************************************************/
@Entity
public class AccountLine {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    private int quantity;

    public AccountLine() {
    }

    public AccountLine(Account account, Quote quote, int quantity) {
        this.account = account;
        this.quote = quote;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
