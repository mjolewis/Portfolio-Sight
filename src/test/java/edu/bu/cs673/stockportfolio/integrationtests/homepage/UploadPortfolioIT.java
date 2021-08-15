package edu.bu.cs673.stockportfolio.integrationtests.homepage;

import edu.bu.cs673.stockportfolio.integrationtests.homepage.HomePage;
import edu.bu.cs673.stockportfolio.integrationtests.login.LoginPage;
import edu.bu.cs673.stockportfolio.integrationtests.utilityPages.ResultPage;
import edu.bu.cs673.stockportfolio.integrationtests.signup.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**********************************************************************************************************************
 * Test user story: "As a user, I can signup, login, and upload my portfolio."
 *********************************************************************************************************************/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UploadPortfolioIT {

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
    @DisplayName("Test portfolio upload.")
    public void testPortfolioUpload() {
        file = new File("/src/test/resources/csv/TestPortfolio.csv");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;
        
        String testTickerOne = "GS";
        String testTickerTwo = "FB";
        String testCompanyNameOne = "Goldman Sachs Group, Inc.";
        String testCompanyNameTwo = "Facebook Inc - Class A";

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
        String actualTickerOne = homePage.find(driver, testTickerOne);
        String actualTickerTwo = homePage.find(driver, testTickerTwo);
        String actualCompanyNameOne = homePage.find(driver, testCompanyNameOne);
        String actualCompanyNameTwo = homePage.find(driver, testCompanyNameTwo);

        assertAll("Upload portfolio",
                () -> assertTrue(result, "Portfolio upload failed."),
                () -> assertEquals(testTickerOne, actualTickerOne, "GS symbol is incorrect."),
                () -> assertEquals(testTickerTwo, actualTickerTwo, "FB symbol is incorrect."),
                () -> assertEquals(testCompanyNameOne, actualCompanyNameOne, "Company name is incorrect."),
                () -> assertEquals(testCompanyNameTwo, actualCompanyNameTwo, "Company name is incorrect."));
    }
    
    @Test
    @DisplayName("Test empty csv file upload")
    public void testEmptyPortfolioUpload() {
        file = new File("/src/test/resources/csv/empty.csv");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;
        
        driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and upload empty csv file
        driver.get(baseURL + "/home");
        homePage.clickUploadPortfolio(driver, filePath);
        homePage.clickUploadPortfolioButton(driver);
        boolean result = resultPage.isErrorMessageDisplayed();
        assertTrue(result);
        resultPage.clickNavLink(driver);
    }
    
    @Test
    @DisplayName("Test upload incorrect formatted csv file")
    public void testInvalidFormatCsvUpload() {
    	file = new File("/src/test/resources/csv/invalidcsv.csv");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;
    	
        driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and upload differently formatted csv file
        driver.get(baseURL + "/home");
        homePage.clickUploadPortfolio(driver, filePath);
        homePage.clickUploadPortfolioButton(driver);

        boolean result = resultPage.isError500Displayed();
        assertTrue(result);
        resultPage.clickHomepageLink(driver);
    }
    
    @Test
    @DisplayName("Test upload none csv")
    public void testNotCsvUpload() {
    	file = new File("/src/test/resources/csv/fakecats.jpg");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;

        driver.get(baseURL + "/signup");
        signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and upload jpg file
        driver.get(baseURL + "/home");
        homePage.clickUploadPortfolio(driver, filePath);
        homePage.clickUploadPortfolioButton(driver);

        boolean result = resultPage.isError500Displayed();
        assertTrue(result);
        resultPage.clickHomepageLink(driver);
    }
    
    @Test
    @DisplayName("Test upload incorrect file type")
    public void testInvalidFileTypeUpload() {
    	file = new File("/src/test/resources/csv/foo.exe.png");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;
    	
    	driver.get(baseURL + "/signup");
    	signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and upload file with invalid formatted file
        driver.get(baseURL + "/home");
        homePage.clickUploadPortfolio(driver, filePath);
        homePage.clickUploadPortfolioButton(driver);
        
        boolean result = resultPage.isErrorMessageDisplayed();
        assertTrue(result);
        resultPage.clickNavLink(driver);
    }
    
    @Test
    @DisplayName("Test upload more than 10mb csv")
    public void testBigCsvUpload() {
    	file = new File("/src/test/resources/csv/100000-Sales-Records.csv");
        String csvDataPath = file.getPath();
        String filePath = projectPath + csvDataPath;
    	
    	driver.get(baseURL + "/signup");
    	signupPage.signup("money@spd.com", "John", "10DigitPassword!");

        driver.get(baseURL + "/login");
        loginPage.login(driver, "John", "10DigitPassword!");

        // Go to homepage and upload big size csv file
        driver.get(baseURL + "/home");
        homePage.clickUploadPortfolio(driver, filePath);
        homePage.clickUploadPortfolioButton(driver);
        
        boolean result = resultPage.isErrorMessageDisplayed();
        assertTrue(result);
        resultPage.clickNavLink(driver);
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
    }
}
