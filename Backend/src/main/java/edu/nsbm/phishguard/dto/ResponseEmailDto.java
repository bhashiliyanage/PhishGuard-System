package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.GenerateBy;
import edu.nsbm.phishguard.enums.Type;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseEmailDto {

    private String emailId;
    private GenerateBy generateBy;
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
