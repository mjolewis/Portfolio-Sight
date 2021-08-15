package edu.bu.cs673.stockportfolio.unittests.service.user;

import static org.mockito.Mockito.when;

import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import edu.bu.cs673.stockportfolio.domain.user.User;
import edu.bu.cs673.stockportfolio.domain.user.UserRepository;
import edu.bu.cs673.stockportfolio.service.authentication.HashService;
import edu.bu.cs673.stockportfolio.service.user.UserNotFoundException;
import edu.bu.cs673.stockportfolio.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 * https://rieckpil.de/difference-between-mock-and-mockbean-spring-boot-applications/
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashService hashService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User(1L, "username", "password123",
                "123", "username@gmail.com", new Portfolio());
    }

    @Test
    public void isUsernameAvailableReturnsTrueIfUsernameIsNotInDatabase() {
        when(userRepository.findAllByUsername(user.getUsername())).thenReturn(null);

        Assertions.assertTrue(userService.isUserNameAvailable(user.getUsername()));
    }

    @Test
    public void isUsernameAvailableReturnsFalseIfUsernameIsInDatabase() {
        when(userRepository.findAllByUsername(user.getUsername())).thenReturn(user);

        Assertions.assertFalse(userService.isUserNameAvailable(user.getUsername()));
    }

    @Test
    public void searchingForExistingUserByIdReturnsUserFromDatabase() {
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.ofNullable(user));

        User result = userService.findUserById(user.getId());

        Assertions.assertEquals(user, result);
    }

    @Test
    public void searchingForNonExistingUserByIdReturnsUserNotFoundException() {
        when(userRepository.findById(500L)).thenThrow(new UserNotFoundException());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findUserById(500L));
    }

    @Test
    public void searchingForExistingUserByUsernameReturnsUserFromDatabase() {
        when(userRepository.findAllByUsername(user.getUsername())).thenReturn(user);

        User result = userService.findUserByName(user.getUsername());

        Assertions.assertEquals(user, result);
    }

    @Test
    public void searchingForNonExistingUserByUsernameReturnsUserNotFoundException() {
        when(userRepository.findAllByUsername("no name")).thenThrow(new UserNotFoundException());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findUserByName("no name"));
    }

    @Test
    public void savingUserToDatabaseReturnsSavedUser() {
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.ofNullable(user));

        User result = userService.save(user);

        Assertions.assertEquals(user, result);
    }

    @Test
    public void savingUserWithNullIdToDatabaseFirstCreatesAValidUserThenSavesUserToDatabaseAndReturnsSavedUser() {
        User userWithNoId = new User();
        userWithNoId.setUsername("no name");
        userWithNoId.setEmail("noname@gmail.com");
        userWithNoId.setSalt("2345jadf2");
        userWithNoId.setPortfolio(new Portfolio());

        when(hashService.getHashedValue("password", userWithNoId.getSalt())).thenReturn("hashedPassword");
        userWithNoId.setPassword(hashService.getHashedValue("password", userWithNoId.getSalt()));

        // save user without id should return a full fledged user
        when(userRepository.save(userWithNoId)).thenReturn(user);
        User result = userService.save(userWithNoId);

        Assertions.assertNotNull(result.getId());
    }
}
