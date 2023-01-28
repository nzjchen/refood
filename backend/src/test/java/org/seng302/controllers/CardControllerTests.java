package org.seng302.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.models.requests.NewCardRequest;
import org.seng302.repositories.CardRepository;
import org.seng302.repositories.UserRepository;
import org.seng302.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;


import javax.xml.bind.ValidationException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebMvcTest(controllers = CardController.class)
@ContextConfiguration(classes = TestApplication.class)
class CardControllerTests {

    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private CardController cardController;
    @MockBean
    private CardRepository cardRepository;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper mapper;

    private User testUser;
    private User anotherUser;
    private NewCardRequest cardRequest;
    private Card card;
    private Address addr;
    private Notification notification;
    private List<Card> fakeExpiredCards;
    private List<Notification> notifications;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException, ValidationException {
        addr = new Address(null, null, null, null, null, "Australia", "12345");
        testUser = new User("Rayna", "YEP", "Dalgety", "", "" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
        testUser.setId(1);
        anotherUser = new User("Bob", "", "Loblaw", "", "", "bblaw@email.com", "2006-03-30","+7 684 622 5902", new Address(null, null, null, null, "New Zealand", null), "ATQWJM");
        anotherUser.setId(2);
        cardRequest = new NewCardRequest(testUser.getId(), "Card Title", "Desc", "Test, Two", MarketplaceSection.FORSALE);

        card = new Card(cardRequest, testUser);
        notification = new Notification(testUser.getId(), card.getId(), card.getTitle(), card.getDisplayPeriodEnd());
        notifications = new ArrayList<>();
        notifications.add(notification);

        //temporary for test
        fakeExpiredCards = new ArrayList<>();
        fakeExpiredCards.add(card);
        cardController = new CardController(userRepository, cardRepository, notificationRepository);
    }

    @Test
    void testPostCard_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(post("/cards")
            .contentType("application/json")
            .content(mapper.writeValueAsString(cardRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testPostCard_wrongCreatorId_returnForbidden() throws Exception {
        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testPostCard_noTitle_returnBadRequest() throws Exception {
        cardRequest.setTitle(null);

        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testPostCard_shortTitle_returnBadRequest() throws Exception {
        cardRequest.setTitle("A");

        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testPostCard_titleTooLong_returnBadRequest() throws Exception {
        cardRequest.setTitle("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZZ");

        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testPostCard_noSection_returnBadRequest() throws Exception {
        cardRequest.setSection(null);

        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testPostCard_returnCreated() throws Exception {
        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void testPostCard_asDgaa_returnCreated() throws Exception {
        anotherUser.setRole(Role.DGAA);

        mvc.perform(post("/cards")
                .contentType("application/json")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser)
                .content(mapper.writeValueAsString(cardRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetCards_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "ForSale"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetCards_noParam_returnBadRequest() throws Exception {
        mvc.perform(get("/cards"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetCards_emptyParam_returnBadRequest() throws Exception {
        mvc.perform(get("/cards")
                .param("section", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetCards_invalidParam_returnBadRequest() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "InvalidSectionName"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetCards_returnOk() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "ForSale")
                .param("pageNum", String.valueOf(1))
                .param("resultsPerPage", String.valueOf(1))
                .param("sortBy", "created")
                .param("reverse", String.valueOf(true)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetCards_returnOk_not_reverse() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "ForSale")
                .param("pageNum", String.valueOf(1))
                .param("resultsPerPage", String.valueOf(1))
                .param("sortBy", "created"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetCards_returnOk_no_required_params() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "ForSale")
                .param("pageNum", String.valueOf(1))
                .param("resultsPerPage", String.valueOf(1)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetCards_returnOk_with_bad_sortBy() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "ForSale")
                .param("pageNum", String.valueOf(1))
                .param("resultsPerPage", String.valueOf(1))
                .param("sortBy", "bababooey"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser
    void testGetCards_returnOk_sort_by_country() throws Exception {
        mvc.perform(get("/cards")
                .param("section", "ForSale")
                .param("pageNum", String.valueOf(1))
                .param("resultsPerPage", String.valueOf(1))
                .param("sortBy", "country"))
                .andExpect(status().isOk());
    }

    //GET by ID tests

    @Test
    void testGetCardById_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(get("/cards/{id}", card.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetCardById_asUser_returnOk() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        mvc.perform(get("/cards/{id}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetCardById_badId_returnNotAcceptable() throws Exception {
        //If no card found repository will give null
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(null);

        mvc.perform(get("/cards/{id}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testGetCardById_idNotLong_returnNotAcceptable() throws Exception {
        //Any value that isn't long will throw 400, just making sure with a float
        mvc.perform(get("/cards/{id}", 1.1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetExpiredCards_Successful() throws Exception {
        Mockito.when(notificationRepository.findNotificationsByUserId(testUser.getId())).thenReturn(notifications);

        mvc.perform(get("/users/{userId}/cards/notifications", testUser.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetExpiredCards_Forbidden() throws Exception {
        mvc.perform(get("/users/{userId}/cards/notifications", anotherUser.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isForbidden());
    }

    //Get user cards endpoint

    @Test
    void testGetUserCards_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(get("/users/{id}/cards", 1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetUserCards_auth_returnOk() throws Exception {
        int userId = 1;
        Mockito.when(userRepository.findUserById(userId)).thenReturn(testUser);
        mvc.perform(get("/users/{id}/cards", userId)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetUserCards_noUser_returnNotAcceptable() throws Exception {
        int userId = 1;
        Mockito.when(userRepository.findUserById(userId)).thenReturn(null);
        mvc.perform(get("/users/{id}/cards", userId)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testGetUserCards_badId_returnBadRequest() throws Exception {

        mvc.perform(get("/users/{id}/cards", 1.1)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteCardById_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(delete("/cards/{cardId}", card.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testDeleteCard_wrongCreatorId_returnForbidden() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        mvc.perform(delete("/cards/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testDeleteCard_asCreator() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        mvc.perform(delete("/cards/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testDeleteCard_badId_returnNotAcceptable() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(null);

        mvc.perform(delete("/cards/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void testExtendCard_noAuth_returnUnauthorized() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        mvc.perform(put("/cards/{id}/extenddisplayperiod", card.getId()))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser
    void testExtendCard_isGAA_returnOk() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        User GAAUser = new User("New", "GAA", addr, "email2@email.com", "password", Role.GAA);

        mvc.perform(put("/cards/{id}/extenddisplayperiod", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, GAAUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testExtendCard_isCreator_returnOk() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        mvc.perform(put("/cards/{id}/extenddisplayperiod", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testExtendCard_notCreatorOrGAA_returnForbidden() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        mvc.perform(put("/cards/{id}/extenddisplayperiod", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testExtendCard_IdNotExist_returnUnacceptable() throws Exception {
        //If no card found repository will give null
        Mockito.when(cardRepository.findCardById(999)).thenReturn(null);

        mvc.perform(put("/cards/{id}/extenddisplayperiod", 999)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testDeleteCard_asDGAA() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);

        anotherUser.setRole(Role.DGAA);

        mvc.perform(delete("/cards/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isOk());
    }

    //
    // PUT /cards/{id}
    //
    @Test
    void testEditCard_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(put("/cards/{id}", card.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testEditCard_validEdit_returnOk() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);
        NewCardRequest editedCardRequest = new NewCardRequest(testUser.getId(), "Edited Title", card.getDescription(), "Some keywords", MarketplaceSection.FORSALE);
        mvc.perform(put("/cards/{id}", card.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(editedCardRequest))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    void testEditCard_asGlobalAdmin_returnOk() throws Exception {
        testUser.setRole(Role.GAA);
        card.setUser(anotherUser);
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);
        NewCardRequest editedCardRequest = new NewCardRequest(testUser.getId(), "Edited Title", card.getDescription(), "Some keywords", MarketplaceSection.FORSALE);
        mvc.perform(put("/cards/{id}", card.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(editedCardRequest))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testEditCard_noTitle_returnBadRequest() throws Exception {
        card.setUser(testUser);
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);
        NewCardRequest editedCardRequest = new NewCardRequest(testUser.getId(), null, card.getDescription(), "Some keywords", MarketplaceSection.FORSALE);
        mvc.perform(put("/cards/{id}", card.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(editedCardRequest))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testEditCard_noSection_returnBadRequest() throws Exception {
        card.setUser(testUser);
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);
        NewCardRequest editedCardRequest = new NewCardRequest(testUser.getId(), "Edited Title", card.getDescription(), "Some keywords", null);
        mvc.perform(put("/cards/{id}", card.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(editedCardRequest))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testEditCard_notCardOwner_returnForbidden() throws Exception {
        card.setUser(anotherUser);
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(card);
        NewCardRequest editedCardRequest = new NewCardRequest(testUser.getId(), "Edited Title", card.getDescription(), "Some keywords", MarketplaceSection.FORSALE);
        mvc.perform(put("/cards/{id}", card.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(editedCardRequest))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testEditCard_noCardWithId_returnNotAcceptable() throws Exception {
        Mockito.when(cardRepository.findCardById(card.getId())).thenReturn(null);
        NewCardRequest editedCardRequest = new NewCardRequest(testUser.getId(), "Edited Title", card.getDescription(), "Some keywords", MarketplaceSection.FORSALE);
        mvc.perform(put("/cards/{id}", card.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(editedCardRequest))
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void testCardExpiryUpdatesCreatesNewNotification() throws Exception {
        Mockito.when(cardRepository.findAllByDisplayPeriodEndBefore(Mockito.any(Date.class))).thenReturn(fakeExpiredCards);
        Mockito.when(notificationRepository.findNotificationByCardId(Mockito.anyLong())).thenReturn(null);

        //Check method saves a notification object if there is no existing notification
        cardController.updateExpiredCards();

        Mockito.verify(notificationRepository).save(Mockito.any(Notification.class));
    }

    @Test
    void testCardPastExpiryGetsDeleted() throws Exception {
        Mockito.when(cardRepository.findAllByDisplayPeriodEndBefore(Mockito.any(Date.class))).thenReturn(fakeExpiredCards);
        Mockito.when(notificationRepository.findNotificationByCardId(Mockito.anyLong())).thenReturn(notification);
        Instant now = Instant.now();
        Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));
        card.setDisplayPeriodEnd(yesterday);
        //Check method deletes card if date is 24 hours in the past
        cardController.updateExpiredCards();

        Mockito.verify(cardRepository).delete(Mockito.any(Card.class));
    }

    @Test
    void testNotificationExistsButNotExpired() throws Exception {
        Mockito.when(cardRepository.findAllByDisplayPeriodEndBefore(Mockito.any(Date.class))).thenReturn(fakeExpiredCards);
        Mockito.when(notificationRepository.findNotificationByCardId(Mockito.anyLong())).thenReturn(notification);
        Date today = new Date();
        card.setDisplayPeriodEnd(today);
        //Check method does not delete card if date isn't 24 hours in the past
        cardController.updateExpiredCards();

        Mockito.verify(cardRepository, Mockito.never()).delete(Mockito.any(Card.class));
    }



    @Test
    void testPutNotificationViewStatus_notLoggedIn_returnUnauthorized() throws Exception {
        mvc.perform(put("/cards/notifications/{notificationId}", card.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_invalidViewStatusInput_returnBadRequest() throws Exception {
        mvc.perform(put("/cards/notifications/{notificationId}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Donda\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_notUserAndNotDgaa_returnForbidden() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(notification);
        mvc.perform(put("/cards/notifications/{notificationId}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_noNotificationWithId_returnNotAcceptable() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(null);
        mvc.perform(put("/cards/notifications/{notificationId}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_validRequest_returnOk() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(notification);
        mvc.perform(put("/cards/notifications/{notificationId}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testPutNotificationViewStatus_asGaa_returnOk() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(notification);
        anotherUser.setRole(Role.GAA);
        mvc.perform(put("/cards/notifications/{notificationId}", card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"viewStatus\": \"Read\"}")
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isOk());
    }

    // DELETE Extend Card Notification

    @Test
    void testDeleteExtendCardNotificationById_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(delete("/cards/notifications/{cardId}", card.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testDeleteExtendCardNotification_wrongCreatorId_returnForbidden() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(notification);

        mvc.perform(delete("/cards/notifications/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testDeleteExtendCardNotification_asCreator() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(notification);

        mvc.perform(delete("/cards/notifications/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testDeleteExtendCardNotification_badId_returnNotAcceptable() throws Exception {
        Mockito.when(notificationRepository.findNotificationByCardId(card.getId())).thenReturn(null);

        mvc.perform(delete("/cards/notifications/{cardId}", card.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, testUser))
                .andExpect(status().isNotAcceptable());
    }



}
