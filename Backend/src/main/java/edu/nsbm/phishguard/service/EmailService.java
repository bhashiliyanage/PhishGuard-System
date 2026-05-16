package edu.nsbm.phishguard.service;

import edu.nsbm.phishguard.dto.*;
import edu.nsbm.phishguard.entity.Email;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

public interface EmailService {

    ResponseEmailDto createEmail(CreateEmailDto email);

    Optional<ResponseEmailDto> getEmailById(String emailId);

    List<ResponseEmailDto> getAllEmails();

    ResponseEmailDto updateEmail(String emailId, Email email);

    boolean deleteEmail(String emailId);

    UserRequestedEmailResponse generateEmailByUser(JwtAuthenticationToken authentication);


    SubmitAnswerResponse submitAnswer(SubmitAnswerRequest answerRequest);

    AdminCreatedEmailResponse createEmailByAdmin(AdminCreateEmailRequest email);
}
