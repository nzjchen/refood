<template>
  <div v-if="currentProduct != null">
    <vs-popup title="Modify Inventory Entry" :active.sync="modifyInv" id="modify-modal">
      <div id="header-row">
        <div id="image-container">
          <ReImage :image-path="currentProduct.primaryImagePath" class="image"/>
        </div>
        <div id="product-name">
          <div class="sub-header">Product</div>
            <vs-select id="product-select" v-model="currentProduct">
              <vs-select-item :value="product" :text="`[${product.id}] ${product.name}`" v-for="product in products" :key="product.id"/>
            </vs-select>
        </div>
      </div>
      <vs-divider/>

      <div class="vs-col" vs-order="1" id="first-col-modal">
        <div class="row">
          <label for="pricePerItem">Price Per Item *</label>
          <vs-input
              type="number"
              :danger="(errors.includes('pricePerItem'))"
              danger-text="Price per item must be greater than zero and numeric."
              id="pricePerItem"
              :min="0"
              v-model="invenForm.pricePerItem"/>
        </div>
        <div class="row">
          <label for="total-price">Total Price *</label>
          <vs-input
              type="number"
              :danger="(errors.includes('totalPrice'))"
              danger-text="Total price must be greater than zero and numeric."
              id="total-price"
              :min="0"
              v-model="invenForm.totalPrice"/>
        </div>
        <div class="row">
          <label for="quantity">Quantity *</label>
          <vs-input-number
              :danger="(errors.includes(invenForm.quantity))"
              danger-text="Quantity must be greater than zero."
              min="1"
              :step="1"
              id="quantity"
              v-model="invenForm.quantity"/>
        </div>
      </div>
      <div class="vs-col" vs-order="2" id="second-col-modal">
        <div class="row">
          <label for="bestBefore">Best before</label>
          <vs-input
              :danger="(errors.includes('past-best'))"
              danger-text="Date cannot be in the past"
              type="date"
              id="bestBefore"
              v-model="invenForm.bestBefore"/>
        </div>
        <div class="row">
          <label for="listingExpiry">Expiry Date *</label>
          <vs-input
              :danger="(errors.includes('past-expiry'))"
              danger-text="Expiry date is required and cannot be in the past"
              type="date"
              id="listingExpiry"
              v-model="invenForm.expiryDate"/>
        </div>
        <div class="row">
          <label for="manufactureDate">Manufacture date</label>
          <vs-input
              :danger="(errors.includes('future-manu'))"
              danger-text="Date cannot be in the future"
              type="date"
              id="manufactureDate"
              v-model="invenForm.manufactureDate"/>
        </div>
        <div class="row">
          <label for="sellBy">Sell by</label>
          <vs-input
              :danger="(errors.includes('past-sell'))"
              danger-text="Date cannot be in the past"
              type="date"
              id="sellBy"
              v-model="invenForm.sellBy"/>
        </div>
      </div>
      <div class="vs-col" id="add-button" @click="updateInventory()">
        <vs-button>Update Inventory Entry</vs-button>
      </div>
      <div class="vs-col" id="cancel-button" @click="modifyInv=false">
        <vs-button type="border">Cancel</vs-button>
      </div>
    </vs-popup>
  </div>
</template>

<script>
import api from "../Api";
import {store} from "../store";
import ReImage from "./ReImage";

