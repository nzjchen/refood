package org.seng302.models;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.seng302.utilities.serializers.PrimaryAdministratorSerializer;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Getter @Setter // generate setters and getters for all fields (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name") // Forces any nested business objects to only use name to prevent recursion.
@JsonPropertyOrder({"id", "administrators", "name", "primaryAdministratorId"}) // force json property order to match api.
public class Business implements Serializable {

    public static final String BUSINESS_SESSION_ATTRIBUTE = "business";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "BUSINESS_ADMINS",
                    joinColumns = @JoinColumn(name="BUSINESS_ID"),
                    inverseJoinColumns = @JoinColumn(name="USER_ID"))
    private Set<User> administrators = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="USER_ID")
    @JsonSerialize(using = PrimaryAdministratorSerializer.class)
    @JsonProperty("primaryAdministratorId") // while the entity itself stores the user object, when we output to a JSON,
    private User primaryAdministrator; // it will only use the id as the value, and key as primaryAdministratorId.

    private String name;
    private String description;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date created;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Image> images;
    private String primaryImagePath;
    private String primaryThumbnailPath;

    protected Business() {}

    /**
     * Constructor for the Business object
     * @param name  Name of the business
     * @param description   Brief description of the business
     * @param address   Business address
     * @param businessType  The type of business
     */
    public Business(String name, String description, Address address, BusinessType businessType) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;

        this.images = new HashSet<>();
        this.primaryImagePath = null;
        this.primaryThumbnailPath = null;
    }

    /**
     * This method is called when a post request is made through the controller to create a business
     * Created date is generated automatically
     * The logged in user is set as business administrator by default
     */
    public void createBusiness(User owner) {
        this.administrators.add(owner);
        this.primaryAdministrator = owner;
        this.created = new Date();

        this.images = new HashSet<>();
        this.primaryImagePath = null;
        this.primaryThumbnailPath = null;
    }

    /**
     * Collects and returns a list of Ids of administrators.
     * @return the list of administrators.
     */
    public List<Long> collectAdministratorIds() {
        return this.getAdministrators().stream().map(User::getId).collect(Collectors.toList());
    }

    /**
     * Adds a new image to the business entity
     * @param image image object to add
     */
    public void addBusinessImage(Image image) { this.images.add(image); }

    /**
     * Sets the primary image to the image specified by the path
     * @param path the path to the wanted primary image
     */
    public void setPrimaryImage(String path) { this.primaryImagePath = path; }

    /**
     * Sets the path of the thumbnail of the primary image
     * @param path The path to the image
     */
    public void setPrimaryThumbnailPath(String path) {
        this.primaryThumbnailPath = path;
    }

    /**
     * Remove the image from the images list for the business and update the primary image if needed.
     * @param imageId Id of the image to remove
     */
    public void deleteBusinessImage(String imageId) {
        Image removeImage = null;
        for (Image image: this.images) {
            if (image.getId().equals(imageId)) {
                this.images.remove(image);
                removeImage = image;
                break;
            }
        }
        assert removeImage != null;
        String primaryPath = removeImage.getFileName().substring(removeImage.getFileName().indexOf("business_"));
        if ((primaryPath.equals(this.primaryImagePath.replace("/", "\\")) && System.getProperty("os.name").startsWith("Windows")) || primaryPath.equals(this.primaryImagePath)) {
            this.primaryThumbnailPath = null;
            this.primaryImagePath = null;
        }
    }
}
