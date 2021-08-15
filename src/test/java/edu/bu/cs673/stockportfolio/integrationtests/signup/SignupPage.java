package edu.bu.cs673.stockportfolio.integrationtests.signup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**********************************************************************************************************************
 * A Selenium Page Object representing the Signup flow.
 *
 * "As a user, I can create a new account in order to login to the application."
 *********************************************************************************************************************/
public class SignupPage {

    @FindBy(id = "inputEmail")
    private WebElement email;

    @FindBy(id = "inputUsername")
    private WebElement username;

    @FindBy(id = "inputPassword")
    private WebElement password;

    @FindBy(id = "submitButton")
    private WebElement submitButton;
    
    @FindBy(id = "error-msg")
    private WebElement errorMsg;
    
    @FindBy(xpath = "//a")
    private WebElement xpath;

    /**
     * Constructor starts the process of allowing Selenium to automatically process the element selectors.
     *
     * @param driver A browser driver instance.
     */
    public SignupPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void signup(String email, String username, String password) {
        this.email.sendKeys(email);
        this.username.sendKeys(username);
        this.password.sendKeys(password);
        this.submitButton.click();
    }
    
    public boolean isErrorMessageDisplayed() {
    	return errorMsg.isDisplayed();
    }
    
    public void clickSignup() {
    	this.xpath.click();
    }
}
