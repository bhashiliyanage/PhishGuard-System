package edu.nsbm.phishguard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLeaderboardResponse {

    private Integer rank;
    private String fullName;
    private Integer weeklyScore;
    private Integer monthlyScore;
    private Integer totalScore;
}