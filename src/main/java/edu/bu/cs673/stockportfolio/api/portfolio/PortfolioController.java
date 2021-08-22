package edu.bu.cs673.stockportfolio.api.portfolio;

import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import edu.bu.cs673.stockportfolio.domain.user.User;
import edu.bu.cs673.stockportfolio.service.portfolio.PortfolioNotFoundException;
import edu.bu.cs673.stockportfolio.service.portfolio.PortfolioService;
import edu.bu.cs673.stockportfolio.service.user.UserService;
import edu.bu.cs673.stockportfolio.service.utilities.ResponseService;
import edu.bu.cs673.stockportfolio.service.utilities.ValidationService;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**********************************************************************************************************************
 * The FileUploadController handles client requests to insert and delete files.
 *
 * @note Max file size of 10MB.
 *********************************************************************************************************************/
@Controller
@RequestMapping("/portfolio")
public class PortfolioController {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(PortfolioController.class);
    private final UserService userService;
    private final PortfolioService portfolioService;
    private final ValidationService validationService;
    private final ResponseService responseService;

    public PortfolioController(UserService userService, PortfolioService portfolioService,
                               ValidationService validationService, ResponseService responseService) {
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.validationService = validationService;
        this.responseService = responseService;
    }

    @GetMapping("/export-template")
    public void exportCsvTemplate(HttpServletResponse response) throws Exception {

        // Set the name and content type of the template
        String filename = "PortfolioSight-Template.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        try {
            LOGGER.info().log("Start export for CSV template");
            OutputStream outputStream = response.getOutputStream();
            String outputResult = "Account,Symbol,Quantity\n1234-5678,GS,100\n8765-4321,JPM,200";
            outputStream.write(outputResult.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch(Exception e) {
            LOGGER.error().log("Error exporting CSV template");
        }
    }

    @PostMapping
    public String uploadPortfolio(Authentication authentication,
                                  @RequestParam("csvUpload")MultipartFile multipartFile, Model model) {
        User currentUser = getCurrentUser(authentication);

        if ( multipartFile == null) {
            return "home";
        }

        if (multipartFile.isEmpty()) {
            return responseService.uploadError(true, model);
        }

        boolean result;
        try {
            result = portfolioService.save(multipartFile, currentUser);
        } catch (InvalidFileNameException e) {
            LOGGER.error().log("Portfolio file name is invalid. Check for NUL character or malicious activity. "
                    + e.getMessage());

            return responseService.uploadError(true, model);
        }

        return responseService.uploadSuccess(result, model, currentUser, portfolioService);
    }

    private User getCurrentUser(Authentication authentication) {
        return userService.findUserByName(authentication.getName());
    }

    @PostMapping("/delete")
    public String deletePortfolio(Authentication authentication, Model model) {
        User currentUser = getCurrentUser(authentication);
        Portfolio currentPortfolio = currentUser.getPortfolio();

        if (currentPortfolio == null) {
            return "home";
        }

        boolean result = validationService.validatePortfolioOwner(currentPortfolio, currentUser, model, "delete");

        Long id = currentPortfolio.getId();
        if (result) {
            portfolioService.deletePortfolioBy(id);
        }

        if (isPortfolioDeleted(id)) {
            return responseService.deleteSuccess(true, model);
        }

        // If control flow gets here, the existing portfolio will be presented to the user
        return responseService.deletePortfolioError(true, model);
    }

    // Checks the database to confirm the portfolios existence after the delete request has been committed
    private boolean isPortfolioDeleted(Long id) {
        try {
            Portfolio currentPortfolio = portfolioService.getPortfolioBy(id);
            return currentPortfolio == null;
        } catch (PortfolioNotFoundException e) {
            // Fail gracefully by logging error and return true because the portfolio can't be found
            LOGGER.error().log("Portfolio not found.");
            return true;
        }
    }
}
