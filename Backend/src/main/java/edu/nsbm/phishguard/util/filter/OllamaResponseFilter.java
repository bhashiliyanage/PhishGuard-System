package edu.nsbm.phishguard.util.filter;

import edu.nsbm.phishguard.util.response.LlmResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


public class OllamaResponseFilter {

    public static LlmResponse parseOllamaResponse(String raw) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Strip markdown code blocks if present
            String content = raw
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // Parse the full ollama response
            JsonNode root = objectMapper.readTree(content);

            // Extract the content field inside message
            String innerJson = root.path("message").path("content").asText();

            // Strip markdown from inner content too (just in case)
            innerJson = innerJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // Parse inner JSON
            JsonNode email = objectMapper.readTree(innerJson);

            return new LlmResponse(
                    email.path("sender_email").asText(),
                    email.path("title").asText(),
                    email.path("body").asText(),
                    email.path("link").asText()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Ollama response: " + e.getMessage());
        }
    }
}
