package org.seng302.finders;

import org.seng302.models.Listing;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class ListingFinder {

    /**
     * Helper function to break down query into subqueries
     * @param query Query to be broken. (It cannot take empty strings)
     * @return ArrayList of subqueries
     */
    private ArrayList<String> searchQueryKeywords(String query) {
        ArrayList<String> terms = new ArrayList<>();
        var matcher = Pattern.compile("([^\"]\\S*|\"[^\"]*+\")\\s*").matcher(query);
        while (matcher.find()) {
            boolean fullMatch = matcher.group().trim().startsWith("\"") && matcher.group().trim().endsWith("\"");

            String temp;
            if(!fullMatch) { //If not inside quotes
                temp = matcher.group().replace("\"", "").trim();
            } else {
                temp = matcher.group().trim().replace("\"", ""); //need to trim beforehand as spaces are needed
            }

            terms.add(temp);
        }

        return terms;
    }

    /**
     * Query criteria for business/seller name
     * @param term part of query that helps find the desired business(es)
     * @return Specification object that contains Listings.
     */
    private Specification<Listing> sellerContains(String term) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("inventoryItem").get("product")
                .get("business").get("name")), "%"+term.toLowerCase()+"%");
    }

    /**
     * Helper function that checks the fields if a predicate contains AND, OR, or any other term.
     * @param currentSpecification Specification object to be checked
     * @param nextTerm The upcoming term in the query
     * @param predicate Predicate object
     * @return Specification<Listing> object
     */
    private Specification<Listing> checkFields(Specification<Listing> currentSpecification, String nextTerm, Logic predicate) {
        Specification<Listing> newSpec = sellerContains(nextTerm).and((root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.greaterThan(root.get("closes"), new Date(System.currentTimeMillis())));
        if (predicate.equals(Logic.AND)) {
            currentSpecification = currentSpecification.and(newSpec);
        } else if (predicate.equals(Logic.OR)) {
            currentSpecification = currentSpecification.or(newSpec);
        }
        return currentSpecification;
    }

    /**
     * Gets next specification will perform AND or OR operation depending on current term and next term
     * @param specification current specification
     * @param term current term
     * @param terms list of terms, used to get next term in sequence
     * @return Specification<Listing> current specification with AND or OR operation applied
     */
    private Specification<Listing> getNextSpecification(Specification<Listing> specification, String term, ArrayList<String> terms) {
        if (terms.indexOf(term) != terms.size() - 1) {
            String nextTerm = terms.get(terms.indexOf(term) + 1);
            if (term.strip().equals("AND")) {
                specification = checkFields(specification, nextTerm, Logic.AND);
            } else if (term.strip().equals("OR")) {
                specification = checkFields(specification, nextTerm, Logic.OR);
            } else if(!nextTerm.equals("AND") && !nextTerm.equals("OR")) {
                specification = checkFields(specification, nextTerm, Logic.AND);
            }
        }
        return specification;

    }

    /**
     * Builds full specification object to be used in repository query
     * @param query search query, will be split up into terms and processed
     * @return Specification<Listing> resulting specification that will contain all predicates
     */
    private Specification<Listing> buildAddressSpec(String query) {
        ArrayList<String> terms = searchQueryKeywords(query);

        Specification<Listing> specification = sellerContains(terms.get(0));
        for (String term : terms) {
            specification = getNextSpecification(specification, term, terms);
        }
        return specification;
    }

    /**
     * The only publicly available method to access outside of this class to search for listings
     * @param query The search query to be used to filter search results
     * @return Will return all products if query is blank, otherwise will filter according to what is in the query
     */
    public Specification<Listing> findListing(String query) {
        return buildAddressSpec(query);
    }
}
