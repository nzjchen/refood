package org.seng302.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.seng302.models.*;
import org.seng302.models.requests.UpdateNotificationViewStatusRequest;
import org.seng302.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class with endpoints for adding or deleting notifications for listings.
 */
@RestController
public class ListingNotificationController {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingLikeRepository listingLikeRepository;

    @Autowired
    private ListingNotificationRepository listingNotificationRepository;

    @Autowired
    private BoughtListingRepository boughtListingRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;


    public ListingNotificationController(ListingRepository listingRepository, BusinessRepository businessRepository,
                             UserRepository userRepository, ListingLikeRepository listingLikeRepository,
                             BoughtListingRepository boughtListingRepository, ListingNotificationRepository listingNotificationRepository) {
        this.listingRepository = listingRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.listingLikeRepository = listingLikeRepository;
        this.boughtListingRepository = boughtListingRepository;
        this.listingNotificationRepository = listingNotificationRepository;
    }


    /**
     * Endpoint for creating a notification for a listing that's just been purchased.
     * @param listingId ID of the listing
     * @return 201 if successful, 406 if listing or receiver are null
     */
    @PostMapping("/listings/{listingId}/notify")
    public ResponseEntity<String> addNotificationToListing(@PathVariable("listingId") long listingId,
                                                           HttpSession session) {
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        Listing listing = listingRepository.findListingById(listingId);
        if (listing == null || currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        Inventory inventory = listing.getInventoryItem();
        Product product = productRepository.findProductById(inventory.getProductId());
        BoughtListing boughtListing = new BoughtListing(currentUser, product, listing.getLikes(), listing.getQuantity(), listing.getCreated(), listing.getId(), listing.getPrice());
        boughtListingRepository.save(boughtListing);
        NotificationStatus status = NotificationStatus.BOUGHT;
        Business business = businessRepository.findBusinessById(boughtListing.getProduct().getBusinessId());
        List<ListingLike> userLikes = listingLikeRepository.findListingLikeByListingId(listingId);
        List<User> receivers = userLikes.stream().map(ListingLike::getUser).collect(Collectors.toList());

        for (User receiver : receivers) {
            if (receiver.getId() != currentUser.getId()) {
                ListingNotification listingNotification = new ListingNotification(receiver, boughtListing, status);
                listingNotificationRepository.save(listingNotification);
            }
        }
        ListingNotification listingNotification = new ListingNotification(currentUser, business, boughtListing, status);
        listingNotificationRepository.save(listingNotification);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint for retrieving a list of a user's listing notifications
     * @param userId ID of the user
     * @param session the current active user session
     * @return 200 if successful with a list of listing notifications
     * @throws JsonProcessingException when json mapping object to a json string fails unexpectedly
     */
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<ListingNotification>> getUserListingNotifications(@PathVariable("userId") long userId, HttpSession session) {
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        User user = userRepository.findUserById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        if (currentUser.getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ListingNotification> listingNotifications = listingNotificationRepository.findListingNotificationsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(listingNotifications);
    }

    /**
     * Endpoint for retrieving a list of a business's listing notifications
     * @param businessId Business' ID
     * @return 200 if successful with a list of listing notifications
     */
    @GetMapping("/businesses/{businessId}/notifications")
    public ResponseEntity<List<ListingNotification>> getBusinessListingNotifications(@PathVariable("businessId") long businessId) {
        Business business = businessRepository.findBusinessById(businessId);

        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        List<ListingNotification> listingNotifications = listingNotificationRepository.findListingNotificationByBusinessId(businessId);
        return ResponseEntity.status(HttpStatus.OK).body(listingNotifications);
    }


    /**
     * Updates the view status of a listing notification.
     * @param notificationId unique id of the notification.
     * @param request dto holding the new view status.
     * @param session current user login session.
     * @return 400 if request value is invalid (handled by spring), 401 if unauthorized (spring sec),
     * 403 if the notification does not belong to the current user, 406 if the notification id does not exist.
     */
    @PutMapping("/notifications/{notificationId}")
    public ResponseEntity<String> updateListingNotificationViewStatus(@PathVariable long notificationId,
                                                                      @RequestBody UpdateNotificationViewStatusRequest request,
                                                                      HttpSession session) {
        ListingNotification notification = listingNotificationRepository.findListingNotificationById(notificationId);

        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (user.getId() != notification.getUser().getId() && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        notification.setViewStatus(request.getViewStatus());
        listingNotificationRepository.save(notification);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

  /**
   * Endpoint for deleting a listing notification given the notifications id
   *
   * @param id id of the notification
   * @param session current user login session.
   * @return 200 if successful, 400 if request value is invalid (handled by spring), 401 if unauthorized (spring sec),
   *     403 if the notification does not belong to the current user, 406 if the notification id does not exist.
   */
  @DeleteMapping("/notifications/{notificationId}")
  public ResponseEntity<String> deleteListingNotification(
      @PathVariable("notificationId") long id, HttpSession session) {
    ListingNotification notification = listingNotificationRepository.findListingNotificationById(id);
    if (notification == null) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
    User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
    User user = notification.getUser();
    if (currentUser.getId() != user.getId()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    listingNotificationRepository.delete(notification);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
