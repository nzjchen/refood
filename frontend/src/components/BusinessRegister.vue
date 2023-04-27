<template>
  <div class="card">
    <h3 class="card-header">Create a ReFood Business Account</h3>
    <form autocomplete="off">
      <vs-input id="business-name"
                :danger="this.errors.includes('businessName')"
                type="text"
                class="form-control"
                label="Business Name *"
                v-model="businessName"/>
      <vs-select
          width="90%"
          id="business-type"
          :danger="this.errors.includes('businessType')"
          class="form-control"
          label="Business Type *"
          v-model="businessType"
          autocomplete >
        <vs-select-item v-for="type in availableBusinessTypes" :key="type" :text="type" :value="type"/>
      </vs-select>

      <vs-textarea type="text" class="form-control text-areas" label="Business Description" v-model="description" :counter="140"/>

      <vs-divider style="grid-row: 4;"></vs-divider>
      <div class="label-control">Address</div>

      <div id="address-container">
        <div id="street-number">
          <vs-input v-model="streetNumber" class="form-control" label="Street Number"></vs-input>
        </div>
        <div id="street-address">
          <vs-input v-model="streetAddress" class="form-control" label="Street Address"></vs-input>
        </div>
        <div id="suburb">
          <vs-input v-model="suburb" class="form-control" label="Suburb"></vs-input>
        </div>
        <div id="postcode">
          <vs-input v-model="postcode" class="form-control" label="Postcode"></vs-input>
        </div>
        <div id="city">
          <!-- If wanting to test/check suggested item tiles, remove blur. -->
          <vs-input autocomplete="none" @blur="suggestCities = false;" v-model="city" @input="getCities()" class="form-control" label="City"></vs-input>
          <ul v-if="this.suggestCities" class="suggested-box">
            <li v-for="suggested in this.suggestedCities" @mousedown="setCity(suggested)" :key="suggested" :value="suggested" class="suggested-item">{{suggested}}</li>
          </ul>
        </div>
        <div id="region">
          <vs-input v-model="region" class="form-control" label="Region"></vs-input>
        </div>
        <div id="country">
          <vs-input @blur="suggestCountries = false;" :danger="this.errors.includes('country')" @input="getCountries()" v-model="country" class="form-control" label="Country *"></vs-input>
          <ul v-if="this.suggestCountries" class="suggested-box">
            <li v-for="suggested in this.suggestedCountries" @mousedown="setCountry(suggested)" :key="suggested" :value="suggested" class="suggested-item">{{suggested}}</li>
          </ul>
        </div>
      </div>

      <vs-button class="register-button" @click="checkForm(); createBusinessInfo()">Register</vs-button>
    </form>
  </div>
</template>

<script>
import api from "../Api";
import {mutations, store} from "../store";
import BusinessCommon from "./BusinessCommon";

const BusinessRegister = {
  name: "BusinessRegister",
  data: function () {
    return {
      availableBusinessTypes: [],
      errors: [],
      businessName: "",

      streetNumber: "",
      streetAddress: "",
      suburb: "",
      postcode: "",
      city: "",
      region: "",
      country: "",

      description: "",
      businessType: null,

      suggestCities: false,
      suggestedCities: [],

      suggestCountries: false,
      suggestedCountries: [],
      minNumberOfCharacters: 3,
      user: null
    };
  },
  methods:{
    /**
     * The function checks the inputs of the registration form to ensure they are in the right format.
     * Pushes name of error into an array, and display notification have errors exist.
     */
    checkForm: function () {
      this.errors = BusinessCommon.businessCheckForm(this.businessName, this.description, this.country, this.businessType);
      if (this.errors.length >= 1) {
        if(this.errors.includes("dob") && this.errors.length === 1){
          this.$vs.notify({title:'Failed to create business', text:'You are too young to create a ReFood account.', color:'danger'});
        } else if (this.errors.includes('description')) {
          this.$vs.notify({title:'Failed to create business', text:'Required fields are missing.', color:'danger'});
          this.$vs.notify({
            title: 'Failed to create business',
            text: 'description must be less that 140 characters.',
            color: 'danger'
          });
        } else {
          this.$vs.notify({title:'Failed to create business', text:'Required fields are missing.', color:'danger'});
        }
      }
    },
    /**
     * Creates a POST request when user submits form, using the createUser function from Api.js
     */
    createBusinessInfo: function() {
      // Use createUser function of API to POST user data to backend
      if(this.errors.length === 0) {
        let businessAddress = {
          streetNumber: this.streetNumber,
          streetName: this.streetAddress,
          suburb: this.suburb,
          city: this.city,
          region: this.region,
          country: this.country,
          postcode: this.postcode,
        };

        api.createBusiness(this.businessName, this.description, businessAddress, this.businessType)
          .then((response) => {
            api.actAsBusiness(response.data.businessId)
              .then((busResponse) => {
                this.$log.debug("New business created:", busResponse.data);
                mutations.setActingAsBusiness(response.data.businessId, this.businessName);
                this.$router.push({path: `/home`}).catch(() => {console.log("NavigationDuplicated Warning: same route.")});
              })
              .catch((error) => {
                this.$log.debug(error.response.message);
              });
          })
          .catch((error) => {
            if(error.response) {
              this.$log.debug(error.response.status);
              this.$log.debug(error.response.message);
              this.errors.push("Access token is missing/invalid");
            }
            this.$log.debug("Error Status:", error)
          });
      }},

    /**
     * Utilises BusinessCommon.js' getCountriesFromPhoton to suggest countries
     */
    getCountries: async function() {
      let data = await BusinessCommon.getCountriesFromPhoton(this.country, this.minNumberOfCharacters);
      this.suggestCountries = data['0'];
      this.suggestedCountries = data['1'];
    },

    /**
     * Utilises BusinessCommon.js' getCountriesFromPhoton to suggest cities
     */
    getCities: async function() {
      let data = await BusinessCommon.getCitiesFromPhoton(this.city, this.minNumberOfCharacters);
      console.log(data)
      this.suggestCities = data['0'];
      this.suggestedCities = data['1'];
    },

    /**
     * Set the city as the new city.
     * @param selectedCity string to set as the new city.
     */
    setCity: function(selectedCity) {
      this.city = selectedCity;
      this.suggestCities = false;
    },

    /**
     * Gets all business types from the database, to
     * be used by business type filter
     * */
    getBusinessTypes: function() {
      api.getBusinessTypes()
          .then((response) => {
            this.availableBusinessTypes = response.data
          }).catch((err) => {
        if(err.response.status === 401) {
          this.$vs.notify({title:'Error', text:'Unauthorized', color:'danger'});
        }
        else {
          this.$vs.notify({title:'Error', text:`Status Code ${err.response.status}`, color:'danger'});
        }
      });
    },

    /**
     * Set the country as the new country.
     * @param selectedCountry the country string to set as.
     */
    setCountry: function(selectedCountry) {
      this.country = selectedCountry;
      this.suggestCountries = false;
    },

    getUserInfo: function (userId) {
      api.getUserFromID(userId)
          .then((response) => {
            this.user = response.data;
          }).catch((err) => {
        if (err.response.status === 401) {
          this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
          this.$router.push({name: 'LoginPage'});
        } else {
          this.$log.error(err);
        }

      });
    },

    checkLoggedIn: function() {
      if (store.loggedInUserId == null) {
        this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
        this.$router.push({name: 'LoginPage'});
      }
    },
  },

  mounted: function () {
    api.checkSession()
        .then((response) => {
          this.getUserInfo(response.data.id);
        }).catch(() => {
      this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
    });
    this.getBusinessTypes();
  }


}
export default BusinessRegister;

