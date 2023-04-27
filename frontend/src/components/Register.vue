<template>
  <div class="card">
    <h3 class="card-header">Create a ReFood Account</h3>
    <form>
      <div id="info-field">
        <div id="firstname">
          <vs-input :danger="(errors.includes(firstname))"
                    danger-text="First Name must be between 2 and 20 letters."
                    :success="(firstname.length >= 2 && firstname.length < 20)"
                    class="form-control"
                    type="text"
                    label="First Name *"
                    v-model="firstname"/>
        </div>
        <div id="middlename">
          <vs-input type="text"
                    class="form-control"
                    label="Middle Name"
                    :danger="middlename.length>20"
                    danger-text="Middle Name must be less than 20 characters"
                    :success="middlename.length > 0 && middlename.length < 20"
                    v-model="middlename"/>
        </div>
        <div id="lastname">
          <vs-input :danger="(errors.includes(lastname))"
                    danger-text="Last Name must be between 2 and 20 letters."
                    :success="(lastname.length >= 2 && lastname.length < 20)"
                    type="text"
                    class="form-control"
                    label="Last name *"
                    v-model="lastname"/>
        </div>
        <div id="nickname">
          <vs-input type="text"
                    class="form-control"
                    label="Nickname"
                    :danger="nickname.length>20"
                    danger-text="Nickname must be less than 20 characters"
                    :success="nickname.length > 0 && nickname.length < 20"
                    name="nickname"
                    v-model="nickname"/>
        </div>
        <div id="email">
          <vs-input type="email"
                    class="form-control"
                    label="Email *"
                    :danger="errors.includes(email) && emailInUse"
                    danger-text="Invalid email. (This email may already be in use)"
                    :success="validEmail(email) && !emailInUse"
                    v-model="email"/>
        </div>
        <div id="phonenumber">
          <vs-input type="tel"
                    class="form-control"
                    label="Phone number"
                    :danger="phonenumber.length > 0 && errors.includes(phonenumber)"
                    danger-text="Invalid phone number."
                    :success="validPhoneNum(phonenumber)"
                    name="phonenumber"
                    v-model="phonenumber"/>
        </div>
        <div id="date-of-birth">
          <vs-input type="date"
                    class="form-control"
                    name="dateofbirth"
                    v-model="dateofbirth"
                    :danger="errors.includes(dateofbirth)"
                    danger-text="Enter date of birth"
                    :success="(dateofbirth.length!==0)"
                    min="1900-01-01"
                    :max="maxAllowedDoB"
                    label="Date of birth *"/>
        </div>
        <div id="password-info">
          Password must be at least 8 characters long and must contain one of each of the following:
          <ul>
            <li>Uppercase letter</li>
            <li>Lowercase letter</li>
            <li>Number</li>
            <li>Special Character</li>
          </ul>
        </div>
        <div id="password">
          <vs-input type="password"
                    class="form-control"
                    label="Password *"
                    :danger="password.length > 0 || errors.includes(password)"
                    danger-text="Your password does not meet the requirements."
                    :success="validPassword(password)"
                    name="password (Required)"
                    v-model="password"/>
        </div>
        <div id="confirm-password">
          <vs-input type="password"
                    class="form-control"
                    label="Confirm Password *"
                    :danger="errors.includes(confirm_password)"
                    danger-text="Your password is invalid or do not match."
                    :success="(confirm_password===password && confirm_password.length !== 0)"
                    name="confirm_password (Required)"
                    v-model="confirm_password"/>
        </div>
      </div>
      <!-- ADDRESS -->
      <vs-divider style="grid-row: 2; grid-column: 1/3;"></vs-divider>
      <div class="label-control">Address</div>

      <div id="address-field">
        <div id="street-number">
          <vs-input v-model="streetNumber" class="form-control" label="Street Number"></vs-input>
        </div>
        <div id="street-name">
          <vs-input v-model="streetName" class="form-control" label="Street Name"></vs-input>
        </div>
        <div id="suburb">
          <vs-input v-model="suburb" class="form-control" label="Suburb"></vs-input>
        </div>

        <div id="postcode">
          <vs-input v-model="postcode" class="form-control" label="Postcode"></vs-input>
        </div>
        <div id="city">
          <!-- If wanting to test/check suggested item tiles, remove blur. -->
          <vs-input autocomplete="none" @blur="suggestCities = false;" v-model="city" @input="getCitiesFromPhoton()" class="form-control" label="City"></vs-input>
          <ul v-if="this.suggestCities" class="suggested-box">
            <li v-for="suggested in this.suggestedCities" @mousedown="setCity(suggested)" :key="suggested" :value="suggested" class="suggested-item">{{suggested}}</li>
          </ul>
        </div>
        <div id="region">
          <vs-input v-model="region" class="form-control" label="Region"></vs-input>
        </div>
        <div id="country">
          <vs-input autocomplete="none" @blur="suggestCountries = false;" :danger="this.errors.includes('country')" danger-text="Country required." :success="country.length > 0" @input="getCountriesFromPhoton()" v-model="country" class="form-control" label="Country *"></vs-input>
          <ul v-if="this.suggestCountries" class="suggested-box">
            <li v-for="suggested in this.suggestedCountries" @mousedown="setCountry(suggested)" :key="suggested" :value="suggested" class="suggested-item">{{suggested}}</li>
          </ul>
        </div>
      </div>

      <div id="bio">
        <vs-textarea type="text" class="form-control text-areas" label="Bio" name="bio" v-model="bio"></vs-textarea>
      </div>

      <vs-button class="register-button" @click="checkForm(); createUserInfo()">Register</vs-button>
      <div id="login-container">
        <label>Already registered? </label>
        <router-link to="/login">
          <vs-button type="border" class="login-button">Login</vs-button>
        </router-link>
      </div>


    </form>
  </div>
