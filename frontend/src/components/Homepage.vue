<template>
  <!-- -->
  <div id="container" v-if="getLoggedInUserId() != null">
      <!-- Welcome message that greets user with their name, or a business administrator with the business name -->
      <div id="name-container">
        <!-- Shows a different greeting message depending on who the user is acting as -->
        <div v-if="getBusinessId() != null" class="name" id="business-name">
          Welcome to your home page, {{getBusinessName()}}!
        </div>
        <div v-else class="name" id="name">
          Welcome to your home page, {{getUserName()}}!
        </div>
      </div>

      <div id="sidebar-container">
        <!-- Left navigation with links to profile pages, or the product catalogue -->
        <div id="left-nav" class="sub-container">
          <!-- Shows a different nav depending on who the user is acting as -->
          <div class="userinfo-container" v-if="getBusinessId()">
          <div id="businfo-content">
            <!-- When each list item is clicked, redirects to the relevant page in the application -->
            <vs-button class="left-nav-item" id="bus-profile-btn" @click.native='goToProfile()'>Business Profile</vs-button>
            <vs-button class="left-nav-item" id="bus-catalogue-btn" @click.native='goToProductCatalogue()'>Product Catalogue</vs-button>
            <vs-button class="left-nav-item" @click.native='goToSalesHistory()'>Sales History</vs-button>
          </div>
          </div>
          <div class="userinfo-container" v-else>
            <div id="userinfo-content">
              <vs-button class="left-nav-item" id="user-profile-btn" @click.native='goToProfile()' icon="account_box">Profile</vs-button>
              <vs-button class="left-nav-item" id="marketplace-btn" :to="'/marketplace'" icon="store">Marketplace</vs-button>
              <vs-button class="left-nav-item" id="cards-btn" @click="openMarketModal()" icon="style">My Cards</vs-button>
              <vs-button class="left-nav-item" id="listings-btn" :to="'/search-listings'" icon="search">Browse Listings</vs-button>
            </div>
          </div>
        </div>
        <!-- Watchlist div, will show users 'Favourited' products and businesses when further features have been implemented -->
        <div id="watchlist-container" class="sub-container" v-if="getBusinessId() == null" >
          <div style="display: flex;" class="watchlist-title" id="watchlist-header-container">
            <vs-tabs alignment="fixed">
              <vs-tab id="watchlist-tab" @click="watchlist=true" label="Watchlist (My Likes)" icon="favorite_border" color="red"></vs-tab>
              <vs-tab id="wishlist-tab" @click="watchlist=false" label="Wishlist" icon="star_border"></vs-tab>
            </vs-tabs>
          </div>
          <div v-if="watchlist">
            <div v-if="likedItem.length > 0" style="margin-top: -40px">
              <div v-for="(item, index) in likedItem" :key="item.id" >
                <vs-card style="margin-top: -10px" id="message-notification-card" actionable>
                  <div  id="likes-notification-container">
                    <vs-row v-if="item.likes != 0">
                      <vs-col vs-type="flex" vs-align="center">
                        <vs-tooltip text="Unlike">
                          <vs-icon id="like-icon" icon="favorite" class="msg-icon" color="red" @click="unlike(item.id, index); item.likes = 0"></vs-icon>
                        </vs-tooltip>
                        <div @click="viewListing(item.inventoryItem.product.business.id, item.id)" style="width: 100%; color: white;">
                          p
                        </div>
                      </vs-col>
                    </vs-row>
                    <vs-row v-else>
                      <vs-col vs-type="flex" vs-align="center" vs-justify="space-between">
                        <vs-icon id="unlike-icon" icon="favorite_border" class="msg-icon" color="red" @click="unlike(item.id, index)"></vs-icon>
                      </vs-col>
                    </vs-row>
                    <div @click="viewListing(item.inventoryItem.product.business.id, item.id)">
                      <div id="product-name">{{item.inventoryItem.product.name}}</div>
                      <div id="product-seller"><strong>Seller: </strong>{{item.inventoryItem.product.business.name}}</div>
                      <div id="product-closes" slot="footer"><strong>Closes: </strong>{{item.closes}}</div>
                    </div>
                  </div>
                </vs-card>
              </div>
            </div>
            <div v-else>
              <em style="font-size: 12px;">
                You currently have no liked listings
              </em>
            </div>
          </div>
          <div v-if="!watchlist">
            <div v-if="wishlist.length > 0">
              <vs-tooltip :text="allMuted ? 'Unmute all' : 'Mute all'">
                <vs-button id="mute-all-btn" @click="toggleMuteAll" v-if="allMuted == true" style="width: 100%; margin-bottom: 20px" color="grey">
                  <vs-icon icon="notifications_off" class="msg-icon" color="white"></vs-icon>
                </vs-button>
                <vs-button v-else class="mute-all-btn" @click="toggleMuteAll" style="width: 100%; margin-bottom: 20px" color="grey">
                  <vs-icon icon="notifications" class="msg-icon" color="white"></vs-icon>
                </vs-button>
              </vs-tooltip>
            </div>
            <div v-if="wishlist.length > 0">
              <div v-for="wish in wishlist" :key="wish.id" >
                <vs-card style="margin-top: -10px"  actionable>
                  <div>
                    <vs-row>
                      <vs-col vs-type="flex" vs-align="center" vs-justify="space-between">
                        <vs-tooltip text="Remove from wishlist">
                          <vs-icon id="wishlist-icon" icon="star" class="msg-icon" color="red" @click="removeFromWishlist(wish.id)"></vs-icon>
                        </vs-tooltip>
                        <div @click="viewBusiness(wish.business.id)" style="width: 90%; color: white;">
                          p
                        </div>
                        <vs-tooltip v-if="!isMuted(wish.mutedStatus)" text="Mute this business">
                          <vs-icon icon="notifications" class="msg-icon" color="grey" @click="toggleMuteBusiness(wish)"></vs-icon>
                        </vs-tooltip>
                        <vs-tooltip v-if="isMuted(wish.mutedStatus)" text="Unmute this business">
                          <vs-icon icon="notifications_off" class="msg-icon" color="grey" @click="toggleMuteBusiness(wish)"></vs-icon>
                        </vs-tooltip>

                      </vs-col>
                    </vs-row>
                    <div @click="viewBusiness(wish.business.id)" id="business-card">
                      <div id="business-title">{{wish.business.name}}</div>
                      <div id="business-type">{{wish.business.businessType}}</div>
                    </div>
                  </div>
                </vs-card>
              </div>
            </div>
            <em v-else style="font-size: 12px">
              You currently have no wishlisted businesses
            </em>
          </div>
        </div>
      </div>

      <main v-if="getBusinessId() == null">
        <nav id="newsfeed-navbar">
          <div class="newsfeed-title">
            <span style="display: inline-block; vertical-align: middle;">
              <vs-icon icon="feed" />
            </span>
             News Feed
          </div>
        </nav>
        <vs-divider style="padding: 0 1em;"/>
        <HomePageMessages v-if="getBusinessId() == null" :currency="currencySymbol"></HomePageMessages>
      </main>

      <div v-else-if="graphMode" class="business-main" >
        <vs-card>
          <div class="header-container">
            <vs-icon icon="leaderboard" size="32px" style="margin: auto 0"></vs-icon>
            <div class="title">Sales Graph</div>
            <div class="title-business"> - {{getBusinessName()}}</div>
            <vs-button icon="summarize" class="toggle-button" id="bus-sales-report" @click="graphMode = !graphMode" >Sales Report</vs-button>
          </div>
          <vs-divider/>
          <BusinessSalesGraph :businessId="actingAsBusinessId" :currencySymbol="currencySymbol" />
        </vs-card>
      </div>
      <div v-else class="business-main">
        <vs-card>
          <div class="header-container">
            <vs-icon icon="summarize" size="32px" style="margin: auto 0"></vs-icon>
            <div class="title">Sales Report</div>
            <div class="title-business"> - {{getBusinessName()}}</div>
            <vs-button icon="leaderboard" class="toggle-button" id="bus-sales-graph" @click="graphMode = !graphMode">Sales Graph</vs-button>
          </div>
          <vs-divider/>
          <BusinessSalesReport :businessId="actingAsBusinessId" />
        </vs-card>
      </div>



    <vs-popup title="Your Cards" :active.sync="showMarketModal" id="market-card-modal">
      <div v-if="cards.length > 0" class="container">
        <MarketplaceGrid @cardRemoved="getUserCards(userId)" :cardData="cards.slice((currentCardPage-1)*4, currentCardPage*4)" showSection></MarketplaceGrid>
        <vs-pagination :max="5" :total="Math.ceil(cards.length/4)" v-model="currentCardPage"></vs-pagination>
      </div>
      <!-- If the user has no active cards -->
      <div v-else class="container">
        This user has no active cards on the marketplace right now.
      </div>
    </vs-popup>
  </div>