</script>

<style scoped>

.suggested-box {
  position: absolute;
  display: inline-block;
  list-style: none;
  width: 300px;
}

.suggested-item {
  cursor: pointer;
  position: relative;
  margin: 0 0 0 1em;

  border: 2px solid rgba(0, 0, 0, 0.02);
  padding: 0.5em 1em;
  background: white;
  z-index: 99;
}

.suggested-item:hover {
  background: lightgray;
}

.label-control {
  font-weight: 700;
  font-size: 16px;
  margin: auto auto 0.5em auto;
}

.register-button {
  margin: 1em auto;
  width: 150px;
}

.card {
  max-width: 800px;
  background-color: white;
  margin: 1em auto;
  padding: 0.5em 0 0.5em 0;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  box-shadow: 0 .5rem 1rem rgba(0, 0, 0, .15);

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto auto;
  grid-row-gap: 1em;

}

.card-header {
  grid-row: 1;

  text-align: center;
  font-weight: bold;
  font-size: 24px;
  color: #1F74FF;

  margin: 0;
  padding: 1em 0;

}

form {
  grid-row: 2;

  margin: 0 4em;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(5, auto);
  grid-row-gap: 4px;

}

.form-control {
  margin: 0.25em auto;
  width: 90%;
}

#business-name {
  padding: 0;
}

/* ===== ADDRESS CONTAINER ===== */
#address-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(auto-fit, auto);
  margin: 0 1.5em;
}

input {
  margin: 0;
}

#street-number {
  grid-row: 1;
  grid-column: 1;
}

#street-address {
  grid-row: 1;
  grid-column: 2;
}

#suburb {
  grid-row: 1;
  grid-column: 3;
}

#city {
  grid-row: 2;
  grid-column: 2 / 4;
  margin: auto 0;
}

#city >>> .vs-input {
  width: 95%;
}

#region {
  grid-row: 3;
  grid-column: 1;
}

#country {
  grid-row: 3;
  grid-column: 2 / 4;
}

#country >>> .vs-input {
  width: 95%;
}

#postcode {
  grid-row: 2;
  grid-column: 1;
}

.text-areas {
  margin-top: 1em;
}

.text-areas >>> h4 {
  font-size: 14px;
  font-weight: 400;
}

.text-areas >>> textarea {
  max-width: 500px;
  min-height: 70px;
  max-height: 70px;
}

@media screen and (max-width: 825px) {
  .card {
    width: 90%;
  }
}

@media screen and (max-width: 600px) {
  #address-container {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: repeat(auto-fit, auto);
    margin: 0 1.5em;
  }

  #street-number {
    grid-column: 1;
    grid-row: 1;
  }

  #street-address {
    grid-column: 1;
    grid-row: 2;
  }

  #suburb {
    grid-column: 1;
    grid-row: 3;
  }

  #postcode {
    grid-column: 1;
    grid-row: 4;
  }

  #city {
    grid-column: 1;
    grid-row: 5;
  }

  #city >>> .vs-input {
    margin: 0 1em;
    width: auto;
  }

  #region {
    grid-column: 1;
    grid-row: 6;
  }

  #country {
    grid-column: 1;
    grid-row: 7;
  }

  #country >>> .vs-input {
    margin: 0 1em;
    width: auto;
  }

}

</style>
