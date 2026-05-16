package edu.nsbm.phishguard.repository;

import edu.nsbm.phishguard.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, String> {

    List<Reward> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Reward> findByEmailId(String emailId);

    boolean existsByUserIdAndEmailId(String userId, String emailId);

    @Query("SELECT COALESCE(SUM(r.points), 0) FROM Reward r WHERE r.userId = :userId")
    Integer getTotalPointsByUserId(@Param("userId") String userId);

    @Query("""
            SELECT r.userId AS userId,
                   COALESCE(SUM(r.points), 0) AS totalPoints,
                   SUM(CASE WHEN r.points > 0 THEN 1 ELSE 0 END) AS correctCount,
                   SUM(CASE WHEN r.points < 0 THEN 1 ELSE 0 END) AS wrongCount
            FROM Reward r
            WHERE r.createdAt >= :startDate
            GROUP BY r.userId
            ORDER BY SUM(r.points) DESC
            """)
    List<UserScoreProjection> aggregateScoresSince(@Param("startDate") LocalDateTime startDate);

    @Query("""
            SELECT r.userId AS userId,
                   COALESCE(SUM(r.points), 0) AS totalPoints,
                   SUM(CASE WHEN r.points > 0 THEN 1 ELSE 0 END) AS correctCount,
                   SUM(CASE WHEN r.points < 0 THEN 1 ELSE 0 END) AS wrongCount
            FROM Reward r
            GROUP BY r.userId
            ORDER BY SUM(r.points) DESC
            """)
    List<UserScoreProjection> aggregateAllTimeScores();

    interface UserScoreProjection {
        String getUserId();
        Integer getTotalPoints();
        Integer getCorrectCount();
        Integer getWrongCount();
    }
}