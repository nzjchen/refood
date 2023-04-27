<template>
  <vs-popup id="card-modal" v-if="selectedCard" :title="selectedCard.title" :active.sync="showing">
    <div id="card-modal-name"><vs-icon icon="face" class="inline"></vs-icon>
      <p class="inline text">{{selectedCard.user.firstName}} {{selectedCard.user.lastName}}</p>
    </div>
    <div id="card-modal-location"><vs-icon icon="location_on" class="inline"></vs-icon>
      <p class="inline text">{{selectedCard.user.homeAddress.suburb}} {{selectedCard.user.homeAddress.city}} {{selectedCard.user.homeAddress.country}}</p>
    </div>
    <div id="card-modal-closing"><vs-icon icon="event"></vs-icon>
      <p class="inline text">Closes on {{toStringDate(selectedCard.displayPeriodEnd)}}</p>
    </div>
    <div id="card-modal-description">{{selectedCard.description}}</div>
    <div id="card-modal-keywords" v-for="keyword in selectedCard.keywords.split(' ').filter(e => String(e).trim())" :key="keyword" >#{{keyword}}</div>
    <vs-divider></vs-divider>
    <div id="card-modal-bottom">
      <div id="card-modal-listed">Listed: {{toStringDate(selectedCard.created)}}</div>

      <vs-button class="card-modal-edit-button" @click="setPrefills()" v-if="editing===false && (userId === selectedCard.user.id || userRole === 'DGAA')">Edit Card</vs-button>
      <!-- Add delete button if user is card owner -->
      <vs-button id="card-modal-delete-button" @click="deleteCard()" v-if="selectedCard.user.id == userId || userRole === 'DGAA'" style="margin-left: 10px;">Delete</vs-button>
      <vs-button class="card-modal-message-button" @click="messaging=true" v-else-if="messaging===false && userId !== selectedCard.user.id">Message</vs-button>
      <vs-button id="card-modal-message-cancel" class="card-modal-message-button"  @click="messaging=false; errors=[]; editing=false; message = ''" v-else>Cancel</vs-button>
    </div>

    <transition name="slide" v-if="showTransition">
    <div id="card-modal-message" v-if="messaging">
      <vs-textarea style="margin-bottom: 50px" v-model="message" id="message-input"
                    :counter="250"
      />
      <div v-if="(errors.includes('bad-content'))" style="color: red">Message cannot be blank or too long</div>
      <div v-if="(errors.includes('invalid-card'))" style="color: red">There was something wrong with the card</div>
      <vs-button id="card-modal-message-send" @click="sendMessage()">Send Message</vs-button>
    </div>
    </transition>

    <!-- EDIT CARD -->
    <transition name="slide" v-if="showTransition">
      <div id="card-modal-edit" v-if="editing">
        <vs-row class="addCardField">
          <vs-col vs-w="2" vs-xs="12" class="addCardHeader">Section <span class="required">*</span></vs-col>
          <vs-select vs-w="10" v-model="section" :danger="editErrors.section.error"
                     :danger-text="editErrors.section.message">
            <vs-select-item v-for="section in availableSections" :key="section.key" :text="section.key" :value="section.value"/>
          </vs-select>
        </vs-row>

        <vs-row class="addCardField">
          <vs-col id="title" vs-w="2" vs-xs="12">
            <div class="addCardHeader" >Title <span class="required">*</span> </div>
          </vs-col>
          <vs-col vs-w="9">
            <vs-textarea :class="[{'textarea-danger': editErrors.title.error}, 'addCardInput', 'title-input']" v-model="title" rows="1" :counter="50" @keydown.enter.prevent=""></vs-textarea>
            <transition name="fade">
              <div v-show="editErrors.title.error" class="edit-error">{{editErrors.title.message}}</div>
            </transition>
          </vs-col>
        </vs-row>
        <vs-row class="addCardField">
          <div class="addCardHeader">Description</div>
          <vs-textarea v-model="description" id="description" :class="[{'textarea-danger': descriptionError}]" :counter="250" @keydown.enter.prevent></vs-textarea>
          <vs-col>
            <transition name="fade">
              <div v-show="descriptionError" class="edit-error">{{descriptionErrorMessage}}</div> <!-- vs-textarea doesn't have a message system, so this is a homebrew solution -->
            </transition>
          </vs-col>
        </vs-row>
        <vs-row class="addCardField">
          <vs-col vs-w="2" vs-xs="12">
            <div class="addCardHeader">Keywords</div>
          </vs-col>
          <vs-col vs-w="9">
            <vs-chips color="rgb(145, 32, 159)" placeholder="New Keyword" v-model="keywordListInput">
              <vs-chip v-for="keyword in keywordList" v-bind:key="keyword.value" @click="remove(keyword)"
                       closable>{{keyword}}
              </vs-chip>
            </vs-chips>
             <div id="keyword-count">{{keywordList.length}} / 10</div>
          </vs-col>
        </vs-row>
        <vs-button color="success" id="card-modal-edit-save" @click="saveCardEdit">Save</vs-button>
      </div>
    </transition>

  </vs-popup>
