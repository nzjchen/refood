<template>
  <div v-if="business">
    <div v-if="fromSearch" class="return-button">
      <vs-button @click="returnToSearch()" title="Go Back">Return To Search</vs-button>
    </div>
    <div v-else-if="fromListing" class="return-button">
      <vs-button @click="returnToListing()" title="Go Back">Return To Listing</vs-button>
    </div>

    <div id="container" v-if="this.business != null">
      <!-- Top Component Title -->
      <div id="business-name-container">
        <vs-dropdown class="title-image" vs-trigger-click>
          <ReImage :imagePath="business.primaryImagePath" :isBusiness="true" class="title-image" v-if="business.primaryImagePath"></ReImage>
          <vs-avatar v-else icon="store" color="#1F74FF" size="100px" name="avatar" class="title-image"></vs-avatar>
          <vs-dropdown-menu v-if="getActingAsBusinessId() == business.id">
            <vs-dropdown-item @click="updatePrimary=true; openImageUpload()" class="profileDropdown">
              <vs-icon icon="add_box" style="margin: auto"></vs-icon>
              <div style="font-size: 12px; margin: auto">Add New Primary Image</div>
            </vs-dropdown-item>
            <vs-dropdown-group v-if="images.length > 1 && getActingAsBusinessId() == business.id" class="profileDropdown" vs-collapse vs-icon="collections" vs-label="Update With Existing" style="font-size: 13px">
              <vs-dropdown-item v-for="image in filteredImages()" :key="image.id" @click="updatePrimaryImage(image.id);">
                {{image.name}}
              </vs-dropdown-item>
            </vs-dropdown-group>
          </vs-dropdown-menu>
        </vs-dropdown>
        <div id="business-name"  >{{ business.name }}</div>
        <div id="business-type">{{ business.businessType }}</div>
      </div>
      <!-- Left Side Business Information Panel -->
      <div id="business-container">
        <div v-if="getActingAsBusinessId() === null" class="sub-container">
          <vs-button id="wishlist-button" :icon="inWishlist ? 'star' : 'star_outline'" style="width: 100%" @click="toggleWishlist()">
            {{ inWishlist ? 'Remove from Wishlist' : 'Add to Wishlist' }}
          </vs-button>
        </div>

        <div v-if="getActingAsBusinessId() === business.id" class="sub-container">
          <vs-button id="wishlist-button" icon="settings" style="width: 100%" :to="'/businesses/'+business.id+'/modify'">
            Modify {{business.name}}
          </vs-button>
        </div>

        <div id="description" class="sub-container">
          <div class="sub-header">Description</div>
          {{ business.description }}
        </div>

        <div id="info-container" class="sub-container">
          <div id="created-date">
            <div class="sub-header">Created</div>
            {{ business.created.split(' ')[0] }}
          </div>
          <div id="address">
            <div class="sub-header">Address</div>
            <div id="street-address">{{ business.address.streetNumber }} {{ business.address.streetName }}</div>
            <div id="city">{{ business.address.city }}</div>
            <div id="suburb">{{ business.address.suburb }}</div>
            <div id="region">{{ business.address.region }}</div>
            <div id="country">{{ business.address.country }}</div>
            <div id="postcode">{{ business.address.postcode }}</div>
          </div>
        </div>
      </div>

      <main>
        <!-- Sub Navigation Bar -->
        <vs-tabs id="business-navbar" alignment="fixed">
          <vs-tab class="business-nav-item" label="Listings">
            <BusinessListings :business-id="business.id" :country="user.homeAddress.country"/>
          </vs-tab>
          <vs-tab class="business-nav-item" label="Administrators">
            <BusinessAdministrators :admins="adminList" :pAdminId="business.primaryAdministratorId"/>
          </vs-tab>
          <vs-tab class="business-nav-item" label="Images">
            <BusinessImages v-on:getBusiness="getBusiness" v-on:update="reloadLocation" :images="images" :primaryImagePath="business.primaryImagePath" :business="business"></BusinessImages>
          </vs-tab>
        </vs-tabs>
      </main>

    </div>
    <input type="file" id="fileUpload" ref="fileUpload" style="display: none;" multiple @change="uploadImage($event)"/>
  </div>
</template>


<script>
import api from "../Api";
import BusinessAdministrators from "./BusinessAdministrators";
import BusinessListings from "./BusinessListings";
import BusinessImages from "./BusinessImages";
import ReImage from "./ReImage";
import {store} from "../store";
import { bus } from "../main";

