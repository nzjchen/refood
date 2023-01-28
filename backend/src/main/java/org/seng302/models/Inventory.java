package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.seng302.models.requests.NewInventoryRequest;

import javax.persistence.*;
import javax.xml.bind.ValidationException;
import java.util.Calendar;
import java.util.Date;

/**
 * Entity class that holds the information of a business product inventory.
 */
@Data
@Entity
public class Inventory {

    // Composite key of product id & business id.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name="product_id", referencedColumnName="id", updatable = false, insertable = false),
            @JoinColumn(name="business_id", referencedColumnName="businessId", updatable = false, insertable = false)
    })
    private Product product;

    @JsonIgnore
    @Column(name = "product_id")
    private String productId;

    @JsonIgnore
    @Column(name = "business_id")
    private long businessId;

    private int quantity;
    private double pricePerItem;
    private double totalPrice;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date manufactured;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date sellBy;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date bestBefore;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date expires;

    protected Inventory() { }

    public Inventory(String productId, long businessId, int quantity, double pricePerItem, double totalPrice,
                     Date manufacturedDate, Date sellByDate, Date bestBeforeDate, Date expiryDate) {
        this.productId = productId;
        this.businessId = businessId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
        this.manufactured = manufacturedDate;
        this.sellBy = sellByDate;
        this.bestBefore = bestBeforeDate;
        this.expires = expiryDate;
    }

    /**
     * Used for when a new product request is called.
     * @param newInventoryRequest The request body information that was mapped into a NewInventoryRequest.
     * @param businessId business to assign the product inventory rights to.
     */
    public Inventory(NewInventoryRequest newInventoryRequest, Long businessId) throws ValidationException {
        if(this.validInventoryRequest(newInventoryRequest)) {
            this.productId = newInventoryRequest.getProductId();
            this.businessId = businessId;
            this.quantity = newInventoryRequest.getQuantity();
            this.pricePerItem = newInventoryRequest.getPricePerItem();
            this.totalPrice = newInventoryRequest.getTotalPrice();
            this.manufactured = newInventoryRequest.getManufactured();
            this.sellBy = newInventoryRequest.getSellBy();
            this.bestBefore = newInventoryRequest.getBestBefore();
            this.manufactured = newInventoryRequest.getManufactured();
            this.expires = newInventoryRequest.getExpires();


        } else {
            throw new ValidationException("Inventory request item is not valid");
        }
    }

    /**
     * Validates new inventory request, checks fields for appropriate values
     * @param req request that is going to be checked
     */
    private boolean validInventoryRequest(NewInventoryRequest req) {
        Date today = Calendar.getInstance().getTime();
        return !((req.getQuantity() < 1 || req.getTotalPrice() < 0 || req.getPricePerItem() < 0) ||
                req.getExpires() == null || req.getExpires().before(today) ||
                req.getManufactured() != null && req.getManufactured().after(today) ||
                req.getSellBy() != null && req.getSellBy().before(today) ||
                req.getBestBefore() != null && (req.getBestBefore().before(today) ||
                        req.getBestBefore().after(req.getExpires())));

    }

    /**
     * Used when PUT request is happening, replaces all fields with new objects fields..
     * new object has to be created for validation to happen
     * @param newInventoryItem New inventory object that is going to replace current entities properties
     */
    public void replaceInventoryItem(Inventory newInventoryItem) {
        this.productId = newInventoryItem.getProductId();
        this.quantity = newInventoryItem.getQuantity();
        this.pricePerItem = newInventoryItem.getPricePerItem();
        this.totalPrice = newInventoryItem.getTotalPrice();
        this.manufactured = newInventoryItem.getManufactured();
        this.sellBy = newInventoryItem.getSellBy();
        this.bestBefore = newInventoryItem.getBestBefore();
        this.expires = newInventoryItem.getExpires();
    }

}
