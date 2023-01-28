package org.seng302.models.requests;

import lombok.Getter;
import org.seng302.models.ViewStatus;

/**
 * DTO request class that holds the updated view status of a notification
 */
@Getter
public class UpdateNotificationViewStatusRequest {
    private ViewStatus viewStatus;
}
