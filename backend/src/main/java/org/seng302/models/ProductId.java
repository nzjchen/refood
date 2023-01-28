package org.seng302.models;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Class that helps Spring JPA map the Product table's composite key.
 */
@Embeddable
public class ProductId implements Serializable {
    private String id;
    private long businessId;

    public ProductId(String id, long businessId) {
        this.id = id;
        this.businessId = businessId;
    }

    public ProductId() {
    }
}
