<template>
  <vs-card class="main">
    <div class="container">
      <div class="title-container">
        <vs-icon icon="local_offer" style="margin: auto 0;"/>
        <h1 id="title" class="title-left title" style="color: #4d4d4d !important; font-family: Ubuntu, sans-serif !important;">Community Marketplace</h1>
        <div class="title-right">
          <div style="display: flex;">
            <vs-tooltip text="Grid View">
              <vs-button id="grid-button" icon="grid_view" type="border" @click="displayType = true" style="border: none; padding: 12px;"></vs-button>
            </vs-tooltip>
            <vs-tooltip text="List View">
              <vs-button id="list-button" icon="view_list" type="border" @click="displayType = false" style="border: none;"></vs-button>
            </vs-tooltip>
          </div>
        </div>
      </div>
      <vs-divider></vs-divider>

      <div class="title-container">

        <div class="title-left" >
          <vs-select class="selectExample" v-model="selectSortBy">
            <vs-select-item :key="index" :value="item.value" :text="item.text" v-for="(item, index) in optionsSortBy"/>
          </vs-select>
          <vs-select id="AscendingOrDescendingDropbox" class="selectAscOrDesc" v-model="ascending">
            <vs-select-item :key="index" :value="item.value" :text="item.text" v-for="(item, index) in optionsAscending"/>
          </vs-select>
          <vs-button @click="getSectionCards(currentSection, selectSortBy, ascending);" style="margin: 0 2em 0 0.5em; width: 100px">Sort</vs-button>

        </div>
        <div class="title-right">
          <vs-button icon="add" @click="openModal" >Add a New Item</vs-button>
        </div>
      </div>

      <vs-divider></vs-divider>

      <vs-tabs alignment="fixed" v-model="tabIndex">
        <vs-tab id="saleTab" label="For Sale" @click="getSectionCards('ForSale', selectSortBy, ascending); currentSection = 'ForSale'">
          <div>
            <MarketplaceGrid  v-if="displayType" @cardRemoved="onSuccess" :cardData="cards" />
            <MarketplaceTable v-if="!displayType" @cardRemoved="onSuccess" :tableData="cards" />
          </div>
        </vs-tab>
        <vs-tab id="wantedTab" label="Wanted" @click="getSectionCards('Wanted', selectSortBy, ascending); currentSection = 'Wanted'">
          <div>
            <MarketplaceGrid v-if="displayType" @cardRemoved="onSuccess" :cardData="cards" />
            <MarketplaceTable v-if="!displayType" @cardRemoved="onSuccess" :tableData="cards" />
          </div>
        </vs-tab>
        <vs-tab id="exchangeTab" label="Exchange" @click="getSectionCards('Exchange', selectSortBy, ascending); currentSection = 'Exchange'">
          <div>
            <MarketplaceGrid v-if="displayType" @cardRemoved="onSuccess" :cardData="cards" />
            <MarketplaceTable v-if="!displayType" @cardRemoved="onSuccess" :tableData="cards" />
          </div>

        </vs-tab>
      </vs-tabs>

      <div class="title-container">
        <div class="title-centre">
          <vs-pagination v-model="currentPage" :total="Math.round(numOfCards/itemPerPage +0.4)" @change="getSectionCards(currentSection, selectSortBy, ascending)"/>
        </div>
      </div>
    </div>

  <MarketplaceAddCard ref="marketplaceAddCard" @submitted="onSuccess"/>
  </vs-card>



</template>

<script>
import MarketplaceGrid from './MarketplaceGrid.vue'
import MarketplaceTable from './MarketplaceTable.vue'
import MarketplaceAddCard from './MarketplaceAddCard.vue'
import api from "../Api";
import { store } from "../store"

