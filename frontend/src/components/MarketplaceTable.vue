<template>
  <div id="tableContainer">
        <vs-row id="tableRow">
          <vs-table v-model="selectedItem" @selected="handleSelected" :data="tableData" style="border-spacing: 0 20px; margin: 1em" search>
            <template slot="thead" style="background:blue">
              <vs-th sort-key="id" style="text-align: center;">
                <div>ID</div>
              </vs-th>
              <vs-th sort-key="created">
                <div>Date</div>
              </vs-th >
              <vs-th sort-key="user.firstName">
                <div>Name</div>
              </vs-th >
              <vs-th sort-key="user.address">
                <div>Address</div>
              </vs-th >
              <vs-th sort-key="title">
                <div>Title</div>
              </vs-th >
              <vs-th sort-key="description">
                <div>Description</div>
              </vs-th>
              <vs-th sort-key="keywords">
                <div>Keywords</div>
              </vs-th>
              <vs-th><!-- Actions Column --></vs-th>
            </template>

            <template slot-scope="{data}">
              <vs-tr :key="card.id" v-for="card in data" :data="card">
                <vs-td >
                  <a href="#">{{ card.id }}</a>
                </vs-td>
                <vs-td id="cardCreationDate">{{card.created}}</vs-td>

                <vs-td id="cardUserName">{{card.user.firstName+" "+card.user.lastName}}</vs-td>
                <vs-td id="cardUserAddress" v-if="card.user.homeAddress">{{MarketplaceCommon.getGeneralAddress(card.user.homeAddress)}}</vs-td>
                <vs-td id="cardUserNoAddress" v-if="!card.user.homeAddress"></vs-td>

                <vs-td>{{ card.title }} </vs-td>
                <vs-td style="min-width: 500px;" >{{ card.description }} </vs-td>

                <vs-td v-if="card.keywords" id="keywordWrapper">
                  <div id="cardKeywords"  v-for="keyword in MarketplaceCommon.getKeywords(card.keywords)" :key="keyword.id" >#{{keyword.name}}</div>
                </vs-td>
              </vs-tr>
            </template>
          </vs-table>
        </vs-row>
    <CardModal ref="cardModal" v-if="selectedItem != null" @deleted="notifyOfDeletion" :selectedCard="selectedItem" />
  </div>
</template>

<script>
import CardModal from './CardModal.vue'
import MarketplaceCommon from "./MarketplaceCommon.js";

export default {
  data: function() {
    return {
      selectedItem: null,
      MarketplaceCommon
    }
  },
  components: {
    CardModal
  },
  props: ['tableData'],
  methods: {
    /**
     * Method for opening card modal, calls method in child component to open modal
     */
    handleSelected(item) {
      this.selectedItem = item;
      this.$refs.cardModal.openModal();
    },

    /**
     * Method for notifying the marketplace component that a card has been deleted
     */
    notifyOfDeletion: function() {
      this.$emit('cardRemoved');
    }
  }
}
</script>

<style>

/* CARD STYLING */

#marketImage {
  width: 100%;
  height: auto;
}

#tableRow {
  display: block !important;
}

#cardKeywords {
  color: #1F74FF;
  font-size: 15px;
  padding: 2px;
  float: left;
}


/* REMOVE AUTO SCROLL HIDING, SO USER KNOWS IF PARAGRAPH IS LONGER THAN CARD SIZE */

::-webkit-scrollbar {
  -webkit-appearance: none;
  width: 5px;
}

th {
  background: #1F74FF;
  color: white;
}

td {
  max-width: 100px;
}
table {
  width: 100%;
}

::-webkit-scrollbar-thumb {
  border-radius: 4px;
  background-color: rgba(0, 0, 0, .5);
  box-shadow: 0 0 1px rgba(255, 255, 255, .5);
}

td.td.vs-table--td {
  overflow-y: auto;
}


</style>
