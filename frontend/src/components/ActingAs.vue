<template>
  <div v-if="dataReady" class="userInfo">
    <h2 class = "dgaa" v-if="getUserRole() === 'DGAA' || getUserRole() === 'GAA'"><span>{{getUserRole()}}</span></h2>

    <div id="container">
      <vs-dropdown vs-trigger-click>
        <div v-if="getActingAsBusinessName() == null" class="acting-display">
          <span class="user">{{getUserName()}}</span>
          <ReImage v-if="user.primaryImagePath != null" :imagePath="user.primaryImagePath"  :isUserThumbnail="true" ></ReImage>
          <vs-avatar v-else icon="person" size="30" name="avatar" />
        </div>
        <div v-else class="acting-display">
          <span class="user">{{getActingAsBusinessName()}}</span>
          <ReImage v-if="getActingAs().primaryImagePath !== null" :imagePath="getActingAs().primaryImagePath" :isThumbnail="true" style="border: 1px solid #ddd; border-radius: 50%; padding: 2px;"></ReImage>
          <vs-avatar v-else-if="getUserName() !== null" icon="store" size="30" name="avatar">
          </vs-avatar>
        </div>

        <vs-dropdown-menu class="user-menu">
          <vs-dropdown-item class="dropdown-item" @click="setActingAsUser()" v-if="getActingAsBusinessName()">
              <div class="dropdown-item-name" >{{ getUserName() }} </div>
              <ReImage v-if="user.primaryImagePath != null" :imagePath="user.primaryImagePath" :isUserThumbnail="true" style="border: 1px solid #ddd; border-radius: 50%; padding: 2px;"></ReImage>
              <vs-avatar v-else class="dropdown-item-avatar" size="small">
            </vs-avatar>
          </vs-dropdown-item>

          <vs-dropdown-group vs-label="Businesses" id="businessList">
            <vs-dropdown-item class="dropdown-item" v-for="business in this.businesses" :key="business.id" v-on:click="setActingAsBusinessId(business.id, business.name)">
              <div class="dropdown-item-name">{{business.name}} </div>
              <ReImage v-if="business.primaryImagePath !== null" :imagePath="business.primaryImagePath" :isThumbnail="true" style="border: 1px solid #ddd; border-radius: 50%; padding: 2px;"></ReImage>
              <vs-avatar v-else class="dropdown-item-avatar" icon="store" size="small"></vs-avatar>
            </vs-dropdown-item>
          </vs-dropdown-group>
        </vs-dropdown-menu>
      </vs-dropdown>
    </div>
  </div>
</template>

<script>
import {store, mutations} from "../store";
import api from "../Api";
import ReImage from "./ReImage";
import { bus } from "../main";

const ActingAs =  {
  name: "actingAs",
  components: {ReImage},
  data: function () {
    return {
      loggedInUserId: null,
      userName: null,
      role: null,
      userBusinesses: [],
      businesses: [],
      actingAsBusinessId: null,
      actingAsBusinessName: null,
      user: null,
      dataReady: false,
    }
  },

  created() {
    bus.$on('updatedUserPicture', () => {
      this.getUser();
    })
    bus.$on('updatedBusinessPicture', () => {
      this.getBusinesses();
    })
    bus.$on('updatedUserInfo', () => {
      this.getUser();
    })
  },

  async mounted() {
    await this.getUser();
  },

  methods: {
    /**
     * Retreive the current userName and loggedInUserId of acting as account
     * @returns the acting name
     */
    getUserName() {
      this.userName = this.user.firstName + " " + this.user.lastName;
      this.loggedInUserId = store.loggedInUserId;
      return this.userName;
    },



    /**
     * Retreives the acting account
     * @returns role of the acting account
     */
    getUserRole() {
      this.role = store.role;
      return this.role;
    },

    getActingAs() {
      return this.businesses.filter(x => x.id == store.actingAsBusinessId)[0];
    },


    /**
     * Returns the user currently logged in
     */
    getUser() {
      api.getUserFromID(store.loggedInUserId)
          .then((response) => {
            this.user = response.data;
            this.getBusinesses();
          }).catch((err) => {
        console.log("Error " + err);
      })

    },

    /**
     * Retrieve the businesses administrated by current acting as account, or all business less the current business
     * if currently acting as a business account
     * @returns filtered list of businesses
     */
    getBusinesses(){

      for(let business of this.user.businessesAdministered) {
        api.getBusinessFromId(business.id)
        .then((response) => {
          console.log(response)
          this.businesses.push(response.data);

        }).catch((err) => {
          console.log(err);
        })
      }

      this.dataReady = true;
    },

    /**
     * Set the acting as business id and name and store it in store.js
     * @param businessId id of acting as business account
     * @param businessName name of acting as business account
     */
    setActingAsBusinessId(businessId, businessName){
      api.actAsBusiness(businessId)
          .then(() => {
            this.refreshCachedItems();
            mutations.setActingAsBusiness(businessId, businessName)
            this.$router.push({path: `/home`}).catch(() => this.$router.go());
          }).catch((error) => {
        if(error.response) {
          this.$log.debug("Error Status:", error.response.status, ":", error.response.message)

        }
        this.$log.debug("Error Status:", error)
      });

      // Prominent vue-router contributor suggests to catch error and do nothing with it.
      // @see https://github.com/vuejs/vue-router/issues/2872
    },

    /**
     * Set the acting as to the logge in user, and update store.js file
     */
    setActingAsUser(){
      api.actAsBusiness(0)
          .then(() => {
            this.refreshCachedItems();
            mutations.setActingAsUser();
            this.$router.push({path: `/home`}).catch(() => {console.log("NavigationDuplicated Warning: same route.")});
          }).catch((error) => {
            if(error.response) {
              this.$log.debug("Error Status:", error.response.status, ":", error.response.message)
            }

        this.$log.debug("Error Status:", error)
      });
    },

    /**
     * Empty the business cache, when switching acting as to user account
     */
    refreshCachedItems() {
      if (sessionStorage.getItem('businessesCache') !== null) {
        sessionStorage.removeItem("businessesCache");
      }
    },

    /**
     * Retrieve the acting as business name from store
     * @returns {String} name of the acting business
     */
    getActingAsBusinessName() {
      this.actingAsBusinessName = store.actingAsBusinessName
      return this.actingAsBusinessName;
    },

    /**
     * Display the businesses administrated by the user
     */
    showUserBusinesses: function() {
      let x = document.getElementById('userBusinessPanel');
      x.style.display = "block";
    },

    /**
     * Hide the businesses administrated by the user
     */
    hideUserBusinesses: function() {
      let x = document.getElementById('userBusinessPanel');
      x.style.display = "none";
    }
  },
}
export default ActingAs;
</script>

<style scoped>

.userInfo >>> .vs-con-dropdown {
  cursor: pointer;
}

#container {
  height: 100%;
}


.parent-dropdown {
  height: 100%;
}

.acting-display {
  text-align: center;
  display: flex;
  min-width: auto;
  margin-left: 10px;
  margin-right: 5px;
  justify-content: center;
  align-items: center;
  height: 100%;
  cursor: pointer;
}


span.user {
  font-size: 16px;
  margin: auto auto auto auto;
  text-align: right;
}

.dropdown-item >>> a {
  display: flex;
  text-align: center;
}

.dropdown-item-name {
  margin: auto;
  min-width: 100px;
}

</style>
