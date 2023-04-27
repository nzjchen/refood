<template>
  <vs-card class="main">
        <div id="header-container">
          <h1 id="page-title">
            <vs-icon icon="local_offer"/>
            Sales Listings</h1>
          </div>
        <vs-divider style="padding: 4px;"></vs-divider>

        <div id="catalogue-options">
          <vs-input class="search-input" type="search" v-on:keyup.enter="filterListings" placeholder="Enter a listing..." name="searchbarUser" v-model="productQuery" style="width: 400px; font-size: 24px" size="medium"/>
          <vs-button class="header-button" id="main-search-btn" @click="filterListings">Search</vs-button>
        </div>

        <vs-divider style="padding: 4px;"></vs-divider>

        <div>
          <div id="filter-box" style="display: flex;">
            <div id="search-parameters" display="flex">
              <div class="parameter" id="business" style="display: block">
                <div class="vert-row">
                <h3 class="filter-label">
                  Business Type:
                </h3>
                <vs-select v-model="selectedTypes" multiple class="form-control">
                  <vs-select-item :key="business" :value="business" :text="business" v-for="business in businessTypes"></vs-select-item>
                </vs-select>
                </div>
                <div class="vert-row">
                <h3 class="filter-label">
                  Business Name:
                </h3>
                <vs-input class="filter-input" v-model="businessQuery" type="search" placeholder="Business name.." style="font-size: 24px" size="medium"/>
              </div>
              </div>
              <div class="parameter" id="listings">
                <div class="vert-row">
                  <h3 class="filter-label">
                    Location:
                  </h3>
                  <vs-input class="filter-input" v-model="addressQuery" type="search" placeholder="Address.." style="font-size: 24px" size="medium"/>
                </div>
              <div class="vert-row">
                <h3 class="filter-label">
                  Price range:
                </h3>
                <vs-input class="price-input" v-model="minPrice" type="number" @keypress="checkNumber($event)" :danger="(errors.includes('invalid-minprice'))" danger-text="Invalid"  min="0" placeholder="Min" style="font-size: 24px" size="medium"/>
                <vs-input class="price-input" v-model="maxPrice" type="number" @keypress="checkNumber($event)" :danger="(errors.includes('invalid-maxprice'))" danger-text="Invalid" min="0" placeholder="Max"  style="font-size: 24px" size="medium"/>
              </div>
              </div>

              <div class="parameter" id="address-closing-date">
                <div class="vert-row">
                <h3 class="filter-label">
                  Min Closing Date:
                </h3>
                <vs-input class="filter-input" v-model="minClosingDate" :danger="(errors.includes('past-min-date'))"
                          danger-text="Date can not be in past." type="date" style="font-size: 24px"/>
              </div>
                <div class="vert-row">
                  <h3 class="filter-label">Max Closing Date:
                  </h3>
                  <vs-input class="filter-input" v-model="maxClosingDate" :danger="(errors.includes('past-max-date'))"
                            danger-text="Max closing date must be after min date and in future" type="date" style="font-size: 24px"/>
                </div>
            </div>
            </div>
            <div class="vl"></div>
            <div id="sort-container">
              <div>
                <h3 class="filter-label" style="margin: auto; padding-right: 4px;">Sort By </h3>
                <div>
                <vs-select v-model="sortBy" autocomplete class="form-control" style="margin-bottom: 10px;">
                  <vs-select-item :key="item.value" :value="item.value" :text="item.text" v-for="item in sortOptions">Please select one</vs-select-item>
                </vs-select>
                  <vs-select v-model="sortDirection">
                    <vs-select-item key="asc" value="asc" text="Ascending"></vs-select-item>
                    <vs-select-item key="desc" value="desc" text="Descending"></vs-select-item>
                </vs-select>
                <vs-button class="sort-btn" id="sort-button" @click="filterListings" style="width: 100px">Sort</vs-button>
                </div>
                </div>
            </div>
          </div>
          <vs-divider style="padding: 4px;"></vs-divider>
          <div class="grid-container" style="margin: auto">
            <vs-card class="listing-card" v-for="listing in listings" :key="listing.id" :fixed-height="true">
              <div slot="media" id="media-div" >
                <div id="img-wrap" @click="viewListing(listing)" style="cursor: pointer;">
                <ReImage :imagePath="listing.inventoryItem.product.primaryImagePath"></ReImage>
                </div>
                <div v-if="!likedListingsIds.includes(listing.id)">
                  <vs-icon icon="favorite_border" size="32px" class="like-button" color="red" @click="sendLike(listing.id, listing.inventoryItem.product.name)"></vs-icon>
                </div>
                <div v-else>
                  <vs-icon icon="favorite" size="32px" class="like-button" color="red" @click="deleteLike(listing.id, listing.inventoryItem.product.name)"></vs-icon>
                </div>
              </div>
              <div @click="viewListing(listing)" style="cursor: pointer;">
                <div style="margin: 2px 4px; font-size: 14px; font-weight: bold">{{ listing.inventoryItem.product.name }}</div>
                <div style="margin: 2px 4px; font-size: 14px; font-weight: bold;">{{ listing.inventoryItem.product.business.name }}</div>

                  <div v-if="listing.inventoryItem.product.business.address.city" style="margin: 2px 4px; font-size: 14px; font-weight: bold;">{{ listing.inventoryItem.product.business.address.city }}, {{listing.inventoryItem.product.business.address.country}}</div>
                  <div v-else style="margin: 2px 4px; font-size: 14px; font-weight: bold;">
                    {{listing.inventoryItem.product.business.address.country}}
                  </div>
                <div style="font-size: 14px; padding-left: 4px; margin: auto 0; width: 100%;">
                  <div>{{ currencySymbol }}{{ listing.price }}</div>
                  <div>{{ listing.quantity }}x</div>
                </div>

                <div style="font-size: 12px"> Closes: {{ listing.closes }}</div>
                <vs-divider style="margin-top: 0"></vs-divider>

                <div>{{ listing.moreInfo }}</div>
                <div slot="footer" class="grid-card-footer">
                  Listed: {{ listing.created }}
                </div>
              </div>
            </vs-card>
            <div class="title-centre">
            </div>
          </div>
        </div>
    <div class="title-container">
      <div class="title-centre">
        <vs-pagination v-model="pageNum" :total="totalPages" @change="filterListings(); resetCache();" />
      </div>
    </div>
  </vs-card>
