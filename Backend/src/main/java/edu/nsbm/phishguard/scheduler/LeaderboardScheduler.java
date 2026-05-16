package edu.nsbm.phishguard.scheduler;

import edu.nsbm.phishguard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderboardScheduler {

    private final LeaderboardService leaderboardService;

    @Scheduled(cron = "0 0 0 * * *")
    public void recalculateUserScoresDaily() {
        log.info("=== Daily score recalculation triggered ===");
        try {
            leaderboardService.recalculateAllUserScores();
            log.info("=== Daily score recalculation completed ===");
        } catch (Exception e) {
            log.error("=== Daily score recalculation FAILED: {} ===", e.getMessage(), e);
        }
    }
}