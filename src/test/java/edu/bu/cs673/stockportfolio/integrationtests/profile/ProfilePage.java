package edu.bu.cs673.stockportfolio.integrationtests.profile;

import edu.bu.cs673.stockportfolio.integrationtests.utilityPages.WaitPage;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

/**********************************************************************************************************************
 * A Selenium Page Object representing the Profile flow.
 *
 * "As a user, I can change my user password or delete my profile."
 *********************************************************************************************************************/

public class ProfilePage extends WaitPage {
	private static final String DELETE_ACCOUNT = "delete-account";
    @FindBy(id = DELETE_ACCOUNT)
    private WebElement deleteAccount;
    
    private static final String OLD_PASSWORD = "oldPassword";
    @FindBy(id = OLD_PASSWORD)
    private WebElement oldPassword;
    
    private static final String NEW_PASSWORD = "newPassword";
    @FindBy(id = NEW_PASSWORD)
    private WebElement newPassword;
    
    private static final String CONFIRM_NEW_PASSWORD = "confirmNewPassword";
    @FindBy(id = CONFIRM_NEW_PASSWORD)
    private WebElement confirmNewPassword;
    
    private static final String CHANGE_PASSWORD = "changePassword";
    @FindBy(id = CHANGE_PASSWORD)
    private WebElement changePassword;
    
    private static final String SUCCESS_CHANGE = "successMsg";
    @FindBy(id = SUCCESS_CHANGE)
    private WebElement successChange;
    
    public ProfilePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void clickDeleteAccountButton(WebDriver driver) {
        waitForElement(driver, DELETE_ACCOUNT).sendKeys(Keys.ENTER);
    }
    
    public void confirmPrompt(WebDriver driver) {
    	Alert alert = driver.switchTo().alert();
    	alert.accept();
    }
    
    public void changePassword(WebDriver driver, String oldPassword, String newPassword, String confirmNewPassword) {
        waitForElement(driver, OLD_PASSWORD).sendKeys(oldPassword);
        waitForElement(driver, NEW_PASSWORD).sendKeys(newPassword);
        waitForElement(driver, CONFIRM_NEW_PASSWORD).sendKeys(confirmNewPassword);
        waitForElement(driver, CHANGE_PASSWORD).sendKeys(Keys.ENTER);
    }
    
    public boolean isSuccessMessageDisplayed(WebDriver driver) {
        return isElementDisplayed(driver, SUCCESS_CHANGE);
    }
    
    public String generateRandomUsername() {
    	String randomUsername = RandomStringUtils.randomAlphabetic(5);
    	return randomUsername;
    }
}
