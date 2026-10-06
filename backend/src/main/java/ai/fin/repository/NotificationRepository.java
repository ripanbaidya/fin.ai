package ai.fin.repository;

import ai.fin.entities.Notification;
import ai.fin.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    Page<Notification> findByUser_Id(String userId, Pageable pageable);

    Page<Notification> findByUser_IdAndIsRead(String userId, boolean isRead, Pageable pageable);

    Page<Notification> findByUser_IdAndType(String userId, NotificationType type, Pageable pageable);

    Page<Notification> findByUser_IdAndIsReadAndType(String userId, boolean isRead, NotificationType type, Pageable pageable);

    long countByUser_IdAndIsReadFalse(String userId);

    Optional<Notification> findByIdAndUser_Id(String id, String userId);

    @Modifying
    @Query("update Notification n set n.isRead = true, n.readAt = :readAt where n.id = :id and n.user.id = :userId")
    int markAsRead(@Param("id") String id, @Param("userId") String userId, @Param("readAt") Instant readAt);

    @Modifying
    @Query("update Notification n set n.isRead = true, n.readAt = :readAt where n.user.id = :userId and n.isRead = false")
    int markAllAsRead(@Param("userId") String userId, @Param("readAt") Instant readAt);

    @Modifying
    @Query("delete from Notification n where n.id = :id and n.user.id = :userId")
    int deleteByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    @Modifying
    @Query("delete from Notification n where n.user.id = :userId")
    int deleteAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("delete from Notification n where n.user.id = :userId and n.isRead = true")
    int deleteAllReadByUserId(@Param("userId") String userId);
}
