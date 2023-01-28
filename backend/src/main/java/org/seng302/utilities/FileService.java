package org.seng302.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Objects;

/**
 * Service class that deals with file writing and saving.
 */
@Service
public class FileService {

    private static final Logger logger = LogManager.getLogger(FileService.class.getName());

    private static final int THUMBNAIL_SCALE_HEIGHT = 10;
    private static final int THUMBNAIL_SCALE_WIDTH = 10;

    /**
     * Uploads an image to a given directory.
     * @param file the directory and filename to be saved as.
     * @param bytes the image in a byte array.
     * @throws IOException when writing file fails.
     */
    public void uploadImage(File file, byte[] bytes) throws IOException {
        logger.info(String.format("Uploading Image at %s", file));
        if (file.createNewFile()) {
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream((file)));) {
                stream.write(bytes);
                Objects.requireNonNull(stream).close();
            }
        } else {
            logger.info(String.format("Failed to upload image from %s", file));
        }
    }

    /**
     * Creates a new thumbnail/resized image of a given original image, and uploads it to a new location.
     * @param imageLocation Directory of the original image to transform.
     * @param thumbnailLocation Location of the new resized image to write to.
     * @param imageExtension The extension of the new thumbnail image.
     * @throws IOException when writing the new thumbnail fails.
     */
    public void createAndUploadThumbnailImage(File imageLocation, File thumbnailLocation, String imageExtension) throws IOException {
        logger.info(String.format("Uploading thumbnail image at %s", thumbnailLocation));
        BufferedImage img = ImageIO.read(imageLocation);
        imageExtension = imageExtension.replace(".", ""); // remove any . before the extension.

        BufferedImage bufferedImage = new BufferedImage(img.getWidth() / THUMBNAIL_SCALE_WIDTH, img.getHeight() / THUMBNAIL_SCALE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        bufferedImage.createGraphics().drawImage(img, 0, 0, img.getWidth() / THUMBNAIL_SCALE_WIDTH, img.getHeight() / THUMBNAIL_SCALE_HEIGHT, null);
        ImageIO.write(bufferedImage, imageExtension, thumbnailLocation);
    }

    public byte[] getImage(File file) throws IOException {
        logger.info(String.format("Retrieving Image from %s", file));
        BufferedImage bufferedImage = ImageIO.read(file);
        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
        return ( data.getData() );

    }

}
