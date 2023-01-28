package org.seng302.utilities.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.seng302.models.Business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom serializer to turn the set of Business objects found in User.businessesAdministered
 * into a list of business Id's
 */
public class BusinessesAdministeredListSerializer extends StdSerializer<List<Business>> {

    public BusinessesAdministeredListSerializer() {
        this(null);
    }

    public BusinessesAdministeredListSerializer(Class<List<Business>> t) {
        super(t);
    }


    @Override
    public void serialize(
        List<Business> businesses,
        JsonGenerator generator,
        SerializerProvider provider)
        throws IOException {

        List<String> businessNames = new ArrayList<>();
        for (Business business : businesses) {
            businessNames.add(business.getName());
        }
        generator.writeObject(businessNames);
    }

}
