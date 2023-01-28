package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.seng302.models.requests.NewCardRequest;

import javax.persistence.*;
import javax.xml.bind.ValidationException;
import java.util.Date;
import java.util.Calendar;

/**
 * Entity class for a user created card
 *
 * Rather than include the user object itself, I have chosen only take their user name and address.
 * Possible information leak as when we send the card list to the front end, we sent the json object in it's entirety.
 * ie when including the user, it will also include every other field about them
 * we want only reveal the information required rather than expose a possibe security risk.
 */
@Data
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+12")
    private Date created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+12")
    private Date displayPeriodEnd;
    private String keywords;

    @Enumerated(EnumType.STRING)
    @Column(name = "section")
    private MarketplaceSection section;

    /**
     * Constructor for a new card object
     * @param user User that created the card
     * @param title Card's title
     * @param description Card's description field
     * @param created Date the card was created
     * @param displayPeriodEnd Date the card will be removed
     * @param keywords Hashtags that describe the card
     * @param section The Card's Marketplace section (see the Enum, MarketplaceSection.java)
     */
    public Card(User user, String title, String description, Date created, Date displayPeriodEnd, String keywords, MarketplaceSection section ) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.created = created;
        this.displayPeriodEnd = displayPeriodEnd;
        this.keywords = keywords;
        this.section = section;
    }


    /**
     * Empty constructor for JPA use.
     */
    protected Card() {
    }

    /**
     * New Card request uses the minimum attributes and a reference to the User who created the card for initialization
     * This intializer converts a NewCardRequest to a Card entity
     * The date created is set to the date this constructor is called.
     * @param newCardRequest see NewCardRequest.java. Creates a new card using minimum fields
     * @param user the user object that is creating the new community card.
     */
    public Card(NewCardRequest newCardRequest, User user) throws ValidationException {
        try {
            if (validateNewCard(newCardRequest)) {
                this.user = user;
                this.title = newCardRequest.getTitle();
                this.description = newCardRequest.getDescription();
                this.created = new Date();
                this.displayPeriodEnd = getDisplayPeriodEndDate();
                this.keywords = newCardRequest.getKeywords();
                this.section = newCardRequest.getSection();
            }
        }
        catch (ValidationException exception) {
            throw new ValidationException(exception.getMessage());
        }
    }

    /**
     * Validates a new card object being created from a NewCardRequest DTO.
     * @param newCardRequest DTO class containing the info for a new card entity
     * @return true if the card information is valid.
     * @throws ValidationException if any of the card information is invalid.
     */
    private boolean validateNewCard(NewCardRequest newCardRequest) throws ValidationException {
        if (newCardRequest.getTitle() == null) {
            throw new ValidationException("Title cannot be null");
        }

        if (newCardRequest.getTitle().length() < 2) {
            throw new ValidationException("Title length is too short");
        }

        if (newCardRequest.getTitle().length() > 50) {
            throw new ValidationException("Title length is too long");
        }

        if (newCardRequest.getSection() == null) {
            throw new ValidationException("Marketplace section is missing");
        }
        return true;
    }

    /**
     * getDisplayPeriodEndDate calculates the time a Card's display period will end
     * This is set to two weeks/14 days.
     * To change, alter the const displayPeriod in the function.
     *
     * Since this is called when a new card is created, the displayPeriodEndDate is
     * displayPeriod days after the current date.
     *
     * @return displayPeriodEndDate The date the card will expire
     */
    private Date getDisplayPeriodEndDate () {
        int displayPeriod = 14;

        Calendar displayPeriodEndCalendar = Calendar.getInstance();
        displayPeriodEndCalendar.add(Calendar.DAY_OF_YEAR, displayPeriod);
        return displayPeriodEndCalendar.getTime();
    }

    /**
     * Updates display period by getting date 2 weeks in future and setting display period to that
     * Preconditions: None
     * Postconditions: Display period is updated
     */
    public void updateDisplayPeriodEndDate() {
        Date newDate = getDisplayPeriodEndDate();
        this.setDisplayPeriodEnd(newDate);
    }
}
