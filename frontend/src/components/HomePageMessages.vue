<template>
  <div id="body">
    <vs-popup title="Message" :active.sync="detailedView">
      <div v-if="currentMessage != null" id="message-detail-modal">
        <div class="message-detail-container">
          <vs-icon icon="face"></vs-icon>
          <div class="message-detail-header">
            {{users[currentMessage.sender.id || currentMessage.sender].firstName}} {{users[currentMessage.sender.id || currentMessage.sender].lastName}}
          </div>
        </div>

        <div class="message-detail-container">
          <vs-icon icon="store"></vs-icon>
          <div class="message-detail-header">
            {{currentMessage.card.title}}
          </div>
        </div>

        <div class="message-detail-container message">
        <vs-icon icon="question_answer" class="msg-icon"></vs-icon>
        <div id="message-detail-message">
          {{currentMessage.description}}
        </div>
        </div>

        <vs-divider></vs-divider>
        <div id="card-modal-bottom">
          <div class="message-detail-delivered">
            <vs-icon icon="send"></vs-icon>
            <div id="message-detail-sent">
              {{currentMessage.sent}}
            </div>
          </div>
        <vs-button id="reply-btn" class="card-modal-message-button" v-if="messaging===false" @click="messaging=true">Reply</vs-button>
        <vs-button class="card-modal-message-button"  @click="messaging=false; message = ''; errors=[];" v-else>Cancel</vs-button>
        </div>
        <transition name="slide" v-if="showTransition">
          <div id="card-modal-message" v-if="messaging">
            <vs-textarea style="margin-bottom: 50px" v-model="message" id="message-input"
                         :counter="250"
            />
            <div v-if="(errors.includes('bad-content'))" style="color: red">Message cannot be blank or too long</div>
            <div v-if="(errors.includes('invalid-card'))" style="color: red">There was something wrong with the card</div>
            <vs-button id="card-modal-message-send" @click="sendMessage(currentMessage, message)">Send Reply</vs-button>

          </div>
        </transition>
      </div>

    </vs-popup>
    <!-- === NEWSFEED ITEMS === -->
    <div v-for="item in feedItems" :key="item.id" >
