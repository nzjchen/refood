package org.seng302.steps;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.controllers.ListingController;
import org.seng302.controllers.ListingNotificationController;
import org.seng302.controllers.WishlistItemController;
import org.seng302.models.*;
import org.seng302.models.requests.NewListingRequest;
import org.seng302.repositories.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;


public class wishlistNotificationStepDefs {
    @MockBean
    private WishlistItemController wishlistItemController;

    @InjectMocks
    private ListingController listingController;

    @InjectMocks
    private ListingNotificationController listingNotificationController;

    @MockBean
    private ListingRepository listingRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private WishlistItemRepository wishlistItemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InventoryRepository inventoryRepository;

    @MockBean
    private ListingLikeRepository listingLikeRepository;

    @MockBean
    private BoughtListingRepository boughtListingRepository;

    @MockBean
    private ListingNotificationRepository listingNotificationRepository;

    private ObjectMapper mapper;

    private User testUser1;
    private User testUser2;
    private Business testBusiness;
    private Address user1Address;
    private Address user2Address;
    private Address businessAddress;
    private Product product;
    private Inventory inventoryItem;
    private Listing newListing;
    private WishlistItem wishlistItem2;
    private HttpSession session;
    private ResponseEntity<String> wishListResult1;
    private ResponseEntity<String> listingResult;
    private ResponseEntity<List<ListingNotification>> notificationResult1;
    private ResponseEntity<List<ListingNotification>> notificationResult2;
    private Date laterDate;


    @Before
    public void setup() throws NoSuchAlgorithmException {
        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.set(2022, Calendar.JANUARY, 1);
        laterDate = afterCalendar.getTime();
        Calendar beforeCalendar = Calendar.getInstance();
        afterCalendar.set(2020, Calendar.JANUARY, 1);
        Date beforeDate = beforeCalendar.getTime();
        user1Address = new Address("143", "Smiths Lane", "Ohio", "Ohio", "America", "10054");
        user2Address = new Address("0", "Central Park", "New York City", "New York", "America", "10055");
        businessAddress = new Address("1", "Central Park", "New York City", "New York", "America", "10055");
        testUser1 = new User("Lucas", "Beineke", user1Address, "lucasiscool2001@addams.com", "i<3Wednesday", Role.USER);
        testUser2 = new User("Wednesday", "Addams", user2Address, "pulled@addams.com", "lucasiscool123!", Role.USER);
        testBusiness = new Business("Morticians", "Death is Just Around the Corner :)", businessAddress, BusinessType.HEALTH_CARE_AND_SOCIAL_ASSISTANCE);
        product = new Product("666", testBusiness, "Dead Body", "This is a dead body", "The Addams Family", 69.69, new Date());
        inventoryItem = new Inventory("666", 1L, 4, 69.69, 219.99, beforeDate, laterDate, laterDate, laterDate);
        wishlistItem2 = new WishlistItem(testUser2.getId(), testBusiness);
        Set<User> admins = new HashSet<>();
        admins.add(testUser1);
        testBusiness.setAdministrators(admins);
        ListingNotification listingNotification1 = new ListingNotification(testUser1, newListing, NotificationStatus.WISHLIST);
        ListingNotification listingNotification2 = new ListingNotification(testUser2, newListing, NotificationStatus.WISHLIST);
        List<ListingNotification> notifs1 = new ArrayList<>();
        List<ListingNotification> notifs2 = new ArrayList<>();
        notifs1.add(listingNotification1);
        notifs2.add(listingNotification2);


        session = Mockito.mock(HttpSession.class);
        businessRepository = Mockito.mock(BusinessRepository.class);
        wishlistItemRepository = Mockito.mock(WishlistItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        listingRepository = Mockito.mock(ListingRepository.class);
        inventoryRepository = Mockito.mock(InventoryRepository.class);
        listingLikeRepository = Mockito.mock(ListingLikeRepository.class);
        boughtListingRepository = Mockito.mock(BoughtListingRepository.class);
        listingNotificationRepository = Mockito.mock(ListingNotificationRepository.class);
        wishlistItemRepository = Mockito.mock(WishlistItemRepository.class);
        mapper = new ObjectMapper();

        wishlistItemController = new WishlistItemController(userRepository, businessRepository, wishlistItemRepository);
        listingController = new ListingController(listingRepository, businessRepository, userRepository,
                inventoryRepository, listingLikeRepository, boughtListingRepository, listingNotificationRepository,
                wishlistItemRepository, mapper);
        listingNotificationController = new ListingNotificationController(listingRepository, businessRepository,
                userRepository, listingLikeRepository, boughtListingRepository, listingNotificationRepository);

        Mockito.when(businessRepository.findBusinessById(testBusiness.getId())).thenReturn(testBusiness);
        Mockito.when(session.getAttribute(User.USER_SESSION_ATTRIBUTE)).thenReturn(testUser1);
        Mockito.when(inventoryRepository.findInventoryById(1)).thenReturn(inventoryItem);
        Mockito.when(userRepository.findUserById(testUser1.getId())).thenReturn(testUser1);
        Mockito.when(userRepository.findUserById(testUser2.getId())).thenReturn(testUser2);
        Mockito.when(listingNotificationRepository.findListingNotificationsByUserId(testUser1.getId())).thenReturn(notifs1);
        Mockito.when(listingNotificationRepository.findListingNotificationsByUserId(testUser2.getId())).thenReturn(notifs2);
        wishListResult1 = wishlistItemController.wishlistBusiness(testBusiness.getId(), session);

    }

    @Given("I have a business wishlisted")
    public void iHaveABusinessWishlisted() {
        assertThat(wishListResult1.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
    }

    @When("The business posts a new listing")
    public void businessPostsNewListing() {
        NewListingRequest listingRequest = new NewListingRequest(1, 4, 219.99,
                "Just dead bodies", laterDate);
        listingResult = listingController.createListing(testBusiness.getId(), listingRequest, session);
        assertThat(listingResult.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("I, and all other users who have the business wishlisted, receive a notification")
    public void iReceiveANotification() {
        notificationResult1 = listingNotificationController.getUserListingNotifications(testUser1.getId(), session);
        notificationResult2 = listingNotificationController.getUserListingNotifications(testUser2.getId(), session);
        JSONArray notificationBody1 = new JSONArray(notificationResult1.getBody());
        JSONArray notificationBody2 = new JSONArray(notificationResult2.getBody());
        assertThat(notificationBody1.length()).isEqualTo(1);
        assertThat(notificationBody2.length()).isEqualTo(1);
    }
}
