package org.seng302.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.exceptions.InvalidImageExtensionException;
import org.seng302.finders.UserFinder;
import org.seng302.models.Image;
import org.seng302.models.Role;
import org.seng302.models.User;
import org.seng302.models.requests.LoginRequest;
import org.seng302.models.requests.ModifyUserRequest;
import org.seng302.models.requests.NewUserRequest;
import org.seng302.models.requests.UserRequest;
import org.seng302.models.responses.UserIdResponse;
import org.seng302.repositories.UserRepository;
import org.seng302.utilities.Encrypter;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REST controller for user related calls.
 */
@RestController
public class UserController {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(UserController.class.getName());

    private UserRepository userRepository;

    private UserFinder userFinder;

    @Autowired
    private FileService fileService;


    @Value("${media.image.user.directory}")
    String rootImageDir;

    @Autowired
    public UserController(UserRepository userRepository, UserFinder userFinder) {
        this.userRepository = userRepository;
        this.userFinder = userFinder;
    }

    /**
     * Get request mapping for get user information by Id
     * @param id the user's id in the database
     * @return ResponseEntity
     * @throws JsonProcessingException when
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<String> getUser(@PathVariable String id) throws JsonProcessingException {
        User user = userRepository.findUserById(Long.parseLong(id));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            String userJson = mapper.writeValueAsString(user);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(userJson);
        }
    }

    /**
     * Login POST method. Checks if user exists and provided details are correct and authenticates if true.
     * @param loginRequest a login request containing the email and password.
     * @param session
     * @return 200 if login is successful, 400 if email/password is invalid.
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest, HttpSession session) throws NoSuchAlgorithmException, JsonProcessingException {
        User existingUser = userRepository.findUserByEmail(loginRequest.getEmail());
        if (existingUser != null && Encrypter.hashString(loginRequest.getPassword()).equals(existingUser.getPassword())) {
            UserIdResponse userIdResponse = new UserIdResponse(existingUser);
            session.setAttribute(User.USER_SESSION_ATTRIBUTE, existingUser);
            Authentication auth = new UsernamePasswordAuthenticationToken(existingUser.getEmail(), existingUser.getPassword(), AuthorityUtils.createAuthorityList("ROLE_" + existingUser.getRole()));
            SecurityContextHolder.getContext().setAuthentication(auth);

            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(userIdResponse));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * THIS NEEDS TO BE REMOVED AT SOME POINT - HERE FOR TESTING PURPOSES.
     * Prints out the current user session/authentication details into console.
     */
    @GetMapping("/checksession")
    public ResponseEntity<User> checksession(HttpServletRequest req, HttpSession session) {
        User user = (User) session.getAttribute("user");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    @PostMapping("/logoutuser")
    public ResponseEntity<String> logoutUser(HttpServletRequest req, HttpSession session) {
        session.invalidate();
        return ResponseEntity.status(HttpStatus.OK).build();
   }

    /**
     *
     * @param reqBody ModifyUserRequest object which contains everything that needs to be changed.
     * @param id The id of the user to be modified.
     * @param session HttpSession object to help getting the session of the current user.
     * @return 200 if the user is properly updated, 400 if the supplied data is bad, 409 if the user is attempted to
     * change their email to an email that is already taken, 406 if the user id does not exist, and 401 if the user is
     * not logged in.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<String> modifyUser(@RequestBody ModifyUserRequest reqBody, @PathVariable String id, HttpSession session) throws JsonProcessingException, NoSuchAlgorithmException, ParseException {
        User user = userRepository.findUserById(Long.parseLong(id));
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        else if (userRepository.findUserByEmail(reqBody.getEmail()) != null &&
                !currentUser.getEmail().equals(reqBody.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        else {
            List<String> registrationErrors = registrationUserCheck(reqBody, false);
            if (registrationErrors.isEmpty()) { // No errors found.
                if (reqBody.getNewPassword() != null &&
                        !Encrypter.hashString(reqBody.getPassword()).equals(user.getPassword())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password");
                } else if (reqBody.getNewPassword() != null &&
                        Encrypter.hashString(reqBody.getPassword()).equals(user.getPassword())) {
                    user.setPassword(Encrypter.hashString(reqBody.getNewPassword()));
                }
                
                user.setFirstName(reqBody.getFirstName());
                user.setLastName(reqBody.getLastName());
                user.setMiddleName(reqBody.getMiddleName());
                user.setNickname(reqBody.getNickname());
                user.setBio(reqBody.getBio());
                user.setEmail(reqBody.getEmail());
                user.setDateOfBirth(reqBody.getDateOfBirth());
                user.setPhoneNumber(reqBody.getPhoneNumber());
                user.setFirstName(reqBody.getFirstName());
                user.setHomeAddress(reqBody.getHomeAddress());
                userRepository.save(user);

                UserIdResponse res = new UserIdResponse(user);
                String jsonString = mapper.writeValueAsString(res);
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(jsonString);
            }
            else {
                logger.error("Invalid user modification.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errors with modifying the user's details: " + registrationErrors);
            }
        }
    }

    /**
     * This method inserts a new user into the user repository
     * @param user Body of request in API specific format
     * @return Status code depending on result of registration.
     */
    @PostMapping("/users")
    public ResponseEntity<String> registerUser(@RequestBody NewUserRequest user) throws JsonProcessingException, NoSuchAlgorithmException, ParseException {
        if (userRepository.findUserByEmail(user.getEmail()) == null) {
            List<String> registrationErrors = registrationUserCheck(user, true);
            if (registrationErrors.isEmpty()) { // No errors found.
                User newUser = new User(user);
                userRepository.save(newUser);

                Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
                SecurityContextHolder.getContext().setAuthentication(auth);

                UserIdResponse res = new UserIdResponse(newUser);
                String jsonString = mapper.writeValueAsString(res);

                return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(jsonString);
            }
            else {
                logger.error("Invalid registration.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errors with registration details: " + registrationErrors);
            }
        }
        logger.error("Registration email address already in use.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
    }

    /**
     * This method checks whether all the mandatory fields of the new user are present.
     * @param user The user to check the validity of
     * @return list of errors with the new registration request - if there is any.
     */
    public List<String> registrationUserCheck(UserRequest user, boolean passwordUpdate) throws ParseException {
        List<String> errors = new ArrayList<>();

        if (user.getFirstName() == null || (user.getFirstName() != null && user.getFirstName().length() == 0)) {
            errors.add("First Name");
        }
        if (user.getLastName() == null || (user.getLastName() != null && user.getLastName().length() == 0)) {
            errors.add("Last Name");
        }
        if (user.getEmail() == null || !this.isValidEmail(user.getEmail()) || (user.getEmail() != null && user.getEmail().length() == 0)) {
            errors.add("Email");
        }
        if (passwordUpdate && (user.getPassword() == null || (user.getPassword() != null && user.getPassword().length() == 0))) {
            errors.add("Password");
        }
        if (user.getDateOfBirth() == null || !this.isNotUnderage(user.getDateOfBirth()) || (user.getDateOfBirth() != null && user.getDateOfBirth().length() == 0)) {
            errors.add("Date of Birth");
        }
        if (user.getHomeAddress() == null) {
            errors.add("Home Address");
        }
        else if (user.getHomeAddress().getCountry() == null || (user.getHomeAddress().getCountry() != null && user.getHomeAddress().getCountry().length() == 0)) {
            errors.add("Home Address - Country");
        }

        return errors;
    }

    /**
     * Takes an email as a parameter to check if it is in the right format.
     * @param email The email to be checked
     * @return True if it is in a valid format; otherwise, false
     */
    public boolean isValidEmail(String email) {
        Pattern re = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Matcher m = re.matcher(email);
        return m.matches();
    }

    /**
     * Checks that the user is not underage.
     * @param date The date of birth of the user.
     * @return True if not underage; otherwise, false.
     * @throws ParseException
     */
    public boolean isNotUnderage(String date) throws ParseException {
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Date present = new SimpleDateFormat("dd/MM/yyyy").parse(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()));
        long diffInMillies = present.getTime() - dob.getTime();
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
        return diff >= 13;
    }

    /**
     * Creates a sort object to be used by pageRequest to sort search results.
     * @param sortString
     * @return Sort sortBy Sort specification
     * @throws ResponseStatusException
     */
    private Sort getSortType(String sortString) throws ResponseStatusException {
        Sort sortBy = Sort.by(Sort.Order.asc("email").ignoreCase());
        switch (sortString) {
            case "firstNameAsc":
                sortBy = Sort.by(Sort.Order.asc("firstName").ignoreCase()).and(sortBy);
                break;
            case "lastNameAsc":
                sortBy = Sort.by(Sort.Order.asc("lastName").ignoreCase()).and(sortBy);
                break;
            case "cityAsc":
                sortBy = Sort.by(Sort.Order.asc("homeAddress.city").ignoreCase()).and(sortBy);
                break;
            case "countryAsc":
                sortBy = Sort.by(Sort.Order.asc("homeAddress.country").ignoreCase()).and(sortBy);
                break;
            case "emailAsc":
                break;
            case "firstNameDesc":
                sortBy = Sort.by(Sort.Order.desc("firstName").ignoreCase()).and(sortBy);
                break;
            case "lastNameDesc":
                sortBy = Sort.by(Sort.Order.desc("lastName").ignoreCase()).and(sortBy);
                break;
            case "cityDesc":
                sortBy = Sort.by(Sort.Order.desc("homeAddress.city").ignoreCase()).and(sortBy);
                break;
            case "countryDesc":
                sortBy = Sort.by(Sort.Order.desc("homeAddress.country").ignoreCase()).and(sortBy);
                break;
            case "emailDesc":
                sortBy = Sort.by(Sort.Order.desc("email").ignoreCase());
                break;
            default:
                logger.error("400 error - invalid sort by parameter {}", sortString);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortString");
        }
        return sortBy;
    }


    /**
     * This method retrieves user information by name.
     * @param query Body of the incoming request.
     * @return Http status code and list of users with name/names matching request.
     */
    @GetMapping("/users/search")
    public  ResponseEntity<String> searchUser(@RequestParam(name="searchQuery") String query,
                                              @RequestParam(name="pageNum") int pageNum,
                                              @RequestParam(defaultValue="firstNameAsc") String sortString) throws JsonProcessingException {
        logger.debug("search called");
        Sort sortType;
        try {
            sortType = getSortType(sortString);
        } catch (ResponseStatusException err) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err.getMessage());
        }
        PageRequest pageRequest = PageRequest.of(pageNum, 10, sortType);
        Specification<User> matches = userFinder.findUsers(query);
        Page<User> users = userRepository.findAll(matches, pageRequest);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(users));
    }

    /**
     * Uploads image to relevant user profile, setting the image as primary if it is the first image.
     * @param id Id of the user the image is added to
     * @param image Multipart image of file
     * @param session Current user session
     * @return ResponseEntity with the appropriate status codes - 201, 400, 403, 406.
     * @throws InvalidImageExtensionException custom exception when file extension is not valid
     * @throws IOException Thrown when file reading or writing fails
     */
    @PostMapping("/users/{id}/images")
    public ResponseEntity<String> addUserImage(@PathVariable Long id, @RequestPart(name="filename") MultipartFile image, HttpSession session) throws InvalidImageExtensionException, IOException {
        User user = userRepository.findUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (currentUser.getId() != id && currentUser.getRole() == Role.USER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String imageExtension;
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No image supplied.");
        }
        try { // Throw error if the file is not an image.
            imageExtension = Image.getContentTypeExtension(Objects.requireNonNull(image.getContentType()));
        }
        catch (InvalidImageExtensionException exception) {
            throw new InvalidImageExtensionException(exception.getMessage());
        }
        // Check if business' own folder directory exists - make directory if false.
        File userDir = new File(String.format("%suser_%d", rootImageDir, id));
        if (userDir.mkdirs()) {
            logger.info(String.format("Image of user directory did not exist - new directory created of %s", userDir.getPath()));
        }
        String imageId = "";
        boolean freeImage = false;
        int count = 0;
        while (!freeImage) {
            imageId = String.valueOf(count);
            File checkFile1 = new File(String.format("%s/%s.jpg", userDir, imageId));
            File checkFile2 = new File(String.format("%s/%s.png", userDir, imageId));
            File checkFile3 = new File(String.format("%s/%s.gif", userDir, imageId));
            if (checkFile1.exists() || checkFile2.exists() || checkFile3.exists()) {
                count++;
            }
            else {
                freeImage = true;
            }
        }
        File file = new File(String.format("%s/%s%s", userDir, imageId, imageExtension));
        File thumbnailFile = new File(String.format("%s/%s_thumbnail%s", userDir, imageId, imageExtension));
        logger.info(String.format("Working Directory = %s", System.getProperty("user.dir")));
        logger.info(file.getAbsolutePath());
        fileService.uploadImage(file, image.getBytes());
        fileService.createAndUploadThumbnailImage(file, thumbnailFile, imageExtension);
        String imageName = image.getOriginalFilename();
        // Save into DB.
        Image newImage = new Image(imageName, imageId, file.toString(), thumbnailFile.toString());
        user.addUserImage(newImage);
        user.updatePrimaryImage(id, imageId, imageExtension);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(newImage.getId()));
    }


    /**
     * Sets the primary image for a user from a previously saved image.
     * @param id unique identifier of the user that the image is relating to.
     * @param imageId a multipart image of the file
     * @param session Current user session
     * @return ResponseEntity with the appropriate status codes - 200, 401, 403, 406.
     */
    @PutMapping("/users/{id}/images/{imageId}/makeprimary")
    public ResponseEntity<List<byte[]>> setPrimaryImage(@PathVariable long id, @PathVariable String imageId, HttpSession session) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        User userSession = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (userSession == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if( userSession.getId() != user.getId() && !Role.isGlobalApplicationAdmin(userSession.getRole())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean validImage = false;
        if (user != null) {
            for (Image image: user.getImages()) {
                if (imageId.equals(image.getId())) {
                    validImage = true;
                    break;
                }
            }
        }
        if (user == null || !validImage) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        String imageDir = rootImageDir + "/user_" + id + "/" + imageId;
        String extension = "";
        List<String> extensions = new ArrayList<>();
        extensions.add(".png");
        extensions.add(".jpg");
        extensions.add(".gif");
        for (String ext: extensions) {
            Path path = Paths.get(imageDir + ext);
            if (Files.exists(path)) {
                extension = ext;
                break;
            }
        }
        if (System.getProperty("os.name").startsWith("Windows")) {
            user.setPrimaryImage(String.format("user_%d\\%s%s", id, imageId, extension));
        } else {
            user.setPrimaryImage(String.format("user_%d/%s%s", id, imageId, extension));
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // -- ADMIN REQUESTS

    /**
     * Method to let a DGAA user make a user an GAA admin user.
     * @param id user id to be made admin.
     * @return status code. 200 if it works, 406 if bad id, 401 if missing session token, 403 if no authority (last two handled by spring sec).
     */
    @PutMapping("/users/{id}/makeAdmin")
    public ResponseEntity<String> makeUserAdmin(@PathVariable long id) {
        if (userRepository.findUserById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        userRepository.updateUserRole(id, Role.GAA);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to let a DGAA user revoke GAA admin status from another user. Reverts the user back to USER role.
     * @param id user id to revoke admin user role.
     * @return status code. 200 if it works, 409 if the DGAA is revoking their own role, 406 if bad id, 401 if missing session token, 403 if incorrect role (last two handled by spring sec).
     */
    @PutMapping("/users/{id}/revokeAdmin")
    public ResponseEntity<String> revokeUserAdmin(@PathVariable Long id, HttpSession session) {
        if (userRepository.findUserById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        User self = (User) session.getAttribute("user");
        if (self != null && self.getId() == id) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userRepository.updateUserRole(id, Role.USER);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}
