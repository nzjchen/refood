<template>
  <div class="card">
    <h3 class="card-header">Modify your business' details</h3>
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

      <vs-textarea type="text" class="form-control text-areas" label="Business Description" v-model="description" :counter="200"/>

      <vs-divider style="grid-row: 4;"></vs-divider>
      <div class="label-control">Address</div>

      <div v-if="currencyAlert">
        <vs-alert active="true"
                  icon="priority_high"
                  style="grid-row: 4; grid-column: 1/4; height: fit-content; width: 100%; text-align: center">
          Changing your current country will change your currency for associated listings and products.
        </vs-alert>
      </div>

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
          <vs-input @blur="suggestCountries = false;" :danger="this.errors.includes('country')" @input="getCountries(); checkUpdateCurrency()" v-model="country" class="form-control" label="Country *"></vs-input>
          <ul v-if="this.suggestCountries" class="suggested-box">
            <li v-for="suggested in this.suggestedCountries" @mousedown="setCountry(suggested)" :key="suggested" :value="suggested" class="suggested-item">{{suggested}}</li>
          </ul>
        </div>
      </div>

      <vs-button class="modify-button" @click="checkForm()">Modify</vs-button>
    </form>
  </div>
</template>

<script>
import api from "../Api";
import BusinessCommon from "./BusinessCommon";
import ActingAs from "./ActingAs";
export default {
  name: "ModifyBusiness",
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
      business: null,
      currencyAlert: false
    };
  },
  methods: {

    /**
     * Check whether the country has changed, if so
     * ask the user if they want to change the currency
     */
    checkUpdateCurrency: function() {
      if(this.country !== this.business.address.country) {
        this.currencyAlert = true;
      }
    },


    /**
     * method to update business
     * @param id
     */
    modifyBusiness: function (id) {
      api.updateBusiness(this.businessName, this.description, this.streetNumber, this.streetAddress,this.suburb,
          this.city, this.region, this.country, this.postcode, this.businessType ,id)
          .then(() => {
            this.$vs.notify({title:'Success',
              text:'The business have been successfully modified!',
              color:'success'});
            this.$router.push({ path: '/home' })
            ActingAs.methods.setActingAsBusinessId(id, this.businessName);
          }).catch(() => {
        this.$vs.notify({title:'Error', text:'Error modifying business'});
      })
    },

    /**
     * Method for checking the form is compliant for modifying businesses
     */
    checkForm: function() {
      if (this.businessName.length === 0 && this.streetNumber.length === 0 &&
          this.streetAddress.length === 0 && this.suburb.length === 0 &&
          this.postcode.length === 0 && this.city.length === 0 &&
          this.region.length === 0 && this.country.length === 0 &&
          this.description.length === 0 && this.businessType === null) {
        this.$vs.notify({title:'Cannot modify business', text:'Required fields are missing.', color:'danger'});
      } else {
        this.errors = BusinessCommon.businessCheckForm(this.businessName, this.description, this.country, this.businessType);
        if (this.errors.length === 0) {
          this.modifyBusiness(this.$route.params.id);
        } else {
          if (this.errors.includes("country")) {
            this.$vs.notify({title:'Error', text:'Country field is required', color:'danger'});
          }
          if (this.errors.includes("businessName")) {
            this.$vs.notify({title:'Error', text:'Business name is required', color:'danger'});
          }
          if (this.errors.includes("description")) {
            this.$vs.notify({title:'Error', text:'Description too long', color:'danger'});

          }
        }
      }
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
     * Set the country as the new country.
     * @param selectedCountry the country string to set as.
     */
    setCountry: function(selectedCountry) {
      this.country = selectedCountry;
      this.suggestCountries = false;
    },
  },
  mounted: async function () {
    await api.getBusinessFromId(this.$route.params.id)
        .then((res) => {
          this.business = res.data;
        })
        .catch((error) => {
          this.$log.error(error);
        })
    this.businessName = this.business.name;
    this.streetAddress = this.business.address.streetName;
    this.streetNumber = this.business.address.streetNumber;
    this.suburb = this.business.address.suburb;
    this.postcode = this.business.address.postcode;
    this.region = this.business.address.region;
    this.city = this.business.address.city;
    this.country = this.business.address.country;
    this.description = this.business.description;
    this.businessType = this.business.businessType;
    this.getBusinessTypes();
  },
}
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

.modify-button {
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
  margin-top: 1.5em;
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
