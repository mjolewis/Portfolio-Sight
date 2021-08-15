package edu.bu.cs673.stockportfolio.unittests.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.bu.cs673.stockportfolio.api.portfolio.PortfolioController;
import edu.bu.cs673.stockportfolio.service.authentication.AuthenticationService;
import edu.bu.cs673.stockportfolio.service.authentication.HashService;
import edu.bu.cs673.stockportfolio.service.authentication.SecurityConfig;
import edu.bu.cs673.stockportfolio.service.portfolio.PortfolioService;
import edu.bu.cs673.stockportfolio.service.user.UserService;
import edu.bu.cs673.stockportfolio.service.utilities.ResponseService;
import edu.bu.cs673.stockportfolio.service.utilities.ValidationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**********************************************************************************************************************
 * As a User, I want to upload my portfolio into the application and have the application store my data.
 *********************************************************************************************************************/
//@ExtendWith(SpringExtension.class)
//@AutoConfigureJsonTesters
@WebMvcTest(PortfolioController.class)
//@ContextConfiguration(classes = {SecurityConfig.class, AuthenticationService.class, HashService.class})
@AutoConfigureMockMvc
@WithMockUser()
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mvc;

//    private JacksonTester<Portfolio> json;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private PortfolioService portfolioService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ValidationService validationService;

    @MockBean
    private ResponseService responseService;

    private MockMultipartFile mockFile;

    /**
     * Creates pre-requisites for testing, such as creating an example Portfolio.
     */
    @BeforeEach
    public void setup() {
        mockFile =
                new MockMultipartFile(
                        "portfolio",
                        "/Users/mlewis/Downloads/TestPortfolio.csv",
                        "text/csv",
                        "portfolio".getBytes());
    }

//    @Test
//    public void uploadPortfolioWithValidFile() throws Exception {
//        mvc.perform(
//                multipart("/portfolio")
//                        .file(mockFile))
//                .andExpect(header().exists("Location"))
//                .andExpect(header().string("Location", Matchers.containsString("/portfolio")))
//                .andExpect(status().is3xxRedirection());
//
////        verify(portfolioService, times(1)).save(portfolio, portfolio.getUser());
//    }
//
//    @Test
//    public void uploadPortfolioAndThenDeleteIt() throws Exception {
//        mvc.perform(
//                multipart("/portfolio").file(mockFile))
//                .andExpect(status().is3xxRedirection());
//
//        mvc.perform(
//                multipart("/portfolio/delete").file(mockFile))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(header().exists("Location"))
//                .andExpect(header().string("Location", Matchers.containsString("/delete")));
//    }

//    // Creates an example Portfolio for use in testing.
//    private Portfolio getPortfolio() {
//        Portfolio portfolio = new Portfolio();
//        portfolio.setId(1L);
//        portfolio.setUser(new User());
//        portfolio.addAccount(new Account());
//        return  portfolio;
//    }
}
