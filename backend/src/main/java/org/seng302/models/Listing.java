package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.seng302.models.requests.NewListingRequest;

import javax.xml.bind.ValidationException;
import javax.persistence.*;
import java.util.Date;
import java.util.Calendar;

/**
 * Entity class that represents a listing for the sale of some product from a business' inventory.
 */
@Data
@Entity
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Inventory inventoryItem;
    private int quantity;
    private double price;
    private String moreInfo;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date created;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date closes;

    @Formula("(select count(*) from listing_like l where l.listing_id = id)")
    private int likes; // Will not appear as a column

    /**
     * Constructor a new Listing object. Id not included.
     * @param inventoryItem item that this Listing object will represent.
     * @param quantity the number of items associated with this Listing.
     * @param price the value of the Listing.
     * @param moreInfo an (optional) field for businesses to provide further info about the listing.
     * @param created date and t ime when the listing was created.
     * @param closes date and time when the listing closes.
     */
    public Listing(Inventory inventoryItem, int quantity, double price, String moreInfo, Date created, Date closes) {
        this.inventoryItem = inventoryItem;
        this.quantity = quantity;
        this.price = price;
        this.moreInfo = moreInfo;
        this.created = created;
        this.closes = closes;
        this.likes = 0;
    }


    /**
     * Empty constructor for JPA use.
     */
    protected Listing() {}


    /**
     * Used for when a new product request is called.
     * @param inventoryItem the inventory item object that is being listed for sale
     * @param newListingRequest The request body information that was mapped into a newListingRequest.
     */
    public Listing(Inventory inventoryItem, NewListingRequest newListingRequest) throws ValidationException {
        if(this.validListingRequest(inventoryItem, newListingRequest)){
            this.inventoryItem = inventoryItem;
            this.quantity = newListingRequest.getQuantity();
            this.price = newListingRequest.getPrice();
            this.moreInfo = newListingRequest.getMoreInfo();
            this.created = new Date();
            this.closes = newListingRequest.getCloses();
            this.likes = 0;
        } else {
            throw new ValidationException("Listing request item is not valid!");
        }
    }

    /**
     * Checks if the new listing details are valid.
     * @param inventoryItem the inventory item the listing is associated with.
     * @param req the dto class that holds the details to be checked.
     * @return true if all details are valid, false otherwise.
     */
    private boolean validListingRequest(Inventory inventoryItem, NewListingRequest req) {
        Date today = Calendar.getInstance().getTime();
        if(inventoryItem == null){
            return false;
        } else {
            int quantityOfInventory = inventoryItem.getQuantity();
            return !(req.getQuantity() < 1 || req.getPrice() < 0 || req.getQuantity() > quantityOfInventory || req.getCloses() == null || req.getCloses().before(today));
        }

    }
}
