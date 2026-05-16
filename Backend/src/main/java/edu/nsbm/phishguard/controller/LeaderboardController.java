package edu.nsbm.phishguard.controller;

import edu.nsbm.phishguard.dto.AdminLeaderboardResponse;
import edu.nsbm.phishguard.dto.MyLeaderboardStatsResponse;
import edu.nsbm.phishguard.dto.UserLeaderboardResponse;
import edu.nsbm.phishguard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/weekly")
    public ResponseEntity<List<UserLeaderboardResponse>> getWeeklyLeaderboard() {
        log.info("GET /api/v1/leaderboard/weekly");
        return leaderboardService.getWeeklyLeaderboardForUser();
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<UserLeaderboardResponse>> getMonthlyLeaderboard() {
        log.info("GET /api/v1/leaderboard/monthly");
        return leaderboardService.getMonthlyLeaderboardForUser();
    }

    @GetMapping("/all-time")
    public ResponseEntity<List<UserLeaderboardResponse>> getAllTimeLeaderboard() {
        log.info("GET /api/v1/leaderboard/all-time");
        return leaderboardService.getAllTimeLeaderboardForUser();
    }

    @GetMapping("/admin/weekly")
    public ResponseEntity<List<AdminLeaderboardResponse>> getAdminWeeklyLeaderboard() {
        log.info("GET /api/v1/leaderboard/admin/weekly");
        return leaderboardService.getWeeklyLeaderboardForAdmin();
    }

    @GetMapping("/admin/monthly")
    public ResponseEntity<List<AdminLeaderboardResponse>> getAdminMonthlyLeaderboard() {
        log.info("GET /api/v1/leaderboard/admin/monthly");
        return leaderboardService.getMonthlyLeaderboardForAdmin();
    }

    @GetMapping("/admin/all-time")
    public ResponseEntity<List<AdminLeaderboardResponse>> getAdminAllTimeLeaderboard() {
        log.info("GET /api/v1/leaderboard/admin/all-time");
        return leaderboardService.getAllTimeLeaderboardForAdmin();
    }

    @PostMapping("/admin/recalculate")
    public ResponseEntity<String> recalculate() {
        log.info("POST /api/v1/leaderboard/admin/recalculate - manual trigger");
        try {
            leaderboardService.recalculateAllUserScores();
            return ResponseEntity.ok("User scores recalculated successfully");
        } catch (Exception e) {
            log.error("Manual recalculation failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Recalculation failed: " + e.getMessage());
        }
    }
    @GetMapping("/my-status")
    public ResponseEntity<MyLeaderboardStatsResponse> getMyStats(JwtAuthenticationToken token) {
        String userId = token.getToken().getSubject();
        log.info("GET /api/v1/leaderboard/my-stats/{}", userId);
        return leaderboardService.getMyStats(userId);
    }
}