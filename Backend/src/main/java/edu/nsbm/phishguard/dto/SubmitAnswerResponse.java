package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.Type;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SubmitAnswerResponse {
    private String emailId;
    private String response;
    private Type userChoice;
    private String userId;
    private Boolean isAlreadySubmitted;
    private Boolean isCorrect;
}
