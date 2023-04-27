<template lang="html">
  <vs-popup id="addCardModal" title="Add Card To Marketplace" :active.sync="showing">

    <!-- Section selection -->
    <vs-row class="addCardField">
    <vs-col vs-w="2" vs-xs="12" class="addCardHeader">Section <span class="required">*</span></vs-col>
    <vs-select vs-w="10" v-model="section" :danger="(errors.includes('no-section'))"
               danger-text="You must choose a section.">
        <vs-select-item v-for="section in avaliableSections" :key="section.key" :text="section.key" :value="section.value"/>
    </vs-select>
    </vs-row>

    <!-- Card name -->
    <vs-row class="addCardField">
        <vs-col id="title" vs-w="2" vs-xs="12">
          <div class="addCardHeader" >Title <span class="required">*</span> </div>
        </vs-col> 
        <vs-col vs-w="9">
          <vs-textarea v-model="title" rows="1" :class="[{'textarea-danger': titleError}, 'addCardInput', 'title-input']" :counter="50" @keydown.enter.prevent></vs-textarea>
          <transition name="fade">
            <div v-show="titleError" class="create-error">{{titleErrorMessage}}</div> <!-- vs-textarea doesn't have a message system, so this is a homebrew solution -->
          </transition>
        </vs-col>
    </vs-row>

    <!-- Card description -->
    <vs-row class="addCardField">
        <div class="addCardHeader">Description</div>
        <vs-textarea v-model="description" id="description" :class="[{'textarea-danger': descriptionError}]" :counter="250" @keydown.enter.prevent></vs-textarea>
        <vs-col>
        <transition name="fade">
            <div v-show="descriptionError" class="create-error">{{descriptionErrorMessage}}</div> <!-- vs-textarea doesn't have a message system, so this is a homebrew solution -->
        </transition>
        </vs-col>
    </vs-row>
    <!-- Card keywords -->
    <vs-row class="addCardField">
        <vs-col vs-w="2" vs-xs="12">
            <div class="addCardHeader">Keywords</div>
        </vs-col> 
        <vs-col vs-w="9">
          <div>
            <vs-chips color="rgb(145, 32, 159)" placeholder="New Keyword" :counter="50" v-model="keywordListInput" :class="[{'textarea-danger': keywordError}]" @keydown.enter.prevent>
              <vs-chip v-for="keyword in keywordList" v-bind:key="keyword.value" @click="remove(keyword)"
                      closable>{{keyword}}
              </vs-chip>
            </vs-chips>
            <div id="keyword-count">{{keywordList.length}} / 10</div>
          </div>
        </vs-col>
    </vs-row>
    
    <div id="buttons">
        <vs-button class="addCardButton" @click="addToMarketplace()">Add To Marketplace</vs-button>
        <vs-button class="addCardButton" @click="closeModal()">Cancel</vs-button>
    </div>

  </vs-popup>
</template>

<script>
import api from "../Api";

