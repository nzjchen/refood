<template>
  <div id="listings-container">
    <div :class="{'header-container-grid': !tableView, 'header-container-table': tableView}">
      <div v-show="!tableView" style="display: inline-flex">
        <vs-select @change="sortListingsByKey" id="header-sort" v-model="selectedSort" label="Sort">
          <vs-select-item v-for="sort in sortOptions" :key="sort" :value="sort" :text="sort"/>
        </vs-select>
        <vs-button id="sort-button"
                   type="line"
                   :icon="sortDirection === 'desc' ? 'expand_more' : 'expand_less'"
                   @click="changeSortDirection(); sortListingsByKey();"
                   icon-after>
          <span v-if="sortDirection === 'desc'">Descending</span>
          <span v-else>Ascending</span>
        </vs-button>
      </div>
      <!-- ====== LISTINGS OPTIONS MENU ===== -->
      <div id="view-switch">
        <vs-tooltip text="Grid View">
          <vs-button icon="grid_view" type="border" @click="tableView = false" style="border: none; padding: 12px;"></vs-button>
        </vs-tooltip>
        <vs-tooltip text="List View">
          <vs-button icon="view_list" type="border" @click="tableView = true" style="border: none;"></vs-button>
        </vs-tooltip>
      </div>
    </div>
    <vs-divider></vs-divider>

    <!-- ===== GRID VIEW ===== -->
    <div v-if="!tableView" id="grid-view">
      <vs-card class="listing-card"
               v-for="listing in listings"
               :key="listing.id"
               :fixed-height="true">

        <div slot="media"
             style="cursor: pointer;"
             @click="goToListingPage(listing.id)">
          <ReImage :imagePath="listing.inventoryItem.product.primaryImagePath" style="border-radius: 4px;"></ReImage>
        </div>
        <div style="margin: 2px 4px; font-size: 14px; font-weight: bold">{{ listing.productName }}</div>
        <div style="font-size: 14px; padding-left: 4px; margin: auto 0;">
          <div>{{ currencySymbol }}{{ listing.price }}</div>
          <div>{{ listing.quantity }}x</div>
        </div>

        <div style="font-size: 12px"> Closes: {{ listing.closes }}</div>
        <vs-divider style="margin-top: 0"></vs-divider>

        <div>{{ listing.moreInfo }}</div>
        <div slot="footer" class="grid-card-footer">
          Listed: {{ listing.created }}
        </div>
      </vs-card>
    </div>

    <!-- ===== TABLE VIEW ===== -->
    <div v-else id="table-view">
      <vs-table
          :data="listings"
          noDataText="This business has no listings."
          pagination
          stripe>
        <template slot="thead">
          <vs-th style="border-radius: 8px 0 0 0"> <!-- Product Image Thumbnail --> </vs-th>
          <vs-th sort-key="productName"> Product </vs-th>
          <vs-th sort-key="price"> Price </vs-th>
          <vs-th sort-key="quantity"> Qty </vs-th>
          <vs-th sort-key="closes"> Closes </vs-th>
          <vs-th sort-key="created"> Listed </vs-th>
          <vs-th> Additional Info </vs-th>
          <vs-th style="border-radius: 0 8px 0 0"></vs-th>
        </template>
        <template slot-scope="{data}">
          <vs-tr v-for="listing in data" :key="listing.id">
            <vs-td>
              <ReImage :imagePath="listing.inventoryItem.product.primaryImagePath" class="table-image"/>
            </vs-td>
            <vs-td >{{ listing.productName }}</vs-td>
            <vs-td>{{ currencySymbol }}{{ listing.price }}</vs-td>
            <vs-td>{{ listing.quantity }}x</vs-td>
            <vs-td>{{ listing.closes }}</vs-td>
            <vs-td>{{ listing.created }}</vs-td>
            <vs-td>{{ listing.moreInfo }}</vs-td>
            <vs-td><vs-button @click="goToListingPage(listing.id)">View</vs-button></vs-td>
          </vs-tr>
        </template>
      </vs-table>
    </div>

  </div>
