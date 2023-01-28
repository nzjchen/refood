package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity class that represents a notification for a listing.
 */
@Entity
@Data
public class ListingNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user; // User receiving the notification

    @ManyToOne
    private Business business; // Business that sold the listing

    @ManyToOne
    private BoughtListing boughtListing;

    @ManyToOne
    private Listing listing;

    // Expired - it has been bought or removed by another user.
    // Bought - the user attached to this notification bought the listing.
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    private ViewStatus viewStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+12")
    private Date created;

    /**
     * Basic constructor.
     * @param user the user receiving the notification.
     * @param boughtListing the listing being notified about.
     * @param status the listing status.
     */
    public ListingNotification(User user, BoughtListing boughtListing, NotificationStatus status) {
        this.user = user;
        this.boughtListing = boughtListing;
        this.status = status;
        this.viewStatus = ViewStatus.UNREAD;
        this.created = new Date();
    }

    /**
     * Business constructor.
     * @param user the user receiving the notification.
     * @param business the business receiving the notification.
     * @param boughtListing the listing being notified about.
     * @param status the listing status.
     */
    public ListingNotification(User user, Business business, BoughtListing boughtListing, NotificationStatus status) {
        this.user = user;
        this.business = business;
        this.boughtListing = boughtListing;
        this.status = status;
        this.viewStatus = ViewStatus.UNREAD;
        this.created = new Date();
    }

    public ListingNotification(User user, Listing listing, NotificationStatus status) {
        this.user = user;
        this.listing = listing;
        this.status = status;
        this.viewStatus = ViewStatus.UNREAD;
        this.created = new Date();
    }

    public ListingNotification() {}
}
