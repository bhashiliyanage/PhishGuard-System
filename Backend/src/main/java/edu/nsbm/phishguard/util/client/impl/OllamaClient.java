package edu.nsbm.phishguard.util.client.impl;

import edu.nsbm.phishguard.enums.Type;
import edu.nsbm.phishguard.util.client.LlmClient;
import edu.nsbm.phishguard.util.constant.Prompt;
import edu.nsbm.phishguard.util.filter.OllamaResponseFilter;
import edu.nsbm.phishguard.util.request.OllamaRequest;
import edu.nsbm.phishguard.util.response.LlmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component("ollamaClient")
@RequiredArgsConstructor
public class OllamaClient implements LlmClient {

    @Value("${server.ollama.model}")
    private String model;

    private final WebClient ollamaWebClient;

    public LlmResponse generateMail(Type emailType) {
        if (emailType == Type.PHISHING) {
            OllamaRequest requestPhishing = OllamaRequest.builder()
                    .model(model)
                    .stream(false)
                    .format("json")
                    .think(false)
                    .messages(List.of(
                            new OllamaRequest.Message("system", Prompt.FAKE_MAIL_SYSTEM),
                            new OllamaRequest.Message("user", Prompt.FAKE_MAIL_USER)
                    ))
                    .options(OllamaRequest.Options.builder()
                            .temperature(0.9)
                            .numPredict(1024)
                            .build())
                    .build();
            return OllamaResponseFilter.parseOllamaResponse(ollamaWebClient.post().uri("/api/chat").bodyValue(requestPhishing).retrieve().bodyToMono(String.class).block());

        } else if (emailType == Type.NORMAL) {
            OllamaRequest requestNormal = OllamaRequest.builder()
                    .model(model)
                    .stream(false)
                    .format("json")
                    .think(false)
                    .messages(List.of(
                            new OllamaRequest.Message("system", Prompt.NORMAL_MAIL_SYSTEM),
                            new OllamaRequest.Message("user", Prompt.NORMAL_MAIL_USER)
                    ))
                    .options(OllamaRequest.Options.builder()
                            .temperature(0.9)
                            .numPredict(1024)
                            .build())
                    .build();
            return OllamaResponseFilter.parseOllamaResponse(ollamaWebClient.post().uri("/api/chat").bodyValue(requestNormal).retrieve().bodyToMono(String.class).block());
        } else return null;
    }
}