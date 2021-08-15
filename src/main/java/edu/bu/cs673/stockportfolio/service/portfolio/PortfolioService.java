package edu.bu.cs673.stockportfolio.service.portfolio;

import edu.bu.cs673.stockportfolio.domain.account.Account;
import edu.bu.cs673.stockportfolio.domain.account.AccountLine;
import edu.bu.cs673.stockportfolio.domain.account.AccountLineRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import edu.bu.cs673.stockportfolio.domain.portfolio.Portfolio;
import edu.bu.cs673.stockportfolio.domain.portfolio.PortfolioRepository;
import edu.bu.cs673.stockportfolio.domain.user.User;
import edu.bu.cs673.stockportfolio.service.company.CompanyService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**********************************************************************************************************************
 * Implements business logic for Portfolio requests, including uploading, modifying, and deleting portfolio data.
 *********************************************************************************************************************/
@Service
@Transactional
public class PortfolioService {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(PortfolioService.class);
    private static final String[] HEADERS = {"Account", "Symbol", "Quantity"};
    private final PortfolioRepository portfolioRepository;
    private final MarketDataServiceImpl marketDataServiceImpl;
    private final AccountLineRepository accountLineRepository;
    private final CompanyService companyService;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            MarketDataServiceImpl marketDataServiceImpl,
                            AccountLineRepository accountLineRepository,
                            CompanyService companyService) {
        this.portfolioRepository = portfolioRepository;
        this.marketDataServiceImpl = marketDataServiceImpl;
        this.accountLineRepository = accountLineRepository;
        this.companyService = companyService;
    }

    /**
     * Either creates a portfolio or appends new data in a non-destructive way, based on prior existence of the
     * portfolio
     * @param multipartFile A representation of an uploaded csv file, which can be either new or existing
     * @param currentUser The current user and owner of the portfolio
     * @return The new or updated employee stored in the repository
     */
    public boolean save(MultipartFile multipartFile, User currentUser) {

        Iterable<CSVRecord> records = null;
        try {
            BufferedReader fileReader = doCreateBufferedReader(multipartFile);
            records = doCreateCSVRecords(fileReader);
        } catch (IOException e) {
            LOGGER.error().log("File upload error for User: " + currentUser + ". ", e.getMessage());
        }

        Map<String, Map<String, Integer>> portfolioData = null;
        List<Quote> quotes = null;
        List<Company> companies;
        if (records != null) {
            portfolioData = doInternalParse(records);

            // Collect all symbols in the portfolio and send them as a a batch request to IEX Cloud
            Set<String> allSymbols = doGetAllSymbols(portfolioData);
            quotes = marketDataServiceImpl.doGetQuotes(allSymbols);

            // Collect company data for the symbols being imported and make an association to its quotes
            companies = marketDataServiceImpl.doGetCompanies(allSymbols);
            doCreateCompanies(companies);
            companyService.doLinkQuotes(quotes);
        }

        Portfolio savedPortfolio = null;
        Portfolio currentPortfolio = currentUser.getPortfolio();
        if (portfolioData != null && currentPortfolio != null) {
            // doInternalParse succeeded and the current user already has a Portfolio stored in the database
            try {
                savedPortfolio = doUpdatePortfolio(currentPortfolio.getId(), portfolioData, quotes);
            } catch (PortfolioNotFoundException e) {
                // Fail gracefully by logging error and allow the program to continue executing
                LOGGER.error().log("Portfolio not found. " + e.getMessage());
            }
        }

        // The current user doesn't have a Portfolio stored in the database
        if (portfolioData != null && currentPortfolio == null) {
            // Create the portfolio and flush the transaction to generate a portfolio id
            Portfolio portfolio = doCreatePortfolio(currentUser);
            savedPortfolio = portfolioRepository.save(portfolio);

            // Add accounts to the portfolio
            List<Account> accounts = doCreateAccounts(portfolioData, savedPortfolio, quotes);

            // Maintain referential integrity between the user, portfolio, and accounts
            currentUser.setPortfolio(portfolio);
            savedPortfolio.setAccounts(accounts);
        }

        return savedPortfolio != null && savedPortfolio.getId() > 0;
    }

    private BufferedReader doCreateBufferedReader(MultipartFile multipartFile) throws IOException {
        return new BufferedReader(new InputStreamReader(
                multipartFile.getInputStream(),
                StandardCharsets.UTF_8));
    }

    private Iterable<CSVRecord> doCreateCSVRecords(BufferedReader fileReader) throws IOException {
        return CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(fileReader);
    }

    // Read portfolio data from CSV records. Store account number, symbols, and quantities as account lines
    private Map<String, Map<String, Integer>> doInternalParse(Iterable<CSVRecord> records) {

        // Data structure organization = Map<accountNumber, Map<symbol, totalQuantity>)
        Map<String, Map<String, Integer>> accountLines = new HashMap<>();
        for (CSVRecord record : records) {

            // Extract the records from the csv file by using the column headers
            String account = record.get(HEADERS[0]);
            String symbol = record.get(HEADERS[1]);
            int quantity = Integer.parseInt(record.get(HEADERS[2]));

            if (accountLines.containsKey(account)) {
                Map<String, Integer> line = accountLines.get(account);
                if (line.containsKey(symbol)) {
                    line.put(symbol, line.get(symbol) + quantity);
                } else {
                    // The account number already exists, but the user added a new symbol to it
                    line.put(symbol, quantity);
                }
            } else {
                accountLines.put(account, new HashMap<>(Map.of(symbol, quantity)));
            }
        }

        return accountLines;
    }

    private Set<String> doGetAllSymbols(Map<String, Map<String, Integer>> portfolioData) {
        Set<String> allSymbols = new HashSet<>();
        portfolioData.forEach((account, accountLine)-> {
            allSymbols.add(String.join(",", accountLine.keySet()));
        });

        return allSymbols;
    }

    private Set<String> doGetAllSymbols(List<List<AccountLine>> portfolioData) {
        Set<String> allSymbols = new HashSet<>();
        portfolioData.forEach(accountLines -> {
            accountLines.forEach(accountLine -> {
                allSymbols.add(String.join(",", accountLine.getQuote().getSymbol()));
            });
        });

        return allSymbols;
    }

    // Update or add new portfolio data depending on the existence of the associated account
    private Portfolio doUpdatePortfolio(Long portfolioId, Map<String, Map<String, Integer>> portfolioData,
                                        List<Quote> allQuotes) {

        return portfolioRepository.findById(portfolioId)
                .map(portfolioToBeUpdated -> {

                    // Get the list of all existing accounts and check for its existence in the new portfolioData. If
                    // the account exists, it will get updated. Otherwise, new account lines are created.
                    List<Account> accounts = portfolioToBeUpdated.getAccounts();

                    portfolioData.forEach((accountNumber, accountLines) -> {
                        Optional<Account> optionalAccount = doAccountFilter(accounts, accountNumber);

                        // Append account lines if the account is new to the portfolio.
                        if (optionalAccount.isEmpty()) {
                            Account newAccount = doCreateAccount(portfolioToBeUpdated, accountNumber);
                            doCreateAccountLines(accountLines, allQuotes, newAccount);

                            // Maintain referential integrity between existing portfolio and new account
                            portfolioToBeUpdated.addAccount(newAccount);
                        }

                        // If the account exists, overwrite the existing account lines with new portfolio data.
                        // This assumes a user uploads a complete portfolio snapshot. The quantity will be incorrect if
                        // the user uploads individual buys and sells. This approach is taken because brokerages
                        // (e.g., Schwab, Fidelity, etc.) export an entire portfolio and not transaction data.
                        if (optionalAccount.isPresent()) {
                            Account accountToBeUpdated = optionalAccount.get();
                            doDestroyExistingAccountLines(accountToBeUpdated);
                            doCreateAccountLines(accountLines, allQuotes, accountToBeUpdated);
                        }
                    });

                    return portfolioToBeUpdated;
                }).orElseThrow(PortfolioNotFoundException::new);
    }

    // Find the current account within the portfolio
    private Optional<Account> doAccountFilter(List<Account> accounts, String accountNumber) {
        return accounts
                .stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    // Instantiate a new account
    private Account doCreateAccount(Portfolio portfolio, String accountNumber) {
        return new Account(portfolio, accountNumber);
    }

    /**
     * Add new companies to the Company Table
     * 
     * @param companies List of companies to be added to the database
     */
    private void doCreateCompanies(List<Company> companies) {
        
        for (Company company : companies) {
            
            companyService.add(company);
        }
    }

    // Find all symbols and quantities within an account, add the quote from IEX Cloud and a new account line.
    private void doCreateAccountLines(Map<String, Integer> accountLines, List<Quote> allQuotes,
                                      Account accountToBeUpdated) {

        accountLines.forEach((symbol, quantity) -> {
            Quote quote = doQuoteFilter(allQuotes, symbol);
            accountToBeUpdated.getAccountLines()
                    .add(new AccountLine (
                            accountToBeUpdated,
                            quote,
                            quantity
                    ));
        });
    }

    // Match a Quote to its symbol. If matched, return an IEX Cloud Quote. Otherwise, return an empty Quote.
    private Quote doQuoteFilter(List<Quote> allQuotes, String symbol) {
        for (Quote quote : allQuotes) {
            if (quote.getSymbol().contains(symbol)) {
                return quote;
            }
        }

        // If control flow gets here, something went wrong with marketDataServiceImpl.doGetQuotes() in the Save method.
        return new Quote();
    }

    // Delete existing account lines.
    private void doDestroyExistingAccountLines(Account accountToBeUpdated) {
        accountLineRepository.deleteAllByAccount_Id(accountToBeUpdated.getId());
    }

    private Portfolio doCreatePortfolio(User user) {
        return new Portfolio(user);
    }

    // Add a Quote to an account only when the Quote's symbol exists within the specified account.
    private List<Account> doCreateAccounts(Map<String, Map<String, Integer>> portfolioData,
                                           Portfolio portfolio, List<Quote> allQuotes) {
        List<String> initializedAccountNumbers = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();

        // Update or create an account with Quotes from IEX Cloud
        portfolioData.forEach((accountNumber, accountLines) -> {
            if (initializedAccountNumbers.contains(accountNumber)) {
                Optional<Account> account = doAccountFilter(accounts, accountNumber);
                account.ifPresent(accountToBeUpdated -> doCreateAccountLines(
                        accountLines,
                        allQuotes,
                        accountToBeUpdated)
                );
            } else {
                Account newAccount = doCreateAccount(portfolio, accountNumber);
                doCreateAccountLines(accountLines, allQuotes, newAccount);
                initializedAccountNumbers.add(newAccount.getAccountNumber());
                accounts.add(newAccount);
            }
        });

        return accounts;
    }

    /**
     * Deletes the Portfolio associated with the given id by detaching it from the User.
     * @note The transaction is flushed after the method returns.
     * @param id The id of the portfolio being deleted.
     */
    public void deletePortfolioBy(Long id) {
        Optional<Portfolio> optionalPortfolio = portfolioRepository.findById(id);

        if (optionalPortfolio.isPresent()) {
            Portfolio currentPortfolio = optionalPortfolio.get();
            currentPortfolio.getUser().setPortfolio(null);
        }
    }

    /**
     * Queries the database for the Portfolio associated with the specified id.
     * @param id An id associated with the Portfolio that needs to be found.
     * @return The Portfolio represented by the specified id. The Entity Manager will insert this Portfolio into the
     * persistence context, so any changes will by synchronized to the database when the transaction is committed.
     * @throws PortfolioNotFoundException The Portfolio associated with the specified id cannot be found.
     */
    public Portfolio getPortfolioBy(Long id) {
        return portfolioRepository.findById(id).orElseThrow(PortfolioNotFoundException::new);
    }
}