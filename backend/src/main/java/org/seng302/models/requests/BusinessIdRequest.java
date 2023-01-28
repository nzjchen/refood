package org.seng302.models.requests;

import lombok.Data;

/**
 * A simple DTO which holds the business id from a request.
 */
@Data
public class BusinessIdRequest {

    private long businessId;

    public BusinessIdRequest(){}

    public BusinessIdRequest(long businessId) {
        this.businessId = businessId;
    }

}