</template>

<script>
import api from "../Api";
import {mutations, store} from "../store"
import HomePageMessages from "./HomePageMessages.vue";
import MarketplaceGrid from "./MarketplaceGrid";
import ListingDetail from "./ListingDetail";
import axios from "axios";
import BusinessSalesReport from "./BusinessSalesReport";
import BusinessSalesGraph from "./BusinessSalesGraph";

const Homepage = {
  name: "Homepage",
  components: {BusinessSalesReport, BusinessSalesGraph, ListingDetail, HomePageMessages, MarketplaceGrid},
  data: function () {
    return {
      unliked: false,
      showListing: false,
      likes: 0,
      likedItem: [],
      userId: null,
      businesses: [],
      actingAsBusinessId: null,
      business: null,
      showMarketModal: false,
      cards: [],
      currentCardPage: 1,
      user: null,
      currencySymbol: "$",
      watchlist: true,
      wishlist: [],
      graphMode: true,
      series: [{
        name: 'Number of sales',
        data: [30, 40, 20, 50, 49, 10, 70, 40, 55, 57, 53, 44]
      }],
      options: {
        chart: {
          id: 'sales-graph-report'
        },
        xaxis: {
          categories: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August',
            'September', 'October', 'November', 'December']
        },
        dataLabels: {
          enabled: false,
        }
      },
      allMuted: false,
    }
  },
  /**
   * Sets the userId variable equal to the userId from the store when the component
   * is first rendered, then gets the users details from the backend using the API
   */
  mounted() {
    this.checkUserSession();
    this.getLikes(this.userId);
    this.getWishlist(this.userId);
  },

  methods: {
    /**
     * Sets display currency based on the user's home country.
     * @param country country for which currency is going to be retrieved
     */
    setCurrency(country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
        .then(response => {
          this.currencySymbol = response.data[0].currencies[0].symbol;
        })
        .catch(err => {
          this.$log.debug(err);
      });
    },

    /**
     * Calculate whether allMuted is true or not
     * This function sets the Mute All button to either mute or unmute
     * It is called every time a Wishlisted Business is added or removed and during page load
     * If all Wishlisted Business' are manually/individually muted, set the button to 'Unmute All'
     * If any single Wishlisted Business is unmuted, set the button to "Mute All'
     */
    calculateAllMuted: function () {
      this.allMuted = true;

      for (let wishlistedBusiness of this.wishlist) {
        if(wishlistedBusiness.mutedStatus === "Unmuted")
          this.allMuted = false;
      }
    },


    /**
     * Checks whether a business has been muted or not
     * @param muteStatus
     * @return Boolean whether muteStatus is equal the string "Muted" or not
     */
    isMuted: function (muteStatus) {
      return muteStatus === "Muted";
    },

    /**
     * Mutes a single wishlisted business
     * To updateWishlistMuteStatus, the current mutedStatus needs to be called.
     * Ie if we were to unmute a currently muted wishlisted business, we must send the status 'Muted' as a parameter
     * @param wishlistedBusinessItem the business to toggle mute on
     */
    toggleMuteBusiness: function (wishlistedBusinessItem) {

      //correct error message
      let status = "Muted";
      if (wishlistedBusinessItem.mutedStatus === "Muted") {
        status = "Unmuted"
      }

      api.updateWishlistMuteStatus(wishlistedBusinessItem.id, wishlistedBusinessItem.mutedStatus)
          .then(() => {
            this.getWishlist(this.userId);
            this.$vs.notify({title: "Business successfully "+status, color: "success"});
          })
          .catch(() => {
            this.$vs.notify({title: "Error, business not"+status, color: "danger"});
          });

    },


    /**
     * Calls the api endpoint, updating the mute status of all the wishlist items
     */
    toggleMuteAll: async function () {
      //toggle the 'Mute All' button
      let muteStatus;
      if (!this.allMuted) {
        this.allMuted = true;
        muteStatus = "Unmuted";
      } else {
        muteStatus = "Muted";
        this.allMuted = false;
      }
      //correct error message
      let status = "Muted";
      if (muteStatus === "Muted") {
        status = "Unmuted"
      }

      //Set the mute/unmute state for every business in the wishlist
      for (let wish of this.wishlist) {
        await api.updateWishlistMuteStatus(wish.id, muteStatus)
          .catch(() => {
            this.$vs.notify({title: "Error updating wishlist "+status, color: "danger"});
          });
      }

      this.getWishlist(this.userId);
      this.$vs.notify({title: "All businesses successfully "+status, color: "success"});
    },

    /**
     * Call the api function and retrieve wishlisted businesses
     * @param userId ID of user who is currently logged in
     */
    getWishlist: function (userId) {
      api.getUsersWishlistedBusinesses(userId)
        .then((res) => {
          this.wishlist = res.data;
          this.calculateAllMuted();

        })
        .catch((error) => {
          if (error.response) {
            this.$vs.notify({title: "Error retrieving wishlist", color: "danger"});
          }
        })
    },

    /**
     * Call api function to remove business from user's wishlist
     * @param wishlistItemId ID of the business that the user 'Wishlisted'
     */
    removeFromWishlist: function (wishlistItemId) {
      api.removeBusinessFromWishlist(wishlistItemId)
              .then(() => {
                this.getWishlist(this.userId);
                this.calculateAllMuted();
                this.$vs.notify({title: "Successfully  removed business from wishlist", color: "success"});
              })
              .catch((error) => {
                if (error.response) {
                  this.$vs.notify({title: "Error removing business", color: "danger"});
                }
                this.$log.debug(error);
              })
    },

    /**
     * Go to business profile
     * @param businessId Id of the business in 'Wishlisted Businesses'
     */
    viewBusiness: function (businessId) {
      this.$router.push({name: "Business", params: {id: businessId}})
    },

    /**
     * Open listing
     * @param businessId ID of the business owning the selected listing
     * @param listingId The selected listing's ID
     */
    viewListing: function (businessId, listingId) {
      this.$router.push({name: "Listing", params: {businessId: businessId, listingId: listingId}})
    },

    /**
     * Unlike an item
     * @param id ID of user who is currently logged in
     * @param index Location of the Listing in,this.likedItem, that the user unliked
     */
    unlike: function (id, index) {
      this.likes -= 1;
      this.likedItem.splice(index, 1)
      api.removeLikeFromListing(id)
      .then(() => {
        this.$vs.notify({title: "Successfully unliked listing", color: "success"});
      })
      .catch((error) => {
        if (error.response) {
          this.$vs.notify({title: "Error unliking listing", color: "danger"});
        }
        this.$log.debug(error);
      })
    },

    /**
     * Retrieves all the listings that the user has liked.
     * @param userId ID of user who is currently logged in
     */
    getLikes: function(userId) {
      api.getUserLikedListings(userId)
        .then((res) => {
          let temp = [];
          for (let data of res.data) {
            if (new Date(data["closes"]).getTime() > new Date().getTime()) {
              temp.push(data);
            } else {
              api.removeLikeFromListing(data["id"]);
            }
          }
          this.likedItem = temp;
          this.likes = temp.length;
        })
        .catch((error) => {
          if (error.response) {
            this.$vs.notify({title: "Error retrieving likes", color: "danger"});
          }
        });

      setInterval(() => {
        api.getUserLikedListings(userId)
            .then((res) => {
              let temp = [];
              for (let data of res.data) {
                if (new Date(data["closes"]).getTime() > new Date().getTime()) {
                  temp.push(data);
                } else {
                  api.removeLikeFromListing(data["id"]);
                }
              }
              this.likedItem = temp;
              this.likes = temp.length;
            })
      }, 3000);

    },

    /**
     * Retrieves all the cards that the user has created.
     * @param id ID of user who is currently logged in
     */
    getUserCards: function(id) {
      this.$vs.loading({
        container: ".vs-popup",
      });
      this.cards = [];
      api.getUserCards(id)
        .then((res) => {
          this.cards = res.data;
          for(let card of this.cards){
            if(!card.user.homeAddress){
              card.user = this.user;
            }
          }
        })
        .catch((error) => {
          if (error.response) {
            this.$vs.notify({title: "Error retrieving cards", color: "danger"});
          }
          this.$log.debug(error);
        })
        .finally(() => {
          this.$vs.loading.close(`.vs-popup > .con-vs-loading`);
        });
    },

    /**
     * Gets user info from backend and sets userFirstname property (for welcome message)
     * Also sets the users details in store.js, so the users session can be maintained
     * as they navigate throughout the applications and 'act' as a business
     *
     * @param userId ID of user who is currently logged in
     */
    getUserDetails: function(userId) {
      api.getUserFromID(userId)
          .then((response) => {
            this.user = response.data;
            this.businesses = JSON.parse(JSON.stringify(this.user.businessesAdministered));
            this.userLoggedIn = true;
            /* Sets user details in store.js */
            mutations.setUserDateOfBirth(response.data.dateOfBirth);
            mutations.setUserName(response.data.firstName + " " + response.data.lastName);
            mutations.setUserCountry(response.data.homeAddress.country);
            mutations.setUserBusinesses(this.businesses);
            this.setCurrency(response.data.homeAddress.country);
          }).catch((err) => {
        if (err.response.status === 401) {
          this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
          this.$router.push({name: 'LoginPage'});
        }
        this.$log.error(err);
      })
    },

    /**
     * Show the modal box (marketplace activity).
     * Having a separate function to just open the modal is good for testing.
     */
    openMarketModal: function() {
      this.showMarketModal = true;
      api.checkSession()
        .then(() => {
          this.getUserCards(this.user.id);
        })
        .catch((error) => {
          this.$vs.notify({title:'Error getting session info', text:`${error}`, color:'danger'});
        });
    },

    /**
     * Close the pop-up box with no consequences.
     */
    closeModal: function() {
      this.showModal = false;
    },

    /**
     * Sends an api request to get a business object from a business Id
     * Sets this components business variable to this object
     *
     * @param id business' id
     */
    getBusiness: function(id) {
      api.getBusinessFromId(id)
          .then((res) => {
            this.business = res.data;
          })
          .catch((error) => {
            this.$log.error(error);
          })
    },

    /**
     * Gets the business id from the store, for the business the user is acting as
     *
     * @return busId the business id
     */
    getBusinessId: function() {
      let busId = store.actingAsBusinessId;
      this.actingAsBusinessId = busId;
      if(busId){
        this.getBusiness(busId);
      }
      return busId;
    },

    /**
     * Gets the name of the business the user is acting as from the store
     * @return business the user is acting as
     */
    getBusinessName: function() {
      return store.actingAsBusinessName;
    },

    /**
     * Gets the username of the logged in user from the store
     * @return Username of the logged in user
     */
    getUserName: function() {
      return store.userName;
    },

    /**
     * Gets the logged in users id from the store, and assigns this components
     * userId variable equal to it.
     * @return Id of the logged in user
     */
    getLoggedInUserId: function() {
      this.userId = store.loggedInUserId;
      return this.userId;
    },

    /**
     * Redirects the user to either the business profile page, if acting as a business,
     * or the user profile page, if acting as an individual
     */
    goToProfile: function() {
      if(this.getBusinessId() != null){
        this.$router.push({path: `/businesses/${this.getBusinessId() }`});
      } else {
        this.$router.push({path: `/users/${this.getLoggedInUserId()}`});
      }
    },

    /**
     * Redirects the user to the product catalogue page, if acting as a business
     */
    goToProductCatalogue: function() {
      this.$router.push({path: `/businesses/${this.getBusinessId()}/products`});
    },

    goToSalesHistory: function() {
      this.$router.push({path: `/businesses/${this.getBusinessId()}/sales-history`});
    },

    checkUserSession: function() {
      api.checkSession()
        .then((response) => {
          this.getUserDetails(response.data.id);
        })
        .catch((error) => {
          this.$log.error("Error checking sessions: " + error);
          this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
        });
    }
  },
}

