package edu.nsbm.phishguard.service;

import edu.nsbm.phishguard.dto.AdminLeaderboardResponse;
import edu.nsbm.phishguard.dto.MyLeaderboardStatsResponse;
import edu.nsbm.phishguard.dto.UserLeaderboardResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LeaderboardService {

    /**
     * Called by scheduler — recalculates all score fields on users table.
     */
    void recalculateAllUserScores();

    // ============================
    // USER endpoints (limited data)
    // ============================

    ResponseEntity<List<UserLeaderboardResponse>> getWeeklyLeaderboardForUser();

    ResponseEntity<List<UserLeaderboardResponse>> getMonthlyLeaderboardForUser();

    ResponseEntity<List<UserLeaderboardResponse>> getAllTimeLeaderboardForUser();

    // ============================
    // ADMIN endpoints (full data)
    // ============================

    ResponseEntity<List<AdminLeaderboardResponse>> getWeeklyLeaderboardForAdmin();

    ResponseEntity<List<AdminLeaderboardResponse>> getMonthlyLeaderboardForAdmin();

    ResponseEntity<List<AdminLeaderboardResponse>> getAllTimeLeaderboardForAdmin();
    ResponseEntity<MyLeaderboardStatsResponse> getMyStats(String userId);

}