<template>
  <div id="app" class="main">

    <vs-navbar
        class="vs-navbar"
        v-model="indexActive"
        type="fund"
        color="#1F74FF"
        text-color="rgba(255,255,255,.6)"
        active-text-color="#FFFFFF">
      <div slot="title">
        <router-link @click.native="refreshCachedItems" :to="{path: '/home'}">
          <vs-navbar-title style="color: white" id="navbar-title">
            <img :src="`${publicPath}refood-text-white.png`" alt="ReFood" id="navbar-logo"/>
          </vs-navbar-title>
        </router-link>

      </div>

      <!-- Not Logged In -->
      <div v-if="getLoggedInUser() == null" class="navbar-group">
        <vs-navbar-item index="0-0">
          <router-link to="/register">Register</router-link>
        </vs-navbar-item>
        <vs-navbar-item index="0-1">
          <router-link to="/">Login</router-link>
        </vs-navbar-item>
      </div>

      <!-- Logged In -->
      <div v-else class="navbar-group">
        <vs-navbar-item index="1-0">
          <router-link @click.native="refreshCachedItems" :to="{path: '/home'}">Home</router-link>
        </vs-navbar-item>
        <vs-navbar-item index="1-2">
          <router-link to="/search">Search</router-link>
        </vs-navbar-item>
        <!-- Acting As User -->
        <div v-if="getActingAsUserId() == null" class="sub-navbar-group">
          <vs-navbar-item index="1-1">
            <router-link @click.native="refreshCachedItems" :to="{path: '/search-listings'}">Browse Listings</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="2-0">
            <router-link @click.native="refreshCachedItems" to="/marketplace">Marketplace</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="2-1">
            <router-link @click.native="refreshCachedItems" to="/businesses">Register a Business</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="2-2" @click="refreshCachedItems">
            <router-link :to="{path: `/users/${getLoggedInUser()}`}">Profile</router-link>
          </vs-navbar-item>
        </div>

        <!-- Acting As Business -->
        <div v-else class="sub-navbar-group">
          <vs-navbar-item index="3-0">
            <router-link @click.native="refreshCachedItems" :to="{path: `/businesses/${getActingAsBusinessId()}`}">Business Profile</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="3-1">
            <router-link @click.native="refreshCachedItems" :to="{path: `/businesses/${getActingAsBusinessId()}/products`}">Product Catalogue</router-link>
          </vs-navbar-item>
          <vs-navbar-item index="3-2">
            <router-link @click.native="refreshCachedItems" :to="{path: `/businesses/${getActingAsBusinessId()}/inventory`}">Inventory</router-link>
          </vs-navbar-item>
        </div>
        <div class="userDetail" v-if="getLoggedInUser() != null">
          <ActingAs/>
        </div>


        <div id="logout-nav" @click="logoutUser()">
          <vs-navbar-item index="8">
            <router-link :to="{path: '/'}">
              <span>Logout</span>
            </router-link>
          </vs-navbar-item>
        </div>
      </div>

    </vs-navbar>

    <div id="view">
      <transition
          name="fade"
          mode="out-in"
          :duration="100"
          appear>
        <router-view></router-view>
      </transition>
    </div>

    <footer class="info">
      <h4>REFOOD 2021</h4>
    </footer>

  </div>
</template>
<script>
import Register from "./components/Register";
import ActingAs from "./components/ActingAs";
import Login from "./components/Login";
import ProductCatalogue from "./components/ProductCatalogue";
import BusinessRegister from "./components/BusinessRegister";
import AddToCatalogue from "./components/AddToCatalogue";
import {store, mutations} from "./store";
import api from "./Api";
import 'vuesax';
import 'vuesax/dist/vuesax.css';
// Vue app instance
// it is declared as a reusable component in this case.
// For global instance https://vuejs.org/v2/guide/instance.html
// For comparison: https://stackoverflow.com/questions/48727863/vue-export-default-vs-new-vue
const app = {
  name: "app",
  components: {
    // list your components here to register them (located under 'components' folder)
    // https://vuejs.org/v2/guide/components-registration.html
    Login, Register, BusinessRegister, ActingAs, AddToCatalogue, ProductCatalogue
  },
  // app initial state
  // https://vuejs.org/v2/guide/instance.html#Data-and-Methods
  data: () => {
    return {
      indexActive: 0,

      publicPath: process.env.BASE_URL
    };
  },
  methods: {
    /**
     * Method used to get the current logged in user to use inside template
     * @returns {int} userId of current logged in user
     */
    getLoggedInUser() {
      return store.loggedInUserId;
    },

    getActingAsUserId(){
      return store.actingAsBusinessId;
    },

    getActingAsBusinessId() {
      return store.actingAsBusinessId;
    },

    getPrimaryBusinesses(){
      return store.userBusinesses;
    },
    /**
     * Calls the logout function which removes loggedInUserId
     */
    logoutUser() {
      this.refreshCachedItems();
      api.logout()
          .then(() => {
            mutations.userLogout();
            this.$router.push({path: '/login'})
          })
    },
    refreshCachedItems() {
      if (sessionStorage.getItem('businessesCache') !== null) {
        sessionStorage.removeItem("businessesCache");
      }
      if (sessionStorage.getItem('listingSearchCache') !== null) {
        sessionStorage.removeItem("listingSearchCache");
      }

      if (sessionStorage.getItem("previousListing") !== null) {
        sessionStorage.removeItem("previousListing");
      }
    }
  },

  beforeMount() {
    //Set title to ReFood, shows up in tab display
    document.title = "ReFood"
    api.checkSession()
        .then((response) => {
          if(response.data.id != null){
            api.checkBusinessSession()
                .then((busResponse) => {
                  if(busResponse.status == 200){
                    mutations.setActingAsBusiness(busResponse.data.id, busResponse.data.name);
                  } else {
                    mutations.setActingAsUser();
                  }
                });
          }
          mutations.setUserLoggedIn(response.data.id, response.data.role);
          mutations.setUserBusinesses(response.data.businessesAdministered);
          mutations.setUserName(response.data.firstName + " " + response.data.lastName);
        }).catch((err) => {
      this.$log.debug(err);
    });
  },
};

// make the 'app' available
export default app;
</script>

<style scoped>

[v-cloak] {
  display: none;
}

.vs-navbar {
  padding: 5px;
  color: rgb(255,255,255);
  box-shadow: 0 1px 10px #999;
}

.userDetail {
  display: flex;
  margin-left: 2px;
  cursor: pointer;
}

.userDetail:hover {
  background: #E0E0E0;
}

.navbar-group {
  display: flex;
  flex-direction: row;
  margin: auto;
}

.sub-navbar-group {
  display: flex;
  flex-direction: row;
  margin: auto;
}

.navbar-group >>> li, #logout-nav  {
  margin: auto; /* Fixes tab height issue */
}

#navbar-logo {
  height: 30px;
  margin-top: 4px;
}

@media screen and (max-width: 800px) {
  .navbar-group {
    display: flex;
    flex-direction: column;
  }

  .sub-navbar-group {
    display: flex;
    flex-direction: column;
    margin: 0;
  }

  #logout-nav {
    margin: 0;
  }

}


</style>
