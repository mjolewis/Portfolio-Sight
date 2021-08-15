package edu.bu.cs673.stockportfolio.domain.portfolio;

import edu.bu.cs673.stockportfolio.domain.account.Account;
import edu.bu.cs673.stockportfolio.domain.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**********************************************************************************************************************
 * Data object representing a users Portfolio. An Portfolio is a collection of accounts.
 *********************************************************************************************************************/
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    public Portfolio() {
    }

    public Portfolio(User user) {
        this.user = user;
    }

    public Portfolio(User user, List<Account> accounts) {
        this.user = user;
        this.accounts = accounts;
    }

    public Portfolio(Long id, User user, List<Account> accounts) {
        this.id = id;
        this.user = user;
        this.accounts = accounts;
    }

    /**
     * Create a list of accounts if one doesn't exist. Then add an account to the portfolio.
     * @param account An account object that gets added to the portfolio.
     */
    public void addAccount(Account account) {
        if (accounts == null) {
            this.accounts = new ArrayList<>();
        }

        accounts.add(account);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
