package org.seng302.finders;


import org.seng302.models.Listing;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class ProductFinder {


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
     * Gets specification objects if their name matches query
     * @param name name of product, used to find all instances of
     * @return Specification<Listing> containing matches for name
     */
    private Specification<Listing> nameContains(String name) {
        return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(criteriaBuilder.lower(root.get("inventoryItem").get("product").get("name")), "%"+name.toLowerCase()+"%");
    }


    /**
     * Builds full specification object to be used in repository query
     * @param query search query, will be split up into terms and processed
     * @return Specification<Listing> resulting specification that will contain all predicates
     */
    private Specification<Listing> buildProductSpec(String query) {
        ArrayList<String> terms = searchQueryKeywords(query);
        Specification<Listing> specification = nameContains(terms.get(0)).and((root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.greaterThan(root.get("closes"), new Date(System.currentTimeMillis())));
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
     * @return Specification<Listing> current specification with AND or OR operation applied
     */
    private Specification<Listing> getNextSpecification(Specification<Listing> specification, String term, ArrayList<String> terms) {
        if (terms.indexOf(term) != terms.size() - 1) {
            String nextTerm = terms.get(terms.indexOf(term) + 1);

            if (term.strip().equals("AND")) {
                specification = specification.and(nameContains(nextTerm));
            } else if (term.strip().equals("OR")) {
                specification = specification.or(nameContains(nextTerm));
            } else if(!nextTerm.equals("AND") && !nextTerm.equals("OR")) {
                specification = specification.and(nameContains(nextTerm));
            }
        }

        return specification;

    }

    /**
     * The only publicly available method to access outside of this class to search for products
     * @param query The search query to be used to filter search results
     * @return Will return all products if query is blank, otherwise will filter according to what is in the query
     */
    public Specification<Listing> findProduct(String query) {

        return buildProductSpec(query);

    }
}
