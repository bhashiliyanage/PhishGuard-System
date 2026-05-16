package edu.nsbm.phishguard.repository;

import edu.nsbm.phishguard.entity.Leaderboard;
import edu.nsbm.phishguard.enums.LeaderboardPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, String> {
    Optional<Leaderboard> findByUserIdAndPeriod(String userId, LeaderboardPeriod period);
    @Modifying
    @Query("DELETE FROM Leaderboard l WHERE l.period = :period")
    void deleteByPeriod(@Param("period") LeaderboardPeriod period);

}