const Business = {
  name: "Business",
  components: {BusinessListings, BusinessAdministrators, BusinessImages, ReImage},

  // App's initial state.
  data: function () {
    return {
      fromListing: sessionStorage.getItem("previousListing"),
      fromSearch: sessionStorage.getItem("businessesCache"),
      business: null,
      adminList: null,
      user: null,

      inWishlist: false, // i.e. is it in the user's wishlist.
      wishlistId: null,
      images: [],
      updatePrimary: false,
    };
  },

  methods: {
    /**
     * Called by primary image dropdown component, filtering the primary image out so
     * that only the non-primary images are displayed.
     */
    filteredImages: function() {
      let filteredImages = [];
      for (let image of this.images) {
        if (this.business.primaryImagePath && image.fileName.match(/\d+/g)[1] !== this.business.primaryImagePath.match(/\d+/g)[1]) {
          filteredImages.push(image);
        }
      }
      return filteredImages;
    },

    /**
     * Trigger the file upload box to appear.
     * Used for when the actions dropdown add image action or add image button is clicked.
     */
    openImageUpload: function() {
      this.$refs.fileUpload.click();
    },

    /**
     * Upload business image when image is uploaded on web page
     * @param e Event object which contains file uploaded
     */
    uploadImage: async function(e) {
      //Setup FormData object to send in request
      this.$vs.loading(); //Loading spinning circle while image is uploading (can remove if not wanted)
      for (let image of e.target.files) {
        const fd = new FormData();
        fd.append('filename', image, image.name);
        api.postBusinessImage(this.business.id, fd)
          .then((res) => { //On success
            this.$vs.notify({title:`Image for ${this.business.name} was uploaded`, color:'success'});
            let imageId = res.data.id;
            if (this.updatePrimary) {
              this.updatePrimaryImage(imageId);
            } else {
              this.reloadLocation();
            }

            bus.$emit("updateBusinessPicture", "updated");

          })
          .catch((error) => { //On fail
            if (error.response) {
              if (error.response.status === 400) {
                this.$vs.notify({title:`Failed To Upload Image`, text: "The supplied file is not a valid image.", color:'danger'});
              } else if (error.response.status === 500) {
                this.$vs.notify({title:`Failed To Upload Image`, text: 'There was a problem with the server.', color:'danger'});
              }
            }
          })
          .finally(() => {
            this.$vs.loading.close();
          });
      }
    },

    /**
     * Gets business id user is acting as
     **/
    getActingAsBusinessId() {
      return store.actingAsBusinessId;
    },

    /**
     * Call api endpoint to update the primary image for the business.
     */
    updatePrimaryImage: function(imageId) {
      api.changeBusinessPrimaryImage(this.business.id, imageId)
        .then(async () => {
          await this.getBusiness();
          this.updatePrimary = false;
          bus.$emit("updateBusinessPicture", "updated");
          this.reloadLocation();
        })
        .catch((error) => {
          if(error.response) {
            if (error.response.status === 400) {
              this.$vs.notify({title:`Failed To Update Primary Image`, color:'danger'});
            } else if (error.response.status === 500) {
              this.$vs.notify({title:`Failed To Update Primary Image`, text: 'There was a problem with the server.', color:'danger'});
            }
          }
        });
    },

    /**
     * Reload the component
     */
    reloadLocation: function() {
      location.reload();
    },

    /**
     * Adds or removes the current business from the user's wishlist.
     */
    toggleWishlist: function() {
      if (!this.inWishlist) {
        api.addBusinessToWishlist(this.business.id)
          .then(() => {
            this.inWishlist = true;
            this.$vs.notify({title: "Added to Wishlist",
              text: `${this.business.name} was added to your wishlist`,
              color: "success",
              icon: "add"});
            this.getUserWishlist(); // Refresh the wishlistId
          })
          .catch((error) => {
            this.$log.debug(error);
            this.$vs.notify({title: "Error Adding to Wishlist",
              text: `${this.business.name} could not be added to your wishlist. Please try again`,
              color: "danger",
              icon: "error"});
          });

      }
      else {
        api.removeBusinessFromWishlist(this.wishlistId)
          .then(() => {
            this.inWishlist = false;
            this.$vs.notify({title: "Removed from Wishlist",
              text: `${this.business.name} was removed from your wishlist`,
              color: "success",
              icon: "remove"});
          })
          .catch((error) => {
            this.$log.debug(error);
            this.$vs.notify({title: "Error Removing from Wishlist",
              text: `${this.business.name} could not be removed to your wishlist. Please try again`,
              color: "danger",
              icon: "error"});
          })
      }
    },

    /**
     * Retrieves the user's wishlist.
     * Checks if the current business is wishlisted by the user, and adjusts display accordingly.
     */
    getUserWishlist: function() {
      api.getUsersWishlistedBusinesses(this.user.id)
        .then((res) => {
          for (let data of res.data) {
            if (this.business.id === data.business.id) {
              this.wishlistId = data.id;
              this.inWishlist = true;
              break;
            }
          }
        })
        .catch((error) => {
          this.$log.debug(error);
        });
    },

    /**
     * Retrieves the business information including the administrators.
     */
    getBusiness: async function () {
      api.getBusinessFromId(this.$route.params.id)
          .then((res) => {
            this.business = res.data;
            this.adminList = JSON.parse(JSON.stringify(this.business.administrators)); // It just works?
            this.images = this.business.images;
          })
          .catch((error) => {
            if (error.response) {
              if (error.response.status === 406) {
                this.$vs.notify({title:'Error', text:'This business does not exist.', color:'danger', position:'top-center'})
              }
            }
            this.$log.error(`ERROR trying to obtain business info from Id: ${error}`);
          });
    },

    /**
     * Retrieves the current active user's information.
     * @param userId the id of the currently logged in user.
     */
    getUserInfo: function (userId) {
      api.getUserFromID(userId)
        .then((response) => {
          this.user = response.data;
          this.getUserWishlist();
        })
        .catch((err) => {
          if (err.response) {
            if (err.response.status === 401) {
              this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
              this.$router.push({name: 'LoginPage'});
            }
          }
          this.$log.error(`ERROR trying to obtain user info from Id: ${err}`);
      });

    },

    /**
     * Returns the user back to the search page.
     */
    returnToSearch: function() {
      this.$router.push({path: '/search'})
    },

    /**
     * Returns the user back to the listing page.
     */
    returnToListing: function() {
      this.$router.push({path: `/businesses/${this.business.id}/listings/${this.fromListing}`})
    },

    /**
     * Checks the current user's session.
     * When successful, retrieve the user's information, and the business information.
     */
    checkUserSession: function() {
      api.checkSession()
        .then((response) => {
          this.getUserInfo(response.data.id);
          this.getBusiness();
        })
        .catch((error) => {
          this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
          this.$log.error("Error checking sessions: " + error);
        });
    }
  },

  mounted() {
    this.checkUserSession();
  },

  watch: {
    $route() {
      this.checkUserSession();
    }
  }
}