export default {
    data: function() {
      return {
        showing: false,
        avaliableSections: [
            {key:'For Sale', value:'ForSale'},
            {key:'Wanted', value:'Wanted'},
            {key:'Exchange', value: 'Exchange'}
        ],
        userSession: null,
        id: null,
        section: null,
        title: '',
        description: '',
        keywordListInput: [],
        keywords: '',
        keywordError: false,
        keywordList: [],
        errors: [],
        titleError: false,
        titleErrorMessage: "Card title is invalid",
        descriptionError: false,
        descriptionErrorMessage: "Description is too long"
      }
    },

    watch: {
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
    },


    methods: {
      remove(item) {
        this.keywordListInput.splice(this.keywordListInput.indexOf(item), 1)
      },
      /**
       * Preconditions: User clicks add to inventory button
       * Postconditions:
       **/
      checkForm(){
        this.keywords= '';
        for (let i = 0; i < this.keywordList.length; i++) {
          if (this.keywordList[i]) {
              this.keywords += this.keywordList[i] + " ";
          }
        }

        this.errors = [];


        if (this.section === null) {
          this.errors.push('no-section');
        }

        if (this.title.length <= 2) {
          this.errors.push('no-title');
          this.titleError = true;
        }

        if (this.title.length > 50){
          this.errors.push('long-title');
          this.titleError = true;
        }
        if (this.description.length > 250) {
          this.errors.push("invalid-description");
          this.descriptionError = true;
        } else {
            this.descriptionError = false;
        }

        if (this.errors.includes('no-section')) {
          this.$vs.notify({
            title: 'Failed to add card',
            text: 'Section is Required.',
            color: 'danger'
          });
        }

        if (this.errors.includes('no-title')) {
          this.$vs.notify({
            title: 'Failed to add card',
            text: 'Please check your card title',
            color: 'danger'
          });
          this.titleErrorMessage = "Title is required or is too short";
        }

        if (this.errors.includes('long-title')) {
          this.$vs.notify({
            title: 'Failed to add card',
            text: 'Title exceeds max length',
            color: 'danger'
          });
          this.titleErrorMessage = "Title exceeds max length";
        }

        if (this.errors.includes('invalid-description')) {
            this.$vs.notify({
              title: 'Failed to add card',
              text: this.descriptionErrorMessage,
              color: 'danger'
          });
        }

        if (this.title.trim().length === 0) {
          this.errors.push("invalid-title");
          this.titleError = true;
          this.titleErrorMessage = "Card title is invalid";
        }

        if (this.errors.length > 0) {
          return false;
        }
        return true;
      },

      /**
       * Creates a new card. of type:
       * (long creatorId, String title, String description, String keywords, MarketplaceSection section)
       *
       * 401 if not logged in, 403 if creatorId, session user Id do not match or if not a D/GAA,
       * 400 if there are errors with data, 201 otherwise

       */
      addToMarketplace() {
        this.keywords= '';
        for(let i = 0; i < this.keywordList.length; i++){
          this.keywords += this.keywordList[i] + " ";
        }
        if (this.checkForm()) {
          api.createCard(this.id, this.title, this.description, this.keywords.trimRight(), this.section)
              .then((res) => {
                this.$vs.notify({title: 'Success', text: `created new card: ${res.data.cardId}`, color: 'success'});
                this.$emit('submitted', this.section);

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
                    }
                  } else {
                    this.$vs.notify({
                      title: 'Error',
                      text: 'ERROR trying to obtain user info from session:',
                      color: 'danger'
                    });
                  }
                }
              });
          this.closeModal();
        }
      },

      /**
       * obtains the user's account details to create a new card.
       */
      getSession() {
        api.checkSession()
            .then((response) => {
              this.userSession = response.data;
              this.id = response.data.id;
            })
            .catch((error) => {
              this.$vs.notify({title:'Error', text:'ERROR trying to obtain user info from session:', color:'danger'});
              this.$log.error("Error checking sessions: " + error);
            });
      },

        /**
        * Closes modal by setting showing to false (which is linked to Card :active-sync)
        */
        closeModal() {
            this.showing = false;
        },

        /** 
        * Opens modal by setting showing to true (linked to :active-sync), also resets form data before opening
        */
        openModal() {
          this.resetData();
          this.getSession();

          this.showing = true;
        },

        /**
        * Method for resetting form data, gets called when modal closes
        */
        resetData() {

            this.section = null;
            this.title = '';
            this.description = '';
            this.keywords = '';
            this.keywordList = [];
            this.keywordListInput = [];
            this.titleError = false;
            this.keywordError = false;
            this.descriptionError = false;
            this.errors = [];
        }
    },
}
</script>

<style>

.addCardField {
  margin-bottom: 25px;
  margin-top: 5px;
}

.addCardHeader {
  font-size: 17px;
}

.addCardInput {
  width: 80%;
  font-size: 20px;
}


.addCardButton {
  margin: 5px;
  width: 150px;
}

.required {
  color: red;
}

#buttons {
  text-align: center;
}


#description {
  height: 150px;
  max-height: 150px;
  min-height: 150px;
}

#keyword-count {
  text-align: center;
}

.create-error {
  font-size: 10px;
  position: absolute;
  color: #FF4757;
  margin-left: 8px;
}

.title-input > textarea {
  max-height: 33px;
  min-height: 33px;
}

.title-input { /* Is being used. */
  margin-bottom: 0;
}

.con-chips {
  justify-content: flex-start;
}

</style>