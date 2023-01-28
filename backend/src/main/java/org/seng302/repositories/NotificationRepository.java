package org.seng302.repositories;

import org.seng302.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByUserId(Long userId);

    Notification findNotificationByCardId(Long cardId);
}