export default Business;
</script>


<style scoped>

#container {
  display: grid;
  grid-template-columns: 1fr 1fr 4fr 1fr;
  grid-template-rows: auto auto;
  grid-column-gap: 1em;
}

/* === TOP BANNER BUSINESS NAME === */
#business-name-container {
  grid-column: 2 / 4;
  grid-row: 1;

  display: grid;
  grid-template-columns: 2.5fr 1fr 1.5fr;
  grid-template-rows: auto auto;
  text-align: center;
  background-color: transparent;
  padding: 0.5em 0 0.5em 0;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin: 8px 0 0 0;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);
}

#business-name {
  font-size: 32px;
  padding: 0.5em 0 0.5em 0;
  grid-column: 2;
}

#business-type {
  font-size: 16px;
  padding: 0 0 0.5em 0;
  grid-column: 2;
}

.title-image {
  height: 100px;
  width: 200px;
  object-fit: cover;
  display: flex;
  grid-column: 1;
  grid-row: 1 / 3;
  margin-left: auto;
  justify-content: flex-end;
  margin-right: 20px;
}

.profileDropdown >>> .vs-dropdown--item-link {
  display: flex;
}
/* === LEFT SIDE INFO PANEL === */
#business-container {
  grid-column: 2;
  grid-row: 2;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;
  margin-top: 1em;
}

.sub-container {
  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #FFFFFF;
  overflow: auto;
}

.sub-header {
  font-size: 12px;
  color: gray;
}

.return-button {
  margin: 10px;
}

#description {
  grid-row: 2;
  font-size: 14px;
}

#info-container {
  grid-column: 1;
  grid-row: 3;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(1, auto) repeat(1, 1fr);
  grid-row-gap: 2em;
}

#created-date {
  grid-column: 1;
  grid-row: 1;
}

#address {
  grid-row: 2;
  height: fit-content;
}

/* === MAIN CONTENT SECTION === */
main {
  grid-column: 3;
  grid-row: 2;

  margin: 1em 0 1em 0;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #FFFFFF;
}

#business-navbar {
  grid-column: 2;
  grid-row: 1;
  height: 98%;
}

.business-nav-item {
  padding: 0 1em;
  font-size: 14px;
  height: 100%;
}

#business-navbar >>> .vs-tabs--li { /* Targets individual tab */
  padding: 0 1em;
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

  #business-name-container {
    grid-column: 1;
    grid-row: 1;
  }

  #business-container {
    grid-column: 1;
    grid-row: 2;
  }

  main {
    grid-column: 1;
    grid-row: 3;
  }

  #business-navbar {
    align-content: center;
  }

}

</style>
