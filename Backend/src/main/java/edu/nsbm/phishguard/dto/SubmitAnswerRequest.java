package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.Type;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitAnswerRequest {
    private String emailId;
    private String userId;
    private Type userChoice;
}
