package org.seng302.models.requests;

import lombok.Data;
import org.seng302.models.Address;
import org.seng302.models.BusinessType;

@Data
public class NewBusinessRequest {

    private String name;
    private String description;
    private Address address;
    private BusinessType businessType;

    public NewBusinessRequest(String name, String description, Address address, BusinessType businessType) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;
    }

}
