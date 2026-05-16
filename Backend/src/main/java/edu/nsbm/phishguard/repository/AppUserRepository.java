package edu.nsbm.phishguard.repository;

import edu.nsbm.phishguard.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findAllByOrderByWeeklyScoreDesc();
    List<AppUser> findAllByOrderByMonthlyScoreDesc();
    List<AppUser> findAllByOrderByTotalScoreDesc();
}
