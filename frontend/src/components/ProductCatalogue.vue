<template>
  <vs-card class="main">
      <div class="profile-text-inner">
        <div id="header-container">
          <div id="page-title">Product Catalogue</div>
          <div id="header-menu">
           <div style="display: flex;">
              <vs-tooltip text="Grid View">
                <vs-button icon="grid_view" type="border" @click="displaytype = true" style="border: none; padding: 12px;"></vs-button>
              </vs-tooltip>
              <vs-tooltip text="List View">
                <vs-button icon="view_list" type="border" @click="displaytype = false" style="border: none;"></vs-button>
              </vs-tooltip>
            </div>
          </div>
        </div>

        <vs-divider style="padding: 4px;"></vs-divider>

        <div id="catalogue-options">
          <vs-button class="header-button" :to="{path: `/addtocatalogue`}">Add Product</vs-button>
          <vs-button @click="$router.push(`/businesses/${$route.params.id}/inventory`)" class="header-button" style="margin-right: 8px;">Inventory</vs-button>

          <div id="sort-container">
            <div v-show="displaytype" style="display: flex;">
              <h2 style="margin: auto; padding-right: 4px;">Sort By </h2>
              <select v-model="selected">
                <option disabled value="">Please select one</option>
                <option value="id">ID</option>
                <option value="name">Product Name</option>
                <option value="description">Description</option>
                <option value="recommendedRetailPrice">Recommended Retail Price</option>
                <option value="created">Date Created</option>
              </select>
              <vs-button @click="sortByName(null, selected, 0);" style="margin: 0 2em 0 0.5em; width: 100px">Sort</vs-button>
            </div>
          </div>

          <!-- If search query returns more than 10 products then this should be active -->
          <div class="grid-pagination">
            <div class="displaying">
              Displaying {{ searchRange[0] }}-{{ searchRange[1] }} of {{ filteredProducts.length }}
            </div>
            <div v-if="filteredProducts.length > 10" style="display: flex;">
              <div v-if="filteredProducts.length > productsPerPage" style="display: flex;">
                <vs-pagination v-model="currentPage" :total="Math.round(products.length/productsPerPage +0.4)"/>
              </div>
            </div>
          </div>
        </div>

        <vs-divider style="padding: 4px;"></vs-divider>

        <div v-if="displaytype">
          <div class="grid-container" style="margin: auto">
            <vs-card class="grid-item"
                    v-for="product in filteredProducts.slice(productsPerPage*(currentPage-1),currentPage*productsPerPage)"
                    v-bind:href="product.id"
                    :key="product.id">

              <div slot="media">
                <ReImage :imagePath="product.primaryImagePath" class="grid-image"/>
              </div>

              <div style="font-size: 13pt; height:100%; line-height: 1.5; display:flex; flex-direction: column;">
                <div style="display: flex; flex-direction: column; justify-content: space-between">
                  <div style="font-size: 16px; font-weight: bold;  text-align: justify; word-break: break-all;">{{ product.name }} </div>
                  <p>{{ product.id }}</p>
                </div>
                <vs-divider></vs-divider>
                <div style="font-size: 16px; font-weight: bold; height: 24px;">{{ product.manufacturer }} </div>
                <p style="font-size: 14px; margin-bottom: 8px;">Created: {{ product.created }} </p>
                <div style="height: 75px; font-size: 14px; overflow-y: auto; ">{{ product.description }} </div>
              </div>

              <div slot="footer" class="grid-item-footer">
                <div style="font-size: 25pt; font-weight: bold; margin: auto 0" >{{currencySymbol + " " +  product.recommendedRetailPrice }} </div>
                <vs-dropdown vs-trigger-click class="actionButton">
                  <vs-button style="width: fit-content;" type="flat" icon="settings"></vs-button>
                  <vs-dropdown-menu>
                    <vs-dropdown-item @click="goToModify(product.id);">
                      Modify product
                    </vs-dropdown-item>
                    <vs-dropdown-item @click="openImageUpload(product)">
                      Add Image
                    </vs-dropdown-item>
                    <vs-dropdown-group vs-label="Change Primary Image" vs-collapse>
                      <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="setPrimaryImage(product, pImage);">
                        {{pImage.name}}
                      </vs-dropdown-item>
                    </vs-dropdown-group>

                    <vs-dropdown-group vs-label="Delete An Image" vs-collapse>
                      <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="deleteImage(product, pImage);">
                        {{pImage.name}}
                      </vs-dropdown-item>
                    </vs-dropdown-group>
                    <vs-dropdown-item divider @click="openAddNewInventoryModal(product, currencySymbol)">
                      Add Inventory Entry
                    </vs-dropdown-item>
                  </vs-dropdown-menu>
                </vs-dropdown>
              </div>
            </vs-card>
          </div>
          <div class="grid-pagination" style="justify-content: flex-end">
            <div class="displaying" style="margin: auto 1em auto auto;">
              Displaying {{ searchRange[0] }}-{{ searchRange[1] }} of {{ filteredProducts.length }}
            </div>
            <div v-if="filteredProducts.length > 10" style="display: flex;">
              <div v-if="filteredProducts.length > productsPerPage" style="display: flex;">
                <vs-pagination v-model="currentPage" :total="Math.round(products.length/productsPerPage +0.4)"/>
              </div>
            </div>
          </div>
        </div>



        <div v-if="!displaytype">
            <!-- Separate search within results search bar. Rather than calling the database, this filters the table
            entries within the page by matching the search field to the product's firstname, middlename or lastname -->
            <!-- When each heading is clicked, the sortByName() function is called, passing the json field name and a reference to the toggle array -->

            <vs-table :data="filteredProducts.slice(productsPerPage*(currentPage-1),currentPage*productsPerPage)" style="border-spacing: 0 20px; margin: 1em" stripe>
                <template slot="thead" style="background:blue">
                  <vs-th sort-key="id" style="border-radius: 4px 0 0 0;">
                      <div>ID</div>
                  </vs-th >
                  <vs-th sort-key="name" style="min-width: 100px">
                     <div>Product Name</div>
                  </vs-th>
                  <vs-th sort-key="description">
                    <div>Description</div>
                  </vs-th>
                  <vs-th sort-key="manufacturer">
                    <div>Manufacturer</div>
                  </vs-th>
                  <vs-th sort-key="recommendedRetailPrice">
                    <div>Recommended Retail Price</div>
                  </vs-th>
                  <vs-th sort-key="created">
                    <div>Date Created</div>
                  </vs-th>
                  <vs-th style="border-radius: 0 4px 0 0;"><!-- Actions Column --></vs-th>
                </template>

                <template slot-scope="{data}">
                  <vs-tr :key="product.id" v-for="product in data">
                    <vs-td style="width: 20px; padding-right: 10px">
                      <a style="color: rgb(0,0,238);">{{ product.id }}</a>
                      <div>
                        <ReImage :imagePath="product.primaryImagePath" class="table-image"/>
                      </div>
                    </vs-td>
                    <vs-td>{{ product.name }} </vs-td>
                    <vs-td>{{ product.description }} </vs-td>
                    <vs-td>{{ product.manufacturer }} </vs-td>
                    <vs-td style="text-align: center">{{currencySymbol + " " + product.recommendedRetailPrice }} </vs-td>
                    <td>{{ product.created }} </td>
                    <td>
                      <!-- Effectively repeated above, should refactor at some point. -->
                      <vs-dropdown vs-trigger-click>
                        <vs-button>Actions</vs-button>
                        <vs-dropdown-menu>
                          <vs-dropdown-item id="modify-dropdown" @click="goToModify(product.id);">
                            Modify product
                          </vs-dropdown-item>
                          <vs-dropdown-item @click="openImageUpload(product)">
                            Add Image
                          </vs-dropdown-item>
                          <vs-dropdown-group vs-label="Change Primary Image" vs-collapse>
                              <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="setPrimaryImage(product, pImage);">
                                {{pImage.name}}
                              </vs-dropdown-item>
                          </vs-dropdown-group>
                          <vs-dropdown-group vs-label="Delete An Image" vs-collapse>
                              <vs-dropdown-item v-for="pImage in product.images" :key="pImage" @click="deleteImage(product, pImage);">
                                {{pImage.name}}
                              </vs-dropdown-item>
                          </vs-dropdown-group>
                          <vs-dropdown-item divider @click="openAddNewInventoryModal(product, currencySymbol)">
                            Add Inventory Entry
                          </vs-dropdown-item>
                        </vs-dropdown-menu>
                      </vs-dropdown>
                    </td>
                  </vs-tr>

                  <!-- If search query returns more than 10 products then this should be active -->
                  <tfoot v-if="filteredProducts.length > 1">
                  <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td class="displaying">Displaying {{ searchRange[0] }}-{{ searchRange[1] }} of
                      {{ filteredProducts.length }}</td>
                    <div class="title-centre">
                      <vs-pagination v-model="currentPage" :total="Math.round(products.length/productsPerPage +0.4)"/>
                    </div>
                  </tr>
                  </tfoot>
                </template>
            </vs-table>
        </div>
      </div>
    <input type="file" id="fileUpload" ref="fileUpload" style="display: none;" multiple @change="uploadImage($event)"/>
    <AddToInventory ref="addToInventoryModal" />
  </vs-card>
