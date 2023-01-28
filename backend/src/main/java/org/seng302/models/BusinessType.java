package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for business type. String representation can be obtained by the overridden toString() method
 */
public enum BusinessType {
    ACCOMMODATION_AND_FOOD_SERVICES("Accommodation and Food Services"),
    RETAIL_TRADE("Retail Trade"),
    CHARITABLE_ORGANISATION("Charitable organisation"),
    NON_PROFIT_ORGANISATION("Non-profit organisation"),
    ADMINISTRATIVE_AND_SUPPORT_SERVICES("Administrative and Support services"),
    AGRICULTURE_FORESTRY_AND_FISHING("Agriculture, Forestry and Fishing"),
    ARTS_AND_RECREATION_SERVICES("Arts and Recreation Services"),
    CONSTRUCTION("Construction"),
    EDUCATION_AND_TRAINING("Education and Training"),
    ELECTRICITY_GAS_WATER_AND_WASTE_SERVICES("Electricity, Gas, Water and Waste Services"),
    FINANCIAL_AND_INSURANCE_SERVICES("Financial and Insurance Services"),
    HEALTH_CARE_AND_SOCIAL_ASSISTANCE("Health Care and Social Assistance"),
    INFORMATION_MEDIA_AND_TELECOMMUNICATION("Information Media and Telecommunication"),
    MANUFACTURING("Manufacturing"),
    MINING("Mining"),
    PROFESSIONAL_SCIENTIFIC_AND_TECHNICAL_SERVICES("Professional, Scientific and Technical Services"),
    PUBLIC_ADMINISTRATION_AND_SAFETY("Public Administration and Safety"),
    RENTAL_HIRING_AND_REAL_ESTATE_SERVICES("Rental, Hiring and Real Estate Services"),
    TRANSPORT_POSTAL_AND_WAREHOUSING("Transport, Postal and Warehousing"),
    WHOLESALE_TRADE("Wholesale Trade"),
    OTHER_SERVICES("Other Services");


    private final String label;

    BusinessType(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.label;
    }
}