</template>
<script>
import api from "../Api";
import axios from "axios";
import {mutations} from '../store.js';


const Register = {
  name: "Register",
  data: function () {
    return {
      emailInUse: false,
      errors: [],
      firstname: "",
      middlename: "",
      lastname: "",
      nickname: "",
      bio: null,
      email: "",
      password: "",
      confirm_password: "",
      dateofbirth: "",
      phonenumber: "",

      streetNumber: "",
      streetName: "",
      suburb: "",
      postcode: "",
      city: "",
      region: "",
      country: "",
      maxAllowedDoB: new Date(),

      suggestCities: false,
      suggestedCities: [],

      suggestCountries: false,
      suggestedCountries: [],
      minNumberOfCharacters: 3


    };
  },
  mounted() {
    this.maxAllowedDoB.setFullYear(this.maxAllowedDoB.getFullYear()-13);
    this.maxAllowedDoB = this.maxAllowedDoB.toISOString().split('T')[0];
  },
  methods:{
    /**
     * The function checks the inputs of the registration form to ensure they are in the right format.
     * The function also updates the errors list that will be displayed on the page if at least one of the input boxes
     * is in the wrong format.
     */
    checkForm: function() {
      this.errors = [];
      if (this.firstname.length < 2 || this.firstname.length >= 20) {
        this.errors.push(this.firstname);
      }

      if (this.lastname.length < 2 || this.lastname.length >= 20) {
        this.errors.push(this.lastname);
      }

      if (!this.validEmail(this.email)) {
        this.errors.push(this.email);
      }
      if (this.bio != null) {
        if (this.bio.length > 40) {
          this.errors.push(this.bio);
        }
      }

      if (!this.validPassword(this.password)) {
        this.errors.push(this.password);
      }

      if (this.confirm_password.length === 0 || this.password !== this.confirm_password) {
        this.errors.push(this.confirm_password);
      }
      if (this.dateofbirth.length === 0) {
        this.errors.push(this.dateofbirth);
      }

      if (this.country.length === 0) {
        this.errors.push('country');
      }

      if (this.phonenumber !== null && this.phonenumber !== "" && !this.validPhoneNum(this.phonenumber)) {
        this.errors.push(this.phonenumber);
      }

      if (this.errors.length >= 1) {
        this.$vs.notify({title: 'Failed to register', text: 'Required fields are missing.', color: 'danger'});
        if (this.errors.includes(this.bio)) {
          this.$vs.notify({
            title: 'Failed to register',
            text: 'Bio must be less that 40 characters.',
            color: 'danger'
          });
        }
      }
    },


    /**
     * The function ensures that the inputted email is in the right email format using a Regular Expression to check it.
     * @param email The email to be checked.
     * @returns {boolean} True if is in the right format; otherwise, false.
     */
    validEmail: function (email) {
      var re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },

    /**
     * Checks if the inputted phone number is in the right format using a Regular Expression to check it.
     * @param phonenumber The phone number to be checked
     * @returns {boolean} True if is in the right format; otherwise, false.
     */
    validPhoneNum: function (phonenumber) {
      var re = /(?<![A-Za-z0-9.])[0-9.]+(?![A-Za-z0-9.])/;
      return re.test(phonenumber);
    },

    /**
     * Checks if the new user's password has at least one lowercase letter, one uppercase letter, one number,
     * one special character, and is at least 8 characters long.
     * @param password The password to be checked
     * @returns {boolean} True if is in the right format; otherwise, false.
     */
    validPassword: function (password) {
      var re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
      return re.test(password);
    },

    /**
     * Creates a POST request when user submits form, using the createUser function from Api.js
     */
    createUserInfo: function() {
      if(this.errors.length === 0){
        this.emailInUse = false;
        const homeAddress = {
          streetNumber: this.streetNumber,
          streetName: this.streetName,
          suburb: this.suburb,
          city:this.city,
          region: this.region,
          country: this.country,
          postcode: this.postcode
        }

        api.createUser(this.firstname, this.middlename, this.lastname, this.nickname, this.bio, this.email, this.dateofbirth, this.phonenumber, homeAddress, this.password)
            .then((response) => {
              this.$log.debug("New item created:", response.data);
              mutations.setUserLoggedIn(response.data.userId, response.data.role); //Store user info into program state, used for later calls
              api.login(this.email, this.password)
                  .then((innerResponse) => {
                    mutations.setUserLoggedIn(innerResponse.data.userId, innerResponse.data.role);
                    //LOAD USER PAGE, USING ROUTER
                    this.$router.push({path: '/home'})
                  }).catch((error) => {
                this.$log.debug("Error logging in from registration: " + error);
              });
            }).catch((error) => {
          if(error.response.status === 409){
            this.emailInUse = true;
            this.errors.push(this.email);
          }
        });
      }},


    getCitiesFromPhoton: function() {
      if (this.city.length >= this.minNumberOfCharacters) {

        this.suggestCities = true;
        axios.get(`https://photon.komoot.io/api/?q=${this.city}&osm_tag=place:city&lang=en`)
            .then( res => {
              this.suggestedCities = res.data.features.map(location => location.properties.name);
              this.suggestedCities = this.suggestedCities.filter(city => city != null);
              var found = {};
              this.suggestedCities = this.suggestedCities.filter(function(country) {
                // eslint-disable-next-line no-prototype-builtins
                return found.hasOwnProperty(country) ? false : (found[country] = true);
              });
            })
            .catch( error => {
              console.log("Error with getting cities from photon." + error);
            });
      }
      else {
        this.suggestCities = false;
      }
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
     * Retrieve a list of suggested countries using the photon open api.
     */
    getCountriesFromPhoton: function() {
      if (this.country.length >= this.minNumberOfCharacters) {

        this.suggestCountries = true;
        axios.get(`https://photon.komoot.io/api/?q=${this.country}&osm_tag=place:country&lang=en`)
            .then( res => {
              this.suggestedCountries = res.data.features.map(location => location.properties.country);
            })
            .catch( error => {
              console.log("Error with getting countries from photon." + error);
            });
      }
      else {
        this.suggestCountries = false;
      }
    },

    /**
     * Set the country as the new country.
     * @param selectedCountry the country string to set as.
     */
    setCountry: function(selectedCountry) {
      this.country = selectedCountry;
      this.suggestCountries = false;
    },
  }
}
export default Register;
</script>
<style scoped>

/*
Register button's styling
 */
.register-button {
  grid-column: 2;
  grid-row: 6;
  width: 150px;
  margin: auto 1em 1em auto;
}

/*
Login button's styling
 */
#login-container {
  grid-column: 1;
  grid-row: 6;
  text-align: center;
  margin: auto auto 1em 1em;
}