</template>

<script>
import api from "../Api";
import axios from "axios";
import ReImage from "./ReImage";

const SearchListings = {
  name: "SearchListings",
  components: {ReImage},
  data: function() {
    return {
      user: null,
      listings: [],
      searchbarListings: "",
      businessTypes: [],
      errors: [],
      toggle: [1,1,1,1,1],
      filteredListings: [],
      currencySymbol: "$",
      selected: "",
      sortOptions:[
        {text: 'Price', value: "price"},
        {text: 'Closing Date', value: "closes"},
        {text: 'Created Date', value: "created"},
        {text: 'City', value: "city"},
        {text: 'Country', value: "country"},
        {text: 'Business Type', value: "businessType"},
        {text: 'Product Name', value: "name"},
        {text: 'Quantity', value: "quantity"},
        {text: 'Manufacturer', value: "manufacturer"},
        {text: 'Seller', value: "seller"},
        {text: 'Expiry Date', value: "expires"}
      ],
      businessQuery: null,
      productQuery: null,
      addressQuery: null,
      businessType: null,
      sortBy: "closes",
      minPrice: null,
      maxPrice: null,
      selectedTypes: [],
      minClosingDate: null,
      maxClosingDate: null,

      likedListingsIds: [],
      numListings: 12,
      pageNum: 1,
      totalPages: 0,
      sortDirection: "desc"
    }
  },

  mounted() {
    if (sessionStorage.getItem('listingSearchCache') !== null) {
      let prevSearch;
      prevSearch = JSON.parse(sessionStorage.getItem('listingSearchCache'));
      this.businessQuery = prevSearch['businessQuery']
      this.productQuery = prevSearch['productQuery']
      this.addressQuery = prevSearch['addressQuery']
      this.sortBy = prevSearch['sortBy']
      this.selectedTypes = prevSearch['selectedTypes']
      this.minPrice = prevSearch['minPrice']
      this.maxPrice = prevSearch['maxPrice']
      this.minClosingDate = prevSearch['minClosingDate']
      this.maxClosingDate = prevSearch['maxClosingDate']
      this.numListings = prevSearch['numListings']
      this.pageNum = prevSearch['pageNum']
      this.sortDirection = prevSearch['sortDirection']
    }
    api.checkSession()
      .then((response) => {
        this.user = response.data;
        api.getUserLikedListings(this.user.id)
          .then((res) => {
            for (let i = 0; i < res.data.length; i++) {
              this.likedListingsIds.push(res.data[i]["id"]);
            }
          }).catch((err) => {
            throw new Error(`Error trying to get user's likes: ${err}`)
        });
        this.getBusinessTypes();
        this.filterListings();
        this.setCurrency(this.user.homeAddress.country)
      }).catch((err) => {
      throw new Error(`Error trying to get user id: ${err}`);
    })
  },

  methods: {
    resetCache: function() {
      if (sessionStorage.getItem('listingSearchCache') !== null) {
        sessionStorage.removeItem('listingSearchCache');
      }
    },

    /**
     * checks whether the input is a number
     */
    checkNumber ($event) {
      let keyCode = $event.keyCode;
      if ((keyCode < 48 || keyCode > 57) && keyCode !== 46) { // 46 is dot
        $event.preventDefault();
      }
    },
    /**
     * Gets all business types from the database, to
     * be used by business type filter
     * */
    getBusinessTypes: function() {
      api.getBusinessTypes()
      .then((response) => {
        this.businessTypes = response.data
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
     * Calls the third-party RESTCountries API to retrieve currency information based on user home country.
     * Sets the currency symbol view to the retrieved data.
     * @param country the country to obtain the currency symbol from.
     **/
    setCurrency: function (country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
        .then( response => {
          this.currencySymbol = response.data[0].currencies[0].symbol;
        }).catch( err => {
          console.log("Error with getting cities from REST Countries." + err);
      });
    },

    /**
     * Helper function to reduce duplication
     **/
    filterListingsHelper: function() {
      api.filterListingsQuery(this.businessQuery, this.productQuery, this.addressQuery, this.sortBy, this.selectedTypes, this.minPrice, this.maxPrice,
          this.minClosingDate,  this.maxClosingDate, this.numListings, this.pageNum-1, this.sortDirection)
          .then((response) => {
            this.listings = response.data.content
            this.totalPages = response.data.totalPages;
          })
          .catch(err => {
            if(err.response.status === 400) { // Catch 400 Bad Request
              this.$vs.notify({title:'Error', text:'Some filters are invalid', color:'danger'});

            }
            else { // Catch anything else.
              this.$vs.notify({title:'Error', text:`Status Code ${err.response.status}`, color:'danger'});
            }
          });
    },

    /**
     * Searches all listings in the ReFood database, applies the filters
     * that the user has filled in, along with the chosen sort
     * */
    filterListings: function(){
      if(!this.minClosingDate){
        this.minClosingDate = Date.now()
      }
      if(this.checkForm()){
        this.filterListingsHelper();
      }
    },

    /**
     * Redirects the user to either the business profile page, if acting as a business,
     * or the user profile page, if acting as an individual
     */
    viewListing: function(listing) {
      let searchQuery = {
        businessQuery: this.businessQuery,
        productQuery: this.productQuery,
        addressQuery: this.addressQuery,
        sortBy: this.sortBy,
        selectedTypes: this.selectedTypes,
        minPrice: this.minPrice,
        maxPrice: this.maxPrice,
        minClosingDate: this.minClosingDate,
        maxClosingDate: this.maxClosingDate,
        numListings: this.numListings,
        pageNum: this.pageNum,
        sortDirection: this.sortDirection
      }
      sessionStorage.setItem("listingSearchCache", JSON.stringify(searchQuery));
      this.$router.push({path: `/businesses/${listing.inventoryItem.product.business.id }/listings/${listing.id}`});
    },
    /**
     * Checks the form inputs, validating the inputted values.
     * @return boolean true if no errors found, false otherwise.
     */
    checkForm: function() {
      this.errors = [];
      let today = new Date();

      if(this.minPrice != null){
        if(parseInt(this.minPrice) < 0){
          this.errors.push('invalid-minprice');
        }
      }

      if(this.maxPrice != null){
        if(parseInt(this.maxPrice) < 0){
          this.errors.push('invalid-maxprice');
        }
      }

      if(this.maxPrice != null && this.minPrice != null){
        if (parseInt(this.maxPrice) < parseInt(this.minPrice)){
          this.errors.push('invalid-maxprice');
        }
      }




      const dateInPast = function(firstDate, secondDate) {
        return firstDate.setHours(0, 0, 0, 0) <= secondDate.setHours(0, 0, 0, 0);
      }

      let minTimestamp;
      let minDateObject;
      if (this.minClosingDate !== null) {
        minTimestamp = Date.parse(this.minClosingDate);
        minDateObject = new Date(minTimestamp)
        if (dateInPast(minDateObject, today) === true) {
          this.errors.push('past-min-date');
        }
      }

      let maxTimestamp;
      let maxTimeObject;
      if (this.maxClosingDate !== null) {
        maxTimestamp = Date.parse(this.maxClosingDate);
        maxTimeObject = new Date(maxTimestamp);

        if(maxTimeObject < minDateObject || dateInPast(maxTimeObject, today) === true){
          this.errors.push('past-max-date');
        }
      }

      if (this.errors.length > 0) {
        return false;
      }
      return true;
    },
    /**
     * method to submit a like for a listing.
     * @param listingId Id of the listing.
     * @param listingName Name of the listing.
     */
    sendLike: function(listingId, listingName) {
      api.addLikeToListing(listingId)
          .then(() => {
            this.likedListingsIds.push(listingId);
            this.$vs.notify({text: `${listingName} has been  liked and added to your watchlist!`, color: 'success'});
          })
          .catch((err) => {
            throw new Error(`Error trying to like listing ${listingId}: ${err}`);
          })
    },

    /**
     * Deletes a like for a listing.
     * @param listingId Id of the listing.
     * @param listingName Name of the listing.
     */
    deleteLike: function(listingId, listingName) {
      api.removeLikeFromListing(listingId)
          .then(() => {
            this.likedListingsIds.splice(this.likedListingsIds.indexOf(listingId),1);
            this.$vs.notify({text: `${listingName} has been unliked and deleted from your watchlist!`, color: 'success'});
          })
          .catch((err) => {
            throw new Error(`Error trying to delete listing ${listingId} from your watchlist: ${err}`);
          })
    }
  }
}




export default SearchListings;
</script>

<style scoped>
#filter-box {
  background: rgb(31, 116, 255);
  padding: 10px;
}

#filter-box .vs-button-primary.vs-button-filled {
  background: #ffffff !important;
  color: black;
}

#page-title {
  font-size: 30px;
  margin: 12px 8px 4px;
}

