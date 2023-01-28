package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.seng302.models.requests.NewProductRequest;
import org.seng302.utilities.serializers.ProductBusinessSerializer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity class that holds the information of a business product.
 */
@Data
@Entity
@IdClass(ProductId.class)
public class Product {

    // Composite key of product id & business id.
    @Id
    private String id;
    @Id
    @JsonIgnore
    private long businessId;

    @ManyToOne
    @JsonSerialize(using = ProductBusinessSerializer.class)
    @JoinColumn(name = "businessId", insertable = false, updatable = false)
    private Business business;

    private String name;
    private String description;
    private String manufacturer;
    private double recommendedRetailPrice;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date created;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Creates a table PRODUCT_IMAGES.
    private List<Image> images;

    private String primaryImagePath;

    protected Product() { }

    public Product(String id, Business business, String name, String description, String manufacturer, double recommendedRetailPrice, Date created) {
        this.id = id;
        this.businessId = business.getId();
        this.business = business;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.created = created;
        this.images = new ArrayList<>();

        this.primaryImagePath = null;
    }

    /**
     * Used for when a new product request is called.
     * @param newProductRequest The request body information that was mapped into a NewProductRequest.
     * @param businessId business to assign the product rights to.
     */
    public Product(NewProductRequest newProductRequest, Long businessId) {
        this.id = newProductRequest.getId();
        this.businessId = businessId;
        this.name = newProductRequest.getName();
        this.description = newProductRequest.getDescription();
        this.manufacturer = newProductRequest.getManufacturer();
        this.recommendedRetailPrice = newProductRequest.getRecommendedRetailPrice();
        this.created = new Date();
        this.images = new ArrayList<>();
        this.primaryImagePath = null;
    }

    /**
     * Adds a new image to the product entity.
     * @param image the image object to add.
     */
    public void addProductImage(Image image) {
        this.images.add(image);
    }

    public void setPrimaryImage(String path) {
        this.primaryImagePath = path;
    }

    public void deleteProductImage(String imageId) {
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
            if (!this.images.isEmpty()) {
                Image primary = this.images.iterator().next();
                String primaryFilename = primary.getFileName();
                int sliceIndex = primaryFilename.indexOf("business_");
                String primaryFile = primaryFilename.substring(sliceIndex);
                this.setPrimaryImage(primaryFile);
            } else {
                this.primaryImagePath = null;
            }
        }
    }
}
