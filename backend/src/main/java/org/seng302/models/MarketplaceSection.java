package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * MarketplaceSection Enumerator
 * Sets which category a Card entity is in:
 * For Sale, Wanted or Exchange
 * These will correspond to tabbed pages located in the frontend Marketplace page
 */
public enum MarketplaceSection {
    FORSALE("ForSale"),
    WANTED("Wanted"),
    EXCHANGE("Exchange");

    private final String label;

    MarketplaceSection(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.label;
    }
}
