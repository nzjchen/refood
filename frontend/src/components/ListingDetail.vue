<template>
  <vs-card v-if="listing" id="listing-detail-container">
    <div v-if="fromSearch" class="return-button">
      <vs-button @click="returnToSearch()" title="Go Back">Return To Search</vs-button>
    </div>

    <div id="content-container">
      <!-- Image area -->
      <div id="image-area">
        <div id="listing-image-container">
          <transition name="slide-fade" mode="out-in">
            <ReImage id="listing-image" :key="currentImage" :imagePath="currentImage"></ReImage>
          </transition>
          <div v-if="listingImages.length > 1">
            <vs-button @click="previousImage(currentImage)">
              <vs-icon icon="arrow_back"></vs-icon>
            </vs-button>
            <vs-button @click="nextImage(currentImage)">
              <vs-icon icon="arrow_forward"></vs-icon>
            </vs-button>
        </div>
        </div>
      </div>
      <!-- Listing details (closing date, business, etc) -->
      <div id="listing-info-area">
        <div id="listing-info-container">
          <div id="business-name"><strong>Business:</strong> {{listing.inventoryItem.product.business.name}}</div>
          <vs-button id="business-profile-button" @click="goToBusinessProfile(listing.inventoryItem.product.business.id)">To profile</vs-button>
          <p vs-justify="left"><strong>Price:</strong> {{currency.symbol}}{{listing.price}} {{currency.code}}</p>
          <p><strong>Quantity:</strong> {{listing.quantity}}</p>
          <p><strong>Created:</strong> {{listing.created}}</p>
          <p><strong>Closes:</strong> {{listing.closes}}</p>
          <p id="listing-moreInfo">{{listing.moreInfo}}</p>
          <p><strong>Likes:</strong> {{listing.likes}}</p>

          <div class="button-group">
            <vs-button v-if="!likedListingsIds.includes(listing.id)" icon="favorite_border" class="listing-detail-btn" @click="sendLike(listing.id, listing.inventoryItem.product.name)">
              <p class="like-listing-text">Like Listing</p>
              <p class="like-listing-text" style="font-size: 11px">Add to watchlist</p>
            </vs-button>
            <vs-button v-else color="danger" class="listing-detail-btn" icon="favorite" @click="deleteLike(listing.id, listing.inventoryItem.product.name)">
              <p class="like-listing-text">Unlike Listing</p>
              <p class="like-listing-text" style="font-size: 11px">Remove from watchlist</p>
              </vs-button>
            <vs-button class="listing-detail-btn" @click="buy()">Buy</vs-button>
          </div>
        </div>
      </div>
    </div>

    <vs-row>
      <!-- Product & Inventory details (manufacturer, description, inventory dates, etc) -->
      <vs-col vs-w="12">
        <div id="product-info-area">
          <p id="listing-name">{{listing.inventoryItem.product.name}}</p>
          <p><strong>Manufacturer:</strong> {{listing.inventoryItem.product.manufacturer}} </p>
          <p v-if="listing.inventoryItem.manufactured"><strong>Manufactured:</strong> {{listing.inventoryItem.manufactured}}</p>
          <p v-if="listing.inventoryItem.sellBy"><strong>Sell By:</strong> {{listing.inventoryItem.sellBy}}</p>
          <p v-if="listing.inventoryItem.bestBefore"><strong>Best Before:</strong> {{listing.inventoryItem.bestBefore}}</p>
          <p v-if="listing.inventoryItem.expires"><strong>Expires:</strong> {{listing.inventoryItem.expires}}</p>
          <p>{{listing.inventoryItem.product.description}}</p>
        </div>
      </vs-col>


    </vs-row>

  </vs-card>
</template>

<script>