export default {
  name: "CommunityMarketplace",
  components: {
    MarketplaceGrid, MarketplaceTable, MarketplaceAddCard
  },

  data: () => {
    return {
      displayType: true,
      currentPage: 1,
      itemPerPage: 12,
      tabIndex: 0,
      ascending: false,
      numOfCards: 0,
      totalPages: 0,

      currentSection: "ForSale",
      cards: [],

      optionsSortBy:[
        {text:'Title',value:'title'},
        {text:'Date Created',value:'created'},
        {text:'Keywords',value:'keywords'},
        {text:'Country',value:'country'},
      ],
      optionsAscending:[
        {text: "Ascending", value:true},
        {text: "Descending", value:false},
      ],
      // optionsItemsPerPage:[
      //   {text:'Showing 12 Per Page',value:'12'},
      //   {text:'Showing 24 Per Page',value:'24'},
      //   {text:'Showing 48 Per Page',value:'48'},
      //   {text:'Showing 96 Per Page',value:'96'},
      // ],
      selectSortBy: 'created',
      selectSortByPrevious: '',
      toggleDirection: 1,
    }
  },

  methods: {
    getSectionCards: function(section, sortBy, ascending) {
      this.$vs.loading({
        container: ".vs-tabs",
      });
      this.cards = [];
      api.getCardsBySection(section, this.currentPage, this.itemPerPage, sortBy, ascending)
          .then((res) => {
            this.cards = res.data.content;
            this.numOfCards = res.data.totalElements;
            this.totalPages = res.data.totalPages;

            //Fixes issue with changing to section with less cards than what you already have
            if(this.currentPage >= Math.round(this.numOfCards/this.itemPerPage +0.4)) {
              this.currentPage = Math.round(this.numOfCards/this.itemPerPage +0.4)
            }

          })
          .catch((error) => {
            console.log(error);
          })
          .finally(() => {
            this.$vs.loading.close(`.vs-tabs > .con-vs-loading`);
          });

    },

    /**
     * Reloads the data upon sucessful add card.
     * ForSale, Wanted, Exchange
     * Orders the cards by newly created first
     * Tab changes to the new card's section
     *
     * @field tabIndex must track this.tabIndex
     */
    onSuccess(sectionName) {
      switch (sectionName) {
        case "Wanted":
          this.tabIndex = 1;
          break;
        case "Exchange":
          this.tabIndex = 2;
          break;
        case "Country":
          this.tabIndex = 3;
          break;
        default:
          this.tabIndex = 0;
          sectionName = "ForSale";
          break;
      }

      this.getSectionCards(sectionName);
      this.selectSortBy = 'created';
      this.ascending = false;
    },

    /**
    * Method for opening modal, calls method in child component to open modal
    */
    openModal: function() {
      this.$refs.marketplaceAddCard.openModal();
    },
   },


  mounted() {

    api.checkSession()
      .then(() => {
        if(store.actingAsBusinessId != null) {
          this.$router.push({path: "/home"})
        }
        this.getSectionCards("ForSale", "created", false);
      })
      .catch((error) => {
        this.$vs.notify({title:'Error getting session info', text:`${error}`, color:'danger'});
      });
  }

}

</script>

<style scoped>

#saleTab {
  color: #1F74FF;
}

#wantedTab {
  color: #1F74FF;
}

#exchangeTab {
  color: #1F74FF;
}

#AscendingOrDescendingDropbox {
  margin-left: 5px;
}

/* REMOVE AUTO SCROLL HIDING, SO USER KNOWS IF PARAGRAPH IS LONGER THAN CARD SIZE */

::-webkit-scrollbar {
  -webkit-appearance: none;
  width: 5px;
}

::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background-color: rgba(0, 0, 0, .5);
  box-shadow: 0 0 1px rgba(255, 255, 255, .5);
}

.container {
  margin: 1em;
}

.main {
  background-color: white;
  width: 75%;
  margin: 1em auto;
}

.title-container {
  display: flex;
  margin: auto;
  padding-bottom: 0.5em;
  padding-top: 0.5em;
}
.title-left {
  margin: auto auto auto 4px;
  display: flex;
}

.title-centre {
  margin-right: auto;
  margin-left: auto;
  display: flex;
}

.title-right{
  margin-right: 0;
  margin-left: auto;
  display: flex;
}

.menu-title {
  margin: auto;
  padding-right: 4px;
  font-size: 20px;
}

.title {
  font-size: 30px;

}

.con-select {
  margin: auto;
}

.vs-button {
  height: 35px;
}


</style>
