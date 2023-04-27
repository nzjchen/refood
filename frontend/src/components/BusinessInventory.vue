<template>
  <vs-card id="container" class="listing-card">
    <div id="header-container">
      <div style="display: flex;">
        <div id="title"> Inventory </div>
      </div>
      <div id="header-buttongroup">
        <vs-button class="header-button" @click="$router.push(`/businesses/${$route.params.id}/products`)">Product Catalogue</vs-button>
        <vs-button class="header-button" @click="$router.push(`/businesses/${$route.params.id}/sales-history`)">Sales History</vs-button>
      </div>
    </div>

    <!-- NEW LISTING MODAL -->
    <vs-popup :active.sync="newListingPopup"
              title="Create a new listing">
      <div class="new-listing-modal">
        <div class="row" v-if="invItem != null">
          <ReImage :image-path="invItem.product.primaryImagePath" class="image"/>
        </div>
        <div id="listing-product-name">
          <div class="sub-header">Inventory Item Name</div>
          <div style="font-size: 18px; text-align: center; font-weight: bold" v-if="invItem != null">{{ invItem.product.name }}</div>
          <div v-if="invItem != null">{{ invItem.productId }}</div>
        </div>
        <vs-divider id="listing-divider"></vs-divider>
        <div id="listing-price">
          <span :class="{currencyAfterError: newListingErrors.price.error, currencyBeforeError: !newListingErrors.price.error}">{{currency}}</span>
          <vs-input type="number" v-model="price"
                    label="Price" min="0"
                    :danger="newListingErrors.price.error"
                    :danger-text="newListingErrors.price.message"></vs-input>
        </div>
        <div id="listing-qty">
          <div>
            <label style="font-size: 13.6px">Quantity</label>
            <vs-input-number
                v-model="listingQuantity"
                :max="listingQuantityMax"
                :min="1"></vs-input-number>
          </div>
          <div v-if="invItem != null" style="font-size: 10px; text-align: center; position: absolute">Max: {{ invItem.quantity }}</div>
          <div v-show="newListingErrors.quantity.error" style="font-size: 10px; color: #FF4757; text-align: center; position: absolute">{{ newListingErrors.quantity.message }}</div>
        </div>
        <div id="listing-closes">
          <vs-input type="datetime-local"
                    v-model="closes"
                    :danger="newListingErrors.closes.error"
                    :danger-text="newListingErrors.closes.message"
                    :min="minimumDate"
                    label="Close Date"></vs-input>
        </div>
        <div id="listing-moreInfo">
          <vs-textarea class="textarea"
                       v-model="moreInfo"
                       label="More Info"></vs-textarea>
        </div>
        <div id="listing-button-group">
          <vs-button id="create-button" color="success" @click="createNewListing()" style="width: 100px;">Create</vs-button>
          <vs-button id="cancel-button" color="danger" style="width: 100px;" @click="closeNewListing()">Cancel</vs-button>
        </div>
      </div>
    </vs-popup>

    <vs-divider></vs-divider>
    <!-- Table View -->
    <vs-table id="table"
              :data="this.inventory"
              noDataText="You don't have any inventory."
              :pagination="true"
              :maxItems="5"
              stripe>
      <template class="table-head" slot="thead" >
        <vs-th sort-key="productId" style="border-radius: 4px 0 0 0;" > Image </vs-th>
        <vs-th class="product-column" sort-key="productName"> Product </vs-th>
        <vs-th class="dateInTable" sort-key="manufactured"> Manufactured </vs-th>
        <vs-th class="dateInTable" sort-key="sellBy"> Sell By </vs-th>
        <vs-th class="dateInTable" sort-key="bestBefore"> Best Before </vs-th>
        <vs-th class="dateInTable" sort-key="expires"> Expires </vs-th>
        <vs-th sort-key="quantity"> Qty </vs-th>
        <vs-th id="pricePerItemCol"  sort-key="pricePerItem"> Price Per Item </vs-th>
        <vs-th sort-key="totalPrice"> Total Price </vs-th>
        <vs-th style="border-radius: 0 4px 0 0;"> <!-- Action Button Column --> </vs-th>
      </template>
      <template slot-scope="{data}">
        <vs-tr v-for="inventory in data" :key="inventory.id">
          <vs-td id="productIdCol" :data="inventory.productId">
          {{inventory.productId}}
          <div style="height: 80px" @click="openFullProductModal(inventory)">
            <vs-tooltip text="Click here for more product details">
              <vs-tooltip :text="inventory.product.description" title="Description" position="bottom">
                <ReImage :imagePath="inventory.product.primaryImagePath" class="inventory-image"/>
              </vs-tooltip>
            </vs-tooltip>
          </div>
            </vs-td>
          <vs-td :data="inventory.productName" class="product-cell"> {{inventory.productName}} </vs-td>
          <vs-td :data="inventory.manufactured"> {{inventory.manufactured}} </vs-td>
          <vs-td :data="inventory.sellBy"> {{inventory.sellBy}} </vs-td>
          <vs-td :data="inventory.bestBefore" >{{inventory.bestBefore}} </vs-td>
          <vs-td :data="inventory.expires">{{inventory.expires}} </vs-td>
          <vs-td :data="inventory.quantity">{{inventory.quantity}} </vs-td>
          <vs-td :data="inventory.pricePerItem">{{currency}} {{inventory.pricePerItem}} </vs-td>
          <vs-td :data="inventory.totalPrice">{{currency}} {{inventory.totalPrice}}</vs-td>
          <vs-td>

            <vs-dropdown vs-trigger-click>
              <vs-button>Actions</vs-button>
              <vs-dropdown-menu>
                <vs-dropdown-item @click="openModifyModal(inventory)">Modify</vs-dropdown-item>
                <vs-dropdown-item @click="openNewListingModal(inventory)">New Listing</vs-dropdown-item>
              </vs-dropdown-menu>
            </vs-dropdown>
          </vs-td>
        </vs-tr>
      </template>
    </vs-table>
    <ModifyInventory ref="modifyInventoryModal" @submitted="onSuccess" :item="activeModifyItem"></ModifyInventory>

    <!-- SHOW FULL PRODUCT INFO MODAL -->
    <!-- Accessed by pressing the image of the product. -->
    <vs-popup id="full-product-modal" title="Full Product" :active.sync="showFullProduct" v-if="showFullProduct">
      <div>
        <ReImage :image-path="selectedProduct.primaryImagePath" class="full-image"/>
      </div>

      <div style="font-size: 13pt; height:100%; line-height: 1.5; display:flex; flex-direction: column;">
        <div style="display: flex; flex-direction: column; justify-content: space-between">
          <div style="font-size: 16px; font-weight: bold;  text-align: justify; word-break: break-all; margin-top: 4px;">{{ selectedProduct.name }} </div>
          <p>{{ selectedProduct.id }}</p>
        </div>
        <vs-divider></vs-divider>
        <div style="font-size: 16px; font-weight: bold; height: 24px;">{{ selectedProduct.manufacturer }} </div>
        <p style="font-size: 14px; margin-bottom: 8px;">Created: {{ selectedProduct.created }} </p>
        <div style="height: 75px; font-size: 14px; overflow-y: auto; ">{{ selectedProduct.description }} </div>
      </div>

      <div style="font-size: 25pt; font-weight: bold; margin: auto 0" >{{currency + selectedProduct.recommendedRetailPrice }} </div>
    </vs-popup>
  </vs-card>
