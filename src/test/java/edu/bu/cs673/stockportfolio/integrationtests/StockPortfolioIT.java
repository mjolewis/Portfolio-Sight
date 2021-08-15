package edu.bu.cs673.stockportfolio.integrationtests;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
        "edu.bu.cs673.stockportfolio.integrationtests"
})
public class StockPortfolioIT {
    // This class remains empty, it is used only as a holder for the above annotations
}
