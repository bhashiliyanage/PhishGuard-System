package edu.nsbm.phishguard.configaration;

import edu.nsbm.phishguard.util.client.LlmClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LlmClientConfig {

    @Value("${llm.provider}")
    private String provider;

    @Bean
    @Primary
    public LlmClient llmClient(
            @Qualifier("ollamaClient") LlmClient ollamaClient,
            @Qualifier("groqClient") LlmClient groqClient) {

        return switch (provider) {
            case "groq"   -> groqClient;
            case "ollama" -> ollamaClient;
            default -> throw new IllegalArgumentException("Unknown LLM provider: " + provider);
        };
    }
}