#header-container {
  display: flex;
  justify-content: space-between;
}

#sort-container {
  display: flex;
  width: auto;
}

#sort-container .con-select {
  margin-right: 0px;
}

.header-button {
  margin: 0 0.5em;
  min-width: 100px;
}

.main {
  background-color: white;
  width: 90%;
  margin: 1em auto;
}

.prevNextSearchButton {
  margin-left: 1em;
  width: 100px;
}

.displaying {
  text-align: right;
  margin: auto;
}


.profile-text-inner {
  margin: 2em auto;
  width: 95%;
}

/* ===== GRID CARD ===== */

.grid-container {
  display: grid;
  justify-content: space-around;
  grid-template-columns: repeat(auto-fill, 375px);
  grid-column-gap: 2em;

  margin: 50px auto auto auto;
}

.grid-item {
  border-radius: 4px;
  font-size: 30px;
  text-align: left;
  margin: 10px;
  max-width: 350px;
}

.grid-image {
  height: 225px;
  border-radius: 4px 4px 0 0;
  object-fit: cover;
}

.grid-item-footer {
  display: flex;
  justify-content: space-between;
  padding: 0;
}

i.vs-icon.notranslate.icon-scale.icon-item.vs-select--item-icon.material-icons.null {
  font-size: 17px;
}

