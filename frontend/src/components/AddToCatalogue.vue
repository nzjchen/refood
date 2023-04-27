<template>
  <div class="card" v-if="this.user != null">
    <h3 class="card-header">Add a Product to your Catalogue</h3>
    <form>
      <div id="info-field">
        <div id="product-name-field">
          <vs-input
              :danger="(errors.includes(productName))"
              danger-text="Product name is required"
              class="form-control"
              type="text"
              label="Product name *"
              v-model="productName"/>
        </div>
        <div id="product-id">
          <vs-input
              :danger="(errors.includes(productId))"
              danger-text="Product id is required"
              class="form-control"
              type="text"
              label="Product ID *"
              v-model="productId"/>
        </div>
        <div id="rrp">
          <div id="currencySymbol">{{this.currencySymbol}}</div>
          <vs-input
              :danger="(errors.includes('no-rrp') || errors.includes('rrp') || errors.includes('invalid-rrp'))"
              danger-text="RRP is required and must be at least 0 and a Number."
              id="currencyInput"
              label="Recommended Retail Price *"
              type="text"
              v-model="rrp"/>
          <div id="currencyCode">{{this.currencyCode}}</div>
        </div>
        <div id="manufacturer">
          <vs-input
              :danger="(errors.includes('no-manu'))"
              danger-text="Manufacturer is Required."
              class="form-control"
              type="text"
              label="Manufacturer *"
              v-model="manufacturer"/>
        </div>
        <div id="description">
          <vs-textarea
              class="form-control"
              type="text"
              width="450px"
              label="Description"
              v-model="description"/>
        </div>
      </div>
      <vs-button
          class="add-button"
          @click="checkForm(); createItem();">Add Item to Catalogue</vs-button>
    </form>
  </div>
</template>

<script>
import api from "../Api";
import axios from "axios";
import {store} from "../store";

const AddToCatalogue = {
  name: "AddToCatalogue",

  data: function () {
    return {
      user: null,
      errors: [],
      productId: "",
      productName: "",
      description: "",
      manufacturer: "",
      currencySymbol: "",
      currencyCode: "",
      rrp: ""
    };
  },
  methods: {
    /**
     * The function checks the inputs of the registration form to ensure they are in the right format.
     * The function also updates the errors list that will be displayed on the page if at least one of the input boxes
     * is in the wrong format.
     */
    checkForm: function () {
      this.errors = [];
      if (this.productName.length === 0) {
        this.errors.push(this.productName);
      }

      if (this.productId.length === 0) {
        this.errors.push(this.productId);
      }

      if (this.manufacturer.length === 0) {
        this.errors.push('no-manu');
      }

      if (this.rrp.length === 0 || this.rrp === null) {
        this.errors.push('no-rrp');
      } else if (this.rrp < 0) {
        this.errors.push('rrp');
      }

      if (isNaN(this.rrp)) {
        this.errors.push('invalid-rrp');
      }

      if (this.errors.length >= 1) {
        if (this.errors.includes(this.productName) || this.errors.includes(this.productId)
            || this.errors.includes('rrp') || this.errors.includes('no-rrp')
            || this.errors.includes('invalid-rrp') || this.errors.includes('no-manu')) {
          this.$vs.notify({
            title: 'Failed to create catalogue item',
            text: 'Required fields are missing.',
            color: 'danger'
          });
        }
      }
      if (this.productName.length > 25) {
        this.errors.push('long-prodName');
        this.$vs.notify({
          title: 'Failed to create catalogue item',
          text: 'Product name is too long! Make sure it is 25 characters or less',
          color: 'danger'
        });
      }

      if (this.description.length > 140) {
        this.errors.push('long-desc');
        this.$vs.notify({
          title: 'Failed to create catalogue item',
          text: 'Description is too long! Make sure it is 140 characters or less',
          color: 'danger'
        });
      }
    },

    /**
     * Creates a POST request when user submits form, using the createUser function from Api.js
     */
    createItem: function () {
      //Use creatItem function of API to POST user data to backend
      //https://www.npmjs.com/package/json-server
      if (this.errors.length === 0) {
        api.createProduct(store.actingAsBusinessId, this.productId, this.productName, this.description, this.manufacturer, this.rrp)
          .then((response) => {
            this.$log.debug("New catalogue item created:", response.data);
            this.$router.push({path: `/businesses/${store.actingAsBusinessId}/products`});
          }).catch((error) => {
          if (error.response) {
            this.$log.debug(error);
            if (error.response.status === 400) {
              this.$vs.notify({
                title: 'Failed to create catalogue item',
                text: 'Product ID is already in use',
                color: 'danger'
              });
            }
            this.$log.debug(error.response.status);
          }
          this.$log.debug("Error Status:", error)
        });
      }
    },

    /**
     * Retrieves the current user information.
     */
    getUserInfo: function (userId) {
      api.getUserFromID(userId) //Get user data
        .then((response) => {
          this.user = response.data;
          this.setCurrency(this.user.homeAddress.country);
        }).catch((err) => {
        if (err.response.status === 401) {
          this.$vs.notify({title: 'Unauthorized Action', text: 'You must login first.', color: 'danger'});
          this.$router.push({name: 'LoginPage'});
        } else {
          this.$log.debug(err);
        }
      });
    },

    /**
     * Sets the currency symbol and code display to the current user's country.
     * Calls a third party API to retrieve the information.
     * @param country the user's country to retrieve information for.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
        .then(response => {
          this.currencySymbol = response.data[0].currencies[0].symbol;
          this.currencyCode = response.data[0].currencies[0].code;
        }).catch(err => {
          this.$log.debug(err);
      });
    },

    /**
     * Checks the user session to see if the user is logged in.
     */
    checkUserSession: function() {
      api.checkSession()
        .then((response) => {
          this.getUserInfo(response.data.id);
        })
        .catch((error) => {
          this.$log.debug("Error checking sessions: " + error);
          this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
        });
    }
  },

  mounted: function () {
    this.checkUserSession();
  }
}

