/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Declare all available services here
 */
import axios from 'axios'

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 10000
});


export default {
    /**
     * Attempt to authenticate a user account with a username and password
     * @param username The user's username input
     * @param password The user's password input
     * @param token Authentication token added to API request header
     * @returns {Promise<AxiosResponse<any>>}
     */
    login: (email, password) => instance.post('login', {email, password}, {withCredentials: true}),

    logout: () => instance.post('logoutuser', [], {withCredentials: true}),

    checkSession: () => instance.get('checksession', {withCredentials: true}),

    checkBusinessSession: () => instance.get('checkbusinesssession', {withCredentials: true}),

    // user POST create new user account data
    /**
     * Create a new user by storing their data to the database
     * @param firstName Their firstname
     * @param middleName Their middlename (OPTIONAL)
     * @param lastName Their lastname
     * @param nickname Their nickname (OPTIONAL)
     * @param bio Their bio (OPTIONAL)
     * @param email Their email
     * @param dateOfBirth Their date of birth
     * @param phoneNumber Their phone
     * @param homeAddress Their home address
     * @param password Their password hashed using password-hash library
     * @param registerDate Their registration date
     * @returns {Promise<AxiosResponse<any>>}
     */
    createUser: async(firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password) =>
        instance.post('users', {firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password}),

    /**
     * Modifies a user by setting their details to the ones sent here
     * @param userId user's ID
     * @param firstName user's new firstname
     * @param middleName user's new middlename
     * @param lastName user's new lastname
     * @param nickname user's new nickname
     * @param bio new bio
     * @param email new email
     * @param dateOfBirth new birthday
     * @param phoneNUmber new phone number
     * @param homeAddress new address
     * @param password current password
     * @param newPassword new password
     * @returns {Promise<AxiosResponse<any>>} 200 if the user is properly updated, 400 if the supplied data is bad,
     * 409 if the user is attempted to change their email to an email that is already taken,
     * 406 if the user id does not exist, and 401 if the user is not logged in.
     */
    modifyUser: async(userId, firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password, newPassword) =>
        instance.put(`/users/${userId}`, {firstName, middleName, lastName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password, newPassword},
            {withCredentials: true}),


    // ------ USER IMAGES

    /**
     * Create a new user image
     * @param id user id
     * @param image Image to save
     * @returns {Promise<AxiosResponse<any>>} Relevant status code and the newly created image entity in the body as a string
     */
    postUserImage: (id, image) => instance.post(`users/${id}/images`, image, {headers: {'Content-Type': 'multipart/form-data'}, withCredentials: true,}),

    /**
     * Update the primary image of a business, send a put request
     * @param businessId ID of the business
     * @param imageId Id of the image
     * @returns {Promise<AxiosResponse<any>>} Relevant status code
     */
    changeUserPrimaryImage: (userId, imageId) => instance.put(`users/${userId}/images/${imageId}/makeprimary`, {}, {withCredentials: true}),

    /**
     * Delete the business image
     * @param businessId Id of the business whos image is deleted
     * @param imageId id of the image to delete
     * @returns {Promise<AxiosResponse<any>>}
     */
    deleteUserImage: (userId, imageId) => instance.delete(`/users/${userId}/images/${imageId}`, {withCredentials: true}),

    /**
     * Get a specific user via their unique ID number
     * @param userId The user's unique ID number
     * @returns {Promise<AxiosResponse<any>>}
     */
    getUserFromID: (userId) => instance.get(`users/${userId}`, {withCredentials: true}),

    /**
     * Get all users from the database.
     * @returns {Promise<AxiosResponse<any>>}
     */
    getAll: () => instance.get('Users', {
        transformResponse: [function (data) {
            return data? JSON.parse(data)._embedded.students : data;
        }]
    }),

    /**
     * Query search results that uses searchQuery function
     * @returns {Promise<AxiosResponse<any>>}
     */
    searchUsersQuery: (query, pageNum, sortString) => instance.get(`/users/search`,{params: {searchQuery: query, pageNum: pageNum, sortString: sortString}, withCredentials: true}),

    /**
     *  Query search that returns businesses based on the parameter query
     * @param query to help narrow down the businesses
     * @param type String that contains the business type, if the type does not exist, the backend will ignore it.
     * @param page
     * @param sortString
     * @returns {*}
     */
    searchBusinessesWithTypeQuery: (query, type, page, sortString) => instance.get('/businesses/search', {params: {query: query, type: type, page: page, sortString: sortString}, withCredentials: true}),

    /**
     * Method (frontend) to let a DGAA user make a user an GAA admin user.
     * @param id user id to be made admin.
     */
    makeUserAdmin: async(id) =>
        instance.put('/users/'+id+'/makeAdmin',{},{withCredentials: true}),

    /**
     * Method (frontend) to let a DGAA user revoke GAA admin status from another user. Reverts the user back to USER role.
     * @param id user id to revoke admin user role.
     */
    revokeUserAdmin: async(id) =>
        instance.put('/users/'+id+'/revokeAdmin',{}, {withCredentials: true}),

    // ------ BUSINESS IMAGES

    /**
     * Create a new business image
     * @param id Business's id
     * @param image Image to save
     * @returns {Promise<AxiosResponse<any>>} Relevant status code and the newly created image entity in the body as a string
     */
    postBusinessImage: (id, image) => instance.post(`businesses/${id}/images`, image, {headers: {'Content-Type': 'multipart/form-data'}, withCredentials: true,}),

    /**
     * Update the primary image of a business, send a put request
     * @param businessId ID of the business
     * @param imageId Id of the image
     * @returns {Promise<AxiosResponse<any>>} Relevant status code
     */
    changeBusinessPrimaryImage: (businessId, imageId) => instance.put(`businesses/${businessId}/images/${imageId}/makeprimary`, {}, {withCredentials: true}),

    /**
     * Delete the business image
     * @param businessId Id of the business whos image is deleted
     * @param imageId id of the image to delete
     * @returns {Promise<AxiosResponse<any>>}
     */
    deleteBusinessImage: (businessId, imageId) => instance.delete(`/businesses/${businessId}/images/${imageId}`, {withCredentials: true}),

    // ------ BUSINESSES

    /**
     * Create a new business by storing their data in the database
     * @param name business name
     * @param description business description
     * @param address business address
     * @param businessType business type
     */
    createBusiness: async(name, description, address, businessType) =>
        instance.post('businesses', {name, description, address, businessType}, {withCredentials: true}),

    /**
     * Retrieve a single business with their unique id.
     * @param businessId the unique id of the business.
     */
    actAsBusiness: async(businessId) =>
        instance.post('actasbusiness',{businessId}, {withCredentials: true}),

    /**
     * Retrieve a single business with their unique id.
     * @param businessId the unique id of the business.
     * @returns {Promise<AxiosResponse<any>>} a business json containing relevant information.
     */
    getBusinessFromId: (businessId) => instance.get(`businesses/${businessId}`, {withCredentials: true}),

    /**
     * Put request to make a user a business administrator (not primary).
     * @param businessId unique identifier of the business.
     * @param userId unique identifier of the user.
     * @returns {Promise<AxiosResponse<any>>} a response with a OK if it is successful.
     */
    makeUserBusinessAdmin: (businessId, userId) => instance.put(`businesses/${businessId}/makeAdministrator`, {userId}, {withCredentials: true}),

    /**
     * Put request to remove administrator rights to a business.
     * @param businessId business identifier to remove rights to.
     * @param userId user identifier to remove the rights from.
     * @returns {Promise<AxiosResponse<any>>} a response with appropriate status code.
     */
    removeUserAsBusinessAdmin: (businessId, userId) => instance.put(`businesses/${businessId}/removeAdministrator`, {userId}, {withCredentials: true}),

    /**
     * Create a new product.
     * @param businessId id of the business to
     * @param id product id (chosen by user)
     * @param name product name
     * @param description product description
     * @param manufacturer manufacturer of this product.
     * @param recommendedRetailPrice product recommended retail price in their local currency
     */
    createProduct: async(businessId, id, name, description, manufacturer, recommendedRetailPrice) =>
        instance.post(`/businesses/${businessId}/products`, {businessId, id, name, description, manufacturer, recommendedRetailPrice}, {withCredentials: true}),

    /**
     * Adds a new inventory to the current business
     * @param businessId id of the business to
     * @param productId product id (chosen by user)
     * @param quantity quantity of the product
     * @param pricePerItem price per one item
     * @param totalPrice total price (It is not pricePerItem times some number)
     * @param manufactured Manufacture datee
     * @param sellBy To be sold beforee this date
     * @param bestBefore To be consumed before this date
     * @param expires product will expire on this date
     */
    createInventory: async(businessId, productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore, expires) =>
        instance.post(`/businesses/${businessId}/inventory`, {productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore, expires}, {withCredentials: true}),

    /**
     * Obtains the inventory of the said business from the database
     * @param businessId the id of the said business
     */
    getInventory: async(businessId) =>
        instance.get(`/businesses/${businessId}/inventory`, {withCredentials: true}),

    /**
     * Adds a new listing for the said business' marketplace
     * @param businessId the id of the said business
     * @param inventoryItemId the id of the inventory item
     * @param quantity the quantity of the products
     * @param price the total price of the listing
     * @param moreInfo OPTIONAL: Info about listing
     * @param closes Closing date
     */
    createListing: async(businessId, inventoryItemId, quantity, price, moreInfo, closes) =>
        instance.post(`/businesses/${businessId}/listings`, {inventoryItemId, quantity, price, moreInfo, closes}, {withCredentials: true}),

    /**
     * Adds a new inventory to the current business
     * @param businessId id of the business to
     * @param productId product id (chosen by user)
     * @param quantity quantity of the product
     * @param pricePerItem price per one item
     * @param totalPrice total price (It is not pricePerItem times some number)
     * @param manufactured Manufacture datee
     * @param sellBy To be sold beforee this date
     * @param bestBefore To be consumed before this date
     * @param expires product will expire on this date
     * @returns {Promise<*>}
     */
    modifyInventory: async(businessId, inventoryId, productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore, expires) =>
        instance.put(`/businesses/${businessId}/inventory/${inventoryId}`, {productId, quantity, pricePerItem, totalPrice, manufactured, sellBy, bestBefore, expires}, {withCredentials: true}),

    /**
     * modifies catalog product
     * @param id product id (chosen by user)
     * @param name product name
     * @param description product description
     * @param manufacturer manufacturer of this product.
     * @param recommendedRetailPrice product recommended retail price in their local currency
     */
    modifyProduct: async (businessId, productId, id, name, description, manufacturer, recommendedRetailPrice) =>
        instance.put(`/businesses/${businessId}/products/${productId}`, {businessId, productId, id, name, description, manufacturer, recommendedRetailPrice}, {withCredentials: true}),

    /**
     * Get request to return products owned by a business.
     * @param businessId
     * @returns {Promise<AxiosResponse<any>>}
     */
    getBusinessProducts: (businessId) => instance.get(`businesses/${businessId}/products`,  {withCredentials: true}),

    /**
     * Post request to send a product image
     * @param businessId business identifier to remove rights to.
     * @param productId product identifier, product that the image is for
     * @param image FormData object containing image file to be sent to server
     * @returns {Promise<AxiosResponse<any>>} a response with appropriate status code.
     */
    postProductImage: (businessId, productId, image) => instance.post(`businesses/${businessId}/products/${productId}/images`, image, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true,
    }),

    /**
     * Put request to set the primary image from the existing images for a product.
     * @param businessId
     * @param productId
     * @param imageId
     * @returns {Promise<AxiosResponse<any>>} A response with appropriate status code.
     */
    setPrimaryImage: (businessId, productId, imageId) => instance.put(`businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`, "", {withCredentials: true}),

    /**
     * Delete request to remove product image.
     * @param businessId
     * @param productId
     * @param imageId
     * @returns {Promise<AxiosResponse<any>>} A response with appropriate status code.
     */
    deletePrimaryImage: (businessId, productId, imageId) => instance.delete(`businesses/${businessId}/products/${productId}/images/${imageId}`, {withCredentials: true}),

    // === BUSINESS INVENTORY LISTINGS

    /**
     * Retrieves a business' sale listings.
     * @param businessId Id of business.
     * @returns {Promise<AxiosResponse<any>>} 200 with (a potentially empty) array of listings. 401, 406 otherwise.
     */
    getBusinessListings: (businessId) => instance.get(`/businesses/${businessId}/listings`, {withCredentials: true}),

    /**
     * Retrieves a business' inventory data.
     * @param businessId the unique id of the business.
     * @returns {Promise<AxiosResponse<any>>} 200 with (a potentially empty) array of listings. 401, 403, 406 otherwise.
     */
    getBusinessInventory: (businessId) => instance.get(`/businesses/${businessId}/inventory`, {withCredentials: true}),

    // === MARKETPLACE CARDS

    /**
     * Retrieves community marketplace cards from a given section.
     * @param section the name of the section to retrieve the cards from.
     * @param sortBy The expected values are CREATED, TITLE, COUNTRY, and KEYWORDS.
     *               Where the cards will be sorted by one of the four attributes, and the default is by CREATED.
     * @param reverse Default is false, which will return the order of cards in ascending order.
     Otherwise, will return them in descending order.
     * @returns {Promise<AxiosResponse<any>>} 200 with (a potentially empty) array of cards. 400, 401 otherwise.
     */
    getCardsBySection: (section, pageNum, resultsPerPage, sortBy, reverse) => instance.get(`/cards`,
        {params:{section: section,
                pageNum: pageNum,
                resultsPerPage: resultsPerPage,
                sortBy: sortBy,
                reverse: reverse}, withCredentials: true}),

    /**
     * Deletes a community marketplace card.
     * @param cardId the id of the card to delete.
     * @returns {Promise<AxiosResponse<any>>} 200 with (a potentially empty) array of cards. 400, 401 otherwise.
     */
    deleteCard: (cardId) => instance.delete(`/cards/${cardId}`, {withCredentials: true}),

    /**
     * Gets the user's cards
     * @param userId
     * @returns {Promise<AxiosResponse<any>>} 200 with (a potentially empty) array of cards. 400, 401 otherwise.
     */
    getUserCards: (userId) => instance.get(`/users/${userId}/cards`, {withCredentials: true}),

    /**
     * Creates a new card. of type:
     * (long creatorId, String title, String description, String keywords, MarketplaceSection section)
     *
     * @param newCardRequest
     * @param creatorId     user's id
     * @param title         card title
     * @param description   card description
     * @param keywords      keywords to describe the card (functionality added later)
     * @param section       marketplace section
     *
     * @returns {Promise<AxiosResponse<any>>} A response with appropriate status code:
     * 401 if not logged in, 403 if creatorId, session user Id do not match or if not a D/GAA,
     * 400 if there are errors with data, 201 otherwise
     */

    createCard: async(creatorId, title, description, keywords, section) =>
        instance.post('/cards', {creatorId, title, description, keywords, section}, {withCredentials: true}),



    /**
     * Extends card display period by 24 hours (from current time)
     * @param cardId card that is going to be extended
     * @returns {Promise<AxiosResponse<any>>}:
     *  401 if no auth, 403 if not users card, 406 if bad ID, 200 if successful
     */
    extendCardDisplayPeriod: (cardId) => instance.put(`/cards/${cardId}/extenddisplayperiod`, {}, {withCredentials: true}),



    /**
     * Gets users notifications, which can contain a deleted or expiring notification
     * @param userId ID of user we want notifications for
     * @returns {Promise<AxiosResponse<any>>}:
     *  401 if no auth, 403 if not user, 406 if bad ID, 200 if successful
     */
    getNotifications: (userId) => instance.get(`/users/${userId}/cards/notifications`, {withCredentials: true}),


    /**
     * Deletes a message with ID
     * If the user is not the recipient, they cannot delete it.
     *
     * @param messageId Id of message to be deleted
     * @returns {Promise<AxiosResponse<any>>} A response with status code:
     *      * 401 if not logged in, 403 if the session user is not a D/GAA or the message recipient,
     * 400 if there are errors with data, 201 otherwise
     */
    deleteMessage: (messageId) => instance.delete(`/messages/${messageId}`, {withCredentials: true}),

    /**
     * Modifies a selected card. of type:
     * (long creatorId, String title, String description, String keywords, MarketplaceSection section)
     *
     * @param newCardRequest (same details for modifying)
     * @param creatorId     user's id
     * @param title         card title
     * @param description   card description
     * @param keywords      keywords to describe the card (functionality added later)
     * @param section       marketplace section
     *
     * @returns {Promise<AxiosResponse<any>>} A response with appropriate status code:
     * 401 if not logged in, 403 if creatorId, session user Id do not match or if not a D/GAA,
     * 400 if there are errors with data, 201 otherwise
     */
    modifyCard: async(cardId, creatorId, title, description, keywords, section) =>
        instance.put(`/cards/${cardId}`, {creatorId, title, description, keywords, section}, {withCredentials: true}),


    /**
     *
     * @param userId        The intended recipient of the message
     * @param cardId        Id of the card the message relates to
     * @param description   Message content
     * @returns {Promise<messageId<any>>}   The ID of the created message
     *
     */
    postMessage: async(userId, cardId, description) =>
        instance.post(`/users/${userId}/messages`, {cardId, description}, {withCredentials: true}),

    /**
     * Get router endpoint for retrieving a users messages
     * @param userId
     * @returns {Promise<AxiosResponse<any>>} Messages of the user
     */
    getMessages: (userId) => instance.get(`/users/${userId}/messages`, { withCredentials: true }),

    // === LISTING NOTIFICATIONS

    /**
     * GET all notifications relating to listings.
     * @param businessId
     * @param listingId
     * @param userId
     * @returns {Promise<AxiosResponse<any>>}
     */
    getListingNotifications: (userId) => instance.get(`/users/${userId}/notifications`, { withCredentials: true }),

    /**
     * Post api endpoint to post listing notification for particular listing
     * Status of the listing is set to bought
     * @param listingId ID of the listing
     * @returns {Promise<AxiosResponse<any>>}
     *          201 if created
     *          400 if bad request
     *          401 if not logged in
     *          406 if business, user or listing are invalid
     */
    postListingNotification: async(listingId) =>
        instance.post(`/listings/${listingId}/notify`, {}, {withCredentials: true}),

    /**
     *
     * @param listingId
     * @returns {Promise<AxiosResponse<any>>}
     */
    deleteListing: (listingId) => instance.delete(`/businesses/listings/${listingId}`, {withCredentials: true}),

    /**
     * POST endpoint - Adds a new like to a business sale listing.
     * @param id of the sale listing to add a new like to.
     * @param session the current active user session.
     * @return 401 if unauthorized, 406 if the listing does not exist, 201 otherwise.
     */
    addLikeToListing: (listingId) =>
        instance.post(`/businesses/listings/${listingId}/like`, {}, {withCredentials: true}),

    /**
     * Retrieves and returns a list of LISTINGS that the user has liked.
     * @param id unique identifier of the user.
     * @param session current active user session.
     * @return 401 if unauthorized, 403 if forbidden (not dgaa or correct user), 406 if the user does not exist.
     * 200 otherwise, may return an empty list (because the user has not liked anything).
     */
    getUserLikedListings: (id) => instance.get(`/users/${id}/likes`, {withCredentials: true}),

    /**
     * Removes a user's like from a sale listing (unlikes it).
     * @param id the unique identifier of the sale listing to unlike.
     * @param session the current user session - used to figure out who is doing the unliking.
     * @return 401 if unauthorized, 400 if it wasn't liked already, 406 if the listing doesn't exist, 200 otherwise.
     */
    removeLikeFromListing: (id) =>
        instance.delete(`/businesses/listings/${id}/like`, {withCredentials: true}),

    /**
     * Query search results that uses searchQuery function
     * @returns {Promise<AxiosResponse<any>>}
     */
    filterListingsQuery: async(businessQuery, productQuery, addressQuery, sortBy, businessTypes,
                               minPrice, maxPrice, minClosingDate, maxClosingDate, count, offset, sortDirection) =>
        instance.post('/businesses/listings', {businessQuery, productQuery, addressQuery, sortBy, businessTypes, minPrice, maxPrice, minClosingDate, maxClosingDate},
            {params: {count: count, offset: offset, sortDirection: sortDirection}, withCredentials: true }),


    /**
     * Get all business types
     * @returns 401 if unauthorized, 200 if authenticated and requested correct endpoint
     */
    getBusinessTypes: () => instance.get('/businesses/types', {withCredentials: true}),


    /**
     * gets business notifications
     * @param business ID
     * @return 401 if unauthorized, 400 if it wasn't liked already, 406 if the listing doesn't exist, 200 otherwise.
     */
    getBusinessListingNotifications: (businessId) =>
        instance.get(`/businesses/${businessId}/notifications`, {withCredentials: true}),

    /**
     * Deletes listing notification
     * @param listingId
     * @returns {Promise<AxiosResponse<any>>}
     */
    deleteListingNotification: (listingId) =>
        instance.delete(`/notifications/${listingId}`, {withCredentials: true}),

    /**
     * Deletes listing notification
     * @param listingId
     * @returns {Promise<AxiosResponse<any>>}
     */
    deleteCardExpiredNotification: (cardId) =>
        instance.delete(`/cards/notifications/${cardId}`, {withCredentials: true}),

    /**
     * Retrieves and returns a list of BUSINESSES that the user has wishlisted.
     * @param id unique identifier of the user.
     * @param session current active user session.
     * @return 401 if unauthorized, 406 if the user does not exist.
     * 200 otherwise, may return an empty list (because the user has nothing in their item wishlist).
     */
    getUsersWishlistedBusinesses: (userId) => instance.get(`/users/${userId}/wishlist`, {withCredentials: true}),
    /**
     * Retrieves the user's business wishlist
     * @param userId id of the user
     * @returns {Promise<AxiosResponse<any>>} 400 if there was a problem with the data supplied,
     * 401 if unauthed, 406 if the user does not exist, 200 otherwise.
     */
    getUserBusinessWishlist: (userId) => instance.get(`/users/${userId}/wishlist`, {withCredentials: true}),

    /**
     * Adds business to user's wishlist, creating a wishlistItem object
     * @param businessId
     * @returns {Promise<AxiosResponse<any>>}
     */
    addBusinessToWishlist: (businessId) =>
        instance.post(`/businesses/${businessId}/wishlist`, {}, {withCredentials: true}),

    /**
     * Updates whether the wishlisted business is muted or not
     * @param wishlistId id of the wishlist relationship object
     * @param mutedStatus true or false, muted or not
     * @returns {Promise<AxiosResponse<any>>}
     */
    updateWishlistMuteStatus: (wishlistId, mutedStatus) =>
        instance.put(`/wishlist/${wishlistId}`, {mutedStatus: mutedStatus}, {withCredentials: true}),

    /**
     * Removes business from user's wishlist by removing the relevant wishlistItem object
     * @param wishlistItemId
     * @returns {Promise<AxiosResponse<any>>}
     */
    removeBusinessFromWishlist: (wishlistItemId) =>
        instance.delete(`/wishlist/${wishlistItemId}`, {withCredentials: true}),

    /**
     * Returns a list of all the business' sales.
     * @param businessIdupdateListingNotificationViewStatus
     * @return {Promise<AxiosResponse<any>>}
     */
    getBusinessSales: (businessId) => instance.get(`/businesses/${businessId}/sales`, {withCredentials: true}),

    /**
     * Updates the view status of a listing notification.
     * @param notificationId the unique id of the notification
     * @returns {Promise<AxiosResponse<any>>} 400 if request value is invalid, 401 if unauthorized,
     * 403 if the notification does not belong to the current user, 406 if the notification id does not exist.
     */
    updateListingNotificationViewStatus: (notificationId, status) => instance.put(`/notifications/${notificationId}`, {viewStatus: status}, {withCredentials: true}),

    /**
     * Updates the view status of a message notification.
     * @param messageId the unique id of the message
     * @returns {Promise<AxiosResponse<any>>} 400 if request value is invalid, 401 if unauthorized,
     * 403 if the message does not belong to the current user, 406 if the message id does not exist.
     */
    updateMessageViewStatus: (messageId, status) => instance.put(`/messages/${messageId}`, {viewStatus: status}, {withCredentials: true}),

    /**
     * Updates the view status of a marketplace card notification.
     * @param notificationId the unique id of the notification
     * @returns {Promise<AxiosResponse<any>>} 400 if request value is invalid, 401 if unauthorized,
     * 403 if the notification does not belong to the current user, 406 if the notification id does not exist.
     */
    updateNotificationViewStatus: (notificationId, status) => instance.put(`/cards/notifications/${notificationId}`, {viewStatus: status}, {withCredentials: true}),

    /**
     * Updates the business
     * @returns {Promise<AxiosResponse<any>>} 400 if request value is invalid, 401 if unauthorized,
     * 403 if the notification does not belong to the current user, 406 if the notification id does not exist.
     * @param name
     * @param description
     * @param streetNumber
     * @param streetName
     * @param suburb
     * @param city
     * @param region
     * @param country
     * @param postcode
     * @param businessType
     * @param id
     */
    updateBusiness: (name, description, streetNumber, streetName, suburb, city, region, country, postcode, businessType, id) => instance.put(`/businesses/${id}/modify`, {name, description,
        address: {streetNumber, streetName, suburb, city, region, country, postcode}, businessType}, {withCredentials: true})

}
