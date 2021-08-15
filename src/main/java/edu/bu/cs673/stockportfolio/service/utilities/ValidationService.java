package edu.bu.cs673.stockportfolio.service.utilities;

import edu.bu.cs673.stockportfolio.domain.account.Account;
import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import edu.bu.cs673.stockportfolio.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**********************************************************************************************************************
 * Validates client requests to ensure the current user has authorization to perform the specified action.
 *********************************************************************************************************************/
@Service
public class ValidationService {
    public boolean validateAccountOwner(Account account, User user, Model model, String crud) {
        Portfolio portfolio = account.getPortfolio();
        return validatePortfolioOwner(portfolio, user, model, crud);
    }

    public boolean validatePortfolioOwner(Portfolio portfolio, User user, Model model, String crud){
        String message = null;
        boolean result = true;

        if (portfolio == null) {
            message = "Portfolio not found.";
        } else if (portfolio.getUser() != user) {
            message = "Error. Only the portfolio or account owner can perform an " + crud + ".";
        }

        if (message != null) {
            model.addAttribute("message", message);
            result = false;
        }

        return result;
    }
}