export default AddToCatalogue;

</script>

<style scoped>

/*
Add button's styling
 */
.add-button {
  grid-column: 1 / 3;

  padding: 10px 40px;
  margin: 2em auto;
}

/**
Card styling.
*/
.card {
  font-family: 'Ubuntu', sans-serif;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto auto auto;
  grid-row-gap: 1em;

  max-width: 650px;
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
  grid-column: 1;

  margin: auto;

  display: grid;
  grid-template-columns: repeat(2, auto);
  grid-template-rows: repeat(2, auto);
}

label, input {
  display: block;
}

/**
Styling for form elements.
*/
.form-control {
  font-family: 'Ubuntu', sans-serif;
  padding: 3px 10px;
  margin: 0.5em;
}

#info-field {
  grid-column: 1/3;
  display: grid;
  margin: auto;
  grid-template-columns: repeat(2, auto);
  grid-template-rows: repeat(5, auto);
}

#product-name-field {
  grid-column: 1;
  grid-row: 1;
}

#product-name-field >>> .vs-input {
  padding-left: 2px;
  padding-right: 0;
}

#product-id {
  grid-column: 2;
  grid-row: 1;
}

#product-id >>> .vs-input {
  padding-left: 0;
  padding-right: 0;
}

#manufacturer {
  grid-column: 2;
  grid-row: 2;
}

#description {
  grid-column: 1 / 3;
  grid-row: 3;
}

#description >>> .vs-con-textarea > textarea {
  min-height: 100px;
  max-height: 100px;
}

#rrp {
  grid-column: 1;
  grid-row: 2;

  margin: 0;
  display: flex;
}

#currencySymbol {
  grid-row: 1;
  grid-column: 1;
  margin: auto;
  font-size: 15px;
  line-height: 20px;
}

#currencyInput {
  grid-row: 1;
  grid-column: 2;
}

#currencyCode {
  grid-row: 1;
  grid-column: 3;

  margin: auto;
  font-size: 15px;
}

#manufacturer >>> .vs-input {
  margin-bottom: 1em;
  margin-top: 0;
  padding-top: 0;
  padding-left: 0;
  padding-right: 0;
}


</style>
