import {store} from "../store";
import axios from "axios";

export default {

    /**
     * A function to check for a business registration or modification form
     * @param businessName Name of the business
     * @param description Description of the business
     * @param country The business' country
     * @param businessType The type of the business
     * @returns Will return an empty list if there are no errors,
     * otherwise, will return the list of errors
     */
    businessCheckForm: function(businessName, description, country, businessType) {
        let errors = [];

        if (businessName === undefined || businessName.length === 0) {
            errors.push('businessName');
        }

        if (description != null) {
            if (description.length > 200) {
                errors.push('description');
            }
        }

        if (country === undefined || country.length === 0) {
            errors.push('country');
        }

        if (!this.checkAge()){
            errors.push('dob');
        }

        if (!businessType) {
            errors.push('businessType');
        }
        return errors;
    },


    /**
     * Returns the years since the user was born. No rounding is done in the function.
     * @returns {boolean} Whether the user is old enough, 16, to register a business.
     */
    checkAge: function() {
        let enteredDate = store.userDateOfBirth;
        let years = new Date(new Date() - new Date(enteredDate)).getFullYear() - 1970;
        return (years >= 16);
    },

    /**
     * Retrieve a list of suggested cities using the photon open api.
     * @param city input to be inferred from
     * @param minNumOfChars minimum of characters
     * @returns {Promise<{"0": boolean, "1": []}>} returns a JSON object with a boolean and a list of suggested cities
     */
    getCitiesFromPhoton: async function(city, minNumOfChars) {
        if (city.length >= minNumOfChars) {
            let suggestedCities = [];
            let final = [];
            await axios.get(`https://photon.komoot.io/api/?q=${city}&osm_tag=place:city&lang=en`)
                .then( res => {
                    suggestedCities = res.data.features.map(location => location.properties.name);
                    suggestedCities = suggestedCities.filter(city != null);
                })
                .catch( error => {
                    console.log("Error with getting cities from photon." + error);
                });
            for (let i = 0; i < suggestedCities.length; i++) {
                if (!final.includes(suggestedCities[i])) {
                    final.push(suggestedCities[i]);
                }
            }
            return {'0': true,
                    '1': final};
        }
        else {
            return {'0': false,
                    '1': []};
        }
    },

    /**
     * Retrieve a list of suggested countries using the photon open api.
     * @param country input to be inferred from
     * @param minNumOfChars minimum of characters
     * @returns {Promise<{"0": boolean, "1": []}>} returns a JSON object with a boolean and a list of suggested countries
     */
    getCountriesFromPhoton: async function(country, minNumOfChars) {
        if (country.length >= minNumOfChars) {
            let suggestedCountries = []
            await axios.get(`https://photon.komoot.io/api/?q=${country}&osm_tag=place:country&lang=en`)
                .then( res => {
                    suggestedCountries = res.data.features.map(location => location.properties.country);
                })
                .catch( error => {

                    this.$log.error(error)
                });
            return {'0': true,
                    '1': suggestedCountries};
        }
        else {
            return {'0': false,
                    '1': []};
        }
    },
}