package org.seng302.models.requests;

import lombok.Data;
import org.seng302.models.NotificationStatus;

import java.util.Date;

/**
 * DTO request class that holds the information of a notification to send to users.
 */
@Data
public class ListingNotificationRequest {
  private long id;
  private long userId;
  private long listingId;
  private NotificationStatus status;
  private Date created;

  public ListingNotificationRequest(long id, long userId, long listingId, NotificationStatus status, Date created) {
    this.id = id;
    this.userId = userId;
    this.listingId = listingId;
    this.status = status;
    this.created = created;
  }
}
