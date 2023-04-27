<template>
  <div>
    <div id="container" v-if="this.user != null">

      <!-- Far left side options menu-->
      <div id="options-bar">
        <div class="sub-header" style="text-align: center"> Options </div>
        <vs-button class="options-card" id="option-view-cards" @click="openMarketModal()">Marketplace Cards</vs-button>
        <vs-button class="options-card" id="option-add-to-business" v-if="userViewingBusinesses.length >= 1" @click="openModal()"> Add to Business </vs-button>
        <vs-button class="options-card" v-if="user.id === curUserId" @click="goToModifyUser()">Edit Profile</vs-button>
      </div>

      <div id="name-container">
        <vs-dropdown class="title-image" vs-trigger-click>
          <ReImage :imagePath="user.primaryImagePath" :isUser="true" class="title-image" v-if="user.primaryImagePath"></ReImage>
          <vs-avatar v-else icon="store" color="#1F74FF" size="100px" name="avatar" class="title-image"></vs-avatar>
          <vs-dropdown-menu v-if="this.curUserId == this.user.id">
            <vs-dropdown-item @click="updatePrimary=true; openImageUpload()" class="profileDropdown">
              <vs-icon icon="add_box" style="margin: auto"></vs-icon>
              <div style="font-size: 12px; margin: auto">Add New Primary Image</div>
            </vs-dropdown-item>
            <vs-dropdown-group v-if="images.length > 1" class="profileDropdown" vs-collapse vs-icon="collections" vs-label="Update With Existing" style="font-size: 13px">
              <vs-dropdown-item v-for="image in filteredImages()" :key="image.id" @click="updatePrimaryImage(image.id);">
                {{image.name}}
              </vs-dropdown-item>
            </vs-dropdown-group>
          </vs-dropdown-menu>
        </vs-dropdown>

        <div id="full-name"> {{ this.user.firstName }} {{ this.user.middleName }} {{ this.user.lastName }} </div>
        <div id="nickname"> {{ this.user.nickname }} </div>
      </div>

      <!-- Left Profile Side -->
      <div id="profile">
        <div id="bio" class="sub-container">
          <div class="sub-header">Bio</div>
          {{ this.user.bio }}
        </div>

        <div class="sub-container" id="info-container">
          <div id="created">
            <div class="sub-header">Member Since</div>
            {{ this.user.created.split(' ')[0] }}
            <div style="font-size: 12px">{{ calculateDuration(user.registerDate) }}</div>

          </div>
          <div id="email">
            <div class="sub-header">Email</div>
            {{ this.user.email }}
          </div>
          <div id="birthDate">
            <div class="sub-header">Date of Birth</div>
            {{ this.user.dateOfBirth }}
          </div>
          <div id="address">
            <div class="sub-header">Home Address</div>
              <div id="street-address">{{ user.homeAddress.streetNumber }} {{ user.homeAddress.streetName }}</div>
              <div id="suburb">{{ user.homeAddress.suburb }}</div>
              <div id="city">{{ user.homeAddress.city }}</div>
              <div id="region">{{ user.homeAddress.region }}</div>
              <div id="country">{{ user.homeAddress.country }}</div>
              <div id="postcode">{{ user.homeAddress.postcode }}</div>
          </div>
          <div id="phonenumber">
            <div class="sub-header">Phone Number</div>
            {{ this.user.phoneNumber }}
          </div>
        </div>

      </div>

      <!-- Right Content Side -->
      <main>
        <vs-tabs  alignment="fixed">
          <vs-tab label="Businesses">
          <div class="sub-header" id="businesses-header">Businesses</div>
            <ul id="business-list">
              <li class="card" v-for="business in businesses" :key="business.id" v-bind:business="business" @click="goToBusinessPage(business)">
                <div class="card-name">{{ business.name }}</div>
                <div class="card-type">{{ business.businessType }}</div>
                <div class="card-description">{{ business.description }}</div>
              </li>
            </ul>
          </vs-tab>
          <vs-tab label="Images">
            <UserImages :user="user" :primaryImage="user.primaryImagePath" :images="images"  @getUser="getUserInfo(user.id)" @update="reloadLocation"></UserImages>
          </vs-tab>
        </vs-tabs>
      </main>

  </div>

    <!-- show users marketplace activity modal -->
    <vs-popup :active.sync="showMarketModal" title="Marketplace Activity" id="market-card-modal">
      <div v-if="cards.length > 0" class="container">
        <MarketplaceGrid @cardRemoved="getUserCards(user.id)" :cardData="cards.slice((currentCardPage-1)*4, currentCardPage*4)" :showSection="false"></MarketplaceGrid>
        <vs-pagination :max="5" :total="Math.ceil(cards.length/4)" v-model="currentCardPage"></vs-pagination>
      </div>
      <!-- If the user has no active cards -->
      <div v-else class="container">
        This user has no active cards on the marketplace right now.
      </div>
    </vs-popup>

    <!-- Add user to business as admin modal -->
    <vs-popup title="Add User to Business" :active.sync="showModal">
      <div style="margin: 2em 0; text-align: center;">
        Select a Business
        <vs-select class="business-dropdown" v-model="selectedBusiness">
          <vs-select-item v-for="business in userViewingBusinesses" :key="business.id" :text="business.name" :value="business"/>
        </vs-select>
      </div>

      <div id="modal-footer">
        <vs-button type="flat" class="modal-button modal-cancel-button" @click="closeModal()">
          Cancel
        </vs-button>
        <vs-button class="modal-button modal-ok-button" id="add-user" @click="addUserToBusiness()">
          Add
        </vs-button>
      </div>
    </vs-popup>
    <input type="file" id="fileUpload" ref="fileUpload" style="display: none;" multiple @change="uploadImage($event)"/>
  </div>
