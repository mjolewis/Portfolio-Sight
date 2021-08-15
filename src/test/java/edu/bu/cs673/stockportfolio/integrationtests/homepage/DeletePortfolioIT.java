package edu.bu.cs673.stockportfolio.integrationtests.homepage;

import edu.bu.cs673.stockportfolio.integrationtests.homepage.HomePage;
import edu.bu.cs673.stockportfolio.integrationtests.login.LoginPage;
import edu.bu.cs673.stockportfolio.integrationtests.utilityPages.ResultPage;
import edu.bu.cs673.stockportfolio.integrationtests.signup.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;


/**********************************************************************************************************************
 * Test user story: "As a user, I can signup, login, upload portfolio, and delete my portfolio"
 *********************************************************************************************************************/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeletePortfolioIT {
	@LocalServerPort
    private int port;

    private static WebDriver driver;
    private String baseURL;
    private SignupPage signupPage;
    private LoginPage loginPage;
    private HomePage homePage;
    private ResultPage resultPage;
    private File file;
    private String projectPath;

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
        resultPage = new ResultPage(driver);
        file = new File("");
        projectPath = file.getAbsolutePath();
    }

    @Test
    @DisplayName("Test delete portfolio")
    public void testPortfolioDelete() {
        file = new File("/src/test/resources/csv/PortfolioDashboardTest.csv");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;

        driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and upload file
        driver.get(baseURL + "/home");
        homePage.clickUploadPortfolio(driver, filePath);
        homePage.clickUploadPortfolioButton(driver);

        boolean result = resultPage.isSuccessMessageDisplayed(driver);
        resultPage.clickNavLink(driver);
        driver.get(baseURL + "/home");
        
        // Delete portfolio from homepage and verify url
        homePage.clickDeletePortfolioButton(driver);
        String currentUrl = driver.getCurrentUrl();

        assertAll("Delete",
                () -> assertEquals(baseURL + "/portfolio/delete", currentUrl,
                        "Incorrect endpoint for delete"));
        
        result = resultPage.isSuccessMessageDisplayed(driver);
        assertTrue(result);
        resultPage.clickNavLink(driver);
        
        // Go to home and verify portfolio is deleted
        driver.get(baseURL + "/home");
        result = homePage.checkPortfolioTablePresent(driver);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test delete not existing portfolio")
    public void testDeleteNotExistingPortfolio() {
        driver.get(baseURL + "/login");
        loginPage.login(driver, "john", "10DigitPassword!");
        driver.get(baseURL + "/home");
        
        // Delete not existing portfolio and verify error message is displayed
        homePage.clickDeletePortfolioButton(driver);
        boolean result = homePage.checkPortfolioTablePresent(driver);
        assertFalse(result);
    }
    
    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
    }
}