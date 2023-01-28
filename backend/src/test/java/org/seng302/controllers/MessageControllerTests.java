package org.seng302.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.seng302.models.*;
import org.seng302.models.requests.NewCardRequest;
import org.seng302.models.requests.NewMessageRequest;

import org.seng302.repositories.MessageRepository;
import org.seng302.repositories.UserRepository;
import org.seng302.repositories.CardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.xml.bind.ValidationException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageController.class)
@ContextConfiguration(classes = TestApplication.class)
class MessageControllerTests {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private MessageController messageController;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CardRepository cardRepository;

    @Autowired
    private ObjectMapper mapper;

    private User testUserA;
    private User testUserB;
    private Card cardA;
    private Card cardB;

    private Message message;
    private List<Message> messages;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException, ValidationException {
        testUserA = new User("Rayna", "YEP", "Dalgety", "Universal", "zero tolerance task-force" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
        testUserA.setId(1L);
        testUserB = new User("Elwood", "YEP", "Altamirano", "Visionary", "mobile capacity", "ealtamirano8@phpbb.com","1927-02-28","+381 643 240 6530",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ItqVNvM2JBA");
        testUserB.setId(2L);

        NewCardRequest cardRequest = new NewCardRequest(testUserA.getId(), "Card Title", "Desc", "Test, Two", MarketplaceSection.FORSALE);

        Card card = new Card(cardRequest, testUserA);

        message = new Message(testUserB, testUserA, card, "hello", new Date());
        cardRequest = new NewCardRequest(testUserA.getId(), "Card Title", "Desc", "Test, Two", MarketplaceSection.FORSALE);

        cardA = new Card(cardRequest, testUserA);
        cardB = new Card(cardRequest, testUserB);

        NewMessageRequest newMessageRequest = new NewMessageRequest(cardA.getId(), "desc");

        message = new Message(testUserB, testUserA, cardA, "hello", new Date());
        Message messageInvalid = new Message(testUserB, testUserA, cardA, "hello", new Date());

        messages = new ArrayList<Message>();
        messages.add(message);
    }


    @Test
    void getMessages_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(get("/users/{userId}/messages", testUserA.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getMessages_badId_returnBadRequest() throws Exception {

        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(messageRepository.findMessageByReceiver(testUserA)).thenReturn(null);

        mvc.perform(get("/users/{userId}/messages", 'a'))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getMessages_validIdAndCorrectUser_returnOk() throws Exception {

        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(messageRepository.findMessageByReceiver(testUserA)).thenReturn(messages);

        mvc.perform(get("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserA))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getMessages_badUser_returnForbidden() throws Exception {

        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(messageRepository.findMessageByReceiver(testUserA)).thenReturn(messages);

        mvc.perform(get("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getMessages_userDoesntExist_returnNotAcceptable() throws Exception {

        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(null);

        mvc.perform(get("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isNotAcceptable());
    }


    /**
    * Post Messages
    **/

    @Test
    @WithMockUser
    void postMessages_validIdAndCorrectUser_returnOk() throws Exception {
        NewMessageRequest newMessageRequest = new NewMessageRequest(cardA.getId(), "Simple description");
        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(cardRepository.findCardById(cardA.getId())).thenReturn(cardA);

        mvc.perform(post("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newMessageRequest)))
                .andExpect(status().isCreated());
    }

    //401: Invalid/unauthorized user
    @Test
    void postMessages_noAuth_returnUnauthorized() throws Exception {
        NewMessageRequest newMessageRequest = new NewMessageRequest(cardA.getId(), "Simple description");
        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(cardRepository.findCardById(cardA.getId())).thenReturn(cardA);

        mvc.perform(post("/users/{userId}/messages", 1)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newMessageRequest)))
                .andExpect(status().isUnauthorized());
    }

    //400: invalid data
    @Test
    @WithMockUser
    void postMessages_noMessageDescription_returnIsBadRequest() throws Exception {
        NewMessageRequest newMessageRequest = new NewMessageRequest(cardA.getId(), null);
        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(cardRepository.findCardById(cardA.getId())).thenReturn(cardA);

        mvc.perform(post("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newMessageRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void postMessages_badCardId_returnIsBadRequest() throws Exception {
        String newMessageRequest = "{\n" +
                "    \"cardId\": \"send it\"\n" +
                "    \"description\": \"No wof reg, send it\"\n" +
                "}";
        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(cardRepository.findCardById(cardA.getId())).thenReturn(cardA);

        mvc.perform(post("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB)
                .contentType("application/json")
                .content(newMessageRequest))
                .andExpect(status().isBadRequest());
    }

    //card not found
    @Test
    @WithMockUser
    void postMessages_cardDoesntExist_returnIsForbidden() throws Exception {
        NewMessageRequest newMessageRequest = new NewMessageRequest(cardA.getId(), "Simple description");
        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(testUserA);
        Mockito.when(cardRepository.findCardById(cardA.getId())).thenReturn(cardA);

        mvc.perform(post("/users/{userId}/messages", 80)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newMessageRequest)))
                .andExpect(status().isForbidden());
    }

    //user not found
    @Test
    @WithMockUser
    void postMessages_receiverDoesntExist_returnIsForbidden() throws Exception {
        NewMessageRequest newMessageRequest = new NewMessageRequest(cardA.getId(), "Simple description");
        Mockito.when(userRepository.findUserById(testUserA.getId())).thenReturn(null);
        Mockito.when(cardRepository.findCardById(cardA.getId())).thenReturn(cardA);

        mvc.perform(post("/users/{userId}/messages", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newMessageRequest)))
                .andExpect(status().isForbidden());
    }

    //
    // DELETE - Message by id
    //
    @Test
    void deleteMessage_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(delete("/messages/{messageId}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void deleteMessage_noExistingMessage_returnNotAcceptable() throws Exception {
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(null);

        mvc.perform(delete("/messages/{messageId}", message.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void deleteMessage_wrongUserAndNotGAA_returnForbidden() throws Exception {
        message.getReceiver().setId(5);
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(message);

        mvc.perform(delete("/messages/{messageId}", message.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteMessage_isUser_returnOk() throws Exception {
        message.setReceiver(testUserB);
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(message);

        mvc.perform(delete("/messages/{messageId}", message.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteMessage_isDgaa_returnOk() throws Exception {
        message.getReceiver().setId(5);
        testUserB.setRole(Role.DGAA);
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(message);

        mvc.perform(delete("/messages/{messageId}", message.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isOk());
    }


    //
    // PUT - Notification View Status
    //
    @Test
    void testPutNotificationViewStatus_notLoggedIn_returnUnauthorized() throws Exception {
        mvc.perform(put("/messages/{messageId}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_invalidViewStatusInput_returnBadRequest() throws Exception {
        mvc.perform(put("/messages/{messageId}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Donda\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_notUserAndNotDgaa_returnForbidden() throws Exception {
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(message);
        mvc.perform(put("/messages/{messageId}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_noNotificationWithId_returnNotAcceptable() throws Exception {
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(null);
        mvc.perform(put("/messages/{messageId}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_validRequest_returnOk() throws Exception {
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(message);
        mvc.perform(put("/messages/{messageId}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserA))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_asGaa_returnOk() throws Exception {
        Mockito.when(messageRepository.findMessageById(message.getId())).thenReturn(message);
        testUserB.setRole(Role.GAA);
        mvc.perform(put("/messages/{messageId}", message.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUserB))
                .andExpect(status().isOk());
    }


}