.grid-item >>> footer {
  padding-bottom: 1em;
  margin-bottom: 4px;
}

/* ===== ===== ===== */

#catalogue-options {
  display: flex;
  margin-left: 10px;
  margin-bottom: 1em;
}

#catalogue-options .search-input {
  width: 80% !important;
}

#catalogue-options .header-button {
  width: 10%;
}

#grid-pagination {
  margin: auto 0 auto auto;
  display: flex;
}

th {
  background: #1F74FF;
  color: white;
}

.table-image >>> img {
  width: 100%;
  height: 100px;
  border-radius: 4px 4px 0 0;
  object-fit: cover;
}

.actionButton {
  text-align: left;
  cursor: pointer;
}

div#filter-box {
  display: flex;
  border-radius: 10px;
}

.con-select, .filter-input {
  width: auto;
  margin-right: 15px;
  clear: both;
}

#search-parameter {
  width: 85%;
  display: flex;
}

.vert-row .price-input {
  float: left;
  margin-right: 10px;
}

.parameter {
  width: 33%;
  display: inline-block;
  float: left;
  margin-right: 0px
}

.vl {
  border-left: 2px solid white;
  border-radius: 2px;
  height: auto;
  margin-right: 10px;
}

div#search-parameters {
  width: 80%;
}