</template>

<script>
import axios from "axios";
import api from "../Api";
import {store} from "../store";
import ModifyInventory from "./ModifyInventory";
import ReImage from "./ReImage";

export default {
  name: "BusinessInventory",
  components: {ModifyInventory, ReImage},
  data: function() {
    return {
      errors: [],
      inventory: [],
      currency: "$",
      products: [],
      invItem: null,

      modifyModal: false,
      activeModifyItem: null,

      // New Sale Listing Variables
      newListingInventoryItem: null,
      newListingPopup: false,
      newListingErrors: {
        price: {error: false, message: "Price cannot be empty or negative."},
        quantity: {error: false, message: "Please enter a positive quantity."},
        closes: {error: false, message: "Please enter a valid date."}
      },
      listingQuantity: 1,
      listingQuantityMax: 0,
      price: 0.0,
      moreInfo: "",
      closes: "",

      showFullProduct: false,
      selectedProduct: null,
      minimumDate: new Date().toISOString().split('T')[0] + "T23:59",
    }
  },

  mounted() {
    console.log(this.minimumDate)
    this.getSession();
    this.getBusinessInventory();
    this.getProducts(this.$route.params.id);
  },

  methods: {

    /**
     * Opens the new listing modal, sets fields to default to the selected item data.
     */
    openNewListingModal(inventory) {
      this.invItem = inventory;
      this.price = this.invItem.pricePerItem;
      this.listingQuantityMax = this.invItem.quantity;
      this.closes = this.invItem.expires + 'T00:00';
      this.newListingPopup = true;
    },

    /**
     * Opens the modify inventory modal by calling the open function inside the component.
     */
    openModifyModal(inventory) {
      this.$refs.modifyInventoryModal.open(inventory, this.currency);
    },

    onSuccess() {
      this.getBusinessInventory();
    },

    getInventory() {
      return this.inventory.filter(item => (item.quantity>0));
    },

    getProducts(businessId) {
      api.getBusinessProducts(businessId)
        .then((response) => {
          this.products = response.data;
        })
        .catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to load products";
        });

    },

    /**
     * Validates the fields for a new public listing.
     * @return true if all of the required fields meet the requirements, false otherwise.
     */
    validateNewListing: function() {
      Object.values(this.newListingErrors).forEach(input => input.error = false);

      let isValid = true;
      if (this.price < 0 || this.price == "") {
        this.price = 0.00;
        this.newListingErrors.price.error = true;
        isValid = false;
      }
      if (this.closes === "" || this.closes == null) {
        this.newListingErrors.closes.error = true;
        this.newListingErrors.closes.message = "Please enter a valid date.";
        isValid = false;
      }
      if (new Date(this.closes) < Date.now()) {
        this.newListingErrors.closes.error = true;
        this.newListingErrors.closes.message = "Please enter a future date.";
        isValid = false;
      }

      return isValid;
    },

    /**
     * Checks if the new list form is valid, then creates a new listing to be sent to backend if true.
     */
    createNewListing: function() {
      console.log(this.closes)
      if (this.validateNewListing()) {
        if (this.errors.length === 0) {
          api.createListing(store.actingAsBusinessId, this.invItem.id, this.listingQuantity, this.price, this.moreInfo, this.closes)
            .then((response) => {
              this.$log.debug("New listing has been posted:", response.data);
              this.$vs.notify({
                title: 'Listing successfully posted',
                color: 'success'
              });
              this.newListingPopup = false;
            })
            .catch((error) => {
              if (error.response) {
                console.log(error);
                if (error.response.status === 400) {
                  this.$vs.notify({
                    title: 'Failed to add a listing',
                    text: 'Incomplete form, or the product does not exist.',
                    color: 'danger'
                  });
                }
              }
              else if (error.response.status === 403) {
                this.$vs.notify( {
                  title: 'Failed to add a listing',
                  text: 'You do not have the rights to access this business',
                  color: 'danger'
                });
              }
              this.$log.debug("Error Status:", error)
            });
        }
      }
    },

    openFullProductModal: function(inventory) {
      this.showFullProduct = true;
      this.selectedProduct = inventory.product;
    },

    /**
     * Closes the new listing modal, and reset error fields.
     */
    closeNewListing: function() {
      this.newListingPopup = false;
      Object.values(this.newListingErrors).forEach(input => input.error = false);
    },

    /**
     * Calls get session endpoint to get user country, if successful calls setCurrency ()
     */
    getSession: function() {
      api.checkSession()
      .then((response) => {
        //Call set currency inside here to currency is not removed when page refreshes
        this.setCurrency(response.data.homeAddress.country)
      }).catch(err => {
        this.$log.debug(err);
      })
    },

    /**
     * Sets display currency based on the user's home country.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
        .then(response => {
          this.currency = response.data[0].currencies[0].symbol;
        }).catch(err => {
          this.$log.debug(err);
      });
    },

    /**
     * Calls API get businessInventory function, also adds new fields to inventory object for sorting and sets default order
    */
    getBusinessInventory() {
      api.getBusinessInventory(this.$route.params.id)
      .then((response) => {
        this.inventory = response.data;

        for(let inventoryItem of this.inventory) {
          //Issue with sorting using object properties, so pulled required properties into inventory object
          inventoryItem['productName'] = inventoryItem.product.name;
          inventoryItem['productId'] = inventoryItem.product.id;
        }
        //Default ordering is product name, so all similar products will be next to each other
        this.inventory = this.inventory.sort((productOne, productTwo) => (productOne.name < productTwo.name) ? 1 : -1)

      })
      .catch((err) => {
        if (err.response) {
          if (err.response.status === 401) {
            this.$router.push({path: '/login'})
          }
        }
      });
    }
  }
}


