package edu.bu.cs673.stockportfolio.integrationtests.profile;

import edu.bu.cs673.stockportfolio.integrationtests.homepage.HomePage;
import edu.bu.cs673.stockportfolio.integrationtests.login.LoginPage;
import edu.bu.cs673.stockportfolio.integrationtests.utilityPages.ResultPage;
import edu.bu.cs673.stockportfolio.integrationtests.utilityPages.ErrorPage;
import edu.bu.cs673.stockportfolio.integrationtests.signup.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;


/**********************************************************************************************************************
 * Test user story: "As a user, I can signup, login, change password, and delete my account"
 *********************************************************************************************************************/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserProfileIT {
	@LocalServerPort
    private int port;

    private static WebDriver driver;
    private String baseURL;
    private SignupPage signupPage;
    private LoginPage loginPage;
    private HomePage homePage;
    private ProfilePage profilePage;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        driver = new ChromeDriver();
        baseURL = "http://localhost:" + port;
        signupPage = new SignupPage(driver);
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        profilePage = new ProfilePage(driver);
    }

    @Test
    @DisplayName("Test profile page")
    public void testProfile() {
    	driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and profile
        driver.get(baseURL + "/home");
        homePage.clickProfile(driver);
        String currentUrl = driver.getCurrentUrl();

        assertAll("Profile",
                () -> assertEquals(baseURL + "/profile", currentUrl,
                        "Incorrect endpoint for profile"));
    }
    
    @Test
    @DisplayName("Test delete profile")
    public void testDeleteProfile() throws InterruptedException {
       	driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", "John5", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John5", "10DigitPassword!");
        driver.get(baseURL + "/home");
        homePage.clickProfile(driver);
        
        profilePage.clickDeleteAccountButton(driver);
        profilePage.confirmPrompt(driver);
        String currentUrl = driver.getCurrentUrl();

        assertAll("Back to sign up",
                () -> assertEquals(baseURL + "/signup", currentUrl,
                        "Incorrect endpoint for sign up after profile deletion"));
    }
    
    @Test
    @DisplayName("Test change password")
    public void testChangePassword() throws InterruptedException {
    	String username = profilePage.generateRandomUsername();
    	
    	driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", username, "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, username, "10DigitPassword!");

        driver.get(baseURL + "/home");
        homePage.clickProfile(driver);
        profilePage.changePassword(driver, "10DigitPassword!", "11DigitPassword!!", "11DigitPassword!!");
        profilePage.confirmPrompt(driver);
        
        boolean result = profilePage.isSuccessMessageDisplayed(driver);
        assertTrue(result);
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
    }
}
