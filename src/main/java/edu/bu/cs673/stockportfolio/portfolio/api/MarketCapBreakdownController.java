package edu.bu.cs673.stockportfolio.portfolio.api;

import java.util.*;

import edu.bu.cs673.stockportfolio.portfolio.service.PortfolioNotFoundException;
import edu.bu.cs673.stockportfolio.portfolio.service.ResponseService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.bu.cs673.stockportfolio.portfolio.model.persistence.Account;
import edu.bu.cs673.stockportfolio.portfolio.model.persistence.Portfolio;
import edu.bu.cs673.stockportfolio.user.model.persistence.User;
import edu.bu.cs673.stockportfolio.portfolio.service.PortfolioService;
import edu.bu.cs673.stockportfolio.user.service.UserService;
import edu.bu.cs673.stockportfolio.marketdata.config.MarketCapType;

@Controller
@RequestMapping("/mc_breakdown")
public class MarketCapBreakdownController {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(MarketCapBreakdownController.class);
    private final PortfolioService portfolioService;
    private final ResponseService responseService;
    private final UserService userService;

    public MarketCapBreakdownController(PortfolioService portfolioService,
                                        ResponseService responseService, UserService userService) {

        this.portfolioService = portfolioService;
        this.responseService = responseService;
        this.userService = userService;
    }

    @GetMapping
    public String marketCapBreakdownView(Authentication authentication, Model model) {
        User user = getUser(authentication);
        Portfolio portfolio;
        Map<String, Float> data;

        if (user.getPortfolio() != null) {

            try {
                portfolio = portfolioService.getPortfolioBy(user.getPortfolio().getId());
                List<Account> accounts = portfolio.getAccounts();

                for ( MarketCapType marketCapType : MarketCapType.values() ) {
                    data = responseService.aggregateSumBySymbol(accounts, marketCapType);
                    model.addAttribute(marketCapType.toString(), data);
                }
            } catch (PortfolioNotFoundException e) {
                // Fail gracefully by logging error, while allowing control flow to return an empty html page.
                LOGGER.error().log("Portfolio not found.");
            }
        }

        return "mc_breakdown";
    }

    private User getUser(Authentication authentication) {
        return userService.findUserByName(authentication.getName());
    }
}
