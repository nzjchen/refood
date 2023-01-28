package org.seng302.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.seng302.models.*;
import org.seng302.models.requests.NewMessageRequest;
import org.seng302.models.requests.UpdateNotificationViewStatusRequest;
import org.seng302.repositories.MessageRepository;
import org.seng302.repositories.UserRepository;
import org.seng302.repositories.CardRepository;
import org.seng302.models.responses.MessageIdResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.xml.bind.ValidationException;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Controller class that handles the endpoints of community marketplace cards.
 */
@RestController
public class MessageController {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;


    /**
     * Gets user messages, does this by grabbing user from their id, then finding the cards that the user has, then finding the messages that are related to those cards
     * Preconditions: Logged in and acting as a user
     * Postconditions: User's messages will be retrieved if any exist
     * @param userId ID of user that we are going to get messages from
     * @return 200 if successful, 40
     * @throws JsonProcessingException
     */
    @GetMapping("/users/{userId}/messages")
    public ResponseEntity<String> getUserMessages(@PathVariable long userId, HttpSession session) throws JsonProcessingException {
        User user = userRepository.findUserById(userId);
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        if(userId != currentUser.getId() && !Role.isGlobalApplicationAdmin(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Message> messages = messageRepository.findMessageByReceiver(user);
        for(Message message: messages) {
            message.getSender().setBusinessesAdministered(null);
            message.getReceiver().setBusinessesAdministered(null);
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(messages));
    }

    /**
     * Posts a user's message. We get the recipient from {userId}, the sender from the user's session. The request contains
     * the card id the message is about and the message contents which we use to create a new message. Null or incorrect
     * data returns appropriate

     * Preconditions: Logged in and acting as a valid user. The receiver and card are valid and the message is non null or blank
     * Postconditions: The message will be saved to the database and the ID is returned

     * @param userId ID of user that we are sending the messages to
     * @return MessageIdResponse and 201 if successful
     * @throws JsonProcessingException
     */

    @PostMapping("/users/{userId}/messages")
    public ResponseEntity<String> addUserMessage(@PathVariable long userId, @RequestBody NewMessageRequest newMessageRequest, HttpSession session) throws JsonProcessingException {
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        User receiver = userRepository.findUserById(userId);
        Card card = cardRepository.findCardById(newMessageRequest.getCardId());

        //401 Attempting to create a message without logging in
        //Note: the user cannot send a message as someone else.
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //403 Attempting to send to an invalid user.
        if (receiver == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //403 Attempting to send via an invalid card.
        if (card == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message must have an associated, valid, Card");
        }


        Message newMessage;
        try { // Attempt to create a new card.
            newMessage = new Message(newMessageRequest, currentUser, receiver, card);
        }
        //400 data is not correct
        catch (ValidationException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }

        messageRepository.save(newMessage);
        MessageIdResponse messageIdResponse = new MessageIdResponse(newMessage.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(messageIdResponse));
    }

    /**
     * Message DELETE endpoint, deletes a single message given an ID.
     *
     * Preconditions:
     *  Given card ID is of type Long.
     *  Message exists in database.
     *  User is logged in and the receiver/DGAA.
     * Postconditions:
     *  Message is deleted from the database.
     *
     * @param messageId ID of message to be retrieved from DB.
     * @param session the current user session.
     *
     * @return 401 if not logged in, 403 if creatorId & session user Id don't match OR if not D/GAA,
     * 400 if there are errors with data, 200 if everything works.
     * @throw JsonProcessingException if mapper to convert the response into a JSON string fails.
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessageById (@PathVariable Long messageId, HttpSession session) throws JsonProcessingException {
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        Message message = messageRepository.findMessageById(messageId);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User messageReceiver = message.getReceiver();

        if (messageReceiver.getId() != currentUser.getId() && !Role.isGlobalApplicationAdmin(currentUser.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        messageRepository.deleteMessageById(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(message));
    }

    /**
     * Updates the view status of a listing notification.
     * @param notificationId unique id of the notification.
     * @param request dto holding the new view status.
     * @param session current user login session.
     * @return 400 if request value is invalid (handled by spring), 401 if unauthorized (spring sec),
     * 403 if the notification does not belong to the current user, 406 if the notification id does not exist.
     */
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<String> updateListingNotificationViewStatus(@PathVariable long messageId,
                                                                      @RequestBody UpdateNotificationViewStatusRequest request,
                                                                      HttpSession session) {
        Message message = messageRepository.findMessageById(messageId);

        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (user.getId() != message.getReceiver().getId() && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        message.setViewStatus(request.getViewStatus());
        messageRepository.save(message);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
 