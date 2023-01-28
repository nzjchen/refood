package org.seng302.models;

import lombok.Data;
import org.seng302.exceptions.InvalidImageExtensionException;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity class that represents an Image object.
 */
@Data
@Entity
public class Image {

    @Id
    private String id;

    private String filename;
    private String thumbnailFilename;
    private String name;

    public Image(String name, String id, String filename, String thumbnailFilename) {
        this.filename = filename;
        this.thumbnailFilename = thumbnailFilename;
        this.id = id;
        this.name =name;
    }

    protected Image() {}

    /**
     * Checks the content type given and returns the extension string of the type.
     * @param contentType string extension to be found.
     * @return string representation of the file extension.
     * @throws InvalidImageExtensionException throw exception if the contentType (or rather, the file) is not of a valid extension.
     */
    public static String getContentTypeExtension(String contentType) throws InvalidImageExtensionException {
        switch (contentType) {
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpg";
            case "image/gif":
                return ".gif";
            default:
                throw new InvalidImageExtensionException("Unsupported extension type.");
        }
    }

    public String getId() {
        return this.id;
    }

    public String getFileName() { return this.filename; }

}