#login-container label {
  margin: auto;
}

.login-button {
  width: 150px;
  margin: auto;
}

/**
Card styling.
 */
.card {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto auto;
  grid-row-gap: 1em;

  max-width: 800px;
  background-color: white;
  margin: 1em auto;
  padding: 0.5em 0 0.5em 0;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  box-shadow: 0 .5rem 1rem rgba(0, 0, 0, .15);
}

/**
Card header styling.
 */
.card-header {
  grid-row: 1;
  grid-column: 1;

  text-align: center;
  font-weight: bold;
  font-size: 24px;
  color: #1F74FF;

  margin: 0;
  padding: 0.5em 0;
}

/**
Form styling
 */
form {
  grid-row: 2;
  grid-column: 1/3;

  margin: auto;

  display: grid;
  grid-template-columns: repeat(3, auto);
  grid-template-rows: repeat(4, auto);
}

label, input {
  display: block;
}

/**
Styling for form elements.
 */
.form-control {
  padding: 3px 10px;
  margin: 0.5em;
}

/**
Label styling.
 */
.label-control {
  grid-column: 1/3;
  font-family: 'Ubuntu', sans-serif;
  font-weight: 700;
  font-size: 14px;
  margin: auto auto 0 auto;
}

.suggested-box {
  position: absolute;
  list-style: none;
  width: 400px;
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

/* ===== TOP PART INFO FIELDS ===== */
#info-field {
  grid-column: 1/3;
  display: grid;
  margin: auto;
  grid-template-columns: repeat(3, auto);
  grid-template-rows: repeat(auto-fit, auto);
}

