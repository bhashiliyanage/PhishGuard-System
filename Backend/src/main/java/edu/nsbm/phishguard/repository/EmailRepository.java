package edu.nsbm.phishguard.repository;

import edu.nsbm.phishguard.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email,String> {
}
