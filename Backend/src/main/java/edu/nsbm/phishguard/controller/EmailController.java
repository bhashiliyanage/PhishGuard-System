package edu.nsbm.phishguard.controller;

import edu.nsbm.phishguard.dto.*;
import edu.nsbm.phishguard.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/email")
public class EmailController {
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<ResponseEmailDto>> all() {
        return ResponseEntity.ok(emailService.getAllEmails());
    }

    @PostMapping
    public ResponseEntity<ResponseEmailDto> create(@RequestBody CreateEmailDto emailDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailService.createEmail(emailDto));
    }

    //  this is for the admin to send an email for all users
    @PostMapping("send/admin")
    public ResponseEntity<AdminCreatedEmailResponse> createEmailByAdmin(@RequestBody AdminCreateEmailRequest email) {
        return ResponseEntity.ok(emailService.createEmailByAdmin(email));
    }

    //  this is for the user to request an email using AI
    @PostMapping("request/user")
    public ResponseEntity<UserRequestedEmailResponse> requestEmailByUser(JwtAuthenticationToken authentication) {
        return ResponseEntity.ok(emailService.generateEmailByUser(authentication));
    }

    @PutMapping("/answer")
    public ResponseEntity<SubmitAnswerResponse> answerEmail(@RequestBody SubmitAnswerRequest answerRequest, JwtAuthenticationToken authentication) {
        answerRequest.setUserId(authentication.getToken().getSubject());
        SubmitAnswerResponse submitAnswerResponse = emailService.submitAnswer(answerRequest);
        if (submitAnswerResponse.getIsAlreadySubmitted())
            return ResponseEntity.status(HttpStatus.LOCKED).body(submitAnswerResponse);
        return ResponseEntity.ok(submitAnswerResponse);
    }

}
