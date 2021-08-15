package edu.bu.cs673.stockportfolio.integrationtests.utilityPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**********************************************************************************************************************
 * A Selenium Page Object representing the error of user actions.
 *
 * "As a user, I can upload a CSV based portfolio and get notified whether or not the upload was successful."
 *********************************************************************************************************************/
public class ErrorPage extends WaitPage {

    private static final String ERROR_MESSAGE = "error-message";
    @FindBy(id = ERROR_MESSAGE)
    private WebElement errorMessage;
 
    private static final String HOMEPAGE_LINK = "home-page";
    @FindBy(id = HOMEPAGE_LINK)
    private WebElement homepageLink;

    public ErrorPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public boolean isErrorMessageDisplayed(WebDriver driver) {
        return isElementDisplayed(driver, ERROR_MESSAGE);
    }

    public void clickHomepageLink(WebDriver driver) {
    	waitForElement(driver, HOMEPAGE_LINK);
    }
}