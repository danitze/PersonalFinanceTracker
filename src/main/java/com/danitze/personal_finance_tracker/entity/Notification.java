package com.danitze.personal_finance_tracker.entity;

import com.danitze.personal_finance_tracker.entity.enums.NotificationChannel;
import com.danitze.personal_finance_tracker.entity.enums.NotificationStatus;
import com.danitze.personal_finance_tracker.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "account_id_idx", columnList = "account_id"),
                @Index(name = "account_id_sent_at_idx", columnList = "account_id,sent_at"),
                @Index(name = "trigger_at_sent_at_idx", columnList = "trigger_at,sent_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "notification_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "message", nullable = false)
    private String message;

    @Lob
    @Column(name = "data")
    private String data;

    @Column(name = "trigger_at")
    private OffsetDateTime triggerAt;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Builder.Default
    @Column(name = "read", nullable = false)
    private boolean read = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.NOT_SENT;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

}