<!--      Marketplace card notifications-->
      <div v-if="item.displayPeriodEnd" class="liked-listing-container" @mouseenter="markAsRead(item)">
        <vs-card id="message-notification-card" class="notification-card" actionable v-bind:class="[{'unread-notification': item.viewStatus === 'Unread'}, 'liked-listing-notification', 'notification-card']">
          <div v-if="!undoId.includes(item.fid)">
          <div class="card-container" v-if="item.status === 'Expired'">
            <div class="pln-top-row">
              <p class="sub-header">MARKETPLACE - {{item.created}}</p>
              <div class="lln-button-group">
                <div v-if="item.viewStatus == 'Important'" style="margin-right: 10px;">
                  <vs-button icon="flag" color="#c3ad32" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
                <div v-else style="margin-right: 10px;">
                  <vs-button icon="outlined_flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
                <vs-button color="danger" id="delete-btn" class="message-button delete-button" @click.stop.prevent="undo(item.id, item.fid, false, true); undoClick=true"  icon="close" style="margin-top: 5px;"></vs-button>
              </div>
            </div>
            <div style="display: flex; justify-content: space-between">
            <div class="lln-description">
              Your marketplace card {{item.title}} has expired
              <div id="buttons" style="margin-top: 5px; text-align: left;">
                <vs-button class="notificationButtons" @click="extendCard(item.cardId, item.title)">Extend</vs-button>
                <vs-button class="notificationButtons" @click="deleteCard(item.cardId, item.title)">Delete card</vs-button>
              </div>
            </div>
          </div>
          </div>

          <div class="card-container" v-else-if="item.status == 'Deleted'">
            <div class="pln-top-row">
              <p class="sub-header">MARKETPLACE - {{item.created}}</p>
              <div class="lln-button-group">
                <div v-if="item.viewStatus == 'Important'" style="margin-right: 10px;">
                  <vs-button icon="flag" color="#c3ad32" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
                <div v-else style="margin-right: 10px;">
                  <vs-button icon="outlined_flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
              </div>
              <div>
                <vs-button color="danger" id="delete-btn" class="message-button delete-button" @click="undo(item.id, item.fid, false, true, 'Deleted');
              undoClick=true" icon="close"></vs-button>
              </div>
            </div>
            <div style="display: flex; justify-content: space-between">
              <div class="lln-description">
                Your marketplace card {{item.title}} has been removed
              </div>
            </div>
          </div>
          </div>
          <div v-else>
            <div style="display: flex">
              <div class="lln-description">
                <span><strong>Notification has been deleted</strong>.</span>
              </div>
              <div class="lln-button-group">
                <vs-icon style="cursor: pointer" icon="undo" @click="undoDelete=true; removeId(item.fid)"></vs-icon>
              </div>
            </div>
          </div>
        </vs-card>
      </div>




      <!-- CARD MESSAGE -->
      <div v-if="item.card" class="liked-listing-container" @mouseenter="markAsRead(item)">
      <vs-card id="message-notification-card" class="notification-card" actionable v-bind:class="[{'unread-notification': item.viewStatus === 'Unread'}, 'liked-listing-notification', 'notification-card']">
        <div v-if="!undoId.includes(item.fid)">
        <div @click="openDetailedModal(item)">
          <p class="sub-header">MARKETPLACE - {{item.sent}}</p>
          <div style="display: flex; justify-content: space-between">
            <div class="lln-description">
              <div id="message-text">New message from {{users[item.sender.id || item.sender].firstName}} {{users[item.sender.id || item.sender].lastName}} about <strong>{{item.card.title}}</strong></div>
            </div>
            <div class="lln-button-group">
              <div v-if="item.viewStatus == 'Important'" style="margin-right: 10px;">
                <vs-button icon="flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
              </div>
              <div v-else style="margin-right: 10px;">
                <vs-button icon="outlined_flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
              </div>
              <div>
                <vs-button color="danger" id="delete-btn" class="message-button delete-button" @click="undo(item.id, item.fid, true, false);
                undoClick=true" icon="close"></vs-button>
              </div>
            </div>
          </div>
        </div>
        </div>
        <div v-else>
          <div style="display: flex">
            <div class="lln-description">
              <span><strong>Notification has been deleted</strong>.</span>
            </div>
            <div class="lln-button-group">
              <vs-icon style="cursor: pointer" icon="undo" @click="undoDelete=true; removeId(item.fid)"></vs-icon>
            </div>
          </div>
        </div>
      </vs-card>
      </div>

      <!-- USER BOUGHT LISTING NOTIFICATION -->
      <div v-else-if="item.boughtListing && item.boughtListing.buyer === currentUserId" @mouseenter="markAsRead(item)" class="bought-listing-container">
        <vs-card v-bind:class="[{'unread-notification': item.viewStatus === 'Unread'}, 'notification-card', 'bought-listing-notification']">
          <div v-if="!undoId.includes(item.fid)">
            <div class="pln-top-row">
              <p class="sub-header">BOUGHT LISTING - {{ item.created }}</p>
              <div class="lln-button-group">
                <div v-if="item.viewStatus == 'Important'">
                  <vs-button icon="flag" color="#c3ad32" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
                <div v-else>
                  <vs-button icon="outlined_flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
              </div>
              <div>
                <vs-button color="danger" icon="close" id="delete-purchased-listing-notification-button" class="lln-delete-button delete-button" @click="undo(item.id, item.fid, false, false);
              undoClick=true"></vs-button>
              </div>
            </div>
            <h3>{{ item.boughtListing.product.name }}</h3>
            <h5>{{ item.boughtListing.product.business.name }}</h5>
            <div class="pln-bottom-row">
              <h4>
                {{ currency }}
                {{ item.boughtListing.price }}
              </h4>
              <div>
                Collect your purchase at <strong>{{ createAddressString(item.boughtListing.product.business.address) }}</strong>
              </div>
            </div>
          </div>
          <div v-else>
            <div style="display: flex">
              <div class="lln-description">
                <span><strong>Notification has been deleted</strong>.</span>
              </div>
              <div class="lln-button-group">
                <vs-icon style="cursor: pointer" icon="undo" @click="undoDelete=true; removeId(item.fid)"></vs-icon>
              </div>
            </div>
          </div>
        </vs-card>
      </div>

      <!-- USER LIKED PURCHASED LISTING NOTIFICATIONS -->
      <div v-else-if="item.boughtListing && item.boughtListing.buyer !== currentUserId" @mouseenter="markAsRead(item)" class="liked-listing-container">
        <vs-card v-bind:class="[{'unread-notification': item.viewStatus === 'Unread'}, 'liked-listing-notification', 'notification-card']">
          <div v-if="!undoId.includes(item.fid)">
          <div class="pln-top-row">
            <p class="sub-header">LIKED LISTING - {{ item.created }}</p>
            <div class="lln-button-group">
              <div v-if="item.viewStatus == 'Important'">
                <vs-button icon="flag" color="#c3ad32" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
              </div>
              <div v-else>
                <vs-button icon="outlined_flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
              </div>
            </div>
            <vs-button color="danger"  icon="close" id="delete-liked-purchased-listing-notification-button" class="lln-delete-button delete-button" @click="undo(item.id, item.fid, false, false);
            undoClick=true"></vs-button>
          </div>
          <div class="lln-description">
            <strong>{{ item.boughtListing.product.name }}</strong>, by {{ item.boughtListing.product.business.name }} was purchased by someone else, and is no longer available.
          </div>
          </div>
          <div v-else>
            <div style="display: flex">
              <div class="lln-description">
                <span><strong>Notification has been deleted</strong>.</span>
              </div>
              <div class="lln-button-group">
                <vs-icon style="cursor: pointer" icon="undo" @click="undoDelete=true; removeId(item.fid)"></vs-icon>
              </div>
            </div>
          </div>
        </vs-card>
      </div>

      <!-- NEW LIKED & WISHLIST LISTING NOTIFICATIONS -->
      <div v-else-if="item.listing" @mouseenter="markAsRead(item)" class="liked-listing-container">
        <vs-card v-bind:class="[{'unread-notification': item.viewStatus === 'Unread'}, 'liked-listing-notification', 'notification-card']">
          <div v-if="!undoId.includes(item.fid)">
            <div class="pln-top-row">
              <p class="sub-header">{{ item.status.toUpperCase() }} LISTING - {{ item.created }}</p>
              <div class="lln-button-group">
                <div v-if="item.viewStatus == 'Important'">
                  <vs-button icon="flag" color="#c3ad32" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
                <div v-else>
                  <vs-button icon="outlined_flag" id="important-listing-notification-button" class="important-button" @click.stop.prevent="markAsImportant(item);"></vs-button>
                </div>
                <vs-button id="delete-liked-listing-notification-button" color="danger" icon="close" class="lln-delete-button delete-button" @click="undo(item.id, item.fid, false, false);
            undoClick=true"></vs-button>
              </div>
            </div>
          <div style="display: flex">
            <div class="lln-description">
              <span v-if="item.status === 'Liked'">You have liked <strong>{{ item.listing.inventoryItem.product.name }}</strong>.</span>
              <span v-else-if="item.status === 'Wishlist'"><strong>{{ item.listing.inventoryItem.product.business.name }}</strong> has just listed <strong>{{ item.listing.inventoryItem.product.name }}</strong>.</span>
              <span v-else>You have unliked <strong>{{ item.listing.inventoryItem.product.name }}</strong>.</span>
            </div>
            <div class="lln-button-group">
              <vs-button id="view-listing-button" class="lln-delete-button view-listing-button" @click="goToListing(item.listing)"> View Listing </vs-button>
            </div>
          </div>
          </div>
          <div v-else>
            <div style="display: flex">
              <div class="lln-description">
                <span><strong>Notification has been deleted</strong>.</span>
              </div>
              <div class="lln-button-group">
                <vs-icon style="cursor: pointer" icon="undo" @click="undoDelete=true; removeId(item.fid)"></vs-icon>
              </div>
            </div>
          </div>
        </vs-card>
      </div>
    </div>
  </div>
