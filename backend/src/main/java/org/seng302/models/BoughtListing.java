package org.seng302.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class BoughtListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User buyer;

    @ManyToOne
    private Product product;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+12")
    private Date sold;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+12")
    private Date listed;

    private int likes;

    private int quantity;

    private long listingId;

    private double price;

    /**
     * Empty constructor for JPA use
     */
    protected BoughtListing() {}

    /**
     * Bought Listing constructor class, created when a listing is sold.
     * @param buyer
     * @param product
     * @param likes
     * @param quantity
     * @param listed
     * @param listingId
     * @param price
     */
    public BoughtListing(User buyer, Product product, int likes, int quantity, Date listed, long listingId, double price) {
        this.buyer = buyer;
        this.product = product;
        this.likes = likes;
        this.quantity = quantity;
        this.sold = new Date();
        this.listed = listed;
        this.listingId = listingId;
        this.price = price;
    }

    /**
     * Barebones constructor for boughtListing
     * @param buyer User object that purchased listing
     * @param product Product the product that has been purchased
     * @param listing Listing object that was purchased
     */
    public BoughtListing(User buyer, Product product, Listing listing) {
        this.buyer = buyer;
        this.product = product;
        this.likes = listing.getLikes();
        this.quantity = listing.getQuantity();
        this.sold = new Date();
        this.listed = listing.getCreated();
        this.listingId = listing.getId();
        this.price = listing.getPrice();
    }
}
