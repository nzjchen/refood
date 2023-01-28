package org.seng302.utilities.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.seng302.models.User;

import java.io.IOException;

/**
 * Serializer class that forces the primaryAdministrator field in the Business class to be the id instead.
 */
public class PrimaryAdministratorSerializer extends StdSerializer<User> {

    public PrimaryAdministratorSerializer() {
        this(null);
    }

    public PrimaryAdministratorSerializer(Class<User> t) {
        super(t);
    }


    @Override
    public void serialize(User value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getId());
    }
}