</template>

<script>
import api from "../Api";
import { store } from '../store';

export default {

  props: {
    currency: {
      type: String,
      default: "$",
    }
  },

  data() {
    return {
      undoId: [],
      undoDelete: false,
      undoClick: false,
      undoCount: 10,
      messaging: false,
      showing: false,
      message: '',
      errors: [],
      users: {},
      detailedView: false,
      currentMessage: null,

      currentUserId: null,
      combCount: 0,
      messages: [],
      listingNotifications: [],
      notifications: [],
      feedItems: [],
    }
  },

  watch: {
    likes() {
      this.getListingNotifications();
    }
  },

  mounted() {
    this.currentUserId = store.loggedInUserId;
    this.getMessages();
    this.getNotifications();
    this.getListingNotifications();
  },

  methods: {
    /**
     * Marks a listing notification as read.
     * @param notification the notification object to update.
     */
    markAsRead: function(notification) {
      if (notification.viewStatus === "Unread") {
        if (notification.card) {
          api.updateMessageViewStatus(notification.id, "Read")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Read";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        } else if (notification.displayPeriodEnd) {
          api.updateNotificationViewStatus(notification.id, "Read")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Read";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        } else {
          api.updateListingNotificationViewStatus(notification.id, "Read")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Read";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        }
      }
    },

    /**
     * Marks a listing notification as important
     * @param notification the notification object to update.
     */
    markAsImportant: function(notification) {
      if (notification.viewStatus != "Important") {
        if (notification.card) {
          api.updateMessageViewStatus(notification.id, "Important")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Important";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        } else if (notification.displayPeriodEnd) {
          api.updateNotificationViewStatus(notification.id, "Important")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Important";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        }
        else {
          api.updateListingNotificationViewStatus(notification.id, "Important")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Important";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        }
      } else {
        if (notification.card) {
          api.updateMessageViewStatus(notification.id, "Read")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Read";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        } else if(notification.displayPeriodEnd) {
          api.updateNotificationViewStatus(notification.id, "Read")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Read";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        }
        else {
          api.updateListingNotificationViewStatus(notification.id, "Read")
              .then((res) => {
                this.$log.debug(res);
                notification.viewStatus = "Read";
              })
              .catch((error) => {
                this.$log.debug(error);
              });
        }

      }
    },
    /**
     * removes Id from undoId list
     * @param id of notification
     **/
    removeId: function (id) {
      for (let i = this.undoId.length - 1; i >= 0; i--) {
        if (this.undoId[i] === id) {
          this.undoId.splice(i, 1);
        }
      }
    },

    /**
     * function to delete a notification and handle undo countdown
     * @param id
     * @param fid
     * @param isMessage
     **/
    undo: function (id, fid, isMessage, isExpiry, title="") {
      this.undoId.push(fid)
      let timer = setInterval(() => {
        if(this.undoCount <= 0 || this.$route.path !== "/home") {
          clearInterval(timer)
          if(isMessage===true) {
            console.log(id);
            this.deleteMessage(id, fid)
            this.undoCount = 10
            this.undoClick = false
            this.undoDelete = false
          } else if (isExpiry) {
            this.deleteCard(id, title)
            this.undoCount = 10
            this.undoClick = false
            this.undoDelete = false
          } else {
            this.deleteNotification(id, fid)
            this.undoCount = 10
            this.undoClick = false
            this.undoDelete = false
          }
        } else if(this.undoDelete===true) {
          this.undoClick = false
          this.undoDelete = false
          this.undoCount = 10
          clearInterval(timer)
        }

        this.undoCount -= 1;
      }, 1000);
    },

    /**
     * Combines the different news feed item types into a single list.
     * Sorts the list by unread notifications, then flagged notifications then read
     * with newest messages first in each sub group.
     */
    combineFeedMessages: function() {
      this.feedItems = this.messages.concat(this.listingNotifications.concat(this.notifications));
      // Set a overall unique id for each feed item. Prevent any overlapping ids which may cause update errors.
      this.feedItems = this.feedItems.map((item, index) => {
        item.fid = index;
        return item;
      });
      this.feedItems.sort(function(a, b) {
        return new Date(a.created) - new Date(b.created);
      });
      for(let item of this.feedItems) {
        switch (item.viewStatus) {
          case ('Important'):
            item.counter = 1;
            break;
          case ('Unread'):
            item.counter = 2;
            break;
          case ('Read'):
            item.counter = 3;
            break;
          default:
            item.counter = 3;
        }
      }
      this.feedItems.sort((a, b) => (b.counter < a.counter) ? 1 : -1)
    },


    /**
     * Calls the backend to retrieve all of the messages for the current user.
     */
    getMessages: function() {
      api.getMessages(this.currentUserId)
          .then((response) => {
            this.messages = response.data;
            for (let message of this.messages) {
              this.users[message.sender.id] = message.sender;
            }

          this.messages = this.messages.map(message => {
            // Map the sent date to a new created attribute - to be used for sorting.
            message.created = message.sent;
            return message;
          });
          this.combineFeedMessages();
        })
        .catch((error) => {
          this.$log.error("Error getting messages: " + error);
          this.$vs.notify({title:`Could not get messages`, text: "There was an error getting messages", color:'danger'});
        });
      this.polling = setInterval(() => {
        api.getMessages(this.currentUserId)
            .then((response) => {
              this.messages = response.data;
              for (let message of this.messages) {
                this.users[message.sender.id] = message.sender;
              }

              this.messages = this.messages.map(message => {
                // Map the sent date to a new created attribute - to be used for sorting.
                message.created = message.sent;
                return message;
              });
            })
      }, 3000)
    },

    /**
     * Calls the delete endpoint in the backend, removing the relevant listing notification
     * @param notificationId the unique id of the listingNotification to be deleted
     * @param fid
     */
    deleteNotification: function(notificationId, fid) {
      api.deleteListingNotification(notificationId)
        .then(() => {
          this.$vs.notify({
            title: `Listing Notification Deleted`,
            color: 'success'
          });
          this.spliceMessage(fid)
          this.getListingNotifications();
        })
        .catch((error) => {
          this.$vs.notify({
            title: 'Failed to delete the listing notification',
            color: 'danger'
          });
          this.$log.debug("Error Status:", error);
        });
      this.polling = setInterval(() => {
        api.getMessages(this.currentUserId)
            .then((response) => {
              this.messages = response.data;
              for (let message of this.messages) {
                this.users[message.sender.id] = message.sender;
              }

              this.messages = this.messages.map(message => {
                // Map the sent date to a new created attribute - to be used for sorting.
                message.created = message.sent;
                return message;
              });
            })
            .catch((error) => {
              this.$log.error("Error getting messages: " + error);
              this.$vs.notify({title:`Could not get messages`, text: "There was an error getting messages", color:'danger'});
            });
      }, 3000)
    },

    /**
     * removes notification from feedItems
     * @param fid
     */
    spliceMessage(fid) {
      for (let i = this.feedItems.length - 1; i >= 0; i--) {
        if (this.feedItems[i].fid === fid) {
          this.feedItems.splice(i, 1);

        }
      }
    },

    /**
     * Calls the backend to delete a given message's id.
     * @param messageId the unique id of the message to be deleted.
     * @param fid
     */
    deleteMessage: function(messageId, fid) {
      api.deleteMessage(messageId)
        .then((response) => {
          this.$vs.notify({
            title: `Message Deleted`,
            text: response.data.sender.firstName +" "+response.data.sender.lastName+ ": "+ response.data.description,
            color: 'success'
          });
          this.spliceMessage(fid)
          this.getMessages();
        })
        .catch((error) => {
          this.$vs.notify({
            title: 'Failed to delete the message',
            color: 'danger'
          });
          this.$log.debug("Error Status:", error);
        });
    },

    /**
     * Retrieves the notifications relating to purchased listings.
     */
    getListingNotifications: function() {
      api.getListingNotifications(store.loggedInUserId)
          .then((res) => {
            this.listingNotifications = res.data;
            if (this.combCount === 0) {
              this.combCount += 1
              this.combineFeedMessages();
            } else {
              this.combCount += 1
            }
          })
          .catch((error) => {
            this.$log.debug(error);
            if (error && error.response) {
              this.$vs.notify({title: `Error ${error.response}`,
                text: "There was a problem getting your newsfeed.",
                color: "danger"});
            }
          });
      this.polling = setInterval(() => {
        api.getListingNotifications(store.loggedInUserId)
            .then((res) => {
              this.listingNotifications = res.data;
            })
      }, 3000)
    },

    /**
     * Sends user message by calling POST messages
     * @param originalMessage the object of the originally sent message.
     * @param message the text string to send back.
     */
    sendMessage(originalMessage, message) {
      if (this.checkMessage(message)) {
        //the server can return either the sender object or it's id
        //we resolve which it is so we are always posting to the correct user
        let senderId = null;
        if (originalMessage.sender.id) {
          senderId = originalMessage.sender.id;
        } else {
          senderId = originalMessage.sender;
        }
        api.postMessage(senderId, originalMessage.card.id, message)
          .then(() => {
            this.$vs.notify({title: 'Reply Sent!', color: 'success'});
            //reset the message after success
            this.message = "";
            this.errors = [];
          })
          .catch((error) => {
            this.$log.debug(error);
            this.$vs.notify({title: 'Error sending message', text: `${error}`, color: 'danger'});
          });
      }
    },

    /**
     * Check the message contents
     * Simply check a blank message is not sent and the message is under the maximum character limit
     */
    checkMessage() {
      if (this.message == null || this.message === "") {
        this.errors.push('bad-content');
        this.$vs.notify({title:'Error sending message', text:`No message content`, color:'danger'});
        return false;
      }

      if (this.message.length > 250) {
        this.errors.push('bad-content');
        this.$vs.notify({title:'Error sending message', text:`Message is too long. Consider sending it in parts.`, color:'danger'});
        return false;
      }

      return true;
    },

    /**
     * Opens the expanded message information modal, allowing the user to view the full message content.
     * @param message the message to expand.
     */
    openDetailedModal: function(message) {
      this.currentMessage = message;
      this.detailedView = true;
      this.showing = true;
    },

    /**
     * Creates and returns a string of a given address to display on the page.
     * @param address the address entity to concatenate information from.
     * @return location string.
     */
    createAddressString: function(address) {
      let location = "";
      if (address.streetNumber) location += address.streetNumber + " ";
      if (address.streetName) location += address.streetName + ", ";
      if (address.suburb) location += address.suburb + ", ";
      if (address.city) location += address.city + ", ";
      if (address.region) location += address.region + ", ";
      if (address.country) location += address.country;

      return location;
    },

    /**
     * Redirects the page to the given full listing page.
     * @param listing the listing page to redirect the browser to.
     */
    goToListing: function(listing) {
      this.$router.push(`/businesses/${listing.inventoryItem.product.business.id}/listings/${listing.id}`);
    },

    /**
     * Check if the listing has been bought by the user
     */
    validPurchaseNotification(notification) {
      if (notification.status === 'Bought' && this.currentUserId !== notification.boughtListing.buyer) {
        return true;
      }
      return false;
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
      if(title == 'Deleted'){
        api.deleteCardExpiredNotification(cardId)
            .then(() => {
              this.getNotifications();
              this.$vs.notify({title:'Deleted', text:`Notification deleted`, color:'success'});
            }).catch(() => {
          this.$vs.notify({title:'Error', text:`Notification ${cardId} could not be deleted due to an error`, color:'danger'});
        })
      } else {
        api.deleteCard(cardId)
            .then(() => {
              this.getNotifications();
              this.$vs.notify({title:'Card Deleted', text:`Card ${title} was deleted`, color:'success'});
            }).catch(() => {
          this.$vs.notify({title:'Error', text:`Card ${title} could not be deleted due to an error`, color:'danger'});
        })
      }

    },

    /**
     * Calls api method to get notifications
     */
    getNotifications() {
      api.getNotifications(this.currentUserId)
          .then((response) => {
            this.notifications = response.data;
            this.combineFeedMessages();
          }).catch((error) => {
          this.$vs.notify({
          title: 'Failed to get notifications',
          color: 'danger'
        });
        this.$log.debug("Error Status:", error);
      });
    },

  },
  computed: {
    /**
     * Weird computed property to stop closing transition from happening when opening modal
     */
    showTransition: function() {
      return this.showing || !this.messaging;
    }
  }
}
</script>

<style scoped>
.notification-card {
  margin: 1em;
  width: auto;
}

.delete-button {
  width: 25px!important;
  height: 25px!important;
}

.delete-button >>> i.material-icons {
  font-size: 20px;
}

#message-notification-container {
  display: flex;
  margin-right: 10px;
}

#message-text {
  width: 100%;
  font-size: 14px;
}

#delete-btn {
  padding: 0.5em;
  font-size: 14px;
  width: 35px;
}

.message-detail-container {
  display: flex;
  margin-top: 5px;
}

#message-detail-message {
  font-size: 15px;
  word-wrap: break-word;
  width: 92%;
  float: right;
  margin-left: 5px;
}

