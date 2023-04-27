<template>
  <div id="notifications">
    <div>
      <vs-dropdown id="dropdownButton" vs-trigger-click>
        <div id="notificationContainer">
          <vs-avatar icon="notifications" size="30" name="avatar" />
          <span id="dropdownButtonName">{{ notifications.length }}</span>
        </div>
        <vs-dropdown-menu>

          <vs-dropdown-group vs-label="Notifications" id="cardList" v-if="notifications.length > 0">
            <div class="dropdown-item" v-for="notification in notifications" :key="notification.cardId">
                <div class="dropdown-item-name">

                    <!-- If card is expired -->
                    <div class="cardContainer" v-if="notification.status === expired">
                        <div id="text">
                        {{notification.title}} has expired
                        </div>
                        <div id="buttons">
                            <vs-button class="notificationButtons" @click="extendCard(notification.cardId, notification.title)">Extend</vs-button>
                            <vs-button class="notificationButtons" @click="deleteCard(notification.cardId, notification.title)">Delete card</vs-button>
                        </div>
                    </div>

                    <!-- If card is deleted -->
                    <div class="cardContainer" v-else-if="notification.status === deleted">
                        {{notification.title}} has been removed
                    </div>

                  <!-- If listing has been purchased -->
                  <div class="cardContainer" v-else-if="validPurchaseNotification(notification) && validBoughtListingNotification(notification)" >
                    <p>
                      {{notification.boughtListing.product.name}} has been purchased
                      <br>
                      {{dateConverter(notification.boughtListing.sold)}}
                    </p>
                  </div>

                  <div class="cardContainer" v-else-if="!validPurchaseNotification(notification) && validBoughtListingNotification(notification)" >
                    <p>
                      You have purchased {{notification.boughtListing.product.name}}
                      <br>
                      {{dateConverter(notification.boughtListing.sold)}}
                    </p>
                  </div>


                </div>
            </div>
          </vs-dropdown-group>
          <!-- If no notifications -->
          <vs-dropdown-group id="noCards" vs-label="No notifications" v-else />

        </vs-dropdown-menu>
      </vs-dropdown>
    </div>
  </div>
</template>

<script>
import api from '../Api';

import { mutations, store } from '../store';

export default {

    data: function() {
        return {
          bought: 'Bought',
          expired: 'Expired',
          deleted: 'Deleted',
          userId: store.loggedInUserId
        };
    },

    methods: {
      /**
       * Check if the listing has been bought by the user
       * @param notification A user's notification
       */
      validPurchaseNotification(notification) {
        if (notification.status === this.bought && this.userId !== notification.boughtListing.buyer) {
          return true;
        }
        return false;
      },
      /**
       * Check if the notification is from an item being bought
       * @param notification A user's notification
       */
      validBoughtListingNotification(notification) {
        if (notification.boughtListing !== null) {
          return true;
        }
        return false;
      },


      /**
       * Converts a regular date to day month year
       */
      dateConverter (dateStr) {
        let simpleDate = new Date(dateStr);
        return simpleDate.getDay() + "-" + simpleDate.getMonth() + "-" + simpleDate.getFullYear();
      },

      /**
       * Calls api method to extend card display period
       * @param cardId card that is going to extended
       * @param title card title for notification
       */
      extendCard(cardId, title) {
        api.extendCardDisplayPeriod(cardId)
        .then(() => {
          this.getNotifications();
          this.$vs.notify({title:'Card Extended', text:`Card ${title} was extended`, color:'success'});
        }).catch(() => {
          this.$vs.notify({title:'Error', text:`Card ${title} could not be extended due to an error`, color:'danger'});
        })
      },

      /**
       * Calls api method to delete card
       * @param cardId card that is going to deleted
       * @param title card title for notification
       */
      deleteCard(cardId, title) {
        api.deleteCard(cardId)
        .then(() => {
          this.getNotifications();
          this.$vs.notify({title:'Card Deleted', text:`Card ${title} was deleted`, color:'success'});
        }).catch(() => {
          this.$vs.notify({title:'Error', text:`Card ${title} could not be deleted due to an error`, color:'danger'});
        })
      },

      /**
       * Calls api method to get notifications
       */
      getNotifications() {
        api.getNotifications(store.loggedInUserId)
        .then((response) => {
            mutations.setNotifications(response.data);
        })
      },
    },

  computed: {
        notifications: {
            get() {

                return store.notifications
            },
            set(val) { //setter for testing purposes
                mutations.setNotifications(val);
            }
        }
    }
}
</script>

<style>

#dropdownButton {
  cursor: pointer;
}

#dropdownButton:hover {
  filter: brightness(75%);
}

.dropdown-item >>> a {
  display: flex;
  text-align: center;
}

.dropdown-item-name {
  min-width: 100px;
  font-size: 12px;
  margin: 5px auto auto;
}

#cardList {
  width: 450px;
  max-height: 350px;
  overflow-y: auto;
}

#text {
  max-width: 60%;
  word-wrap: break-word;
}

#dropdownButtonName {
  text-align: right;
  position: absolute;
  bottom: 0;
  left: 70%;
  font-size: 14px;

  border-radius: 4px;
  padding: 0 4px;
  color: #fff;
  background-color: rgba(var(--vs-danger),1);
}

#notificationContainer {
  display: flex;
  position: relative
  /* min-width: 150px; */
}

#notifications {
  margin-right: 5px;
}

.notificationButtons {
  margin-right: 5px;
}

.cardContainer {
  display: flex;
  justify-content: space-between;
  border-radius: 10px;
  padding: 0.25em;
}



</style>
