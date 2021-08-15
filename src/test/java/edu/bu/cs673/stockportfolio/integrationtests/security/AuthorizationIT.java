package edu.bu.cs673.stockportfolio.integrationtests.security;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**********************************************************************************************************************
 * Test user story: "As a security engineer, I want to protect the product from unauthorized access."
 *********************************************************************************************************************/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorizationIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private String baseURL;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        driver = new ChromeDriver();
        baseURL = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Test restricted access for unauthorized user.")
    public void unauthorizedAccess() {
        driver.get(baseURL + "/signup");
        assertEquals("Sign Up", driver.getTitle());

        driver.get(baseURL + "/login");
        assertEquals("Login", driver.getTitle());

        driver.get(baseURL + "/home");
        assertEquals("Login", driver.getTitle());
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
    }
}
