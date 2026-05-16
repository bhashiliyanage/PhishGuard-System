package edu.nsbm.phishguard.service.impl;

import edu.nsbm.phishguard.dto.AdminLeaderboardResponse;
import edu.nsbm.phishguard.dto.MyLeaderboardStatsResponse;
import edu.nsbm.phishguard.dto.UserLeaderboardResponse;
import edu.nsbm.phishguard.entity.AppUser;
import edu.nsbm.phishguard.entity.Leaderboard;
import edu.nsbm.phishguard.enums.LeaderboardPeriod;
import edu.nsbm.phishguard.repository.AppUserRepository;
import edu.nsbm.phishguard.repository.LeaderboardRepository;
import edu.nsbm.phishguard.repository.RewardRepository;
import edu.nsbm.phishguard.repository.RewardRepository.UserScoreProjection;
import edu.nsbm.phishguard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final AppUserRepository appUserRepository;
    private final RewardRepository rewardRepository;
    private final LeaderboardRepository leaderboardRepository;


    @Override
    @Transactional
    public void recalculateAllUserScores() {
        try {
            log.info("=== Starting score recalculation for all users ===");

            // 1. Aggregate scores from rewards table for each period
            List<UserScoreProjection> weeklyScores = rewardRepository
                    .aggregateScoresSince(LocalDateTime.now().minusDays(7));
            List<UserScoreProjection> monthlyScores = rewardRepository
                    .aggregateScoresSince(LocalDateTime.now().minusDays(30));
            List<UserScoreProjection> allTimeScores = rewardRepository
                    .aggregateAllTimeScores();

            // 2. Update users table (quick-access columns: weeklyScore, monthlyScore, totalScore)
            Map<String, Integer> weeklyMap = toMap(weeklyScores);
            Map<String, Integer> monthlyMap = toMap(monthlyScores);
            Map<String, Integer> totalMap = toMap(allTimeScores);

            List<AppUser> users = appUserRepository.findAll();
            for (AppUser user : users) {
                String userId = user.getId();
                user.setWeeklyScore(weeklyMap.getOrDefault(userId, 0));
                user.setMonthlyScore(monthlyMap.getOrDefault(userId, 0));
                user.setTotalScore(totalMap.getOrDefault(userId, 0));
            }
            appUserRepository.saveAll(users);
            log.info("Users table updated for {} users", users.size());

            // 3. Update leaderboard table (snapshots with rank, correct/wrong counts)
            saveSnapshot(LeaderboardPeriod.LAST_7_DAYS, weeklyScores);
            saveSnapshot(LeaderboardPeriod.LAST_30_DAYS, monthlyScores);
            saveSnapshot(LeaderboardPeriod.ALL_TIME, allTimeScores);

            log.info("=== Score recalculation complete ===");

        } catch (Exception e) {
            log.error("Error recalculating user scores: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to recalculate user scores", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserLeaderboardResponse>> getWeeklyLeaderboardForUser() {
        try {
            log.debug("Fetching weekly leaderboard for user view");
            List<AppUser> users = appUserRepository.findAllByOrderByWeeklyScoreDesc();
            return ResponseEntity.ok(buildUserLeaderboard(users));
        } catch (Exception e) {
            log.error("Error fetching weekly leaderboard (user): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserLeaderboardResponse>> getMonthlyLeaderboardForUser() {
        try {
            log.debug("Fetching monthly leaderboard for user view");
            List<AppUser> users = appUserRepository.findAllByOrderByMonthlyScoreDesc();
            return ResponseEntity.ok(buildUserLeaderboard(users));
        } catch (Exception e) {
            log.error("Error fetching monthly leaderboard (user): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserLeaderboardResponse>> getAllTimeLeaderboardForUser() {
        try {
            log.debug("Fetching all-time leaderboard for user view");
            List<AppUser> users = appUserRepository.findAllByOrderByTotalScoreDesc();
            return ResponseEntity.ok(buildUserLeaderboard(users));
        } catch (Exception e) {
            log.error("Error fetching all-time leaderboard (user): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminLeaderboardResponse>> getWeeklyLeaderboardForAdmin() {
        try {
            log.debug("Fetching weekly leaderboard for admin view");
            List<AppUser> users = appUserRepository.findAllByOrderByWeeklyScoreDesc();
            return ResponseEntity.ok(buildAdminLeaderboard(users));
        } catch (Exception e) {
            log.error("Error fetching weekly leaderboard (admin): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminLeaderboardResponse>> getMonthlyLeaderboardForAdmin() {
        try {
            log.debug("Fetching monthly leaderboard for admin view");
            List<AppUser> users = appUserRepository.findAllByOrderByMonthlyScoreDesc();
            return ResponseEntity.ok(buildAdminLeaderboard(users));
        } catch (Exception e) {
            log.error("Error fetching monthly leaderboard (admin): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdminLeaderboardResponse>> getAllTimeLeaderboardForAdmin() {
        try {
            log.debug("Fetching all-time leaderboard for admin view");
            List<AppUser> users = appUserRepository.findAllByOrderByTotalScoreDesc();
            return ResponseEntity.ok(buildAdminLeaderboard(users));
        } catch (Exception e) {
            log.error("Error fetching all-time leaderboard (admin): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<MyLeaderboardStatsResponse> getMyStats(String userId) {
        try {
            log.debug("Fetching my leaderboard stats for userId: {}", userId);

            if (userId == null || userId.isBlank()) {
                log.warn("getMyStats failed - userId is null or empty");
                return ResponseEntity.badRequest().build();
            }

            // Get user details
            AppUser user = appUserRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("User not found: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Fetch this user's entry for each period (may be null if they have no rewards yet)
            Leaderboard weekly = leaderboardRepository
                    .findByUserIdAndPeriod(userId, LeaderboardPeriod.LAST_7_DAYS).orElse(null);
            Leaderboard monthly = leaderboardRepository
                    .findByUserIdAndPeriod(userId, LeaderboardPeriod.LAST_30_DAYS).orElse(null);
            Leaderboard allTime = leaderboardRepository
                    .findByUserIdAndPeriod(userId, LeaderboardPeriod.ALL_TIME).orElse(null);

            // Build combined response
            MyLeaderboardStatsResponse response = MyLeaderboardStatsResponse.builder()

                    .fullName(buildFullName(user))

                    .weeklyRank(weekly != null ? weekly.getRank() : null)
                    .weeklyPoints(weekly != null ? weekly.getTotalPoints() : 0)
                    .weeklyCorrect(weekly != null ? weekly.getCorrectCount() : 0)
                    .weeklyWrong(weekly != null ? weekly.getWrongCount() : 0)

                    .monthlyRank(monthly != null ? monthly.getRank() : null)
                    .monthlyPoints(monthly != null ? monthly.getTotalPoints() : 0)
                    .monthlyCorrect(monthly != null ? monthly.getCorrectCount() : 0)
                    .monthlyWrong(monthly != null ? monthly.getWrongCount() : 0)

                    .allTimeRank(allTime != null ? allTime.getRank() : null)
                    .allTimePoints(allTime != null ? allTime.getTotalPoints() : 0)
                    .allTimeCorrect(allTime != null ? allTime.getCorrectCount() : 0)
                    .allTimeWrong(allTime != null ? allTime.getWrongCount() : 0)

                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching my stats for userId: {}, error: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private List<UserLeaderboardResponse> buildUserLeaderboard(List<AppUser> users) {
        List<UserLeaderboardResponse> entries = new ArrayList<>();
        int rank = 1;
        for (AppUser user : users) {
            entries.add(UserLeaderboardResponse.builder()
                    .rank(rank++)
                    .fullName(buildFullName(user))
                    .weeklyScore(user.getWeeklyScore())
                    .monthlyScore(user.getMonthlyScore())
                    .totalScore(user.getTotalScore())
                    .build());
        }
        return entries;
    }

    private List<AdminLeaderboardResponse> buildAdminLeaderboard(List<AppUser> users) {
        List<AdminLeaderboardResponse> entries = new ArrayList<>();
        int rank = 1;
        for (AppUser user : users) {
            entries.add(AdminLeaderboardResponse.builder()
                    .rank(rank++)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .fullName(buildFullName(user))
                    .weeklyScore(user.getWeeklyScore())
                    .monthlyScore(user.getMonthlyScore())
                    .totalScore(user.getTotalScore())
                    .createdAt(user.getCreatedAt())
                    .build());
        }
        return entries;
    }

    private String buildFullName(AppUser user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        return (first + " " + last).trim();
    }

    private Map<String, Integer> toMap(List<UserScoreProjection> projections) {
        Map<String, Integer> map = new HashMap<>();
        for (UserScoreProjection p : projections) {
            map.put(p.getUserId(), p.getTotalPoints() != null ? p.getTotalPoints() : 0);
        }
        return map;
    }

    private void saveSnapshot(LeaderboardPeriod period, List<UserScoreProjection> scores) {
        log.info("Saving leaderboard snapshot for period: {} ({} users)", period, scores.size());

        // Delete old entries for this period
        leaderboardRepository.deleteByPeriod(period);
        leaderboardRepository.flush();  // force delete before insert

        // Build new entries with ranks assigned in order (scores are already sorted DESC from repo)
        List<Leaderboard> entries = new ArrayList<>();
        int rank = 1;
        for (UserScoreProjection score : scores) {
            Leaderboard entry = Leaderboard.builder()
                    .userId(score.getUserId())
                    .period(period)
                    .totalPoints(score.getTotalPoints() != null ? score.getTotalPoints() : 0)
                    .correctCount(score.getCorrectCount() != null ? score.getCorrectCount() : 0)
                    .wrongCount(score.getWrongCount() != null ? score.getWrongCount() : 0)
                    .rank(rank++)
                    .build();
            entries.add(entry);
        }

        leaderboardRepository.saveAll(entries);
        log.info("Snapshot saved - period: {}, entries: {}", period, entries.size());
    }
}