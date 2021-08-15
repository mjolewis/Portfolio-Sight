package edu.bu.cs673.stockportfolio.unittests.domain.user;

import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import edu.bu.cs673.stockportfolio.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserEntityTest {

    @InjectMocks
    private User user;

    @Test
    public void createUserWithId() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(2L);
        user = new User(1L, "username", "password",
                "2337dk", "user@gmail.com", portfolio);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, user.getId()),
                () -> Assertions.assertEquals("username", user.getUsername()),
                () -> Assertions.assertEquals("password", user.getPassword()),
                () -> Assertions.assertEquals("2337dk", user.getSalt()),
                () -> Assertions.assertEquals("user@gmail.com", user.getEmail()),
                () -> Assertions.assertEquals(portfolio, user.getPortfolio())
        );
    }

    @Test
    public void createUserWithoutId() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(2L);
        user = new User("username", "password",
                "2337dk", "user@gmail.com", portfolio);

        user.setId(3L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(3L, user.getId()),
                () -> Assertions.assertEquals("username", user.getUsername()),
                () -> Assertions.assertEquals("password", user.getPassword()),
                () -> Assertions.assertEquals("2337dk", user.getSalt()),
                () -> Assertions.assertEquals("user@gmail.com", user.getEmail()),
                () -> Assertions.assertEquals(portfolio, user.getPortfolio())
        );
    }
}
