package edu.bu.cs673.stockportfolio.api.userprofile;

import edu.bu.cs673.stockportfolio.domain.user.User;
import edu.bu.cs673.stockportfolio.service.authentication.HashService;
import edu.bu.cs673.stockportfolio.service.portfolio.PortfolioService;
import edu.bu.cs673.stockportfolio.service.user.UserService;
import edu.bu.cs673.stockportfolio.service.utilities.ResponseService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(PortfolioService.class);
    private final UserService userService;
    private final ResponseService responseService;

    public UserProfileController(UserService userService, ResponseService responseService) {
        this.userService = userService;
        this.responseService = responseService;
    }

    @GetMapping()
    public String getUserProfile() {
        return "user_profile";
    }

    @PostMapping("/delete")
    public String deleteUserProfile(Authentication authentication, HttpServletResponse response, Model model) {
        User currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return responseService.deletePortfolioError(true, model);
        }

        try {
            userService.delete(currentUser);

            if (currentUser.getId() != null) {
                Cookie cookie = new Cookie("JSESSIONID", "");
                cookie.setMaxAge(0);
                response.addCookie(cookie);

                return "signup";
            }
        } catch (Exception e) {
            LOGGER.error().log("Error deleting account for userId=" + currentUser.getId());
        }

        return responseService.deletePortfolioError(true, model);
    }

    @ResponseBody
    @PostMapping("/modifyPwd")
    public String modifyPassword(Authentication authentication,
                                 @RequestParam("oldPwd") String oldPassword,
                                 @RequestParam("newPwd") String newPassword) {

        User currentUser = getCurrentUser(authentication);

        boolean result = false;
        if (currentUser != null) {
            result = userService.verifyPassword(currentUser, oldPassword);
        }

        if (result) {
            result = userService.updatePassword(currentUser, newPassword);
        }

        if (result) {
            return "true";
        }

        return "false";  // The oldPassword does not match the password stored in our database
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findUserByName(authentication.getName());
    }
}
