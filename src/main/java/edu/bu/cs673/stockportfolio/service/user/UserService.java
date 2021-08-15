package edu.bu.cs673.stockportfolio.service.user;

import edu.bu.cs673.stockportfolio.domain.user.User;
import edu.bu.cs673.stockportfolio.domain.user.UserRepository;
import edu.bu.cs673.stockportfolio.service.authentication.HashService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

/**********************************************************************************************************************
 * Implements business logic for User requests.
 *********************************************************************************************************************/
@Service
@Transactional
public class UserService {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final HashService hashService;

    public UserService(UserRepository userRepository, HashService hashService) {
        this.userRepository = userRepository;
        this.hashService = hashService;
    }

    /**
     * Check if the provided username already exists otherwise register the user. If the username is already
     * registered, send a response indicating a registration error.
     *
     * @param username A username sent by the client.
     * @return True if the username is not in the database. Otherwise, false.
     */
    public boolean isUserNameAvailable(String username) {
        return userRepository.findAllByUsername(username) == null;
    }

    /**
     * Either creates or updates a user, based on prior existence of the user.
     *
     * @param user A user object, which can be either new or existing.
     * @return The new or updated user.
     */
    public User save(User user) {
        if (user.getId() != null) {
            return userRepository.findById(user.getId())
                    .map(userToBeUpdated -> {
                        userToBeUpdated.setEmail(user.getEmail());
                        userToBeUpdated.setUsername(user.getUsername());
                        // TODO: 6/16/21 update password securely
                        userToBeUpdated.setPortfolio(user.getPortfolio());
                        return userToBeUpdated;
                    }).orElseThrow(UserNotFoundException::new);
        }

        // Create a new user while hashing the provided password.
        String encodedSalt = generateSalt();
        String hashedPassword = hashService.getHashedValue(user.getPassword(), encodedSalt);

        user.setSalt(encodedSalt);
        user.setPassword(hashedPassword);
        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findUserByName(String userName) {
        return userRepository.findAllByUsername(userName);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public boolean verifyPassword(User user, String oldPassword) {
        Long id = user.getId();

        User existingUser = null;
        if (id != null) {
            existingUser = getExistingUser(id);
        }

        String hashedPasswordBasedOnUserInput;
        String hashedPasswordStoredInDatabase;
        if (existingUser != null) {
            String encodedSalt = existingUser.getSalt();
            hashedPasswordBasedOnUserInput = getHashedPassword(encodedSalt, oldPassword);
            hashedPasswordStoredInDatabase = existingUser.getPassword();

            return hashedPasswordBasedOnUserInput.equals(hashedPasswordStoredInDatabase);
        }

        return false;
    }

    private User getExistingUser(Long id) {
        try {
            return findUserById(id);
        } catch (UserNotFoundException e) {
            LOGGER.error().log("User id=" + " not found.");
            return null;
        }
    }

    private String getHashedPassword(String encodedSalt, String password) {
        return hashService.getHashedValue(password, encodedSalt);
    }

    public boolean updatePassword(User currentUser, String newPassword) {
        String encodedSalt = generateSalt();
        String hashedPassword = getHashedPassword(encodedSalt, newPassword);

        User existingUser;
        try {
            Long id = currentUser.getId();
            existingUser = getExistingUser(id);
        } catch (UserNotFoundException e) {
            LOGGER.error().log("User id=" + " not found.");
            return false;
        }

        if (existingUser != null) {
            existingUser.setSalt(encodedSalt);
            existingUser.setPassword(hashedPassword);
            return true;
        }

        return false;  // Existing user is null. False lets the controller know to update with UI with an error msg
    }
}
