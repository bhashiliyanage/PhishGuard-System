package edu.nsbm.phishguard.entity;

import edu.nsbm.phishguard.enums.LeaderboardPeriod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leaderboard",
        indexes = {
                @Index(name = "idx_leaderboard_period_rank", columnList = "period, rank"),
                @Index(name = "idx_leaderboard_user_period", columnList = "user_id, period")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaderboardPeriod period;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "wrong_count", nullable = false)
    private Integer wrongCount;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @CreationTimestamp
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt;
}