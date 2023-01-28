package org.seng302.finders;

import org.seng302.models.Business;
import org.seng302.models.BusinessType;
import org.seng302.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.regex.Pattern;


@Component
public class BusinessFinder {

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
     * Gets specification objects if their name matches query
     * @param name name of product, used to find all instances of
     * @return Specification<Business> containing matches for name
     */
    private Specification<Business> nameContains(String name) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    /**
     * Retrieve specification matches for all business with businessType matching the given type. Used to further filter
     * businesses for searching.
     * @param type Business type string
     * @return Specification<Business> with all businesses with
     */
    private Specification<Business> typeFilter(String type) throws ResponseStatusException {
        String attribute = "businessType";
        try {
            BusinessType typeEnum = BusinessType.valueOf(type.toUpperCase().replace(",", "").replace(" ", "_"));
            return (root, query, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get(attribute), typeEnum);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid businessType");
        }
    }


    /**
     * Builds full specification object to be used in repository query
     * @param query search query, will be split up into terms and processed
     * @return Specification<Product> resulting specification that will contain all predicates
     */
    private Specification<Business> buildBusinessSpec(String query, String type) {
        ArrayList<String> terms = searchQueryKeywords(query);
        Specification<Business> specification;
        if (!terms.isEmpty()) {
            specification = nameContains(terms.get(0));
            for (String term : terms) {
                specification = getNextSpecification(specification, term, terms);
            }
            if (type != null && type.length() > 0) {
                specification = specification.and(typeFilter(type));
            }
        } else if (type != null && type.length() > 0) {
            specification = typeFilter(type);
        } else {
            specification = nameContains("");
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
    private Specification<Business> getNextSpecification(Specification<Business> specification, String term, ArrayList<String> terms) {
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
    public Specification<Business> findBusinesses(String query, String type) {
        return buildBusinessSpec(query, type);
    }
}




