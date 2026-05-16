package edu.nsbm.phishguard.service.impl;

import edu.nsbm.phishguard.dto.CreateRewardRequest;
import edu.nsbm.phishguard.dto.SubmitAnswerRequest;
import edu.nsbm.phishguard.dto.SubmitAnswerResponse;
import edu.nsbm.phishguard.entity.Email;
import edu.nsbm.phishguard.entity.Reward;
import edu.nsbm.phishguard.enums.GenerateBy;
import edu.nsbm.phishguard.enums.Type;
import edu.nsbm.phishguard.repository.EmailRepository;
import edu.nsbm.phishguard.repository.RewardRepository;
import edu.nsbm.phishguard.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final EmailRepository phishingEmailRepository;

    private static final int CORRECT_PHISHING_POINTS = 10;
    private static final int CORRECT_LEGIT_POINTS = 5;
    private static final int WRONG_PHISHING_PENALTY = -10;
    private static final int WRONG_LEGIT_PENALTY = -5;

    @Override
    @Transactional
    public ResponseEntity<Reward> createReward(CreateRewardRequest request) {
        try {
            log.info("Creating reward - userId: {}, emailId: {}, userChoice: {}",
                    request.getUserId(), request.getEmailId(), request.getType());

            // Validate request fields
            if (request.getUserId() == null || request.getUserId().isBlank()) {
                log.warn("Reward creation failed - userId is null or empty");
                return ResponseEntity.badRequest().build();
            }
            if (request.getEmailId() == null || request.getEmailId().isBlank()) {
                log.warn("Reward creation failed - emailId is null or empty");
                return ResponseEntity.badRequest().build();
            }
            if (request.getType() == null || request.getType() == Type.NOT_SELECT) {
                log.warn("Reward creation failed - type is null or NOT_SELECT, userId: {}", request.getUserId());
                return ResponseEntity.badRequest().build();
            }

            // Check if user already reviewed this email
            boolean alreadyReviewed = rewardRepository.existsByUserIdAndEmailId(
                    request.getUserId(), request.getEmailId());
            if (alreadyReviewed) {
                log.warn("Duplicate review attempt - userId: {}, emailId: {}",
                        request.getUserId(), request.getEmailId());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Fetch email
            Email email = phishingEmailRepository.findById(request.getEmailId())
                    .orElse(null);
            if (email == null) {
                log.error("Email not found - emailId: {}", request.getEmailId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Calculate points
            int points = calculatePoints(email.getEmailType(), request.getType());
            boolean isCorrect = points > 0;

            log.info("Points calculated - userId: {}, emailType: {}, userChoice: {}, correct: {}, points: {}",
                    request.getUserId(), email.getEmailType(), request.getType(), isCorrect, points);

            // Create and save reward
            Reward reward = new Reward();
            reward.setUserId(request.getUserId());
            reward.setEmailId(request.getEmailId());
            reward.setPoints(points);

            Reward savedReward = rewardRepository.save(reward);

            log.info("Reward created successfully - rewardId: {}, userId: {}, points: {}",
                    savedReward.getId(), savedReward.getUserId(), savedReward.getPoints());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedReward);

        } catch (Exception e) {
            log.error("Unexpected error creating reward - userId: {}, emailId: {}, error: {}",
                    request.getUserId(), request.getEmailId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Reward> getRewardById(String id) {
        try {
            log.debug("Fetching reward by id: {}", id);

            if (id == null || id.isBlank()) {
                log.warn("getRewardById failed - id is null or empty");
                return ResponseEntity.badRequest().build();
            }

            Reward reward = rewardRepository.findById(id).orElse(null);
            if (reward == null) {
                log.warn("Reward not found - id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(reward);

        } catch (Exception e) {
            log.error("Error fetching reward by id: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<Reward>> getRewardsByUserId(String userId) {
        try {
            log.debug("Fetching rewards for userId: {}", userId);

            if (userId == null || userId.isBlank()) {
                log.warn("getRewardsByUserId failed - userId is null or empty");
                return ResponseEntity.badRequest().build();
            }

            List<Reward> rewards = rewardRepository.findByUserIdOrderByCreatedAtDesc(userId);
            log.debug("Found {} rewards for userId: {}", rewards.size(), userId);

            return ResponseEntity.ok(rewards);

        } catch (Exception e) {
            log.error("Error fetching rewards for userId: {}, error: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<Reward>> getRewardsByEmailId(String emailId) {
        try {
            log.debug("Fetching rewards for emailId: {}", emailId);

            if (emailId == null || emailId.isBlank()) {
                log.warn("getRewardsByEmailId failed - emailId is null or empty");
                return ResponseEntity.badRequest().build();
            }

            List<Reward> rewards = rewardRepository.findByEmailId(emailId);
            log.debug("Found {} rewards for emailId: {}", rewards.size(), emailId);

            return ResponseEntity.ok(rewards);

        } catch (Exception e) {
            log.error("Error fetching rewards for emailId: {}, error: {}", emailId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<Reward>> getAllRewards() {
        try {
            log.debug("Fetching all rewards");

            List<Reward> rewards = rewardRepository.findAll();
            log.debug("Total rewards found: {}", rewards.size());

            return ResponseEntity.ok(rewards);

        } catch (Exception e) {
            log.error("Error fetching all rewards, error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Integer> getTotalPointsByUserId(String userId) {
        try {
            log.debug("Fetching total points for userId: {}", userId);

            if (userId == null || userId.isBlank()) {
                log.warn("getTotalPointsByUserId failed - userId is null or empty");
                return ResponseEntity.badRequest().build();
            }

            Integer total = rewardRepository.getTotalPointsByUserId(userId);
            int result = total != null ? total : 0;

            log.debug("Total points for userId {}: {}", userId, result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error fetching total points for userId: {}, error: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteReward(String id) {
        try {
            log.info("Deleting reward - id: {}", id);

            if (id == null || id.isBlank()) {
                log.warn("deleteReward failed - id is null or empty");
                return ResponseEntity.badRequest().build();
            }

            Reward reward = rewardRepository.findById(id).orElse(null);
            if (reward == null) {
                log.warn("Cannot delete - reward not found: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            rewardRepository.delete(reward);

            log.info("Reward deleted successfully - id: {}, userId: {}, points: {}",
                    reward.getId(), reward.getUserId(), reward.getPoints());

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Error deleting reward id: {}, error: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private int calculatePoints(Type emailType, Type userChoice) {
        if (emailType == Type.PHISHING && userChoice == Type.PHISHING) {
            return CORRECT_PHISHING_POINTS;
        } else if (emailType == Type.NORMAL && userChoice == Type.NORMAL) {
            return CORRECT_LEGIT_POINTS;
        } else if (emailType == Type.PHISHING && userChoice == Type.NORMAL) {
            return WRONG_PHISHING_PENALTY;
        } else {
            return WRONG_LEGIT_PENALTY;
        }
    }

    public SubmitAnswerResponse submitReward(SubmitAnswerRequest answerRequest) {
        Email email = phishingEmailRepository.findById(answerRequest.getEmailId()).orElse(null);

        if (email == null) {
            return SubmitAnswerResponse.builder()
                    .emailId(answerRequest.getEmailId())
                    .response("Email not found or deleted")
                    .isAlreadySubmitted(false)
                    .isCorrect(false)
                    .build();
        }

        if (rewardRepository.existsByUserIdAndEmailId(answerRequest.getUserId(), answerRequest.getEmailId())) {
            return SubmitAnswerResponse.builder()
                    .emailId(answerRequest.getEmailId())
                    .response("Email already submitted")
                    .isAlreadySubmitted(true)
                    .isCorrect(false)
                    .build();
        }

        boolean isCorrect = email.getEmailType() == answerRequest.getUserChoice();
        int points;

        if (email.getGenerateBy() == GenerateBy.AI && email.getUserId().equals(answerRequest.getUserId())) {
            points = isCorrect ? 1 : -1;
        } else if (email.getGenerateBy() == GenerateBy.ADMIN) {
            points = isCorrect ? 10 : -10;
        } else {
            return SubmitAnswerResponse.builder()
                    .emailId(answerRequest.getEmailId())
                    .response("Not eligible to submit this email")
                    .isAlreadySubmitted(false)
                    .isCorrect(false)
                    .build();
        }

        Reward reward = new Reward();
        reward.setUserId(answerRequest.getUserId());
        reward.setEmailId(answerRequest.getEmailId());
        reward.setPoints(points);
        rewardRepository.save(reward);

        email.setSubmitted(true);
        email.setSubmittedAt(LocalDateTime.now());
        email.setUserChoice(answerRequest.getUserChoice());
        phishingEmailRepository.save(email);

        return SubmitAnswerResponse.builder()
                .emailId(email.getEmailId())
                .response("Answer Submitted")
                .userChoice(answerRequest.getUserChoice())
                .userId(reward.getUserId())
                .isAlreadySubmitted(false)
                .isCorrect(isCorrect)
                .build();
    }
}