.sort-btn {
  margin-top: 10px;
  width: 100% !important;
}

.filter-label {
  color: white;
}

.price-input {
  width: 70px !important;
}

.con-vs-card.fixedHeight {
  height: auto;
}

.vert-row .con-text-validation.span-text-validation-danger.vs-input--text-validation-span.v-enter-to .span-text-validation {
  color: white !important;
}

.listing-card:hover {
  filter: brightness(75%);
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
}


.like-button {
  position: absolute;
  top: 4px;
  right: 4px;

  cursor: pointer;
  text-align: right;

  transition: 0.3s;
}

.like-button.vs-icon:hover {
  transition: opacity 0.3s, font-size 0.3s, right 0.3s;
  opacity: 1.5;
  font-size: 42px!important;
  right: 16px;
}


@media screen and (max-width: 900px) {
  /* Mobile view for filters*/

  .parameter {
    width: 100%;
  }

  #filter-box {
    display: block !important;
  }

  #search-parameters {
    width: 100% !important;
  }

  #sort-container {
    width: auto;
    display: block;
  }

  #catalogue-options {
    flex-direction: column;
  }

  #grid-pagination {
    margin: 1em auto 0 0;
  }

  .header-button {
    margin: 8px;
  }

}

@media screen and (max-width: 625px) {
  .main {
    width: 95%;
  }

  #header-container {
    flex-direction: column;
    margin: auto;
  }

  #page-title {
    margin: auto;
  }

  #header-menu {
    margin: 2em auto 0 auto;
    justify-content: space-evenly;
  }

  #sort-container {
    flex-direction: column;
  }

}

</style>
