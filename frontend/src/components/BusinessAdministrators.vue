<template>
  <div id="administrators-list">
    <!-- Admin Card -->
    <div v-for="user in admins" :key="user.id" v-bind:user="user">
      <vs-chip class="admin-chip" color="primary" :closable="isPrimaryAdmin()" @click="removeUserAsAdmin(user)">
        <div @click="navigateToUser(user.id)" class="admin-label">
          <vs-tooltip text="Go to profile">
            <strong v-if="primaryAdminId === user.id"> {{user.firstName}} {{user.middleName}} {{user.lastName}} </strong>
            <div v-else> {{user.firstName}} {{user.middleName}} {{user.lastName}} </div>
          </vs-tooltip>
        </div>
      </vs-chip>
    </div>
  </div>
</template>

<script>
import api from "../Api";
import {store} from "../store"

const BusinessAdministrators = {
  name: "BusinessAdministrators",

  props: {
    admins: Array,
    pAdminId: Number,
  },

  data: function() {
    return {
      primaryAdminId: this.pAdminId,
    }
  },

  methods: {
    /**
     * Remove selected admin user from business.
     * @param user
     */
    removeUserAsAdmin: function (user) {
      api.removeUserAsBusinessAdmin(this.$route.params.id, user.id)
          .then(() => {
            this.admins = this.admins.filter((admin) => admin.id !== user.id);
            this.$vs.notify({
              title: `Successfully removed user`,
              text: `${user.firstName} was removed as an administrator.`,
              color: 'success'
            });
          })
          .catch((error) => {
            if (error.response.status === 400) {
              this.$vs.notify({
                title: `Failed to remove user`,
                text: `${user.firstName} is the primary administrator.`,
                color: 'danger'
              });
            }
            throw new Error(`ERROR trying to remove user as admin: ${error}`);
          });
    },

    /**
     * Navigate page to the user that was clicked on.
     */
    navigateToUser: function (id) {
      this.$router.push({path: `/users/${id}`})
    },

    /**
     * Checks if the current user is also the primary administrator of this business.
     * @returns {boolean} true if the user is the primary admin, else false.
     */
    isPrimaryAdmin: function () {
      return store.loggedInUserId === this.primaryAdminId;
    },
  },

}

export default BusinessAdministrators;
</script>

<style scoped>

#administrators-list {
  padding: 1em;
  display: flex;
  flex-wrap: wrap;
}

.admin-chip {
  height: 40px;
  font-size: 14px;
  margin: 0.5em 1em;
  border-radius: 4px;
}

.admin-label {
  cursor: pointer;
}

</style>