export default Homepage;
</script>

<style scoped>
#message-notification-card {
  min-height: 100px;
}

.con-vs-card >>> .vs-card--content {
  margin-bottom: 4px;
}

.toggle-button {
  width: 150px;
}

.header-container {
  display: flex;
  margin: auto;
  padding-bottom: 0.5em;
  padding-top: 1em;
}

#product-name {
  text-align: left;
  font-size: 12px;
  font-weight: bold;
}

#business-title {
  text-align: left;
  font-size: 12px;
  font-weight: bold;
}

#product-closes {
  font-size: 12px;
}


#view-icon {
  font-size: 16px;
  margin-left: -1px;
  cursor: pointer;
  transition: font-size 0.3s;
}

#wishlist-icon {
  font-size: 16px;
}

#view-unlike-icon {
  font-size: 16px;
  margin-left: -1px;
  cursor: pointer;
  transition: font-size 0.3s;
}

#view-icon:hover {
  transition: font-size 0.3s;
  font-size: 20px!important;
}

#like-icon:hover {
  transition: font-size 0.3s;
  font-size: 20px!important;
}

#like-icon {
  font-size: 16px;
  cursor: pointer;
  transition: font-size 0.3s;
}

#unlike-icon {
  font-size: 13px;
  margin-left: 150px;
  cursor: pointer
}

