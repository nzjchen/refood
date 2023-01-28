package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.seng302.models.requests.NewMessageRequest;
import javax.xml.bind.ValidationException;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity class for a user message.
 * Represents a single message sent from one user, to another user, regarding a community card.

 */
@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User sender;

    @OneToOne
    private Card card;

    @OneToOne
    private User receiver;

    private String description;

    @Enumerated(EnumType.STRING)
    private ViewStatus viewStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+12")
    private Date sent;

    /**
     * Constructor for a new message object
     * @param sender User that sends the message
     * @param receiver User that receives the messages
     * @param card Card the message is sent from
     * @param description Message's description
     * @param sent Date the message was sent
     */
    public Message(User sender, User receiver, Card card, String description, Date sent) {
        this.sender = sender;
        this.receiver = receiver;
        this.card = card;
        this.description = description;
        this.sent = sent;
        this.viewStatus = ViewStatus.UNREAD;
    }

    /**
     * Empty constructor for JPA use.
     */
    protected Message() {
    }

    /**
     * New Message request uses the minimum attributes and a reference to the User who created the Message for initialization
     * This intializer converts a newMessageRequest to a Message entity
     * The date created is set to the date this constructor is called.
     * @param newMessageRequest see NewMessageRequest.java. Creates a new Message using minimum fields
     * @param user the user object that is creating the new Messaage.
     */
    public Message(NewMessageRequest newMessageRequest, User sender, User receiver, Card card) throws ValidationException {
        try {
            if (validateNewMessage(newMessageRequest)) {
                this.sender = sender;
                this.receiver = receiver;
                this.card = card;
                this.description = newMessageRequest.getDescription();
                this.sent = new Date();
                this.viewStatus = ViewStatus.UNREAD;
            }
        }
        catch (ValidationException exception) {
            throw new ValidationException(exception.getMessage());
        }
    }

    /**
     * Validates a new Message object being created from a NewMessageRequest DTO.
     * @param newMessageRequest DTO class containing the info for a new Message entity
     * @return true if the Message information is valid.
     * @throws ValidationException if any of the Message information is invalid.
     */
    private boolean validateNewMessage(NewMessageRequest newMessageRequest) throws ValidationException {
        //Blank or null description
        if (newMessageRequest.getDescription() == null || newMessageRequest.getDescription().trim().isEmpty()) {
            throw new ValidationException("Message must have a description");
        }

        return true;
    }
}