</template>


<script>
import Modal from "./Modal";
import api from "../Api";
import {store} from "../store";
import MarketplaceGrid from '../components/MarketplaceGrid';
import ReImage from "../components/ReImage";
import CardModal from "../components/CardModal";
import ModifyUser from "../components/ModifyUser";
import UserImages from "../components/UserImages";
import {bus} from "../main";
const moment = require('moment');

const Users = {
  name: "Profile",
  components: {
    ModifyUser, Modal, MarketplaceGrid, CardModal, UserImages, ReImage
  },
  data: function () {
    return {
      // Pagination
      currentCardPage: 1,

      user: null,
      businesses: [],
      userViewingBusinesses: [],
      showMarketModal: false,
      showModal: false,
      selectedBusiness: null,
      displayType: true,
      cards: [],
      images: [],

      displayOptions: false,
      modifyModal: false,
      curUserId: store.loggedInUserId,
    };
  },

  methods: {
    /**
     * Trigger the file upload box to appear.
     * Used for when the actions dropdown add image action or add image button is clicked.
     */
    openImageUpload: function() {
      this.$refs.fileUpload.click();
    },

    /**
     * Upload user image when image is uploaded on web page
     * @param e Event object which contains file uploaded
     */
    uploadImage: async function(e) {
      //Setup FormData object to send in request
      this.$vs.loading(); //Loading spinning circle while image is uploading (can remove if not wanted)
      for (let image of e.target.files) {
        const fd = new FormData();
        fd.append('filename', image, image.name);
        api.postUserImage(this.user.id, fd)
                .then((res) => { //On success
                  this.$vs.notify({title:`Image for ${this.user.firstName} was uploaded`, color:'success'});
                  let imageId = res.data.id;
                  if (this.updatePrimary) {
                    this.updatePrimaryImage(imageId);
                  } else {
                    this.reloadLocation();
                  }

                  bus.$emit("updateUserPicture", "updated");

                })
                .catch((error) => { //On fail
                  if (error.response.status === 400) {
                    this.$vs.notify({title:`Failed To Upload Image`, text: "The supplied file is not a valid image.", color:'danger'});
                  } else if (error.response.status === 500) {
                    this.$vs.notify({title:`Failed To Upload Image`, text: 'There was a problem with the server.', color:'danger'});
                  }
                })
                .finally(() => {
                  this.$vs.loading.close();
                });
      }
    },

    /**
     * Call api endpoint to update the primary image for the user.
     */
    updatePrimaryImage: function(imageId) {
      api.changeUserPrimaryImage(this.user.id, imageId)
              .then(async () => {
                this.updatePrimary = false;
                bus.$emit("updatedUserPicture", "updated");
                this.reloadLocation();
              })
              .catch((error) => {
                if (error.response.status === 400) {
                  this.$vs.notify({title:`Failed To Update Primary Image`, color:'danger'});
                } else if (error.response.status === 500) {
                  this.$vs.notify({title:`Failed To Update Primary Image`, text: 'There was a problem with the server.', color:'danger'});
                }
              });
    },

    /**
     * Show the modal box.
     * Having a separate function to just open the modal is good for testing.
     */
    openModal: function() {
      this.showModal = true;
    },

    /**
     * Retrieves all the cards that the user has created.
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
     * Show the modal box (marketplace activity).
     * Having a separate function to just open the modal is good for testing.
     */
    openMarketModal: function() {
      this.showMarketModal = true;
      this.getUserCards(this.user.id);
    },

    goToModifyUser() {
      this.$router.push({path: `/user/${this.user.id}/editprofile`});
    },


        /**
     * Called by primary image dropdown component, filtering the primary image out so
     * that only the non-primary images are displayed.
     */
    filteredImages: function() {
      let filteredImages = [];
      for (let image of this.images) {
        if (this.user.primaryImagePath && image.fileName.match(/\d+/g)[1] !== this.user.primaryImagePath.match(/\d+/g)[1]) {
          filteredImages.push(image);
        }
      }
      return filteredImages;
    },


    /**
     * Close the pop-up box with no consequences.
     */
    closeModal: function() {
      this.showModal = false;
    },

    /**
    * Reload the component
    */
    reloadLocation: function() {
      location.reload();
    },

    /**
     * Called when the pop-up box has the OK button pressed. Add the user to the given business as an admin.
     */
    addUserToBusiness: function() {
      api.makeUserBusinessAdmin(this.selectedBusiness.id, this.user.id)
        .then(() => {
          // 200 code.
          this.businesses.push(this.selectedBusiness);
          this.$vs.notify({title:`Added user to ${this.selectedBusiness.name}`, text:`Successfully added ${this.user.firstName} as an administrator.`, color:'success'});
          this.closeModal();
        })
        .catch((error) => {
          if (error.response) {
            if (error.response.status === 400) {
              this.$vs.notify({title:`Failed to add user to ${this.selectedBusiness.name}`, text:`${this.user.firstName} is already an administrator.`, color:'danger'});
            }
            else {
              throw new Error(`Error trying to add user to business: ${error.response.status}`);
            }
          }
        });
    },

    /**
     * Calculates the length of time since the user's registration date, outputting a string value.
     * @param registerDate time and date of when the user registered.
     * @return {string} string description of how long it has been since they have registered.
     */
    calculateDuration: function(registerDate) {
      const TimeElapsed = Date.now();
      const today = new Date(TimeElapsed);
      let fromTime = moment(registerDate).diff(today);
      let duration = moment.duration(fromTime);

      let timeString = "(";
      if ((duration._data.years / -1) > 1) timeString += duration._data.years / -1 + " years ";
      if ((duration._data.months / -1 ) > 1) timeString += duration._data.months / -1 + " months";
      else timeString += "under 1 month";
      timeString += ")";

      return timeString;
    },

    /**
     * Performs a get request to get user info to display on page
     * @param userId ID of user that is currently being viewed
     */
    getUserInfo: function(userId) {
      api.getUserFromID(userId) //Get user data
        .then((response) => {
          this.user = response.data;
          if(store.userBusinesses != null){
            this.userViewingBusinesses = store.userBusinesses;
            this.userViewingBusinesses = this.userViewingBusinesses.filter(business => business.primaryAdministratorId === this.curUserId);
          }
          this.images = this.user.images;
          this.businesses = JSON.parse(JSON.stringify(this.user.businessesAdministered));
        })
        .catch((err) => {
          if (err.response) {
            if (err.response.status === 401) {
              this.$vs.notify({title:'Unauthorized Action', text:'You must login first.', color:'danger'});
              this.$router.push({path: "/login"}); //If user not logged in send to login page
            }
            else if (err.response.status === 406) {
              this.$vs.notify({title:'User not found', text:'This user does not exist.', color:'danger'});
              this.$router.push({path: "/home"}); //If user is logged in, but non-existent user
            }
          }
          throw new Error(`Error trying to get user info from id: ${err}`);
      });
    },

    /**
     * Redirects the browser to the business page that was pressed on.
     * @param business id to redirect to.
     */
    goToBusinessPage: function(business) {
      this.$router.push({path: `/businesses/${business.id}`})
    },

  },

  mounted: function () {
    //On page load call getUserInfo function to get user information
    let userId = this.$route.params.id
    this.getUserInfo(userId);
  },
}

