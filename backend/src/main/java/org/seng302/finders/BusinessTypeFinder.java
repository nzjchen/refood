package org.seng302.finders;

import org.seng302.models.BusinessType;
import org.seng302.models.Listing;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BusinessTypeFinder {

    /**
     * Helper function that breaks a query into smaller chunks.
     * @param query Query string for filtering the businesses
     * @return list of terms derived from query.
     */
    private ArrayList<String> searchQueryKeywords(String query) {
        ArrayList<String> terms = new ArrayList<>();
        Matcher matcher = Pattern.compile("([^\"]\\S*|\"[^\"]*+\")\\s*").matcher(query);
        while (matcher.find()) {
            terms.add(matcher.group().replace("\"", ""));
        }
        return terms;
    }

    /**
     * Query builder for filtering business by their types.
     * @param term Part of query string for filtering the businesses
     * @return Specification<Listing> object to help with filtering businesses by their types.
     */
    private Specification<Listing> businessTypeSpec(String term) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("inventoryItem").get("product")
                .get("business").get("businessType"), BusinessType.valueOf(term.toUpperCase().replace(' ', '_').replace('-', '_')));
    }

    /**
     * The only public method in this class, this method retrieves a Specification object to help with
     * querying businesses by their types.
     * @param query Query string for filtering the businesses
     * @return Specification<Listing> object to help with filtering businesses by their types.
     */
    public Specification<Listing> findListingByBizType(String query) {
        ArrayList<String> terms = searchQueryKeywords(query);
        return businessTypeSpec(terms.get(0)).and((root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.greaterThan(root.get("closes"), new Date(System.currentTimeMillis())));
    }
}
