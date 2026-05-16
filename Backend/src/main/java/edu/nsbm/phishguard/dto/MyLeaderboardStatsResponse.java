package edu.nsbm.phishguard.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyLeaderboardStatsResponse {

    private String fullName;

    // Last 7 days
    private Integer weeklyRank;
    private Integer weeklyPoints;
    private Integer weeklyCorrect;
    private Integer weeklyWrong;

    // Last 30 days
    private Integer monthlyRank;
    private Integer monthlyPoints;
    private Integer monthlyCorrect;
    private Integer monthlyWrong;

    // All time
    private Integer allTimeRank;
    private Integer allTimePoints;
    private Integer allTimeCorrect;
    private Integer allTimeWrong;
}