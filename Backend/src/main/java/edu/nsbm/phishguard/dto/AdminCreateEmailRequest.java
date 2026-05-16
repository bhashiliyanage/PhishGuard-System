package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.Type;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminCreateEmailRequest {
    private Type emailType;
    private String senderAddress;
    private String emailTitle;
    private String emailBody;
    private String link;
    private LocalDateTime createdAt;
}
