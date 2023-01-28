package org.seng302.finders;

import org.seng302.models.Listing;
import org.seng302.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class AddressFinder {

    @Autowired
    BusinessRepository businessRepository;


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
     * Gets specification object to search a field with a term
     * @param term term that is going to be search
     * @param field field in entity that is going to be search
     * @return Specification<Product> containing matches for name
     */

    private Specification<Listing> containsTerm(String term, String field) {
      return (root, query, criteriaBuilder)
            -> criteriaBuilder.like(criteriaBuilder.lower(root.get("inventoryItem").get("product").get("business").get("address").get(field)), "%"+term.toLowerCase()+"%");
    }


    /**
     * Builds query using term, goes through each field and checks if the term is inside
     * @param term term that is going to be check
     * @return Specification containing matches for that query
     */
    private Specification<Listing> buildQuery(String term) {
        Specification<Listing> newSpec = containsTerm(term, "country");
        newSpec = newSpec.or(containsTerm(term, "suburb"));
        newSpec = newSpec.or(containsTerm(term, "city"));
        newSpec = newSpec.or(containsTerm(term, "region"));

        return newSpec;
    }

    /**
     * Builds query for next term then computes AND/OR depending on what predicate is given
     * @param currentSpecification existing specification that is going to be extended with more terms
     * @param nextTerm term that is going to be added to specification
     * @param predicate OR/AND terms used to compute correct predicate
     * @return Specification with full query of the next term
     */
    private Specification<Listing> checkFields(Specification<Listing> currentSpecification, String nextTerm, Logic predicate) {
        Specification<Listing> newSpec = buildQuery(nextTerm).and((root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.greaterThan(root.get("closes"), new Date(System.currentTimeMillis())));
        if(predicate.equals(Logic.AND)) {
            currentSpecification = currentSpecification.and(newSpec);
        } else if (predicate.equals(Logic.OR)) {
            currentSpecification = currentSpecification.or(newSpec);
        }
        return currentSpecification;
    }


    /**
     * Builds full specification object to be used in repository query
     * @param query search query, will be split up into terms and processed
     * @return Specification<Product> resulting specification that will contain all predicates
     */
    private Specification<Listing> buildAddressSpec(String query) {
        ArrayList<String> terms = searchQueryKeywords(query);
        Specification<Listing> specification = buildQuery(terms.get(0));
        for (String term : terms) {
            specification = getNextSpecification(specification, term, terms);
        }

        return specification;
    }

    /**
     * Gets next specification will perform AND or OR operation depending on current term and next term
     * @param specification current specification
     * @param term current term
     * @param terms list of terms, used to get next term in sequence
     * @return Specification<Product> current specification with AND or OR operation applied
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
     * The only publicly available method to access outside of this class to search for products
     * @param query The search query to be used to filter search results
     * @return Will return all products if query is blank, otherwise will filter according to what is in the query
     */
    public Specification<Listing> findAddress(String query) {
        return buildAddressSpec(query);
    }
}
