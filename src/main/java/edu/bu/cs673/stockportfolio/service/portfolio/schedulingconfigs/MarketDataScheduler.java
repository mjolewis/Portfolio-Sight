package edu.bu.cs673.stockportfolio.service.portfolio.schedulingconfigs;

import edu.bu.cs673.stockportfolio.service.portfolio.MarketDataServiceImpl;
import edu.bu.cs673.stockportfolio.service.portfolio.QuoteService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MarketDataScheduler {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(MarketDataScheduler.class);
    private final MarketDataServiceImpl marketDataServiceImpl;
    private final QuoteService quoteService;

    public MarketDataScheduler(MarketDataServiceImpl marketDataServiceImpl,
                               QuoteService quoteService) {
        this.marketDataServiceImpl = marketDataServiceImpl;
        this.quoteService = quoteService;
    }

    /**
     * Configures the schedule for getting updated market quotes from IEX Cloud. The task uses cron scheduling to run
     * every minute from 9:00 AM to 4:00 PM Eastern time every day-of-week from Monday through Friday.
     *
     * @Note The zone must be updated to either EDT or EST. EDT is represented by GMT−4 and EST is represented by
     *         GMT−5. EST runs from March to November. It starts during the first Sunday of November until the
     *         second Sunday of March. In the second Sunday of March, clocks switch into the Eastern Daylight Time
     */
    @Scheduled(cron = "0 */1 9-16 * * MON-FRI", zone = "GMT−4")
    public void startSchedule() {
        boolean isMarketOpen = isUSMarketOpen();
        if (isMarketOpen) {
            LOGGER.info().log("US stock market is open. Get latest prices from IEX Cloud");
            quoteService.getLatestPrices();
        } else {
            LOGGER.info().log("US stock market is closed. Potential holiday - Closed during normal trading hours");
        }
    }

    public boolean isUSMarketOpen() {
        return marketDataServiceImpl.isUSMarketOpen();
    }
}