</template>

<script>
import api from "../Api";

export default {
  name: "CardModal",
  props: ['selectedCard'],
  data: function() {
    return {
      showing: false,
      userId: null,
      userRole: null,
      messaging: false,
      message: '',
      editing: false,
      keywords: '',
      title: '',
      keywordList: [],
      keywordListInput: [],
      description: '',
      section: '',
      descriptionError: false,
      descriptionErrorMessage: "Description is too long",
      editErrors: {
        title: {error: false, message: "There is a problem with the card title"},
        section: {error: false, message: "You must choose a section"},
      },
      availableSections: [
        {key:'For Sale', value:'ForSale'},
        {key:'Wanted', value:'Wanted'},
        {key:'Exchange', value: 'Exchange'}
      ],
      recipient: null,
      errors: [],
    }
  },
  methods:
      {
        /**
         * removes keyword
         */
        remove(item) {
          this.keywordList.splice(this.keywordList.indexOf(item), 1)
        },
        /**
         * sets prefilled entries for edit card modal
         */
        setPrefills: function() {
          this.editing = true;
          this.title = this.selectedCard.title;
          this.description = this.selectedCard.description;
          this.section = this.selectedCard.section;
          if (this.selectedCard.keywords === '') {
            this.keywordList = [];
          } else {
            this.keywordList = this.selectedCard.keywords.split(" ");

            this.keywordList = this.keywordList.filter(e => String(e).trim());

            this.keywordListInput = this.keywordList

          }
        },
        /**
         * Opens modal by setting showing to true (linked to :active-sync), also resets form data before opening
         */
        openModal: function() {
          this.resetState();
          this.showing = true;
          this.getUserId();
        },
        /**
         * Converts seconds to date
         */
        toStringDate: function (timestamp) {
          const dateFull = new Date(timestamp);
          return dateFull.toDateString();
        },

        /**
         * Obtain the current logged in user's ID
         */
        getUserId: function() {
          api.checkSession()
              .then((response) => {
                this.userId = response.data.id;
                this.userRole = response.data.role;
              })
              .catch((error) => {
                this.$log.error("Error checking sessions: " + error);
                this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
              });
        },

        /**
         * Preconditions: Must be logged in
         * Postconditions: The card will be deleted
         * Allows the user to delete a card
         */
        deleteCard: function() {
          api.deleteCard(this.selectedCard.id)
          .then(() => {
            this.$emit('deleted');
            this.showing = false;
            this.$vs.notify({title:'Success', text:'Card deleted', color:'success'});
          }).catch((error) => {
            this.$log.error("Error deleting card: " + error);
            this.$vs.notify({title:'Error', text:'ERROR deleting card', color:'danger'});
          });
        },

        /**
         * Sends user message by calling POST messages
         * @param cardId ID of card whose owner the user is going to message
         */
        sendMessage() {
          //Because the server may return either the full user object or just their id
          if (this.checkMessage()) {
            this.sendPostMessage();
          }

        },

        /**
         * Sends post message to back end. Notifies user if the send was a success or an error
         */
        sendPostMessage() {
          api.postMessage(this.recipient, this.selectedCard.id, this.message)
              .then(() => {
                this.$vs.notify({title: 'Message Sent!', color: 'success'});

                //reset the message after success
                this.message = "";
                this.errors=[];

              })
              .catch((error) => {
                this.$log.debug(error);
                this.$vs.notify({title: 'Error sending message', text: `${error}`, color: 'danger'});
              });
        },

        /**
         * Check the message contents
         * Simply check a blank message is not sent
         *
         * @param message   Text to be sent
         * @param recipient Intended receiver of the message
         * @return boolean  False if null or blank, then notify the user.
         *                  Otherwise return true.
         */
        checkMessage() {
          //the recipient can be stored as the userId or user.id depending on what the backend returns
          //we check if it is a valid user Id at the end.
          if (this.selectedCard.user) {
            this.recipient = this.selectedCard.user.id;
          } else {
            this.recipient = this.selectedCard.userId;
          }

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

          if (isNaN(this.recipient)) {
            this.errors.push('invalid-card');

            this.$vs.notify({title:'Error sending message', text:`Receiver is invalid`, color:'danger'});
            return false;
          }

          if (isNaN(this.selectedCard.id)) {
            this.errors.push('invalid-card');

            this.$vs.notify({title:'Error sending message', text:`Card is invalid`, color:'danger'});
            return false;
          }


          return true;
        },

        /**
         * Resets state of messaging information
         */
        resetState() {
          this.message = '';
          this.title = '';
          this.keywords = [];
          this.description = '';
          this.editing = false;
          this.messaging = false;
        },

        /**
         * Saves the the changed input fields of an edited card - provided that the fields are valid.
         * Sends request to backend to successfully edit card.
         */
        saveCardEdit: function() {
          this.keywords = '';
          for(let i = 0; i < this.keywordList.length; i++){
                this.keywords += this.keywordList[i] + " ";
          }
          if (this.validateCardEdit()) {
            this.title = this.title.trim(); // Removing any whitespace before and after.
            api.modifyCard(this.selectedCard.id, this.selectedCard.user.id, this.title, this.description, this.keywords.trimEnd(), this.section)
                .then(() => {
                  this.$emit('deleted');
                  this.$vs.notify({title: "Success", text: "Card successfully edited.", color:"success"});
                  this.showing = false;
                })
                .catch((error) => {
                  let errormsg = "ERROR creating new card: ";
                  if (error) {
                    if (error.response) {
                      if (error.response.status === 401 || error.response.status === 403) {
                        this.$vs.notify({title: 'Error', text: errormsg + 'user account error', color: 'danger'});
                      }

                      if (error.response.status === 400) {
                        this.$vs.notify({title: 'Error', text: errormsg + 'invalid data', color: 'danger'});
                      } else {
                        console.log(error.response.message);
                      }
                    } else {
                      this.$vs.notify({
                        title: 'Error',
                        text: 'ERROR trying to obtain user info from session:',
                        color: 'danger'
                      });
                    }
                  }
                  this.keywords = '';
                });
          }
          else {
            this.$vs.notify({title: "Error saving changes", text: "Please check the input fields and try again.", color: "danger"});
          }
        },

        /**
         * Validates the input fields of the user's card editing.
         */
        validateCardEdit: function() {
          let valid = true;

          if (this.editErrors.title.error) {
            valid = false;
          }

          if (this.editErrors.section.error) {
            valid = false;
          }

          if (this.description.length > 250) {
            this.descriptionError = true;
            valid = false;
          } else {
            this.descriptionError = false;
          }
          return valid;
        }
      },

  computed: {
    /**
     * Weird computed property to stop closing transition from happening when opening modal
     */
    showTransition: function() {
      return this.showing || !this.messaging;
    }
  },

  watch: {
    /**
     * Watches the title value for any changes, and checks validity of it.
     */
    title: function() {
      if (this.title.length < 1 || this.title.trim().length === 0) {
        this.editErrors.title.error = true;
        this.editErrors.title.message = "A valid card title is required";
      }
      else if (this.title.length > 50) {
        this.editErrors.title.error = true;
        this.editErrors.title.message = "Card title is too long";
      }
      else if (this.description.length > 250){
        this.editErrors.description.error = true;
      }
      else {
        this.editErrors.title.error = false;
      }
    },

    /**
     * Makes sure the section doesn't somehow become null.
     */
    section: function() {
      this.editErrors.section.error = this.section == null || this.section === "";
    },

    /**
     * Watches the keyword input for any changes, and checks validity of it.
     */
    keywordListInput: function() {
      if(this.keywordListInput.filter(x => x.length > 20).length > 0) {
        this.$vs.notify({
          title: 'Bad keyword',
          text: 'Keyword exceeds max character limit of 20',
          color: 'danger'
        });
        this.$nextTick(() => {
          this.keywordListInput.pop();
        })
      } else if (this.keywordListInput.length > 10) {
          this.$vs.notify({
            title: 'Too many keywords',
            text: 'You cannot have more than 10 keywords',
            color: 'danger'
          });

          this.$nextTick(() => {
            this.keywordListInput.pop();
          })
      } else if (/\s/.test(this.keywordListInput[this.keywordListInput.length - 1])) {
          this.$vs.notify({
            title: 'Bad keyword',
            text: 'Keyword cannot have spaces',
            color: 'danger'
          });

          this.$nextTick(() => {
            this.keywordListInput.pop();
          })
        } else if (this.keywordListInput[this.keywordListInput.length - 1] == ''){
          this.$vs.notify({
            title: 'Bad keyword',
            text: 'Keyword cannot be empty',
            color: 'danger'
          });

          this.$nextTick(() => {
            this.keywordListInput.pop();
          })

        } else {
          this.keywordList = this.keywordListInput
        }
    }

  }
}
</script>

<style scoped>

#cardModal >>> .con-vs-popup {
  z-index: 10000;
}

.inline {
  display: inline-block;
  position: relative;
}

#card-modal-description {
  word-break: break-word;
}

.text {
  top: -5px;
  left: 5px;
  font-size: large;
}

#card-modal-keywords {
  color: #1F74FF;
  font-size: 15px;
  padding: 2px;
  float: left;
}

#card-modal-bottom {
  display: flex;
  margin-left: 20px;
  flex-wrap: wrap;
}

#card-modal-listed {
  position: relative;
  flex: 50%;
  font-size: large;
  top: 7px;
}

.card-modal-message-button, .card-modal-edit-button {
  flex: 0%;
}

#card-modal-message {
  margin-top: 10px;
}

#card-modal-message >>> textarea {
  max-height: 100px;
  min-height: 100px;
}

#card-modal-message-send {
  float: right;
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

/* === EDIT CARD === */
#card-modal-edit {
  margin-top: 8px;
}

.edit-error {
  font-size: 10px;
  position: absolute;
  color: #FF4757;
  margin-left: 8px;
}

.title-input >>> textarea {
  max-height: 33px;
  min-height: 33px;
}

.title-input { /* Is being used. */
  margin-bottom: 0;
}

</style>
