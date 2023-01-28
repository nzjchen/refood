package org.seng302.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.exceptions.InvalidImageExtensionException;
import org.seng302.finders.BusinessFinder;
import org.seng302.models.*;
import org.seng302.models.responses.BusinessIdResponse;
import org.seng302.models.requests.NewBusinessRequest;
import org.seng302.models.requests.UserIdRequest;
import org.seng302.models.requests.BusinessIdRequest;
import org.seng302.repositories.BoughtListingRepository;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.UserRepository;
import org.seng302.utilities.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class with endpoints for searching, adding, modifying, or deleting businesses.
 */
@RestController
public class BusinessController {

    private static final Logger logger = LogManager.getLogger(BusinessController.class.getName());

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private BoughtListingRepository boughtListingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessFinder businessFinder;

    @Autowired
    private FileService fileService;

    @Value("${media.image.business_images.directory}")
    private String rootImageDir;

    /**
     * Get request mapping for getting business by id
     * @param id the business's id
     * @return ResponseEntity - 401 when unauthorized (handled by spring sec). 406 when business doesn't exist. 200 otherwise.
     * @throws JsonProcessingException when json mapping object to a json string fails unexpectedly.
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<String> getBusiness(@PathVariable String id) throws JsonProcessingException {
        Business business = businessRepository.findBusinessById(Long.parseLong(id));
        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        String businessJson = mapper.writeValueAsString(business);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(businessJson);
    }

    /**
     * Post request mapping to create a new business.
     * @param req DTO containing the new request information.
     * @param session the current user session.
     * @return ResponseEntity 401 if no auth (handled by spring sec), 400 if there are errors in the request, 201 otherwise.
     */
    @PostMapping("/businesses")
    public ResponseEntity<String> createBusiness(@RequestBody NewBusinessRequest req, HttpSession session) throws JsonProcessingException {
        Business business = new Business(req.getName(), req.getDescription(), req.getAddress(), req.getBusinessType());

        User owner = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        business.createBusiness(owner);

        if (isValidBusiness(business)) {
            businessRepository.save(business);
            BusinessIdResponse businessIdResponse = new BusinessIdResponse(business.getId(), business.getBusinessType());

            String jsonString = mapper.writeValueAsString(businessIdResponse);
            return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(jsonString);
        } else {
            logger.debug(owner.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Verifies that the request body to create a business is valid (currently defined as all values are not null)
     * @param business The business object being created
     * @return True if business is valid, else returns False
     */
    public boolean isValidBusiness(Business business) {
        return (business.getName() != null && business.getBusinessType() != null);
    }

    /**
     * A PUT request that makes a user an administrator of a given business.
     * @param userIdRequest DTO containing the userId to make admin of business.
     * @param id the unique business Id.
     * @param session current authenticated login session of the user.
     * @return a response entity with the appropriate status code.
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    public ResponseEntity<String> makeUserBusinessAdministrator(@RequestBody UserIdRequest userIdRequest, @PathVariable long id, HttpSession session) {
        long userId = userIdRequest.getUserId();
        User owner = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        Business business = businessRepository.findBusinessById(id);

        if (business == null) { // 406 business non-existent.
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        // 403 if user making request is not primary admin or DGAA.
        if (business.getPrimaryAdministrator().getId() != (owner.getId()) && !owner.getRole().equals(Role.DGAA)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User userToAdmin = userRepository.findUserById(userId);
        if (userToAdmin == null || business.getAdministrators().contains(userToAdmin)) { // 400 if user is non-existent or is already admin.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        business.getAdministrators().add(userToAdmin);
        businessRepository.save(business);
        return ResponseEntity.status(HttpStatus.OK).build(); // 200
    }

    /**
     * A PUT request that removes a user from being an administrator of a given business.
     * @param userIdRequest DTO class that contains the user id to remove from the business.
     * @param id the unique business id.
     * @param session current user session.
     * @return 406 if business doesn't exist, 403 if user making the request is not the primary admin, or DGAA,
     * 400 if there is an error in user being removed, 200 otherwise.
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    public ResponseEntity<String> removeUserBusinessAdministrator(@RequestBody UserIdRequest userIdRequest, @PathVariable long id, HttpSession session) {
        long userId = userIdRequest.getUserId();
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        Business business = businessRepository.findBusinessById(id);

        if (business == null) { // 406 business non-existent.
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        // 403 if user making request is not primary admin or DGAA.
        if (business.getPrimaryAdministrator().getId() != (currentUser.getId()) && !currentUser.getRole().equals(Role.DGAA)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User userToRevoke = userRepository.findUserById(userId);
        // 400 if user is non-existent, is not an admin, or is the primary admin.
        if (userToRevoke == null || !business.getAdministrators().contains(userToRevoke) || business.getPrimaryAdministrator().getId() == userToRevoke.getId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        business.getAdministrators().remove(userToRevoke);
        businessRepository.save(business);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Retrieves the business information, and stores the information in the session for the user to act as.
     * @param businessIdRequest DTO containing the buinessId to make the user act as.
     * @param session current authenticated login session of the user.
     * @return a response entity with the appropriate status code.
     */
    @PostMapping("/actasbusiness")
    public ResponseEntity<String> actAsBusiness(@RequestBody BusinessIdRequest businessIdRequest, HttpSession session) {
        long businessId = businessIdRequest.getBusinessId();
        if(businessId == 0){
            session.setAttribute(Business.BUSINESS_SESSION_ATTRIBUTE, null);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            Business existingBusiness = businessRepository.findBusinessById(businessId);
            if (existingBusiness != null) {
                session.setAttribute(Business.BUSINESS_SESSION_ATTRIBUTE, existingBusiness);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    /**
     * Checks the current session of the currently controlled business.
     * @param session business' current session
     * @return ResponseEntity containing the business object for the current session
     */
    @GetMapping("/checkbusinesssession")
    public ResponseEntity<Business> checkBusinessSession(HttpSession session) {
        Business business = (Business) session.getAttribute("business");
        if (business != null) {
            return ResponseEntity.status(HttpStatus.OK).body(business);
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        }
    }

    /**
     * Creates a sort object to be used by pageRequest to sort search results.
     * @param sortString string determining the sort type
     * @return Sort sortBy Sort specification
     * @throws ResponseStatusException
     */
    private Sort getSortType(String sortString) throws ResponseStatusException {
        Sort sortBy = Sort.by(Sort.Order.asc("id").ignoreCase());
        switch (sortString) {
            case "id":
                break;
            case "nameAsc":
                sortBy = Sort.by(Sort.Order.asc("name").ignoreCase()).and(sortBy);
                break;
            case "businessTypeAsc":
                sortBy = Sort.by(Sort.Order.asc("businessType").ignoreCase()).and(sortBy);
                break;
            case "cityAsc":
                sortBy = Sort.by(Sort.Order.asc("address.city").ignoreCase()).and(sortBy);
                break;
            case "countryAsc":
                sortBy = Sort.by(Sort.Order.asc("address.country").ignoreCase()).and(sortBy);
                break;
            case "nameDesc":
                sortBy = Sort.by(Sort.Order.desc("name").ignoreCase()).and(sortBy);
                break;
            case "businessTypeDesc":
                sortBy = Sort.by(Sort.Order.desc("businessType").ignoreCase()).and(sortBy);
                break;
            case "cityDesc":
                sortBy = Sort.by(Sort.Order.desc("address.city").ignoreCase()).and(sortBy);
                break;
            case "countryDesc":
                sortBy = Sort.by(Sort.Order.desc("address.country").ignoreCase()).and(sortBy);
                break;
            default:
                logger.error("400 error - invalid sort by parameter {}", sortString);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortString");
        }
        return sortBy;
    }

    /**
     * Searches for businesses, with credintials
     * @param query A string with the search's query
     * @param type Type of business
     * @return Http status code and list of businesses with name/names matching request.
     */
    @GetMapping("/businesses/search")
    public ResponseEntity<String> findBusinesses(@RequestParam(name="query") String query, @RequestParam(name="type") String type, @RequestParam(name="page") int page,  @RequestParam(defaultValue="id") String sortString, HttpSession session) throws JsonProcessingException {
        logger.debug("Searching for businesses...");
        Sort sortType;
        try {
            sortType = getSortType(sortString);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err.getMessage());
        }
        PageRequest pageRequest = PageRequest.of(page, 10, sortType);
        Specification<Business> matches;
        try {
            matches = businessFinder.findBusinesses(query, type);
        } catch (ResponseStatusException e) {
            logger.error("{} error - invalid type parameter {}", e.getStatus(), type);
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        }
        Page<Business> unfilteredBusinesses =  businessRepository.findAll(matches, pageRequest);
        Page<Business> businesses = removeBusinessesAdministered(unfilteredBusinesses);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(businesses));
    }


    /**
     * Sets businesses administered to null for each admin, there is an issue with how our backend serializes business objects and if it encountered
     * a business object inside there it would replace every other instance of that object with the business name
     * @param businesses List of businesses that needs businessesAdministered removed
     * @return List of businesses with businessesAdministered field set to null
     */
    private Page<Business> removeBusinessesAdministered(Page<Business> businesses) {
        logger.debug("Removing businessesAdministered...");
        if (businesses != null) {
            for(Business business: businesses) {
                Set<User> admins = business.getAdministrators();
                for(User admin: admins) {
                    admin.setBusinessesAdministered(null);
                }
            }

            logger.debug("businessesAdministered removed");
        }
        return businesses;
    }


    /**
     * Gets ALL business types from ENUM business type
     * @return ResponseEntity<String> 401 if no auth, 200 if authenticated and requested correct endpoint
     * @throws JsonProcessingException
     */
    @GetMapping("/businesses/types")
    public ResponseEntity<String> getBusinessTypes() throws JsonProcessingException {
        List<BusinessType> businessTypes = getAllTypes();

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(businessTypes));
    }


    /**
     * Gets business types by calling values on BusinessType ENUM
     * @return List<String> values from business type enum
     */
    public List<BusinessType> getAllTypes() {
        logger.debug("Retrieving all business types...");
        return Arrays.asList(BusinessType.values());
    }


    /**
     * Gets all sales from a business with given id
     * @param id id of the business
     * @param session Http session to get user of session
     * @return Response entity with 200 if ok, 403 if not admin of business, and 406 if business doesn't exist
     * @throws JsonProcessingException
     */
    @GetMapping("/businesses/{id}/sales")
    public ResponseEntity<String> getBusinessSales(@PathVariable long id, HttpSession session) throws JsonProcessingException {

        Business business = businessRepository.findBusinessById(id);

        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        ArrayList<Long> adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) { // User is not authorized to add products
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<BoughtListing> sales = boughtListingRepository.findBoughtListingsByBusinessId(id);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(sales));
    }

    /**
     * Receives and saves a new image pertaining to a business
     * @param businessId business' Id
     * @param image multipart image
     * @param session user's session
     * @return 201 if successful, 400 if bad request, 401 if not logged in, 403 if user doesn't have permission,
     *         406 if path is invalid (bad business Id)
     * @throws InvalidImageExtensionException when file writing fails
     * @throws IOException other input/output exceptions (fileService)
     */
    @PostMapping("/businesses/{businessId}/images")
    public ResponseEntity<String> addBusinessImage(@PathVariable long businessId,
                                                   @RequestPart(name="filename") MultipartFile image,
                                                   HttpSession session)
            throws InvalidImageExtensionException, IOException {
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        Business business = businessRepository.findBusinessById(businessId);
        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String imageExtension;
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No image supplied");
        }
        try {
            imageExtension = Image.getContentTypeExtension(Objects.requireNonNull(image.getContentType()));
        } catch (InvalidImageExtensionException exception) {
            throw new InvalidImageExtensionException(exception.getMessage());
        }

        File businessDir = new File(String.format("%sbusiness_%d", rootImageDir, businessId));
        if (businessDir.mkdirs()) {
            logger.info(String.format("Image of business directory did not exist - new directory created of %s", businessDir.getPath()));
        }

        String id = "";
        boolean freeImage = false;
        int count = 0;

        while (!freeImage) {
            id = String.valueOf(count);
            File checkFile1 = new File(String.format("%s/%s.jpg", businessDir, id));
            File checkFile2 = new File(String.format("%s/%s.png", businessDir, id));
            File checkFile3 = new File(String.format("%s/%s.gif", businessDir, id));
            if (checkFile1.exists() || checkFile2.exists() || checkFile3.exists()) {
                count++;
            }
            else {
                freeImage = true;
            }
        }

        File file = new File(String.format("%s/%s%s", businessDir, id, imageExtension));
        File thumbnailFile = new File(String.format("%s/%s_thumbnail%s", businessDir, id, imageExtension));
        logger.info(String.format("Working Directory = %s", System.getProperty("user.dir")));
        logger.info(file.getAbsolutePath());
        fileService.uploadImage(file, image.getBytes());
        fileService.createAndUploadThumbnailImage(file, thumbnailFile, imageExtension);
        String imageName = image.getOriginalFilename();
        String filename = "";
        String thumbnailFilename = "";
        // Save into DB.
        if (System.getProperty("os.name").startsWith("Windows")) {
            filename = (String.format("business_%d\\%s%s", businessId, id, imageExtension));
            thumbnailFilename = (String.format("business_%d\\%s_thumbnail%s", businessId, id, imageExtension));
        } else {
            filename = (String.format("business_%d/%s%s", businessId, id, imageExtension));
            thumbnailFilename = (String.format("business_%d/%s_thumbnail%s", businessId, id, imageExtension));
        }
        if (business.getPrimaryImagePath() == null) {
            business.setPrimaryImage(filename);
            business.setPrimaryThumbnailPath(thumbnailFilename);
        }
        Image newImage = new Image(imageName, id, filename, thumbnailFilename);
        business.addBusinessImage(newImage);
        businessRepository.save(business);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.writeValueAsString(newImage));
    }