import api from "../Api";
import ReImage from "../components/ReImage.vue";
import axios from "axios";
import { store } from "../store";
export default {
  components: {
    ReImage
  },

  data() {
    return {
      fromSearch: sessionStorage.getItem("listingSearchCache"),
      listingId: null,
      businessId: null,
      listing: null,
      noBusiness: false,
      currency: {symbol: '$', code: 'NZD'},
      currentImage: null,
      listingImages: [],
      likedListingsIds: [],
      userId: store.loggedInUserId
    }
  },

  mounted() {

    if(store.actingAsBusinessId != null) {
      this.$router.push({path: "/home"}) //Only users should be able to access this page (as a logged-in user)
    }
    api.getUserLikedListings(this.userId)
        .then((response) => {
          for (let i = 0; i < response.data.length; i++) {
            this.likedListingsIds.push(response.data[i]["id"]);
          }
        }).catch((err) => {
          throw new Error(`Error trying to get user's likes: ${err}`)
    })
    this.businessId = this.$route.params.businessId
    this.listingId = this.$route.params.listingId

    this.getBusinessListings(this.businessId, this.listingId);
    this.setCurrency(store.userCountry);
  },


  methods: {
    /**
     * Gets business listings by calling get endpoint
     * @param businessId ID of the business that has the listings
     * @param listingId ID of the specific listing from that business we want
     */
    getBusinessListings(businessId, listingId) {
      api.getBusinessListings(businessId)
        .then((response) => {
          this.listing = this.filterListingFromListingsResponse(response.data, listingId);
          this.listingImages = this.getListingImages(this.listing.inventoryItem.product.images);
          this.currentImage = this.getPrimaryImage(this.listingImages, this.listing)
        }).catch((error) => {
          this.$log.error(error);
        })
    },

    /**
     * Sets display currency based on the user's home country.
     * @param country country for which currency is going to be retrieved
     */
    setCurrency(country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
        .then(response => {
          this.currency = { //need symbol and code
            symbol: response.data[0].currencies[0].symbol,
            code: response.data[0].currencies[0].code
          }
        }).catch(err => {
          this.$log.debug(err);
        })
      },

    /**
     * Filters response from API to only have listing with matching ID
     * @param listingsResponse response from backend with business listings
     * @param listingId id of the listing that we need to filter
     * @return Array containing listings that match ID, should only contain one.
     */
    filterListingFromListingsResponse(listingsResponse, listingId) {
      return listingsResponse.filter(listing => listing.id == listingId)[0]
    },


    /**
     * Gets all image paths from listing, also mutates path to fit into ReImage component
     * @param images list of images of the listing
     * @return Array of listing image paths
     */
    getListingImages(images) {
      return images.map(image => image.fileName);
    },

    /**
     * Goes to business profile page, also stores listing value in session storage so user can come back to page
     * @param businessId id of business page that we are going to
     */
    goToBusinessProfile(businessId) {
      this.$router.push({path: `/businesses/${businessId}`})
      sessionStorage.setItem('previousListing', this.listing.id)
    },


    /**
     * Gets primary image from list of images and primary image path
     * @param listing listing used to get primaryImagePath to filter images
     * @return image path of primary image
     */
    getPrimaryImage(images, listing) {
      return ".\\src\\main\\resources\\media\\images\\businesses\\" + listing.inventoryItem.product.primaryImagePath.replace("/", "\\");
    },

    /**
     * Sets current image to next image in image list
     * @param currentImage the current image that is being displayed
     */
    nextImage(currentImage) {
      let indexOfImage = this.listingImages.indexOf(currentImage);
      this.currentImage = this.listingImages[(indexOfImage + 1) % this.listingImages.length]
    },

    /**
     * Sets current image to previous image in image list
     * @param currentImage the current image that is being displayed
     */
    previousImage(currentImage) {
      let indexOfImage = this.listingImages.indexOf(currentImage);
      let length = this.listingImages.length
      this.currentImage = this.listingImages[(((indexOfImage - 1) % length) + length) % length] //Negative modulo in JavaScript doesn't work since it's just remainder
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
            this.$vs.notify({text: `${listingName} has been liked and added to your watchlist!`, color: 'success'});
          })
          .catch((err) => {
            throw new Error(`Error trying to like listing ${listingId}: ${err}`);
          })
    },
    /**
     * Returns user to listing search page,
     * their previous search is shown
     */
    returnToSearch: function() {
      this.$router.push({path: '/search-listings'})
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
    },

    /**
     * Calls the post notification and delete listing endpoints after the buy button is clicked
     */
    buy() {
      api.postListingNotification(this.listingId)
              .then((response) => {
                //wait until we have successfully bought the listing before we delete it
                //to avoid race conditions where the listing is half bought before we delete it
                api.deleteListing(this.listingId)
                .catch(err => {
                  if (err.response.status === 406) {
                    this.$vs.notify({title: 'Purchase Failed', text: '406 Not Acceptable', color: 'danger'})
                  } else {
                    this.$vs.notify({title:'Purchase Failed', text:`Status Code ${err.response.status}`, color:'danger'})
                  }
                });

                this.$vs.notify({title:'Success', text:`Successfully purchased!\n${response.status}`, color:'success'})
                this.$router.push({path: `/home`});
              })
              .catch(err => {
                if (err.response.status === 400) {
                  this.$vs.notify({title:'Purchase Failed', text:'400 Bad Request', color:'danger'})
                } else if (err.response.status === 401) {
                  this.$vs.notify({title:'Purchase Failed', text:'401 Not Logged In', color:'danger'})
                } else {
                  this.$vs.notify({title:'Purchase Failed', text:`Status Code ${err.response.status}`, color:'danger'})
                }
              });
    }
  }

}
</script>