#market-card-modal >>> .vs-popup {
  width: 1200px;
}

#market-card-modal {
  z-index: 100;
}

#cards-btn {
  padding-left: 0;
  padding-right: 0;
}

#container {
  display: grid;
  grid-template-columns: 0.6fr 1.2fr 4fr 0.6fr;
  grid-template-rows: 1fr auto;
  grid-column-gap: 1em;
  margin: auto;
  padding: 0 2em;
}

/* Top Name Container */
#name-container {
  grid-column: 2 / 4;
  grid-row: 1;
  text-align: center;
  background-color: white;
  padding: 15px 0 15px 0;
  border-radius: 4px;
  margin: 8px 0 0 0;

  box-shadow: 0 4px 25px 0 rgba(0,0,0,.1);
}

.name {
  font-size: 32px;
  padding: 0.5em 0;
  line-height: 1.2em;
}

#business-name {
  font-size: 32px;
  margin: auto;
}

.title {
  font-size: 30px;
  margin: auto 8px auto 4px;
}
.title-business {
  font-size: 30px;
  font-weight: bold;
  margin: auto auto auto 0;
}

.newsfeed-title {
  font-size: 24px;
  padding: 4px 0 0 0.5em;
}

/* Side-bar panel on left side */
#sidebar-container {
  grid-column: 2;
  grid-row: 2;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;
}

