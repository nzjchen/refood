<template>

  <div>
    <div v-if="isThumbnail" class="businessImage" style="height: 25px; width: 33.33px; display: flex; overflow: hidden; justify-content: center; position: relative; border-radius: 50%">
      <img v-if="this.imagePath != null && isDevelopment()" v-bind:src="require('../../../backend/src/main/resources/media/images/business_images/' + getImgUrl(this.imagePath))" alt="User thumbnail"/>
      <img v-else-if="this.imagePath != null && !isDevelopment()" v-bind:src="getImgUrl(this.imagePath)" alt="Business thumbnail"/>
      <img v-else-if="!this.imagePath && isDevelopment()" src="placeholder.png" alt="Business thumbnail"/>
      <img v-else-if="!isDevelopment() && !this.imagePath" :src="getImgUrl(true)" alt="Business thumbnail"/>
    </div>
    <div v-else-if="isUserThumbnail" class="businessImage" style="height: 25px; width: 33.33px; display: flex; overflow: hidden; justify-content: center; position: relative; border-radius: 50%">
      <img v-if="this.imagePath != null && isDevelopment()" v-bind:src="require('../../../backend/src/main/resources/media/images/users/' + getImgUrl(this.imagePath))" alt="User thumbnail"/>
      <img v-else-if="this.imagePath != null && !isDevelopment()" v-bind:src="getImgUrl(this.imagePath)" alt="User thumbnail"/>
      <img v-else-if="!this.imagePath && isDevelopment()" src="placeholder.png" alt="User thumbnail"/>
      <img v-else-if="!isDevelopment() && !this.imagePath" :src="getImgUrl(true)" alt="User thumbnail"/>
    </div>
    <div v-else-if="isBusiness" class="businessImage" style="display: flex; overflow: hidden; justify-content: center; position: relative">
      <img v-if="this.imagePath != null && isDevelopment()" v-bind:src="require('../../../backend/src/main/resources/media/images/business_images/' + getImgUrl(this.imagePath))" alt="Business image"/>
      <img v-else-if="this.imagePath != null && !isDevelopment()" alt="Business Image" v-bind:src="getImgUrl(this.imagePath)"/>
      <img v-else-if="!this.imagePath && isDevelopment()" src="placeholder.png" alt="Business image"/>
      <img v-else-if="!isDevelopment() && !this.imagePath" :src="getImgUrl(true)" alt="Business image"/>
      <vs-dropdown class="edit-button" vs-trigger-click>
        <vs-icon icon="edit" color="black" size="50px" name="avatar"></vs-icon>
        <vs-dropdown-menu id="dropdown">
          <vs-dropdown-item v-if="imagePath !== primaryImagePath" @click="emitUpdatePrimary" class="profileDropdown">
            <vs-icon icon="collections" size="30px" style="margin: auto; grid-column: 0"></vs-icon>
            <div style="font-size: 16px; margin: auto; grid-column: 1">Make Image Primary</div>
          </vs-dropdown-item>
          <vs-dropdown-item @click="emitDelete" class="profileDropdown">
            <vs-icon icon="delete" size="30px" style="margin: auto"></vs-icon>
            <div style="font-size: 16px; margin: auto">Delete Image</div>
        </vs-dropdown-item>
        </vs-dropdown-menu>
      </vs-dropdown>
    </div>

    <div v-else-if="isUser" class="businessImage" style="display: flex; overflow: hidden; justify-content: center; position: relative">
      <img v-if="this.imagePath != null && isDevelopment()" v-bind:src="require('../../../backend/src/main/resources/media/images/users/' + getImgUrl(this.imagePath))" alt="User image"/>
      <img v-else-if="this.imagePath != null && !isDevelopment()" alt="User Image" v-bind:src="getImgUrl(this.imagePath)"/>
      <img v-else-if="!this.imagePath && isDevelopment()" src="placeholder.png" alt="Business image"/>
      <img v-else-if="!isDevelopment() && !this.imagePath" :src="getImgUrl(true)" alt="Business image"/>
      <vs-dropdown class="edit-button" vs-trigger-click>
        <vs-icon icon="edit" color="black" size="50px" name="avatar"></vs-icon>
        <vs-dropdown-menu id="dropdown">
          <vs-dropdown-item v-if="imagePath !== primaryImagePath" @click="emitUpdatePrimary" class="profileDropdown">
            <vs-icon icon="collections" size="30px" style="margin: auto; grid-column: 0"></vs-icon>
            <div style="font-size: 16px; margin: auto; grid-column: 1">Make Image Primary</div>
          </vs-dropdown-item>
          <vs-dropdown-item @click="emitDelete" class="profileDropdown">
            <vs-icon icon="delete" size="30px" style="margin: auto"></vs-icon>
            <div style="font-size: 16px; margin: auto">Delete Image</div>
        </vs-dropdown-item>
        </vs-dropdown-menu>
      </vs-dropdown>
    </div>

    <div v-else>
      <img v-if="this.imagePath != null && isDevelopment()" v-bind:src="require('../../../backend/src/main/resources/media/images/businesses/' + getImgUrl(this.imagePath))" alt="Product image"/>
      <img v-else-if="this.imagePath != null && !isDevelopment()" alt="Product Image" v-bind:src="getImgUrl(this.imagePath)"/>
      <img v-else-if="!this.imagePath && isDevelopment()" src="placeholder.png" alt="Product image"/>
      <img v-else-if="!isDevelopment() && !this.imagePath" :src="getImgUrl(true)" alt="Product image"/>
    </div>
  </div>
