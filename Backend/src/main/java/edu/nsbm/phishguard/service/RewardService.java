package edu.nsbm.phishguard.service;

import edu.nsbm.phishguard.dto.CreateRewardRequest;
import edu.nsbm.phishguard.dto.SubmitAnswerRequest;
import edu.nsbm.phishguard.dto.SubmitAnswerResponse;
import edu.nsbm.phishguard.entity.Reward;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RewardService {
    ResponseEntity<Reward> createReward(CreateRewardRequest request);
    ResponseEntity<Reward> getRewardById(String id);
    ResponseEntity<List<Reward>> getRewardsByUserId(String userId);
    ResponseEntity<List<Reward>> getRewardsByEmailId(String emailId);
    ResponseEntity<List<Reward>> getAllRewards();
    ResponseEntity<Integer> getTotalPointsByUserId(String userId);
    ResponseEntity<Void> deleteReward(String id);
    SubmitAnswerResponse submitReward(SubmitAnswerRequest answerRequest);
}
