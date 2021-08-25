package edu.bu.cs673.stockportfolio.service.portfolio.schedulingconfigs;

import edu.bu.cs673.stockportfolio.service.portfolio.AnalystRecommendationService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AnalystRecommendationScheduler {
    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(LatestPriceScheduler.class);
    private final AnalystRecommendationService analystRecommendationService;

    public AnalystRecommendationScheduler(AnalystRecommendationService analystRecommendationService) {
        this.analystRecommendationService = analystRecommendationService;
    }

    /**
     * Configures the schedule for getting updated market quotes from IEX Cloud. The task uses cron scheduling to run
     * every Friday at 9:00 AM Eastern time.
     */
    @Scheduled(cron = "0 0 9 * * FRI", zone = "America/New_York")
    public void startSchedule() {
        LOGGER.info().log("Get latest analyst recommendations from IEX Cloud");
        analystRecommendationService.getAnalystRecommendations();
    }
}