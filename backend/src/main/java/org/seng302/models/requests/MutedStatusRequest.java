package org.seng302.models.requests;

import lombok.Data;
import org.seng302.models.MutedStatus;

/**
 * Simple DTO holding the mutedStatus from a request
 */
@Data
public class MutedStatusRequest {
    private MutedStatus mutedStatus;

    public MutedStatusRequest() {}

    public MutedStatusRequest(MutedStatus mutedStatus) { this.mutedStatus = mutedStatus; }
}
