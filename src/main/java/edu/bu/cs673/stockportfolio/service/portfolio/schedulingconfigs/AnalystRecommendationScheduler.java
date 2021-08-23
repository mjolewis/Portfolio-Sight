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
     * every minute from 9:00 AM to 4:00 PM Eastern time every day-of-week from Monday through Friday.
     *
     * @Note The zone must be updated to either EDT or EST. EDT is represented by GMT−4 and EST is represented by
     *         GMT−5. EST runs from March to November. It starts during the first Sunday of November until the
     *         second Sunday of March. In the second Sunday of March, clocks switch into the Eastern Daylight Time
     */
    @Scheduled(cron = "0 0 9 * * FRI", zone = "GMT−4")
    public void startSchedule() {
        LOGGER.info().log("Get latest analyst recommendations from IEX Cloud");
        analystRecommendationService.getAnalystRecommendations();
    }
}