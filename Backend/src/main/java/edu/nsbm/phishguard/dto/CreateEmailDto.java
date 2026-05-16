package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.GenerateBy;
import edu.nsbm.phishguard.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEmailDto {

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
