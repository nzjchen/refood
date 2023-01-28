package org.seng302.models.requests;

import lombok.Data;
import org.seng302.models.BusinessType;

/**
 * A simple DTO which holds the user id from a request.
 */
@Data
public class BusinessTypeRequest {

    private String query;
    private BusinessType businessType;

    public BusinessTypeRequest(){}

    public BusinessTypeRequest(String query, BusinessType businessType) {
        this.query = query;
        this.businessType = businessType;
    }

}
