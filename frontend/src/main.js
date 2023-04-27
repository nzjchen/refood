/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Main entry point for your Vue app
 */

import Vue from 'vue';
import VueRouter from 'vue-router';
import App from './App.vue';
import VueLogger from 'vuejs-logger';
import Vuesax from 'vuesax';
import ModifyCatalog from "./components/ModifyCatalog"
import Login from "./components/Login";
import BusinessRegister from "./components/BusinessRegister";
import Register from "./components/Register";
import Users from "./components/Users.vue";
import Search from "./components/Search.vue";
import SearchListings from "./components/SearchListings.vue"
import Business from "./components/Business.vue";
import Homepage from "./components/Homepage"
import ProductCatalogue from "./components/ProductCatalogue";
import AddToCatalogue from "./components/AddToCatalogue";
import CommunityMarketplace from "./components/CommunityMarketplace";
import BusinessInventory from "./components/BusinessInventory";
import BusinessSalesHistory from "./components/BusinessSalesHistory";
import ListingDetail from "./components/ListingDetail";
import ModifyBusiness from "./components/ModifyBusiness";
import ModifyUser from "./components/ModifyUser";
import 'vuesax/dist/vuesax.css';
import 'material-icons/iconfont/material-icons.css'; // used with vuesax.
import { updateSessionOnRouterChange } from './utilities/UpdateSession';
import VueApexCharts from 'vue-apexcharts'
Vue.use(VueApexCharts)

Vue.component('apexchart', VueApexCharts)

Vue.config.productionTip = false;


const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);
Vue.use(VueRouter);
Vue.use(Vuesax);

const routes = [
  {path: '/home', component: Homepage},
  {path: '/', component: Login},
  {path: '/businesses', component: BusinessRegister},
  {name: 'LoginPage', path: '/', component: Login},
  {path: '/register', component: Register},
  {name: 'SearchListings', path: '/search-listings', component: SearchListings},
  {name: 'ModifyUser', path: '/user/:id/editprofile', component: ModifyUser},
  {name: 'UserPage', path: '/users/:id', component: Users},
  {name: 'AddToCatalogue', path: '/addtocatalogue', component: AddToCatalogue},
  {name: 'BusinessInventory', path: '/businesses/:id/inventory', component: BusinessInventory},
  {path: '/search', component: Search},
  {name: ModifyCatalog, path: '/businesses/:id/products/:productId/modify', component: ModifyCatalog},
  {path: '/businesses/:id/products', component: ProductCatalogue},
  {path: '/marketplace', component: CommunityMarketplace},
  {path: '/businesses/:id', name: 'Business', component: Business},
  {path: '/businesses/:id/sales-history', name: 'BusinessSalesHistory', component: BusinessSalesHistory},
  {path: '/businesses/:businessId/listings/:listingId', name: 'Listing', component: ListingDetail},
  {path: '/businesses/:id/modify', name: 'ModifyBusiness', component: ModifyBusiness},
  {
    path: '*',
    name: 'catchAll',
    component: Login
 }

];

const router = new VueRouter({
  routes,


});

updateSessionOnRouterChange(router);

export const bus = new Vue();

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  render: h => h(App)
});
