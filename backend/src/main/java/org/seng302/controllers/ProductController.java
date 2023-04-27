package org.seng302.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.exceptions.InvalidImageExtensionException;
import org.seng302.models.*;
import org.seng302.models.requests.NewProductRequest;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.ProductRepository;
import org.seng302.utilities.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * REST controller with product related endpoints.
 */
@RestController
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class.getName());

    @Autowired private ProductRepository productRepository;
    @Autowired private BusinessRepository businessRepository;
    @Autowired private FileService fileService;

    @Autowired private ObjectMapper mapper;

    @Value("${media.image.business.directory}")
    String rootImageDir;

    /**
     * Retrieves all of the products in the business' product catalogue.
     * Authentication required endpoint - 401 if unauthorized.
     * @param id unique identifier of the business.
     * @return an (potentially empty) array of products that the business owns.
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<List<Product>> getBusinessProductCatalogue(@PathVariable long id, HttpSession session) {
        Business business = businessRepository.findBusinessById(id);
        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Product> products = productRepository.findProductsByBusinessId(id);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    /**
     * Creates a new product and adds it to the product catalogue of the current acting business
     * Authentication is required, user must be a business admin or a default global admin
     * @param id unique identifier of the business
     * @param req the request body for the new product object
     * @param session http session which holds the authenticated user
     * @return error codes: 403 (forbidden user), 400 (bad request for product), 201 (object valid and created)
     */
    @PostMapping("/businesses/{id}/products")
    public ResponseEntity<String> createProduct(@PathVariable long id, @RequestBody NewProductRequest req, HttpSession session) {
        Business business = businessRepository.findBusinessById(id);

        if (business == null) { // Business does not exist
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            ArrayList<Long> adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
            User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

            if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) { // User is not authorized to add products
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else { // User is authorized
                ArrayList<Object> checkProduct = isValidProduct(req, business);
                boolean isValid = (Boolean) checkProduct.get(0);
                String errorMessage = (String) checkProduct.get(1);
                if (isValid) {
                    Product newProduct = new Product(req, business.getId());
                    productRepository.save(newProduct);
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
            }
        }
    }


    /**
     * Updates product information using PUT request, SINCE IT IS A PUT REQUEST ALL FIELDS HAVE TO EXIST
     * Authentication is required, user must be a business admin or a default global admin
     * @param businessId Business id, used to get business to get product catalogue
     * @param productId Product id, is a string since product ids can be any value
     * @param req the request body for the new product object (using NewProduct request of this because it contains the required variables for this request)
     * @param session http session which holds the authenticated user
     * @return error codes: 403 (forbidden user), 400 (Bad Request (Incorrect fields or business doesn't exist))), 200 (Object Updated)
     */
    @PutMapping("/businesses/{businessId}/products/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable("businessId") long businessId, @PathVariable("productId") String productId, @RequestBody NewProductRequest req, HttpSession session) {
        Business business = businessRepository.findBusinessById(businessId);
        Product product = productRepository.findProductByIdAndBusinessId(productId, businessId);

        if (business == null || product == null) { // Business or product does not exist
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            ArrayList<Long> adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
            User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

            if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) { // User is not authorized to add products
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else { // User is authorized
                ArrayList<Object> checkProduct = isValidProductPut(req);
                boolean isValid = (Boolean) checkProduct.get(0);
                String errorMessage = (String) checkProduct.get(1);
                if(isValid) {
                    productRepository.updateProduct(req.getId(), req.getName(), req.getDescription(), req.getRecommendedRetailPrice(), req.getManufacturer(), productId);
                    return ResponseEntity.status(HttpStatus.OK).build();
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
            }
        }
    }

    /**
     * Checks the product request body to ensure all fields are valid
     * @param product The product to be created, includes id, name, description and price
     * @param business The business that is creating the product
     * @return True if all fields are valid, otherwise false
     */
    private ArrayList<Object> isValidProduct(NewProductRequest product, Business business) {
        boolean isValid = true;
        String errorMessage = null;

        if (productRepository.findProductByIdAndBusinessId(product.getId(), business.getId()) != null) {
            errorMessage = "A product already exists with this ID";
            isValid = false;
        } else if (product.getId() == null || product.getId().equals("")) {
            errorMessage = "Product id can not be empty";
            isValid = false;
        } else if (product.getName() == null || product.getName().equals("")) {
            errorMessage = "Product name can not be empty";
            isValid = false;
        } else if (product.getRecommendedRetailPrice() == null || product.getRecommendedRetailPrice() < 0) {
            errorMessage = "Product recommended retail price must be at least 0";
            isValid = false;
        }

        ArrayList<Object> returnObjects = new ArrayList<>();
        returnObjects.add(isValid);
        returnObjects.add(errorMessage);
        return returnObjects;
    }

    /**
     * Checks the product update request body to ensure all fields are valid.
     * @param product the updated product details
     * @return an array with the first element indicating if it it's valid, and a second element with an error message (if any).
     */
    private ArrayList<Object> isValidProductPut(NewProductRequest product) {
            boolean isValid = true;
            String errorMessage = null;

            if (product.getId() == null || product.getId().equals("")) {
                errorMessage = "Product id can not be empty";
                isValid = false;
            } else if (product.getName() == null || product.getName().equals("")) {
                errorMessage = "Product name can not be empty";
                isValid = false;
            } else if (product.getRecommendedRetailPrice() == null || product.getRecommendedRetailPrice() < 0) {
                errorMessage = "Product recommended retail price must be at least 0";
                isValid = false;
            } else if (product.getDescription() == null || product.getDescription().equals("")) {
                errorMessage = "Product must have description";
                isValid = false;
            }

            ArrayList<Object> returnObjects = new ArrayList<>();
            returnObjects.add(isValid);
            returnObjects.add(errorMessage);
            return returnObjects;
        }


    /**
     * Receives and saves a new image pertaining to a product.
     * Authorization required - 401 code response if not.
     * @param businessId unique identifier of the business that the image is relating to.
     * @param productId product identifier that the image is relating to.
     * @param image a multipart image of the file
     * @return ResponseEntity with the appropriate status codes - 201, 400, 403, 406.
     * @throws IOException Thrown when file writing fails.
     */
    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<String> addProductImage(@PathVariable long businessId, @PathVariable String productId, @RequestPart(name="filename") MultipartFile image, HttpSession session) throws InvalidImageExtensionException, IOException {
        Business business = businessRepository.findBusinessById(businessId);
        if (business == null)  {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        Product product = productRepository.findProductByIdAndBusinessId(productId, businessId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
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
        File businessDir = new File(String.format("%sbusiness_%d", rootImageDir, businessId));
        if (businessDir.mkdirs()) {
            logger.info(String.format("Image of business directory did not exist - new directory created of %s", businessDir.getPath()));
        }

        String id = "";
        boolean freeImage = false;
        int count = 0;

        while (!freeImage) {
            id = "product_" + productId + "_" + String.valueOf(businessId) + "_" + String.valueOf(count);
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
        fileService.uploadImage(file, image.getBytes());
        fileService.createAndUploadThumbnailImage(file, thumbnailFile, imageExtension);
        String imageName = image.getOriginalFilename();
        // Save into DB.
        Image newImage = new Image(imageName, id, file.toString(), thumbnailFile.toString());
        product.addProductImage(newImage);
        if (product.getPrimaryImagePath() == null) {
            if (System.getProperty("os.name").startsWith("windows")) {
                product.setPrimaryImage(String.format("business_%d\\%s%s", businessId, id, imageExtension));
            } else {
                product.setPrimaryImage(String.format("business_%d/%s%s", businessId, id, imageExtension));
            }
        }
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Sets the primary image for a product from a previously saved image.
     * @param businessId unique identifier of the business that the image is relating to.
     * @param productId product identifier that the image is relating to.
     * @param imageId a multipart image of the file
     * @return ResponseEntity with the appropriate status codes - 200, 401, 403, 406.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    public ResponseEntity<List<byte[]>> setPrimaryImage(@PathVariable long businessId, @PathVariable String productId, @PathVariable String imageId, HttpSession session) {
        Business business = businessRepository.findBusinessById(businessId);
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        Product product = productRepository.findProductByIdAndBusinessId(productId, businessId);

        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean validImage = false;
        if (product != null) {
            for (Image image: product.getImages()) {
                if (imageId.equals(image.getId())) {
                    validImage = true;
                    break;
                }
            }
        }
        if (product == null || !validImage) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        String imageDir = rootImageDir + "/business_" + businessId + "/" + imageId;
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
            product.setPrimaryImage(String.format("business_%d\\%s%s", businessId, imageId, extension));
        } else {
            product.setPrimaryImage(String.format("business_%d/%s%s", businessId, imageId, extension));
        }
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
         * deletes an image
         * @param businessId unique identifier of the business that the image is relating to.
         * @param productId product identifier that the image is relating to.
         * @param image a multipart image of the file
         * @return ResponseEntity<String>
         * @throws IOException
         */
    @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
    public ResponseEntity<String> deleteProductImage(@PathVariable long businessId, @PathVariable String productId, @PathVariable String imageId, HttpSession session, HttpServletRequest request) throws IOException {
        String imageExtension = "";
        Business business = businessRepository.findBusinessById(businessId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //checks if business exists
        if (business == null)  {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        //checks if user us logged in and authorized
        if (auth.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //checks if user is forbidden
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        //checks if product exists
        Product product = productRepository.findProductByIdAndBusinessId(productId, businessId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
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
            product.deleteProductImage(imageId);
            productRepository.save(product);
            Files.delete(Paths.get(businessDir + "/" + imageId + imageExtension));
            Files.delete(Paths.get(businessDir + "/" + imageId + "_thumbnail" + imageExtension));
            logger.info(String.format("File %s successfully removed", checkFile.toString()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image does not exist.");
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
