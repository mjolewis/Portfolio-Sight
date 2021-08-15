package edu.bu.cs673.stockportfolio.service.authentication;

import edu.bu.cs673.stockportfolio.domain.user.User;
import edu.bu.cs673.stockportfolio.domain.user.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**********************************************************************************************************************
 * Implements the Spring interface AuthenticationProvider. This allows the service to integrate our provider with
 * different authentication schemes.
 *********************************************************************************************************************/
@Service
public class AuthenticationService implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final HashService hashService;

    public AuthenticationService(UserRepository userRepository, HashService hashService) {
        this.userRepository = userRepository;
        this.hashService = hashService;
    }

    /**
     * Checks the user's hashed password with the newly hashed value from the user input to see if the two match.
     *
     * @param authentication A Spring Authentication object.
     * @return An authentication token.
     * @throws AuthenticationException Authentication error object.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findAllByUsername(username);
        if (user != null) {
            String encodedSalt = user.getSalt();
            String hashedPassword = hashService.getHashedValue(password, encodedSalt);
            if (user.getPassword().equals(hashedPassword)) {
                return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
