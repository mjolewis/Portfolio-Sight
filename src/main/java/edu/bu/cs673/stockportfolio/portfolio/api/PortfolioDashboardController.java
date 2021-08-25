package edu.bu.cs673.stockportfolio.portfolio.api;

import edu.bu.cs673.stockportfolio.portfolio.model.persistence.Account;
import edu.bu.cs673.stockportfolio.portfolio.model.persistence.Portfolio;
import edu.bu.cs673.stockportfolio.portfolio.service.PortfolioNotFoundException;
import edu.bu.cs673.stockportfolio.portfolio.service.PortfolioService;
import edu.bu.cs673.stockportfolio.portfolio.service.ResponseService;
import edu.bu.cs673.stockportfolio.user.model.persistence.User;
import edu.bu.cs673.stockportfolio.user.service.UserService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**********************************************************************************************************************
 * Handles requests to populate the homepage after a user is logged in.
 *********************************************************************************************************************/
@Controller
@RequestMapping("/home")
public class PortfolioDashboardController {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(PortfolioDashboardController.class);
    private final UserService userService;
    private final PortfolioService portfolioService;
    private final ResponseService responseService;

    public PortfolioDashboardController(UserService userService,
                                        PortfolioService portfolioService,
                                        ResponseService responseService) {
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.responseService = responseService;
    }

    @GetMapping
    public String getHomePage(Authentication authentication, Model model) {
        User user = getUser(authentication);
        model.addAttribute("user", user);

        Portfolio portfolio = user.getPortfolio();
        if (portfolio != null) {
            try {
                Long id = portfolio.getId();
                portfolio = portfolioService.getPortfolioBy(id);
            } catch (PortfolioNotFoundException e) {
                // Fail gracefully by logging error and returning an arrayList to mimic an empty portfolio
                LOGGER.error().log("Portfolio not found.");
                model.addAttribute("portfolio", new ArrayList<>());
            }
        }

        if (portfolio != null) {
            List<Account> accounts = portfolio.getAccounts();
            model.addAttribute("portfolio", responseService.createPortfolioTable(accounts));
        }

        return "home";
    }

    private User getUser(Authentication authentication) {
        return userService.findUserByName(authentication.getName());
    }
}