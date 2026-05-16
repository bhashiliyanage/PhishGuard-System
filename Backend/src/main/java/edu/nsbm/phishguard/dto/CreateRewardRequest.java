package edu.nsbm.phishguard.dto;

import edu.nsbm.phishguard.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateRewardRequest {
     private String userId;
     private String emailId;
     private Type type;

}
