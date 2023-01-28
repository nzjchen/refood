package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Entity class that holds information of an address for a user or business.
 */
@Data
@Entity
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    private String streetNumber;
    private String streetName;
    private String suburb;
    private String city;
    private String region;
    @NotNull
    private String country;
    private String postcode;

    /**
     * Constructor for Spring JPA use.
     */
    protected Address() {}

    /**
     * Create a new Address entity, with all fields being set (potentially null).
     * @param streetNumber number of the street
     * @param streetName name of the road or street the entity is based.
     * @param suburb the suburb.
     * @param city the city.
     * @param region the region/province/area the entity is based in.
     * @param country the country.
     * @param postcode postcode where the address is in.
     */
    public Address(String streetNumber, String streetName, String suburb, String city, String region, String country, String postcode) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = suburb;
        this.city = city;
        this.region = region;
        this.country = country;
        this.postcode = postcode;
    }

    /**
     * Create a new Address entity, without the suburb field.
     * This should not be used - this is available to keep test Address objects working.
     * @param streetNumber number of the street
     * @param streetName name of the road or street the entity is based.
     * @param city the city.
     * @param region the region/province/area the entity is based in.
     * @param country the country.
     * @param postcode postcode where the address is in.
     */
    public Address(String streetNumber, String streetName, String city, String region, String country, String postcode) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.suburb = null;
        this.city = city;
        this.region = region;
        this.country = country;
        this.postcode = postcode;
    }
}
