package edu.nsbm.phishguard.service.impl;

import edu.nsbm.phishguard.dto.*;
import edu.nsbm.phishguard.entity.Email;
import edu.nsbm.phishguard.enums.GenerateBy;
import edu.nsbm.phishguard.enums.Type;
import edu.nsbm.phishguard.repository.EmailRepository;
import edu.nsbm.phishguard.service.EmailService;
import edu.nsbm.phishguard.service.RewardService;
import edu.nsbm.phishguard.util.apputil.RandomEmailType;
import edu.nsbm.phishguard.util.client.LlmClient;
import edu.nsbm.phishguard.util.response.LlmResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailRepository emailRepository;
    private final ObjectMapper mapper;
    private final LlmClient llmClient;
    private final RewardService rewardService;

    @Override
    public ResponseEmailDto createEmail(CreateEmailDto email) {
        Email save = emailRepository.save(mapper.convertValue(email, Email.class));
        return mapper.convertValue(save,ResponseEmailDto.class);
    }

    @Override
    public Optional<ResponseEmailDto> getEmailById(String emailId) {
        return Optional.empty();
    }

    @Override
    public List<ResponseEmailDto> getAllEmails() {
        List<ResponseEmailDto> responseEmailDtoList = new ArrayList<>();
        emailRepository.findAll().forEach(email -> {
            responseEmailDtoList.add(mapper.convertValue(email,ResponseEmailDto.class));
        });
        return responseEmailDtoList;
    }

    @Override
    public ResponseEmailDto updateEmail(String emailId, Email email) {
        return null;
    }

    @Override
    public boolean deleteEmail(String emailId) {

        return false;
    }

    @Override
    public UserRequestedEmailResponse generateEmailByUser(JwtAuthenticationToken authentication) {

        Type randomEmailType = RandomEmailType.getRandomEmailType();

        LlmResponse llmResponse = llmClient.generateMail(randomEmailType);
        if (llmResponse != null) {
            Email email = getEmail(authentication, randomEmailType, llmResponse);
            emailRepository.save(email);
            UserRequestedEmailResponse userRequestedEmailResponse = mapper.convertValue(email, UserRequestedEmailResponse.class);
            userRequestedEmailResponse.setEmailType(null);
            return userRequestedEmailResponse;
        }
        return null;
    }

    @Override
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest answerRequest) {

        return rewardService.submitReward(answerRequest);
    }

    @Override
    public AdminCreatedEmailResponse createEmailByAdmin(AdminCreateEmailRequest emailRequest) {
        Email email = mapper.convertValue(emailRequest, Email.class);
        email.setGenerateBy(GenerateBy.ADMIN);
        email.setSubmitted(false);
        Email savedEmail = emailRepository.save(email);
        return mapper.convertValue(savedEmail, AdminCreatedEmailResponse.class);
    }

    private static @NonNull Email getEmail(JwtAuthenticationToken authentication, Type randomEmailType, LlmResponse llmResponse) {
        Email email = new Email();
        email.setGenerateBy(GenerateBy.AI);
        email.setEmailType(randomEmailType);
        email.setUserChoice(Type.NOT_SELECT);
        email.setUserId(authentication.getToken().getSubject());
        email.setSenderAddress(llmResponse.getSenderEmail());
        email.setEmailTitle(llmResponse.getTitle());
        email.setEmailBody(llmResponse.getBody());
        email.setLink(llmResponse.getLink());
        email.setSubmitted(false);
        return email;
    }
}
