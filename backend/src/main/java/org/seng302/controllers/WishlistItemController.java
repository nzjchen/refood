package org.seng302.controllers;

import org.seng302.models.*;
import org.seng302.models.requests.MutedStatusRequest;
import org.seng302.repositories.*;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Controlled class containing restful api calls regarding users and their wish listed businesses.
 */
@RestController
public class WishlistItemController {

    @Autowired
    WishlistItemRepository wishlistItemRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public WishlistItemController(UserRepository userRepository, BusinessRepository businessRepository, WishlistItemRepository wishlistItemRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    /**
     * GET request called to get the businesses on a users wishlist. Error response returned when user is not
     * logged in
     * @param session logged in users session
     * @param id Users id
     * @return ResponseEntity with relevant http status
     */
    @GetMapping("/users/{id}/wishlist")
    public ResponseEntity<List<WishlistItem>> getWishlistBusiness(@PathVariable long id, HttpSession session) {
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        User user = userRepository.findUserById(id);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        Sort sort = Sort.by(Sort.Order.asc("business.name").ignoreCase());
        List<WishlistItem> wishlistItems = wishlistItemRepository.findWishlistItemsByUserId(id, sort);
        for (WishlistItem wish:wishlistItems) {
            wish.getBusiness().setAdministrators(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(wishlistItems);
    }

    /**
     * Post request called when a user attempts to add a business to their wishlist. Error response returned when attempting
     * to wishlist a business with id not found in the repository.
     * @param businessId Id of the business to wishlist
     * @param session user's logged in session
     * @return ResponseEntity with relevant http status
     */
    @PostMapping("/businesses/{businessId}/wishlist")
    public ResponseEntity<String> wishlistBusiness(@PathVariable long businessId, HttpSession session) {
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        long userId = user.getId();
        Business business = businessRepository.findBusinessById(businessId);
        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        WishlistItem wishlistItem = new WishlistItem(userId, business);
        wishlistItemRepository.save(wishlistItem);
        return ResponseEntity.status(HttpStatus.CREATED).body("Business added to wishlist");
    }

    /**
     * Updates the mutedStatus of a wishlist item
     * @param mutedStatusRequest muted status JSON body to update the wishlist item to
     * @param id id of the wishlist item
     * @param session user's logged in session
     * @return 200 if successful,
     *         400 if request is bad (JSON body is bad, ID is bad or session is bad),
     *         401 if not logged in,
     *         403 if user tries to delete a wishlist item that they are not the owner of AND the user is not a D/GAA,
     *         406 if requested route does exist (so not a 404) but some part of the request is not acceptable,
     *             for example trying to access a resource by an ID that does not exist
     */
    @PutMapping("/wishlist/{id}")
    public ResponseEntity<String> wishlistMutedStatusUpdate(@RequestBody MutedStatusRequest mutedStatusRequest, @PathVariable long id, HttpSession session) {
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        long userId = user.getId();
        MutedStatus mutedStatus = mutedStatusRequest.getMutedStatus();
        WishlistItem wishlist = wishlistItemRepository.findWishlistItemById(id);
        if (wishlist == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else if (wishlist.getUserId() != userId && user.getRole() == Role.USER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (mutedStatus == MutedStatus.MUTED) {
            wishlist.unmuteBusiness();
        } else {
            wishlist.muteBusiness();
        }
        wishlistItemRepository.save(wishlist);
        return ResponseEntity.status(HttpStatus.OK).body("Wishlist muted status changed to " + mutedStatus.toString());
    }

    /**
     * Delete request, to remove a business from a users wishlist
     * @param id Id of the wishlistItem object
     * @param session user's logged in session
     * @return ResponseEntity with relevant http status
     */
    @DeleteMapping("/wishlist/{id}")
    public ResponseEntity<String> removeBusinessFromWishlist(@PathVariable long id, HttpSession session) {
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        WishlistItem wishlistItem = wishlistItemRepository.findWishlistItemById(id);
        if (wishlistItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else if (wishlistItem.getUserId() != user.getId() && user.getRole() == Role.USER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            wishlistItemRepository.delete(wishlistItem);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
