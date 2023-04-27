<template>
  <vs-card id="container">
    <div id="header-container">
      <vs-icon icon="history" size="32px"></vs-icon>
      <div id="title">Sales History</div>
    </div>
    <vs-divider/>

    <vs-table
        :data="soldListings"
        noDataText="Your listings will be displayed here once you have sold at least one product."
        stripe>
      <template slot="thead">
        <vs-th style="border-top-left-radius: 4px;"><!-- IMAGE COLUMN --></vs-th>
        <vs-th>Product</vs-th>
        <vs-th>Sold</vs-th>
        <vs-th>Listed</vs-th>
        <vs-th>Quantity</vs-th>
        <vs-th>Price</vs-th>
        <vs-th style="border-top-right-radius: 4px;">Likes</vs-th>
      </template>
      <template slot-scope="{data}">
        <vs-tr v-for="(listing, index) in data" :key="index">
          <vs-td class="listing-image-column"><ReImage :image-path="listing.product.primaryImagePath" class="listing-image"/></vs-td>
          <vs-td>{{listing.product.name}}</vs-td>
          <vs-td>{{listing.sold}}</vs-td>
          <vs-td>{{listing.product.created}}</vs-td>
          <vs-td>{{listing.quantity}}</vs-td>
          <vs-td>{{listing.product.recommendedRetailPrice}}</vs-td>
          <vs-td>{{listing.likes}}</vs-td>
        </vs-tr>

      </template>
    </vs-table>
  </vs-card>
</template>

<script>
import ReImage from "./ReImage";
import api from "../Api";
import axios from "axios";

export default {
  name: "BusinessSalesHistory",
  components: {ReImage},

  data: function() {
    return {
      currency: "$",
      businessId: '',
      soldListings: []
    }
  },

  mounted: function() {
    this.businessId = this.$route.params.id;
    this.getSalesHistory();
    this.getSession();
  },

  methods: {
    /**
     * Calls get sales history
     */
    getSalesHistory: function () {
      api.getBusinessSales(this.businessId)
        .then((res) => {
          this.soldListings = res.data;
        })
        .catch(err => {
          console.log(err)
        });
    },

    /**
     * Calls get session endpoint to get user country, if successful calls setCurrency ()
     */
    getSession: function() {
      api.checkSession()
        .then((response) => {
          this.setCurrency(response.data.homeAddress.country)
        })
        .catch(err => {
          this.$log.debug(err);
      });
    },

    /**
     * Sets display currency based on the user's home country.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
        .then(response => {
          this.currency = response.data[0].currencies[0].symbol;
        })
        .catch(err => {
          this.$log.debug(err);
      });
    },
  },
}
</script>

<style scoped>

#container {
  background-color: white;
  width: 75%;
  margin: 1em auto;
}

#header-container {
  display: flex;
  margin: auto;
  padding-bottom: 0.5em;
  padding-top: 1em;
}

#title {
  font-size: 30px;
  margin: auto auto auto 4px;
}

.listing-image-column {
  width: 100px!important;
}

.listing-image >>> img {
  margin: auto;
  width: 90%;
  height: 70%;
  object-fit: cover;
  border-radius: 4px;
}

</style>