export default Users;
</script>

<style scoped>

#market-card-modal >>> .vs-popup {
  width: 1200px;
}

#market-card-modal {
  z-index: 100;
}

#option-view-cards {
  padding-left: 0;
  padding-right: 0;
}

#container {
  display: grid;
  grid-template-columns: 1fr 1fr 4fr 1fr;
  grid-template-rows: 1fr auto;
  grid-column-gap: 1em;
  margin: auto;
  padding: 0 2em;
}

/* Options Bar */
#options-bar {
  grid-column: 1;
  grid-row: 2;

  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #FFFFFF;
  margin: 1em 0 1em 0;
}

.options-card {
  width: 100%;
  padding: 12px 20px;
  margin: 0.5em auto;
}

/* Name Header */
#name-container {

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

#full-name {
  font-size: 32px;
  line-height: 30px;
  padding: 0.5em 0 0.5em 0;
}

#nickname {
  font-size: 16px;
  padding: 0 0 0.5em 0;
  grid-column: 2;
}

.profileDropdown >>> .vs-dropdown--item-link {
  display: flex;
}

/* Left Profile Side */
#profile {
  grid-column: 2;
  grid-row: 2;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;
}

.sub-header {
  font-size: 12px;
  color: gray;
}

.sub-container {
  padding: 2em;
  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  background-color: #FFFFFF;
}