</template>

<script>
export default {
  name: "ReImage",

  props: {
    imagePath: {
      type: String,
      default: null,
    },
    isBusiness: {
      type: Boolean,
      default: false,
    },
    isUser: {
      type: Boolean,
      default: false
    },
    isUserThumbnail: {
      type: Boolean,
      default: false
    },
    primaryImagePath: {
      type: String,
      default: null
    },
    isThumbnail: {
      type: Boolean,
      default: false
    }
  },


  methods: {
    
    /**
     * Return the string of the beginning of the image path.
     * This differs depending on whether the application is running
     * on the test or the production server, and whether the image
     * is a business image, user image, or product image.
     */
    getPathStart() {
      let pathStart;
      if (this.isUser || this.isUserThumbnail) {
        if (process.env.NODE_ENV === 'staging') {
          pathStart = '/test/user_images/';
        } else {
          pathStart = '/prod/user_images/';
        }
      } else if (this.isBusiness || this.isThumbnail) {
        if (process.env.NODE_ENV === 'staging') {
          pathStart = '/test/business_images/';
        } else {
          pathStart = '/prod/business_images/';
        }
      } else {
        if (process.env.NODE_ENV === 'staging') {
          pathStart = '/test/prod_images/';
        } else {
          pathStart = '/prod/prod_images/';
        }
      }
      return pathStart;
    },

    /**
     * Retrieves the image url link for the given product.
     * @param product the product to retrieve the image for.
     * @return a string link to the product image, or the default image if it doesn't have a product.
     **/
    getImgUrl(product) {

      if (product === true && process.env.NODE_ENV !== 'staging') {
        return '/prod/placeholder.png';
      } else if (product === true) {
        return '/test/placeholder.png';
      } else if (this.imagePath != null && !this.isDevelopment() && process.env.NODE_ENV !== 'staging') {
        return this.getPathStart() + this.imagePath.replace("/\\/g", "/");
      } else if (this.imagePath != null && !this.isDevelopment()) {
        return this.getPathStart() + this.imagePath.replace("/\\/g", "/");
      } else if (this.imagePath != null) {
        return this.imagePath.replace(/\\/g, "/").replace("./src/main/resources/media/images/businesses/", "");
      } else {
        return '../../public/placeholder.png';
      }
    },

    /**
     * Checks if the current web environment is in development mode.
     */
    isDevelopment() {
      return (process.env.NODE_ENV === 'development');
    },

    /**
     * Emit delete to parent component, so that the image is deleted
     */
    emitDelete: function() {
      this.$emit('delete');
    },

    /**
     * Emit to parent component updatePrimary, so that the image is set as the primary image.
     */
    emitUpdatePrimary: function() {
      this.$emit("updatePrimary");
    }
  },
}
</script>
<style scoped>

  .profileDropdown >>> .vs-dropdown--item-link {
    display: flex;
    transition: .1s ease;
  }

  .businessImage:hover .edit-button {
    opacity: .8;
  }

  .edit-button {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100%;
    width: 100%;
    opacity: 0;
    transition: .9s ease;
    background-color: #008CBA;
  }

  #dropdown {
    display: grid;
    grid-template-columns: auto auto;
  }
</style>
