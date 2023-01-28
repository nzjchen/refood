package org.seng302.models.responses;

import lombok.Data;
import org.seng302.models.BusinessType;
/**
 * A simple DTO which holds the business id to transfer to frontend.
 */
@Data
public class BusinessIdResponse {

    private Long businessId;
    private BusinessType type;

    public BusinessIdResponse(Long businessId, BusinessType type) {
        this.businessId = businessId;
        this.type = type;
    }

    public BusinessIdResponse(Long businessId){
        this.businessId = businessId;
    }


}
