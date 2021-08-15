package edu.bu.cs673.stockportfolio.service.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**********************************************************************************************************************'
 * Spring configuration class that implements the methods that modify Spring's configuration to use our Services. The
 * WebSecurityConfigurerAdapter describes the methods that modify Spring's security configuration.
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.authenticationService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Cross site scripting protection https://www.baeldung.com/spring-prevent-xss
//        http.
//                headers()
//                .xssProtection()
//                .and()
//                .contentSecurityPolicy("script-src 'self' " +
//                        "code.jquery.com cdnjs.cloudflare.com maxcdn.bootstrapcdn.com");

        http.authorizeRequests()
                // Allow unauthenticated users to access the signup endpoint.
                .antMatchers("/signup", "/css/*", "/js/*", "/images/*").permitAll()
                // Allow authenticated users access to all endpoints
                .anyRequest().authenticated()
                .and()
                // Permit all requests to the login endpoint.
                .formLogin().loginPage("/login").permitAll()
                .and()
                // Automatically redirect successful logins to the home endpoint.
                .formLogin().defaultSuccessUrl("/home", true)
                .and()
                .logout().permitAll()
                .and()
                // Session timeout occurs at 300 seconds (set in properties file). Redirect timed out session to login.
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login");
    }
}