<style scoped>

.return-button {
  margin-top: -15px;
  padding-left: 10px;
  position: fixed;
  left: 0px;
}

.slide-fade-enter-active {
  transition: all .1s ease;
}
.slide-fade-leave-active {
  transition: all .3s cubic-bezier(1.0, 0.5, 0.8, 1.0);
}
.slide-fade-enter, .slide-fade-leave-to {
  transform: translateX(10px);
  opacity: 0;
}

#content-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, 50%);
}

#listing-detail-container {
  height: 100%;
  width: 60%;
  margin: 1em auto;
}

#listing-info-area {
  text-align: center;
  font-size: 15px;
  margin: 0 1em;
}

#listing-image-container {
}

#listing-image >>> img {
  width: 100%;
  object-fit: cover;
}

#product-info-area {
  margin-top: 1em;
  margin-left: 1em;
  text-align: center;
  font-size: 15px;
  padding-bottom: 2em;
}

p {
  text-align: left;
}

#business-name {
  text-align: center;
  margin-left: 50px;
  font-size: 25px;
  cursor: pointer;
}

#business-profile-button {
  text-align: center;
  margin-left: 50px;
  margin-bottom: 1em;
  margin-top: 0.5em;
}

#listing-name {
  font-size: 25px;
  margin-bottom: 10px;
}

.like-listing-text {
  text-align: center;
}

.listing-detail-btn {
  width: 45%;
  text-align: center;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 1em;
}

@media screen and (max-width: 1300px) {
  #content-container {
    grid-template-columns: 1fr;
  }

  #listing-image > img {
    width:  300px;
    height: 200px;
    object-fit: cover;
  }

  #product-info-area {
    text-align: center;
    font-size: 12px;
    padding-bottom: 2em;
    margin-top: 2em;
  }

  #listing-info-area {
    text-align: center;
    font-size: 12px;
  }

  #listing-name {
    font-size: 20px;
    margin-bottom: 5px;
  }

  #business-name {
    font-size: 20px;
    margin-bottom: 0.5em;
  }

  #listing-image {
    margin: auto;
  }

  #listing-image-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
}


@media screen and (max-width: 600px) {
  #listing-detail-container {
    width: 90%;
  }
}


@media screen and (max-width: 400px) {
  #listing-detail-container {
    width: 100%;
  }

  #listing-image > img {
    width:  200px;
    height: 150px;
    object-fit: cover;
  }
}


</style>