.sub-container {
  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 4px 25px 0 rgba(0,0,0,.1);
  background-color: #FFFFFF;
}

#watchlist-container {
  grid-column: 1;
  grid-row: 3;
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(1, auto) repeat(1, 1fr);
  grid-row-gap: 2em;
}

#watchlist-header-container {
  display: flex;
  justify-content: space-between;
}

.watchlist-title {
  font-size: 18px;
}

/* News feed styles. */
main {
  grid-column: 3;
  grid-row: 2;

  margin: 1em 0 1em 0;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #FFFFFF;
}

#newsfeed-navbar {
  grid-column: 2;
  grid-row: 1;
  font-size: 18px;
  padding-top: 1em;
  padding-left: 4px;
}

.business-main {
  grid-column: 3;
  grid-row: 2;
  margin-top: 1em;
}

.business-main >>> .vs-card--content {
  width: 100%;
}

/* left navigation panel styling */

#left-nav {
  grid-row: 2;
}

.left-nav-item {
  width: 100%;
  margin: 0.5em auto;
  text-align: center;
  letter-spacing: 1px;
}

@media screen and (max-width: 1200px) {
  .watchlist-title {
    transition: 0.3s;
    font-size: 16px;
    margin-left: 0;
  }
}

/* For when the screen gets too narrow - mainly for mobile view */
@media screen and (max-width: 700px) {
  #container {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
    margin: auto;
    padding: 0 2em;
  }

  #name-container {
    grid-column: 1;
    grid-row: 1;
  }

  #sidebar-container {
    grid-column: 1;
    grid-row: 2;
  }

  main {
    grid-column: 1;
    grid-row: 3;
  }

  #newsfeed-navbar {
    align-content: center;
  }

  .watchlist-title {
    font-size: 18px;
    margin-left: 4px;
    transition: 0.1s;
  }
}
</style>
