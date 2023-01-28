package org.seng302.models;

import lombok.Data;

import javax.persistence.*;

/**
 * WishlistItem entity class modelling the wishlisting of a business
 * by a user
 * Entity has a generated integer as the key, and stores the user's id, business's id and a boolean
 * attribute muted storing whether notifications regarding the business are displayed to the user
 * or not.
 */
@Data
@Entity
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @OneToOne
    @JoinColumn(name = "business_id", referencedColumnName="id", nullable = false)
    private Business business;

    private MutedStatus mutedStatus;

    /**
     * Empty constructor for JPA use
     */
    public WishlistItem() {}

    /**
     * Basic constructor to wishlist a new business given a user id and business id.
     * @param userId the entity that is following the business.
     * @param business the entity that is being followed by the user.
     */
    public WishlistItem(Long userId, Business business) {
        this.userId = userId;
        this.mutedStatus = MutedStatus.UNMUTED;
        this.business = business;
    }

    /**
     * Function to set wishlistItem to muted, so user will not receive notifications from the
     * followed business
     */
    public void muteBusiness() {
        this.mutedStatus = MutedStatus.MUTED;
    }

    /**
     * Function to unmute wishlistItem, so the user will receive notifications from the
     * followed business
     */
    public void unmuteBusiness() {
        this.mutedStatus = MutedStatus.UNMUTED;
    }
}