#bio {
  grid-column: 1;
  grid-row: 2;
}

#info-container {
  grid-column: 1;
  grid-row: 3;

  font-size: 16px;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto) repeat(1, 1fr);
  grid-row-gap: 1em;

}

/* Main Content Side */
main {
  grid-column: 3;
  grid-row: 2;

  border-radius: 4px;
  box-shadow: 0 11px 35px 2px rgba(0, 0, 0, 0.14);
  margin: 1em 0 1em 0;
  padding: 0;
  background-color: #FFFFFF;
}

/* Business Card Component Related */
#businesses-header {
  padding: 1em 3em 0 3em;
}

#business-list {
  padding: 0 1em;
}

.card {
  background-color: transparent;
  padding: 1em;
  border-radius: 4px;
  border: 2px solid rgba(0, 0, 0, 0.02);
  margin: 0 1em 1em 1em;
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15);

  display: grid;
  grid-template-columns: auto auto;
  grid-template-rows: auto auto auto;
}

.card:hover {
  box-shadow: 0 0.5em 1em rgba(0,1,1,.25);
  cursor: pointer;
}

.card >>> .card-name {
  grid-row: 1;
  grid-column: 1;

  padding: 0.25em 0 0.25em 0;
  font-size: 24px;
}

.card >>> .card-type {
  grid-row: 1;
  grid-column: 2;

  text-align: end;
  height: fit-content;
  line-height: normal;
  font-size: 16px;
  padding: 0.5em 0 1em 0;
}

.card >>> .card-description {
  grid-column: 1/3;
  font-size: 12px;
  padding: 0.5em 0 0.5em 0;
}


/* For when the screen gets too narrow - mainly for mobile view */
@media screen and (max-width: 700px) {
  #container {
    display: grid;
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto auto;
  }

  #name-container {
    grid-column: 1;
    grid-row: 1;
  }

  #options-bar {
    grid-column: 1;
    grid-row: 2;
  }

  #profile {
    grid-column: 1;
    grid-row: 3;
  }

  main {
    grid-column: 1;
    grid-row: 4;
    margin: 0;
  }

  .card {
    max-width: 100%;
  }

}


/* Make User Admin Popup Box */
.business-dropdown {
  text-align: center;
  font-size: 14px;
  text-decoration: none;

  width: 75%;
  margin: auto;
  padding: 12px 16px;
}

#modal-footer {
  text-align: center;
  margin: auto;
}

.modal-ok-button {
  text-align: center;
  width: 100px;
  margin: 0 1em;
  padding: 10px 20px;
}

.modal-cancel-button {
  text-align: center;
  width: 100px;
  margin: 0 1em;
  padding: 10px 20px;
}



</style>