export default {
  name: "ModifyInventory",

  components: {ReImage},

  data() {
    return {
      currentProduct: null,
      item: null,
      invenForm: {
        prodId: '',
        pricePerItem: 0.0,
        totalPrice: 0.0,
        quantity: 0,
        bestBefore: '',
        expiryDate: '',
        manufactureDate: '',
        sellBy: ''
      },

      modifyInv: false,
      products: [],
      errors: [],

      currency: "$",
    }
  },

  methods: {
    /**
     * Called to open the modal, setting fields to the current inventory item's values.
     * Typically called from an outside component.
     */
    open: function(inventory, currency) {
      this.modifyInv = true;

      this.item = inventory;
      this.currency = currency;
      this.getBusinessProducts();
      this.setCurrentItem(this.item);
    },

    /**
     * Checks the form for valid input fields.
     * @return boolean true if there are no errors, false otherwise.
     */
    checkForm: function() {
      this.errors = [];

      const today = new Date();
      const invalidChars = /^([a-zA-Z0-9\u0600-\u06FF\u0660-\u0669\u06F0-\u06F9 _.-]+)$/;
      if (!invalidChars.test(this.invenForm.prodId)) {
        this.errors.push("invalid-chars");
      }

      if (isNaN(this.invenForm.pricePerItem)) {
        this.errors.push('pricePerItem');
      }

      if (this.invenForm.totalPrice == null) {
        this.errors.push('totalPrice');
      }

      let dateInPast = function(firstDate, secondDate) {
        return firstDate.setHours(0, 0, 0, 0) <= secondDate.setHours(0, 0, 0, 0);
      }

      let dateInFuture = function(firstDate, secondDate) {
        return firstDate.setHours(0, 0, 0, 0) > secondDate.setHours(0, 0, 0, 0);
      }

      if (this.invenForm.bestBefore === '' && this.invenForm.sellBy === '' && this.invenForm.manufactureDate === ''
          && this.invenForm.expiryDate === '') {
        this.errors.push('no-dates');
      }

      if (this.invenForm.expiryDate === '') {
        this.errors.push('past-expiry');
      }

      if (this.invenForm.prodId.length === 0 || this.errors.includes('invalid-chars')) {
        this.errors.push(this.invenForm.prodId);
      }

      if (this.invenForm.pricePerItem <= 0.0) {
        this.errors.push('pricePerItem');
      }

      if (this.invenForm.totalPrice <= 0.0) {
        this.errors.push('totalPrice');
      }

      let timestamp;
      let dateObject;
      if (this.invenForm.bestBefore !== '') {
        timestamp = Date.parse(this.invenForm.bestBefore);
        dateObject = new Date(timestamp)
        if (dateInPast(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('past-best');
        }
      }

      if (this.invenForm.expiryDate !== '') {
        timestamp = Date.parse(this.invenForm.expiryDate);
        dateObject = new Date(timestamp)
        if (dateInPast(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('past-expiry');
        }
      }

      if (this.invenForm.manufactureDate !== '') {
        timestamp = Date.parse(this.invenForm.manufactureDate);
        dateObject = new Date(timestamp)
        if (dateInFuture(dateObject, today) === true) {
          this.errors.push('future-date');
          this.errors.push('future-manu');
        }
      }

      if (this.invenForm.sellBy !== '') {
        timestamp = Date.parse(this.invenForm.sellBy);
        dateObject = new Date(timestamp)
        if (dateInPast(dateObject, today) === true) {
          this.errors.push('past-date');
          this.errors.push('past-sell');
        }
      }

      if (this.invenForm.quantity <= 0) {
        this.errors.push(this.invenForm.quantity);
      }

      if (this.invenForm.bestBefore > this.invenForm.expiryDate) {
        this.errors.push("best-before-expiry");
      }

      if (this.errors.includes('no-dates')) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Date is Required.',
          color: 'danger'
        });
      }

      if (this.errors.includes('past-date')) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Dates cannot be in the past.',
          color: 'danger'
        });
      }

      if (this.errors.includes('future-date')) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Dates cannot be in the future',
          color: 'danger'
        });
      }

      if (this.errors.includes(this.invenForm.quantity)) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Quantity must be greater than zero.',
          color: 'danger'
        });
      }

      if (this.errors.includes("best-before-expiry")) {
        this.$vs.notify({
          title: 'Failed to create inventory item',
          text: 'Best before date cannot be after the expiry date.',
          color: 'danger'
        });
      }

      if (this.errors.length > 0) {
        return false;
      }
      return true;
    },

    /**
     * Retrieves the product catalogue of the business.
     */
    getBusinessProducts() {
      api.getBusinessProducts(store.actingAsBusinessId)
          .then((response) => {
            this.$log.debug("Data loaded: ", response.data);
            this.products = response.data;

            // Done to make the select dropdown properly find the correct product object and set as default.
            this.currentProduct = this.products.find(product => {
              return product.id === this.item.productId;
            });
          })
          .catch((error) => {
            this.$log.debug(error);
            this.error = "Failed to load products";
          });
    },

    /**
     * Makes a call to the server to update the inventory with the new modified details (when the inputs are valid).
     */
    updateInventory: function() {
      this.invenForm.prodId = this.currentProduct.id;
      this.checkForm();
      if (this.errors.length === 0) {
        api.modifyInventory(store.actingAsBusinessId, this.item.id, this.invenForm.prodId, this.invenForm.quantity, this.invenForm.pricePerItem, this.invenForm.totalPrice, this.invenForm.manufactureDate, this.invenForm.sellBy, this.invenForm.bestBefore, this.invenForm.expiryDate)
            .then((response) => {
              this.$log.debug("Inventory item updated:", response.data);
              this.addNewInv = false;
              this.modifyInv = false;
              this.$emit('submitted');
              this.$vs.notify( {
                title: `Item successfully modified`,
                color: 'success'
              });
            })
            .catch((error) => {
              if (error.response) {
                if (error.response.status === 400) {
                  this.$vs.notify( {
                    title: 'Failed to add an inventory item',
                    text: 'Incomplete form, or the product does not exist.',
                    color: 'danger'
                  });
                } else if (error.response.status === 403) {
                  this.$vs.notify( {
                    title: 'Failed to add an inventory item',
                    text: 'You do not have the rights to access this business',
                    color: 'danger'
                  });
                }
              }
              this.$log.debug("Error Status:", error)
        });
      }
    },

    /**
     * Sets the input fields to the current inventory item values.
     * @param item the item object to get the values from.
     */
    setCurrentItem(item) {
      if (item !== undefined) {
        this.invenForm.prodId = item.productId;
        this.invenForm.manufactureDate = item.manufactured;
        this.invenForm.sellBy = item.sellBy;
        this.invenForm.bestBefore = item.bestBefore;
        this.invenForm.expiryDate = item.expires;
        this.invenForm.quantity = item.quantity;
        this.invenForm.pricePerItem = item.pricePerItem;
        this.invenForm.totalPrice = item.totalPrice;
      }
    },

    /**
     * Updates the total price value when either quantity or price per item changes.
     */
    updateTotalPrice: function() {
      this.invenForm.totalPrice = (this.invenForm.quantity * this.invenForm.pricePerItem).toFixed(2);
    },

  },

  watch: {
    "invenForm.quantity": function() {
      this.updateTotalPrice();
    },

    "invenForm.pricePerItem": function() {
      this.updateTotalPrice();
    },
  }
}
</script>


<style scoped>

.vs-popup-primary >>> header {
  background-color: #1F74FF;
  color: #FFFFFF;
}

.vs-popup--header {
  background-color: #1F74FF;
  color: #FFFFFF;
}

#modify-modal {
  z-index: 100;
}

#product-select {
  width: 275px;
}

#first-col-modal {
  margin-right: 160px;
  margin-left: 5px;
}

.row {
  margin-bottom: 15px;
}

.full-image >>> div img {
  height: 210px;
  border-radius: 4px;
  object-fit: cover;
}

.full-image {
  margin-bottom: 1em;
}

.image >>> img {
  margin: auto;
  width: 150px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

#cancel-button {
  margin-left: 4px;
}

#image-container {
  margin: auto;
}

@media screen and (max-width: 630px) {
  #header-row {
    display: flex;
    flex-direction: column;
  }

  #add-button {
    margin: 0 auto;
  }

  #cancel-button {
    margin: 0.5em auto;
  }
}


</style>