#message-detail-sent {
  font-size: 14px;
  margin-left: 5px;
}

.message-detail-header {
  font-size: 20px;
  margin-left: 5px;
}

#card-modal-bottom {
  display: flex;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

#card-modal-message-send {
  float: right;
}

.message-detail-delivered {
  position: relative;
  flex: 50%;
  font-size: large;
  top: 7px;
  display: flex;
}

.message {
  margin-top: 25px;
}

#message-detail-modal button {
  padding: 10px 30px;
}

.sub-header {
  font-size: 12px;
  color: gray;
}

/* === PURCHASE LISTING NOTIFICATION === */
.unread-notification {
  box-shadow: 0 0 4px red!important;
}

.pln-top-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-bottom: 5px;
}

.pln-delete-button {
  margin-left: 1em;
}

.pln-bottom-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.liked-listing-notification > .vs-card--content {
  display: grid;
  grid-template-columns: 3fr 1fr;
  grid-template-rows: auto auto;
}

.lln-description {
  grid-row: 2;
  margin: auto 0;
}

.lln-button-group {
  grid-row: 1/3;
  grid-column: 2;

  margin: auto 0 auto auto;
  display: flex;
  flex-direction: row;
}

.lln-delete-button {
  margin: auto 0 auto 1em;
}


@media only screen and (max-width: 1250px){
  #message-notification-container {
    display: grid;
  }

  #message-notification-card {
    height: 100%;
  }

  #message-text {
    margin-bottom: 10px;
    height: 100%;
  }

  .lln-delete-button {
    margin-bottom: 4px;
  }
}

/* Taken from https://codepen.io/kdydesign/pen/VrQZqx */
.slide-enter-active {
  -moz-transition-duration: 0.3s;
  -webkit-transition-duration: 0.3s;
  -o-transition-duration: 0.3s;
  transition-duration: 0.3s;
  -moz-transition-timing-function: ease-in;
  -webkit-transition-timing-function: ease-in;
  -o-transition-timing-function: ease-in;
  transition-timing-function: ease-in;
}

.slide-leave-active {
  -moz-transition-duration: 0.3s;
  -webkit-transition-duration: 0.3s;
  -o-transition-duration: 0.3s;
  transition-duration: 0.3s;
  -moz-transition-timing-function: cubic-bezier(0, 1, 0.5, 1);
  -webkit-transition-timing-function: cubic-bezier(0, 1, 0.5, 1);
  -o-transition-timing-function: cubic-bezier(0, 1, 0.5, 1);
  transition-timing-function: cubic-bezier(0, 1, 0.5, 1);
}

.slide-enter-to, .slide-leave {
  max-height: 250px;
  overflow: hidden;
}

.slide-enter, .slide-leave-to {
  overflow: hidden;
  max-height: 0;
}

</style>
