package edu.nsbm.phishguard.controller;

import edu.nsbm.phishguard.dto.CreateRewardRequest;
import edu.nsbm.phishguard.entity.Reward;
import edu.nsbm.phishguard.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @PostMapping
    public ResponseEntity<Reward> createReward(@RequestBody CreateRewardRequest request,
                                               JwtAuthenticationToken token) {
        request.setUserId(token.getToken().getSubject());
        log.info("POST /api/v1/rewards - userId: {}, emailId: {}",
                request.getUserId(), request.getEmailId());
        return rewardService.createReward(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reward> getRewardById(@PathVariable String id) {
         return rewardService.getRewardById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reward>> getRewardsByUserId(@PathVariable String userId) {
         return rewardService.getRewardsByUserId(userId);
    }

    @GetMapping("/email/{emailId}")
    public ResponseEntity<List<Reward>> getRewardsByEmailId(@PathVariable String emailId) {
         return rewardService.getRewardsByEmailId(emailId);
    }

    @GetMapping
    public ResponseEntity<List<Reward>> getAllRewards() {
         return rewardService.getAllRewards();
    }

    @GetMapping("/user/{userId}/points")
    public ResponseEntity<Integer> getTotalPointsByUserId(@PathVariable String userId) {
         return rewardService.getTotalPointsByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable String id) {
         return rewardService.deleteReward(id);
    }
}