</template>

<script>
import api from "../Api";
import {store} from "../store";
import axios from "axios";
import AddToInventory from "./AddToInventory";
import ReImage from "./ReImage";

const ProductCatalogue = {
  name: "ProductCatalogue",
  components: {AddToInventory, ReImage},
  data: function() {
    return {
      products: [],
      business: null,
      businessId: null,
      currentPage: 1,
      productsPerPage: 12,

      errors: [],
      toggle: [1,1,1,1,1],
      filteredProducts: [],
      enableTable: false,
      productSearchIndexMin: 0,
      productSearchIndexMax: 12,
      displaytype: true,
      currencySymbol: "",
      selected: "",

      selectedProduct: null, // Used to select product to upload image to.
    };
  },

  /**
   * api.getBusinessProducts() queries the test back-end (json-server)
   * at /businesses/${businessId}/products which returns a JSON object list of test products which can
   * be filtered by the webpage.
 */
  mounted() {
    api.checkSession()
      .then((response) => {
        this.businessId = this.$route.params.id;
        this.userId = response.data.id;
        this.getUserInfo(this.userId);
        this.business = this.getBusinessName();

        api.getBusinessProducts(this.businessId)
            .then((innerResponse) => {
              this.products = innerResponse.data;
              this.filteredProducts = innerResponse.data;
            })
            .catch((error) => {
              this.$log.debug(error);
              this.error = "Failed to load products";
            })
            .finally(() => (this.loading = false));
      }).catch((error) => {
        this.$log.error(error);
    });
  },

  methods: {

    /**
     * Opens the add new inventory modal by calling the open function inside the component.
     * @param product the select product to add a new inventory entry for.
     * @param currency the currently viewable currency symbol being used.
     */
    openAddNewInventoryModal(product, currency) {
      this.$refs.addToInventoryModal.open(product, currency);
    },

    setPrimaryImage(product, image) {
      this.imageId = image.id;
      this.productId = product.id;
      api.setPrimaryImage(this.businessId, this.productId, this.imageId)
          .then(() => {
            location.reload();
          }).catch((err) => {
            throw new Error(`Error trying to get user info from id: ${err}`);
      });
      this.$vs.notify({title:"Product image updated.", text:"New primary image successfully set.", color:"success"});
    },

    deleteImage(product, image) {
      this.imageId = image.id;
      this.productId = product.id;
      api.deletePrimaryImage(this.businessId, this.productId, this.imageId)
          .then(() => {
            location.reload();
            this.$vs.notify({title:"Product image deleted.", text:"Image successfully deleted.", color:"success"});
          }).catch((err) => {
            console.log(err);
      });
    },

    getUserInfo: function(userId) {
      if(store.loggedInUserId != null) {
        api.getUserFromID(userId) //Get user data
            .then((response) => {
              this.user = response.data;
              this.setCurrency(this.user.homeAddress.country);
            }).catch((err) => {
          throw new Error(`Error trying to get user info from id: ${err}`);
        });
      } else {
        this.$router.push({path: "/login"}); //If user not logged in send to login page
      }
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

    getBusinessID: function () {
      return store.actingAsBusinessId
    },

    getBusinessName: function () {
      return store.actingAsBusinessName
    },


    /**
     * Redirects page to the 'modify product' page with the given product ID.
     */
    goToModify: function(productId) {
      this.$router.push({path: `/businesses/${store.actingAsBusinessId}/products/${productId}/modify`});
    },

    /**
     * makes the checkproduct an administrator
     * if they are already, revoke privledges...
     */
    toggleAdmin: function (currentproduct) {
      if (currentproduct.role === 'product') {
        //currentproduct.id = true;
        api.makeproductAdmin(currentproduct.id);
        currentproduct.role = 'GAA'
      } else if (currentproduct.role === 'GAA') {
        api.revokeproductAdmin(currentproduct.id);
        currentproduct.role = 'product'
      }
    },

    /**
     * Filters the displayed products alphabetically by
     * @param event
     * @param JSONField is the name of the field to sort by (as string)
     */
    sortByName: function (event, JSONField) {

      const indexarray = ["id", "name", "description", "recommendedRetailPrice", "created"];

      //toggles the classlist (arrow up or down) in the child DOM element: <i/>
      if(event) {
        if(event.target.firstElementChild) {
          event.target.firstElementChild.classList.toggle('fa-angle-double-down');
          event.target.firstElementChild.classList.toggle('fa-angle-double-up');
        }
      }

      if (this.filteredProducts) {
        this.filteredProducts.sort(function(a, b) {
          var aField = a[JSONField];
          var bField = b[JSONField];

          //first check if null
          if(aField == null) {
            return 1;
          }
          if(bField == null) {
            return -1;
          }

          //check if a or b contains any numbers or whitespace
          //before capitalzation to avoid errors
          //also check if boolean
          if (!(typeof aField === "boolean" || typeof bField === "boolean")) {
            if ( !(/[^a-zA-Z]/.test(aField) && (/[^a-zA-Z]/.test(bField))) ) {
              aField = aField.toUpperCase();
              bField = bField.toUpperCase();
            }
          }


          //first < second
          if (aField > bField ) {
            return 1;
          }
          //second < first
          if (aField< bField) {
            return -1;
          }
          // a must be equal to b
          return 0;
        });

        let index = indexarray.indexOf(JSONField);

        if (index >= 0) {
          if (this.toggle[index]) {
            this.filteredProducts.reverse();
            this.toggle[index]=0;
          } else {
            this.toggle[index]=1;
          }
        }
      }
    },

    /**
     * Helper function to control the number of search range values.
     * It increments Minimum and Maximum search index by 10 as long as the maximum search index does not exceed
     * the number of filtered products.
     */
    increaseSearchRange: function () {
      //Stop value from going over range
      if(this.productSearchIndexMax < this.filteredProducts.length) {
        //console.log(this.productSearchIndexMax, this.filteredproducts.length, this.productSearchIndexMin)
        let minMaxDiff = this.productSearchIndexMax - this.productSearchIndexMin;
        this.productSearchIndexMin += minMaxDiff;
        this.productSearchIndexMax += minMaxDiff;
      }
    },

    /**
     * Helper function to control the number of search range values.
     * It decrements Minimum and Maximum search index by 10 as long as the minimum is at least 10.
     */
    decreaseSearchRange: function () {
      //Stop value from reaching negative
      if(this.productSearchIndexMin >= 1) {
        let minMaxDiff = this.productSearchIndexMax - this.productSearchIndexMin;
        this.productSearchIndexMin -= minMaxDiff;
        this.productSearchIndexMax -= minMaxDiff;
      }
    },

    /**
     * Trigger the file upload box to appear.
     * Used for when the actions dropdown add image action is clicked.
     */
    openImageUpload: function(product) {
      this.selectedProduct = product;
      this.$refs.fileUpload.click();
    },

    /**
     * Upload product image when image is uploaded on web page
     * @param e Event object which contains file uploaded
     */
    uploadImage: function(e) {
      //Setup FormData object to send in request
      this.$vs.loading(); //Loading spinning circle while image is uploading (can remove if not wanted)
      for (let image of e.target.files) {
        const fd = new FormData();
        fd.append('filename', image, image.name);
        api.postProductImage(this.businessId, this.selectedProduct.id, fd)
          .then(() => { //On success
            this.$vs.notify({title:`Image for ${this.selectedProduct.id} was uploaded`, color:'success'});
            location.reload();
          })
          .catch((error) => { //On fail
            if (error.response.status === 400) {
              this.$vs.notify({title:`Failed To Upload Image`, text: "The supplied file is not a valid image.", color:'danger'});
            } else if (error.response.status === 500) {
              this.$vs.notify({title:`Failed To Upload Image`, text: 'There was a problem with the server.', color:'danger'});
            } else if (error.response.status === 413) {
              this.$vs.notify({title:`Failed To Upload Image`, text: 'The image is too large.', color:'danger'});
            }
          })
          .finally(() => {
            this.$vs.loading.close();
        });
      }
    }

  },

  computed: {
    /**
     * Computes ranges to be displayed below table, max of range is switched
     * to length of product query if the length is less than the current range
     * @returns {Array} Array with start index and end index for searchRange
     */
    searchRange: function () {
      let max = this.productSearchIndexMax;

      if (max > this.filteredProducts.length) {
        max = this.filteredProducts.length
      }

      return [this.productSearchIndexMin + 1, max]
    }
  }
}

export default ProductCatalogue;
</script>

<style scoped>

#page-title {
  font-size: 30px;
  margin: auto 0;
}

#header-container {
  display: flex;
  justify-content: space-between;
}

#header-menu {
  display: flex;
}

#sort-container {
  display: flex;
}

.switch-container {
  display: flex;
  margin-right: 2em;
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

.grid-item >>> footer {
  padding-bottom: 1em;
  margin-bottom: 4px;
}

/* ===== ===== ===== */

#catalogue-options {
  display: flex;
  margin-bottom: 1em;
}

.grid-pagination {
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

@media screen and (max-width: 900px) {
  #catalogue-options {
    flex-direction: column;
  }

  .grid-pagination {
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
