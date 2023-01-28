package org.seng302.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.models.requests.UpdateNotificationViewStatusRequest;
import org.seng302.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ListingNotificationController.class)
@ContextConfiguration(classes = TestApplication.class)
class ListingNotificationControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @InjectMocks
    private ListingNotificationController listingLikeController;
    @MockBean
    private ListingRepository listingRepository;
    @MockBean
    private ListingLikeRepository listingLikeRepository;
    @MockBean
    private ListingNotificationRepository listingNotificationRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private BoughtListingRepository boughtListingRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private InventoryRepository inventoryRepository;

    private User user;
    private User otherUser;
    private Business business;
    private Product product1;
    private Product product2;
    private Listing listing;
    private Listing listing2;
    private BoughtListing boughtListing1;
    private BoughtListing boughtListing2;
    private ListingNotification notification;
    private ListingNotification notification2;
    private ListingNotification notification3;
    private List<ListingNotification> notificationList;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException {
        user = new User("Rayna", "YEP", "Dalgety", "Universal", "", "rdalgety3@ocn.ne.jp", "2006-03-30", "+7 684 622 5902", null, "ATQWJM");
        user.setId(1L);
        userRepository.save(user);

        otherUser = new User("Rayna", "YEP", "Dalgety", "Universal", "", "dalgety3@ocn.ne.jp", "2006-03-30", "+7 684 622 5902", null, "ATQWJM");
        otherUser.setId(2L);
        userRepository.save(otherUser);

        Address address = new Address("39", "Ilam Rd", "Christchurch", "Canterbury", "New Zealand", "8041");

        business = new Business("Howdy", "Partner", address, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1L);
        businessRepository.save(business);

        product1 = new Product("07-4957066", business, "Uno", "desc", "", 2.0, new Date());
        product2 = new Product("12-3456678", business, "Dos", "desc", "", 5.0, new Date());
        productRepository.save(product1);
        productRepository.save(product2);

        Inventory inventory = new Inventory("07-4957066", 1, 10, 2.0, 20.0, new Date(), new Date(), new Date(), new Date());
        Inventory inventory2 = new Inventory("12-3456678", 1, 10, 5.0, 50.0, new Date(), new Date(), new Date(), new Date());
        inventoryRepository.save(inventory);
        inventoryRepository.save(inventory2);

        listing = new Listing(inventory, 5, 2.0, "Seller may be interested in offers", new Date(), new Date());
        listing.setId(1L);
        listing2 = new Listing(inventory2, 5, 5.0, "Test more info", new Date(), new Date());
        listing2.setId(2L);
        boughtListing1 = new BoughtListing(user, product1, listing);
        boughtListing2 = new BoughtListing(user, product2, listing2);
        boughtListing1.setId(1L);
        boughtListing2.setId(2L);
        boughtListingRepository.save(boughtListing1);
        boughtListingRepository.save(boughtListing2);
        notification = new ListingNotification(user, boughtListing1, NotificationStatus.BOUGHT);
        notification.setUser(user);
        notification.setId(1);
        notification2 = new ListingNotification(user, business, boughtListing2, NotificationStatus.BOUGHT);
        notification3 = new ListingNotification(otherUser, business, boughtListing2, NotificationStatus.LIKED);

        notificationList = new ArrayList<>();
        notificationList.add(notification);
        notificationList.add(notification2);
        assertThat(notificationList.size()).isEqualTo(2);
    }

    //
    // POST - Listing Notification
    //

    /**
     * Tests that a user not logged in can't make the POST
     */
    @Test
    void testPostNewNotification_noAuth_returnUnauthorized() throws Exception {
        mvc.perform(post("/listings/{listingId}/notify", listing.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * An invalid listing returns 406 (not acceptable)
     */
    @Test
    @WithMockUser
    void testPostNewNotification_noExistingListing_returnNotAcceptable() throws Exception {
        Mockito.when(boughtListingRepository.findBoughtListingByListingId(listing.getId())).thenReturn(null);
        mvc.perform(post("/listings/{listingId}/notify", listing.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * A BoughtListing notification is created when endpoint called with valid user, listing and valid IDs
     */
    @Test
    @WithMockUser
    void testPostNewNotification_successfulNotification_returnCreated() throws Exception {
        Mockito.when(listingRepository.findListingById(listing.getId())).thenReturn(listing);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductById("07-4957066")).thenReturn(product1);
        mvc.perform(post("/listings/{listingId}/notify", listing.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isCreated());
    }

    //
    // GET - Listing Notification
    //
    @Test
    void testNoAuthGetUserListingNotifications() throws Exception {
        mvc.perform(get("/users/{userId}/notifications", user.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetUserNotifications_invalidUserId_returnNotAcceptable() throws Exception {
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(null);
        Mockito.when(listingNotificationRepository.findListingNotificationsByUserId(user.getId())).thenReturn(notificationList);
        mvc.perform(get("/users/{userId}/notifications", user.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testGetUserNotifications_successfulRetrieval_returnOk() throws Exception {
        Mockito.when(userRepository.findUserById(user.getId())).thenReturn(user);
        Mockito.when(listingNotificationRepository.findListingNotificationsByUserId(user.getId())).thenReturn(notificationList);
        mvc.perform(get("/users/{userId}/notifications", user.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testGetBusinessNotifications_invalidBusinessId_returnNotAcceptable() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(null);
        Mockito.when(listingNotificationRepository.findListingNotificationsByUserId(business.getId())).thenReturn(notificationList);
        mvc.perform(get("/businesses/{businessId}/notifications", business.getId()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void testGetBusinessNotifications_successfulRetrieval_returnOk() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(listingNotificationRepository.findListingNotificationByBusinessId(business.getId())).thenReturn(notificationList);
        mvc.perform(get("/businesses/{businessId}/notifications", business.getId()))
                .andExpect(status().isOk());
    }

    /**
     * Tests attempted deletion of a non-existent liked listing notification, should return Not Acceptable status
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testDeleteNonExistentListingNotification_returnNotAcceptable() throws Exception {
        Mockito.when(listingNotificationRepository.findListingNotificationById(1)).thenReturn(null);
        mvc.perform(delete("/notifications/{notificationId}", 1))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests attempted deletion of a liked listing notification that doesn't relate to the current user
     * should return Forbidden status
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testDeleteListingNotificationBelongingToAnotherUser_returnForbidden() throws Exception {
        Mockito.when(listingNotificationRepository.findListingNotificationById(3)).thenReturn(notification3);
        mvc.perform(delete("/notifications/{notificationId}", 3)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests successful deletion of a users liked listing notification
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testDeleteLikedListingNotification_returnOk() throws Exception {
        notification2.setStatus(NotificationStatus.LIKED);
        Mockito.when(listingNotificationRepository.findListingNotificationById(2)).thenReturn(notification2);
        mvc.perform(delete("/notifications/{notificationId}", 2)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
    }

    /**
     * Tests successful deletion of a users unliked listing notification
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testDeleteUnlikedListingNotification_returnOk() throws Exception {
        notification2.setStatus(NotificationStatus.UNLIKED);
        Mockito.when(listingNotificationRepository.findListingNotificationById(2)).thenReturn(notification2);
        mvc.perform(delete("/notifications/{notificationId}", 2)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
    }

    /**
     * Tests successful deletion of a users purchased listing notification
     * @throws Exception
     */
    @Test
    @WithMockUser
    void testDeletePurchasedListingNotification_returnOk() throws Exception {
        notification2.setStatus(NotificationStatus.BOUGHT);
        Mockito.when(listingNotificationRepository.findListingNotificationById(2)).thenReturn(notification2);
        mvc.perform(delete("/notifications/{notificationId}", 2)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
    }
}