</script>

<style scoped>
  #container {
    width: 75%;
    margin: 1em auto;
  }

  /* ===== PAGE HEADER ===== */
  #header-container {
    display: flex;
    justify-content: space-between;
    padding-top: 4px;
  }

  #title {
    font-size: 30px;
    margin: auto 8px;
  }

  #header-buttongroup {
    display: inline-flex;
    justify-content: space-around;
  }

  .header-button {
    margin: 0 0.5em;
    width: 150px;
  }


  /* ===== NEW LISTING DIALOG/PROMPT ====== */

  .new-listing-modal {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: repeat(auto-fit, 1fr);
    grid-row-gap: 0.5em;
  }

  .vs-popup-primary >>> header {
    background-color: #1F74FF;
    color: #FFFFFF;
  }

  .textarea >>> textarea {
    resize: none;
    max-width: 200px;
    min-height: 100px;
    max-height: 100px;
  }

  #listing-product-id {
    grid-row: 1;
    grid-column: 1;

    text-align: center;
  }

  #listing-product-name {
    grid-row: 1;
    grid-column: 2;

    text-align: center;
    margin: auto;
  }

  #listing-divider {
    grid-row: 2;
    grid-column: 1 / 3;
  }

  #listing-qty {
    margin: auto;
    grid-column: 2;
    grid-row: 3;
  }

  #listing-price {
    margin: auto;
    grid-column: 1;
    grid-row: 3;

    display: flex;
  }

  #listing-closes {
    margin: auto;
    padding-left: 12px;
    grid-column: 1;
    grid-row: 4;
  }

  #listing-moreInfo {
    margin: 0.75em auto auto auto;
    width: 90%;
    grid-row: 5;
    grid-column: 1 / 3;
  }

  #listing-button-group {
    margin: 0.75em auto auto auto;
    width: 90%;
    grid-row: 6;
    grid-column: 2;

    display: flex;
    justify-content: space-around;
  }

  .sub-header {
    font-size: 12px;
    color: gray;
  }

  .currencyBeforeError {
    margin: auto 4px 4px 0;
  }

  .currencyAfterError {
    margin: auto 4px 32px 0;
  }


  /* ===== INVENTORY TABLE ===== */
  .table-head {
    border-radius: 1em;
  }

  th {
    background: #1F74FF;
    color: white;
    font-size: 12px;
  }

  #table {
    margin: 0.5em;
    white-space: nowrap;
  }

  #table >>> .vs-con-tbody {
    min-height: 640px;
  }

  #productIdCol {
    font-size: 10px;
  }

  #pricePerItemCol {
    font-size: 11px;
  }

  td {
    font-size: 12px;
    white-space: normal;
  }

  .inventory-image >>> div img {
    cursor: pointer;
    max-width: 100px;
    border-radius: 4px;
    object-fit: cover;
  }

  .inventory-image:hover {
    transition: opacity 0.3s;
    opacity: 0.5;
  }


  .image >>> div img {
    margin: auto;
    width: 150px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
  }

  .product-column {
    min-width: 150px;
  }

  .row {
    margin: auto;
  }



  @media screen and (max-width: 850px) {
    #header-container {
      flex-direction: column;
    }

    #title {
      padding: 0.5em 0 1em 0.5em;
    }
  }

  @media screen and (max-width: 450px) {
    .new-listing-modal {
      display: grid;
      grid-template-columns: 1fr;
      grid-template-rows: repeat(auto-fit, 1fr);
      grid-row-gap: 0.5em;
    }

    #listing-product-id {
      grid-row: 1;
      grid-column: 1;
    }

    #listing-product-name {
      grid-row: 2;
      grid-column: 1;

    }

    #listing-divider {
      grid-row: 3;
      grid-column: 1;
    }

    #listing-qty {
      grid-column: 1;
      grid-row: 4;
      margin-bottom: 1em;
    }

    #listing-price {
      grid-column: 1;
      grid-row: 5;
    }

    #listing-closes {
      grid-column: 1;
      grid-row: 6;
    }

    #listing-moreInfo {
      grid-row: 7;
      grid-column: 1;
    }

    #listing-button-group {
      grid-row: 8;
      grid-column: 1;

      display: flex;
      justify-content: space-around;
    }

    .textarea >>> textarea {
      resize: none;
      min-height: 100px;
      max-height: 100px;
      max-width: 100px;
    }

  }

</style>