    /**
     * PUT request for updating business image
     * @param businessId id of business that is going to be updated
     * @param imageId id of image that is going to be set to primary
     * @param session session of user, used to check if user is admin
     * @return 200 is updated, 401 no auth, 403 if not admin, 406 if image of business doesn't exist
     */
    @PutMapping("/businesses/{businessId}/images/{imageId}/makeprimary")
    public ResponseEntity<String> changePrimaryImage(@PathVariable long businessId, @PathVariable String imageId, HttpSession session) {
        Business business = businessRepository.findBusinessById(businessId);
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        //Check there is an image match
        boolean validImage = business.getImages().stream().anyMatch(image -> imageId.equals(image.getId()));

        if(!validImage) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        String extension = getExtension(businessId, imageId);
        business = setBusinessImage(business, businessId, imageId, extension);
        businessRepository.save(business);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Sets primary image of business to image path
     * @param businessId id of business, used in path
     * @param imageId id of image, used in image path
     * @param business business object, used to update primary image
     * @param extension extension of image, used in image path
     * @return business with primary image updated
     */
    private Business setBusinessImage(Business business, long businessId, String imageId, String extension) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            business.setPrimaryImage(String.format("business_%d\\%s%s", businessId, imageId, extension));
            business.setPrimaryThumbnailPath(String.format("business_%d\\%s_thumbnail%s", businessId, imageId, extension));
        } else {
            business.setPrimaryImage(String.format("business_%d/%s%s", businessId, imageId, extension));
            business.setPrimaryThumbnailPath(String.format("business_%d/%s_thumbnail%s", businessId, imageId, extension));
        }

