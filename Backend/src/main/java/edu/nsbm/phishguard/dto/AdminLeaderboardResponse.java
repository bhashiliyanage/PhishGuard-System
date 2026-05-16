package edu.nsbm.phishguard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminLeaderboardResponse {

    private Integer rank;
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer weeklyScore;
    private Integer monthlyScore;
    private Integer totalScore;
    private LocalDateTime createdAt;
}