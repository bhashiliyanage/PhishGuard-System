package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.GenerateBy;
import edu.nsbm.phishguard.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreatedEmailResponse {

    private String emailId;
    private GenerateBy generateBy;
    private Type emailType;
    private String senderAddress;
    private String emailTitle;
    private String emailBody;
    private String link;
    private LocalDateTime createdAt;

}