#firstname {
  grid-column: 1;
  grid-row: 1;
}

#middlename {
  grid-column: 2;
  grid-row: 1;
}

#lastname {
  grid-column: 3;
  grid-row: 1;
}

#nickname {
  grid-column: 1;
  grid-row: 2;
}

#email {
  grid-column: 2 / 4;
  grid-row: 2;
  margin: auto 1em auto 0;
}

#email >>> .vs-input {
  width: 100%;
}

#phonenumber {
  grid-column: 1;
  grid-row: 3;
}
#password-info {
  grid-row: 4;
  grid-column: 1/4;
  font-size: 12px;
  margin-left: 2em;
}

#password {
  grid-column: 1;
  grid-row: 5;
}

#confirm-password {
  grid-column: 2;
  grid-row: 5;
}

#date-of-birth {
  grid-column: 2;
  grid-row: 3;
}

/* ===== ADDRESS FORM ===== */
#address-field {
  grid-column: 1/3;
  display: grid;
  grid-template-columns: repeat(3, auto);
  grid-template-rows: repeat(auto-fit, auto);
}

#street-number {
  grid-row: 1;
  grid-column: 1;
}

#street-name {
  grid-row: 1;
  grid-column: 2;
}

#suburb {
  grid-row: 1;
  grid-column: 3;
}

#city {
  grid-row: 2;
  grid-column: 2/4;
  margin: auto 1em auto 0;
}

#city >>> .vs-input {
  width: 100%;
}

#region {
  grid-row: 3;
  grid-column: 1;
}

#country {
  grid-row: 3;
  grid-column: 2/4;
  margin: auto 1em auto 0;
}

#country >>> .vs-input {
  width: 100%;
}

#postcode {
  grid-row: 2;
  grid-column: 1 / 3;
}

/* ===== BIO ===== */
#bio {
  grid-column: 1 / 3;
  grid-row: 5;
  margin: 1em;
}

.text-areas {
  margin: auto 0;
}

.text-areas >>> h4 {
  font-size: 14px;
  font-weight: 400;
}

.text-areas >>> textarea {
  max-width: 615px;
  max-height: 120px;
  min-height: 120px;
}

@media screen and (max-width: 700px) {
  .card {
    max-width: 90%;

  }

  form {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: repeat(auto-fit, auto);
    grid-row-gap: 0.5em;
    margin: 0;
  }

  #info-field {
    margin: 0 1em;
    grid-template-columns: 1fr;
    grid-template-rows: repeat(auto-fit, auto);
  }

  #firstname {
    grid-column: 1;
    grid-row: 1;
  }

  #middlename {
    grid-column: 1;
    grid-row: 2;
  }

  #lastname {
    grid-column: 1;
    grid-row: 3;
  }

  #nickname {
    grid-column: 1;
    grid-row: 4;
  }

  #email {
    grid-column: 1;
    grid-row: 5;
  }

  #phonenumber {
    grid-column: 1;
    grid-row: 6;
  }

  #date-of-birth {
    grid-column: 1;
    grid-row: 7;
  }

  #password-info {
    grid-column: 1;
    grid-row: 8;
  }

  #password {
    grid-column: 1;
    grid-row: 9;
  }

  #confirm-password {
    grid-column: 1;
    grid-row: 10;
  }

  #address-field {
    margin: 0 1em;
    grid-template-columns: 1fr;
    grid-template-rows: repeat(auto-fit, auto);
  }

  #street-number {
    grid-column: 1;
    grid-row: 1;
  }

  #street-name {
    grid-column: 1;
    grid-row: 2;
  }

  #suburb {
    grid-column: 1;
    grid-row: 3;
  }

  #city {
    grid-column: 1;
    grid-row: 4;
  }

  #region {
    grid-column: 1;
    grid-row: 5;
  }

  #country {
    grid-column: 1;
    grid-row: 6;
  }

  #postcode {
    grid-column: 1;
    grid-row: 7;
  }

  #bio {
    margin: 0 2em;
    grid-column: 1;
    grid-row: 6;
  }

  .register-button {
    grid-column: 1;
    grid-row: 7;
    margin: auto ;
  }

  #login-container {
    grid-column: 1;
    grid-row: 8;
    margin: auto;
    text-align: center;
  }

  .form-control {
    width: auto;
  }

}
</style>
