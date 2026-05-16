package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.Type;
import lombok.*;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserRequestedEmailResponse {
    private String emailId;
    private Type emailType;
    private Type userChoice;
    private String userId;
    private String emailName;
    private String senderAddress;
    private String emailTitle;
    private String emailBody;
    private Boolean submitted;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
}
