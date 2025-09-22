package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.Notification;
import com.danitze.personal_finance_tracker.entity.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByAccountAndReadFalse(Account account);

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.account = :account
            AND (:notReadOnly = FALSE or n.read = FALSE)
            ORDER BY n.createdAt DESC
            """)
    Page<Notification> findPageByAccount(
            @Param("account") Account account,
            @Param("notReadOnly") boolean notReadOnly,
            Pageable pageable
    );

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.status IN :statuses
            AND (n.triggerAt IS NULL OR n.triggerAt <= :now)
            """)
    List<Notification> findAllPending(
            @Param("statuses") Set<NotificationStatus> statuses,
            @Param("now") OffsetDateTime now
    );

    List<Notification> findAllByStatus(NotificationStatus status);

}
