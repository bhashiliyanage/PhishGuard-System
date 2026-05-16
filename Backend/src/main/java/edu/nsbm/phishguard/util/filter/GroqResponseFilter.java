package edu.nsbm.phishguard.util.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nsbm.phishguard.util.response.LlmResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GroqResponseFilter {

    public static LlmResponse parseGroqResponse(String raw) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(raw);

            // Groq: choices[0].message.content
            String content = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            JsonNode email = mapper.readTree(content);

            return new LlmResponse(
                    email.path("sender_email").asText(),
                    email.path("title").asText(),
                    email.path("body").asText(),
                    email.path("link").asText()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq response: " + e.getMessage());
        }
    }
}
