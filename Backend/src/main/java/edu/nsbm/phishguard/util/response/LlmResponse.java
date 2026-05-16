package edu.nsbm.phishguard.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class LlmResponse {
    private String senderEmail;
    private String title;
    private String body;
    private String link;
}
