package edu.bu.cs673.stockportfolio.integrationtests.utilityPages;

import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**********************************************************************************************************************
 * The WaitPage is a utility class that implements a WebDriverWait object for all Page objects.
 *********************************************************************************************************************/
public abstract class WaitPage {
    private final FluentLogger log = FluentLoggerFactory.getLogger(WaitPage.class);
    private static final int WAIT_TIME_IN_SECONDS = 30;

    protected WebElement waitForElement(WebDriver driver, String elementId) {
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIME_IN_SECONDS);

        return wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id(elementId))));
        //return wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(elementId))));
    }

    protected String waitForPageSearch(WebDriver driver, String text) {
        WebElement wait;
        try {
            wait = new WebDriverWait(driver, WAIT_TIME_IN_SECONDS)
                    .until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[text()='" + text + "']"))));
        } catch (NoSuchElementException e) {
            log.error().log(e.getMessage());
            return null;
        }

        return wait.getText();
    }

    protected boolean isElementDisplayed(WebDriver driver, String elementId) {
        try {
            WebElement element = waitForElement(driver, elementId);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            log.error().log(e.getMessage());
            return false;
        }
    }
}