        return business;
    }

    /**
     * Gets extension of image by finding file that matches path with extension
     * @param businessId id of business used to find extension from image path
     * @param imageId id of image used to find extension from image path
     * @return Value of extension for image
     */
    private String getExtension(long businessId, String imageId) {
        String imageDir = rootImageDir + "/business_" + businessId + "/" + imageId;
        String extension = "";
        List<String> extensions = new ArrayList<>();
        extensions.add(".png");
        extensions.add(".jpg");
        extensions.add(".gif");
        for (String ext : extensions) {
            Path path = Paths.get(imageDir + ext);
            if (Files.exists(path)) {
                extension = ext;
                break;
            }
        }

        return extension;
    }

    /**
     * Updates business
     * @param id id of the business
     * @param session Http session to get user session
     * @return Response entity,
     *         200 if successfully update
     *         400 Error with given data
     *         401 Missing auth token
     *         403 if trying to modify business that isn't yours
     *         406 if trying to access business that doesn't exist
     */
    @PutMapping("/businesses/{id}/modify")
    public ResponseEntity<String> modifyBusiness(@RequestBody NewBusinessRequest request, @PathVariable long id, HttpSession session) {
        Business business = businessRepository.findBusinessById(id);
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        if(checkBusinessRequest(request)) { // If valid -> description < 140 & name < 30
            if (business == null) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }

            ArrayList<Long> adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
            if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) { // User is not authorized to add products
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            business = updateBusiness(request, business);
            businessRepository.save(business);
            return ResponseEntity.status(HttpStatus.OK).build();

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates business object to contain new data from request, used in modify business.
     * @param businessRequest request containing new business information
     * @param business the business that is going to be updated
     * @return updated business object
     */
    private Business updateBusiness(NewBusinessRequest businessRequest, Business business) {
        businessRequest.getAddress().setId(business.getAddress().getId());
        business.setBusinessType(businessRequest.getBusinessType());
        business.setAddress(businessRequest.getAddress());
        business.setName(businessRequest.getName());
        business.setDescription(businessRequest.getDescription());

        return business;
    }

    /**
     * Checks request and ensures request values are valid
     * @param businessRequest request containing new business information
     * @return True if valid, false otherwise
     */
    private boolean checkBusinessRequest(NewBusinessRequest businessRequest) {
        int nameLength = businessRequest.getName().length();
        int descriptionLength = businessRequest.getDescription().length();
        String country = businessRequest.getAddress().getCountry();

        if(businessRequest.getBusinessType() == null) {
            return false;
        }
        //https://www.quora.com/Which-country-has-the-longest-name 🥴
        if(country == null || country.length() < 1 || country.length() > 74) {
            return false;
        }
        if(nameLength > 30 || nameLength < 1 || businessRequest.getName() == null) {
            return false;
        }
        return descriptionLength <= 200;
    }

    /**
     * Deletes a business image
     * @param businessId unique identifier of the business that the image is relating to.
     * @param imageId Id of the image to remove
     * @return ResponseEntity<String> Status identifying completion result
     * @throws IOException
     */
    @DeleteMapping("/businesses/{businessId}/images/{imageId}")
    public ResponseEntity<String> deleteBusinessImage(@PathVariable long businessId, @PathVariable String imageId, HttpSession session) throws IOException {
        String imageExtension = "";
        Business business = businessRepository.findBusinessById(businessId);
        //check if the business exists
        if (business == null)  {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        //check if user is an administrator for the business
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //finds correct image path
        String imageDir = rootImageDir + "business_" + businessId + "/" + imageId;
        boolean pathExists = false;
        List<String> extensions = new ArrayList<>();
        extensions.add(".png");
        extensions.add(".jpg");
        extensions.add(".gif");
        for (String ext: extensions) {
            Path path = Paths.get(imageDir + ext);
            if (Files.exists(path)) {
                pathExists = true;
                imageExtension = ext;
                break;
            }
        }
        File businessDir = new File(rootImageDir + "business_" + businessId);
        File checkFile = new File(String.format("%s/%s%s", businessDir, imageId, imageExtension));
        if (pathExists) {
            business.deleteBusinessImage(imageId);
            businessRepository.save(business);
            Files.delete(Paths.get(businessDir + "/" + imageId + imageExtension));
            Files.delete(Paths.get(businessDir + "/" + imageId + "_thumbnail" + imageExtension));
            logger.info(String.format("File %s successfully removed", checkFile.toString()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image does not exist.");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
