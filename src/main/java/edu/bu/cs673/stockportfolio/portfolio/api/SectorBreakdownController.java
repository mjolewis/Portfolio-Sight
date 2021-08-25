package edu.bu.cs673.stockportfolio.portfolio.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.bu.cs673.stockportfolio.portfolio.service.ResponseService;
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

@Controller
@RequestMapping("/sector_breakdown")
public class SectorBreakdownController {

    private final PortfolioService portfolioService;
    private final ResponseService responseService;
    private final UserService userService;

    public SectorBreakdownController(PortfolioService portfolioService, ResponseService responseService, UserService userService) {

        this.portfolioService = portfolioService;
        this.responseService = responseService;
        this.userService = userService;
    }

    @GetMapping
    public String sectorBreakdownView(Authentication authentication, Model model) {
        
        User user = getUser(authentication);
        Portfolio portfolio = null;
        Map<String, Float> data = new LinkedHashMap<String, Float>();
            
        if (user.getPortfolio() != null) {

            portfolio = portfolioService.getPortfolioBy(user.getPortfolio().getId());
            List<Account> accounts = portfolio.getAccounts();

            data = responseService.aggregateSumBySector(accounts);
        }

        model.addAttribute("data", data);
        return "sector_breakdown";
    }

    private User getUser(Authentication authentication) {

        return userService.findUserByName(authentication.getName());
    }
}
