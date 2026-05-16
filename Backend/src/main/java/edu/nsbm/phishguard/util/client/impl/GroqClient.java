package edu.nsbm.phishguard.util.client.impl;

import edu.nsbm.phishguard.enums.Type;
import edu.nsbm.phishguard.util.client.LlmClient;
import edu.nsbm.phishguard.util.constant.Prompt;
import edu.nsbm.phishguard.util.filter.GroqResponseFilter;
import edu.nsbm.phishguard.util.request.GroqRequest;
import edu.nsbm.phishguard.util.response.LlmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component("groqClient")
@RequiredArgsConstructor
public class GroqClient implements LlmClient {

    @Value("${server.groq.model}")
    private String model;

    private final WebClient groqWebClient;

    @Override
    public LlmResponse generateMail(Type emailType) {
        return switch (emailType) {
            case PHISHING -> callGroq(Prompt.FAKE_MAIL_SYSTEM, Prompt.FAKE_MAIL_USER);
            case NORMAL   -> callGroq(Prompt.NORMAL_MAIL_SYSTEM, Prompt.NORMAL_MAIL_USER);
            default       -> null;
        };
    }

    private LlmResponse callGroq(String systemPrompt, String userPrompt) {
        log.info("Calling Groq with model: {}", model);

        GroqRequest request = GroqRequest.builder()
                .model(model)
                .messages(List.of(
                        new GroqRequest.Message("system", systemPrompt),
                        new GroqRequest.Message("user", userPrompt)
                ))
                .temperature(0.9)
                .maxTokens(1024)
                .responseFormat(new GroqRequest.ResponseFormat("json_object"))
                .build();

        String raw = groqWebClient.post()
                .uri("/openai/v1/chat/completions")  // ← fixed
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Groq raw response: {}", raw);
        return GroqResponseFilter.parseGroqResponse(raw);  // ← use correct filter
    }
}