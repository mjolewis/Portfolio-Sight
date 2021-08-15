package edu.bu.cs673.stockportfolio.domain.user;

import com.sun.istack.NotNull;
import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

/**********************************************************************************************************************
 * The User object represents a user of our software product. Each User can have a Portfolio with many Accounts. Each
 * Account can have many AccountLines. An AccountLine represents a symbol and quantity for an active investment.
 *********************************************************************************************************************/
@Entity
@Check(constraints = "CHECK (LENGTH(TRIM(username)) > 0) &&"
        + " CHECK (LENGTH(TRIM(email)) > 0) &&"
        + " CHECK (LENGTH(TRIM(password)) > 10)")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Nationalized
    @NotNull
    private String username;

    @Nationalized
    @NotNull
    private String password;

    private String salt;

    @Nationalized
    @NotNull
    private String email;

    // mappedBy creates a foreign key
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Portfolio portfolio;

    public User() {
    }

    public User(String username, String password, String salt, String email, Portfolio portfolio) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.portfolio = portfolio;
    }

    public User(Long id, String username, String password, String salt, String email, Portfolio portfolio) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.portfolio = portfolio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
