package edu.nsbm.phishguard.entity;

import edu.nsbm.phishguard.enums.GenerateBy;
import edu.nsbm.phishguard.enums.Type;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "emails")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String emailId;

    @Enumerated(EnumType.STRING)
    private GenerateBy generateBy;

    @Enumerated(EnumType.STRING)
    private Type emailType;

    @Enumerated(EnumType.STRING)
    private Type userChoice;

    private String userId;
    private String emailName;
    private String senderAddress;
    private String emailTitle;

    @Column(columnDefinition = "TEXT")
    private String emailBody;

    @Column(columnDefinition = "TEXT")
    private String link;

    private Boolean submitted;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime submittedAt;
}