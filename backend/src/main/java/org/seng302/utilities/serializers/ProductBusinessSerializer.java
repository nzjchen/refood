package org.seng302.utilities.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.seng302.models.Business;

import java.io.IOException;

/**
 * Custom serializer to turn the Business objects found in the Product object into a more digestible form.
 * This will help solve any nesting issues.
 */
public class ProductBusinessSerializer extends StdSerializer<Business> {

    public ProductBusinessSerializer() {
        this(null);
    }

    public ProductBusinessSerializer(Class<Business> t) {
        super(t);
    }


    @Override
    public void serialize(
        Business business,
        JsonGenerator generator,
        SerializerProvider provider)
        throws IOException {

        Business newBusiness = new Business(business.getName(), business.getDescription(), business.getAddress(), business.getBusinessType());
        newBusiness.setCreated(business.getCreated());
        newBusiness.setId(business.getId());
        generator.writeObject(newBusiness);
    }

}
