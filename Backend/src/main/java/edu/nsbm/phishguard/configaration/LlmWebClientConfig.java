package edu.nsbm.phishguard.configaration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LlmWebClientConfig {

    @Value("${server.ollama.url}")
    private String ollamaUrl;

    @Value("${server.groq.url}")
    private String groqUrl;

    @Value("${server.groq.api-key}")
    private String apiKey;

    @Bean
    public WebClient ollamaWebClient() {
        return WebClient.builder()
                .baseUrl(ollamaUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient groqWebClient(){
        return WebClient.builder()
                .baseUrl(groqUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