</template>

<script>
import api from "../Api";
import axios from "axios";
import ReImage from "./ReImage";

export default {
  name: "BusinessListings",

  components: {ReImage},

  props: {
    businessId: Number,
    country: String,
  },

  data: function() {
    return {
      listings: [],
      tableView: false, // Default grid-card option.

      sortOptions: ["Listing Date", "Closing Date", "Product Name"], // Not implemented yet.
      selectedSort: null,
      sortDirection: "desc",

      currencySymbol: "$",
    }
  },

  methods: {
    /**
     * Redirects browser to full detail listing page.
     */
    goToListingPage: function(listingId) {
      this.$router.push(`/businesses/${this.businessId}/listings/${listingId}`);
    },

    /**
     * Retrieves the sale listings of this business.
     */
    getListings: function() {
      api.getBusinessListings(this.businessId)
        .then((res) => {
          this.listings = res.data;
          // Add product name to listing object to make it accessible for table to sort.
          this.listings = this.listings.map(listing => {
            listing.productName = listing.inventoryItem.product.name;
            return listing;
          })
        })
        .catch((error) => {
          this.$log.error(error);
        });
    },

    /**
     * Change sort direction to ascending/descending for grid view only.
     */
    changeSortDirection: function() {
      this.sortDirection = this.sortDirection === "desc" ? "asc" : "desc";
    },

    /**
     * Changes sort view of the listings for grid view only.
     */
    sortListingsByKey: function() {
      let selectedKey;
      let sortKeyDirection = this.sortDirection;
      if (this.selectedSort === "Closing Date") selectedKey = "closes";
      else if (this.selectedSort === "Product Name") selectedKey = "productName";
      else selectedKey = "created";

      this.listings.sort(function(a, b) {
          if (a[selectedKey] < b[selectedKey]) return sortKeyDirection === "desc" ? 1 : -1;
          if (a[selectedKey] > b[selectedKey]) return sortKeyDirection === "desc" ? -1 : 1;
          return 0;
      });
    },

    /**
     * Sets display currency based on the user's home country.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
          .then(response => {
            this.currencySymbol = response.data[0].currencies[0].symbol;
          }).catch(err => {
        this.$log.debug(err);
      });
    },
  },

  mounted: function() {
    this.getListings();
    this.setCurrency(this.country);
  },
}
</script>

<style scoped>
  #listings-container {
    display: flex;
    flex-direction: column;
    margin: 1em auto;
  }

  .header-container-grid {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .header-container-table {
    padding-top: 25px;
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
  }

  #view-switch {
    display: flex;
    margin: auto 0 0 0;
  }

  #sort-button {
    top: 5px;
    margin: auto auto 0 auto;
  }

  .table-image >>> img {
    min-width: 75px;
    max-width: 75px;
    height: 75px;
    object-fit: cover;

    border-radius: 4px;
    margin: 0 0 auto 0;
  }


  /* === LISTING CARD ==== */
  #grid-view {
    display: grid;
    grid-template-columns: repeat(auto-fit, 250px);
    justify-content: space-around;
  }

  #grid-view::after {
    content: "";
    flex: auto;
  }

  .listing-card {
    width: 225px;
    height: 400px;
    margin: 0.5em 1em;
  }

  .image >>> img {
    height: 150px;
    object-fit: cover;

    border-radius: 4px;
    margin: 0 0 auto 0;
  }


  .grid-card-footer {
    font-size: 12px;
  }

  .listing-card >>> footer {
    margin-left: 0;
    margin-right: auto;
    right: auto;
  }

  @media screen and (max-width: 500px) {
    .header-container-grid {
      flex-direction: column;
    }

    #view-switch {
      margin-top: 0.5em;
    }
  }

</style>
