<template>
  <div id="report-container">


    <div id="content-container">
      <!-- FILTER/GRANULARITY PICKER -->
      <vs-card id="options-container">
        <div class="options-header" style="display: flex; justify-content: center">
          <span style="padding-right: 4px">Period</span>
          <vs-tooltip text="Adjusts the length of time of each statistic">
            <vs-icon icon="info" size="14px"/>
          </vs-tooltip>
        </div>
        <vs-button v-bind:class="[{'active-button': activePeriodButton === '1-d'}, 'options-button']"
                   type="border"
                   style="grid-column: 1;"
                   @click="onPeriodChange('1-d')">
          1 Day
        </vs-button>
        <vs-button v-bind:class="[{'active-button': activePeriodButton === '1-w'}, 'options-button']"
                   type="border"
                   style="grid-column: 3;"
                   @click="onPeriodChange('1-w')">
          1 Week
        </vs-button>
        <vs-button v-bind:class="[{'active-button': activePeriodButton === '1-m'}, 'options-button']"
                   type="border"
                   style="grid-column: 1;"
                   @click="onPeriodChange('1-m')">
          1 Month
        </vs-button>
        <vs-button v-bind:class="[{'active-button': activePeriodButton === '6-m'}, 'options-button']"
                   type="border"
                   style="grid-column: 3;"
                   @click="onPeriodChange('6-m')">
          6 Months
        </vs-button>
        <vs-button v-bind:class="[{'active-button': activePeriodButton === '1-y'}, 'options-button']"
                   type="border"
                   style="grid-column: 1;"
                   @click="onPeriodChange('1-y')">
          1 Year
        </vs-button>
        <vs-button v-bind:class="[{'active-button': activePeriodButton === 'all'}, 'options-button']"
                   type="border"
                   style="grid-column: 3;"
                   @click="onPeriodChange('all')">
          All
        </vs-button>
        <div class="options-header" style="font-size: 14px">Custom</div>
        <vs-input type="date" size="small" class="date-input" style="grid-column: 1" v-model="pickedStart" label="Start" :danger="checkErrors('start')"
                  :danger-text="getError('start')"/>
        <vs-input type="date" size="small" class="date-input" v-model="pickedEnd" label="End" :danger="checkErrors('end')"
                  :danger-text="getError('end')" style="grid-column: 3"/>
        <vs-button type="border" size="small" style="grid-column: 1/4; width: 100px; margin: auto;" @click="onPeriodChange('custom')">Go</vs-button>

        <vs-divider style="grid-column: 1/4; margin: 0 auto;"/>

        <div class="options-header">Summary Interval</div>
        <vs-button id="week-granularity" v-bind:class="[{'active-button': activeGranularityButton === 'w'}, 'options-button']"
                   type="border"
                   style="grid-column: 1;"
                   @click="onGranularityChange('w')">
          Week
        </vs-button>
        <vs-button id="month-granularity" v-bind:class="[{'active-button': activeGranularityButton === 'm'}, 'options-button']"
                   type="border"
                   style="grid-column: 3;"
                   @click="onGranularityChange('m')">
          Month
        </vs-button>
        <vs-button id="year-granularity" v-bind:class="[{'active-button': activeGranularityButton === 'y'}, 'options-button']"
                   type="border"
                   style="grid-column: 1;"
                   @click="onGranularityChange('y')">
          Year
        </vs-button>
        <vs-button v-bind:class="[{'active-button': activeGranularityButton === 'all'}, 'options-button']"
                   type="border"
                   style="grid-column: 3;"
                   @click="onGranularityChange('all')">
          All
        </vs-button>
      </vs-card>

      <vs-card id="summary-container">
        <h3>Summary</h3>
        <vs-divider style="margin-top: 4px"/>
        <!-- === FULL SUMMARY === -->
        <div v-if="activeGranularityButton==='all'">
          <div class="row-summary-container">
            <h2 class="summary-header">{{currentYearReport.title}}</h2>
            <div class="summary-subheader">NUMBER OF SALES</div>
            <div>{{currentYearReport.totalSales}}</div>
            <div class="summary-subheader">AVG ITEMS PER SALE</div>
            <div>{{currentYearReport.averageItemsPerSale}}</div>
            <div class="summary-subheader">TOTAL SALE VALUE</div>
            <div>{{currency + currentYearReport.totalSaleValue}}</div>
            <div class="summary-subheader">AVG SALE VALUE</div>
            <div>{{currency + currentYearReport.averageSale}}</div>
          </div>
        </div>
        <!-- === GRANULARITY SUMMARY === -->
        <div v-else>
          <div  id="summary-list" v-for="(summary, index) in reportGranularity" :key="index" >
            <div class="row-summary-container">
              <h2 class="summary-header">{{summary.title}}</h2>
              <div class="summary-subheader">NUMBER OF SALES</div>
              <div>{{summary.totalSales}}</div>
              <div class="summary-subheader">AVG ITEMS PER SALE</div>
              <div>{{summary.averageItemsPerSale}}</div>
              <div class="summary-subheader">TOTAL SALE VALUE</div>
              <div>{{currency + summary.totalSaleValue}}</div>
              <div class="summary-subheader">AVG SALE VALUE</div>
              <div>{{currency + summary.averageSale}}</div>
            </div>
            <vs-divider style="margin-top: 4px"/>
          </div>
        </div>
      </vs-card>

      <div id="stats-container">
        <div class="stat-box">
          <div class="stat-subheader">Average Sale</div>
          <h2 style="padding-left: 12px">{{currency + currentYearReport.averageSale}}</h2>
          <div v-if="this.activePeriodButton !== `all`" class="sub-header stat-change">
            <vs-icon v-if="increaseFromLastYear(currentYearReport.averageSale, lastYearReport.averageSale) < 0" id="iconAverageSale" color="red" icon="arrow_drop_down" class="stat-change-icon"/> <!-- decrease icon -->
            <vs-icon v-else color="green" icon="arrow_drop_up" class="stat-change-icon"/> <!-- increase icon -->
            <div>{{increaseFromLastYear(currentYearReport.averageSale, lastYearReport.averageSale)}}% from last year</div>
          </div>
          <div class="sub-header stat-date"> {{this.formatDate(dateStart)}} - {{this.formatDate(dateEnd)}}</div>
        </div>
        <div class="stat-box">
          <div class="stat-subheader">Average Price Per Item</div>
          <h2 style="padding-left: 12px">{{currency + currentYearReport.averagePricePerItem}}</h2>
          <div v-if="this.activePeriodButton !== `all`" class="sub-header stat-change">
            <vs-icon v-if="increaseFromLastYear(currentYearReport.averagePricePerItem, lastYearReport.averagePricePerItem) < 0" id="iconAverageSale" color="red" icon="arrow_drop_down" class="stat-change-icon"/> <!-- decrease icon -->
            <vs-icon v-else color="green" icon="arrow_drop_up" class="stat-change-icon"/> <!-- increase icon -->
            <div>{{increaseFromLastYear(currentYearReport.averagePricePerItem, lastYearReport.averagePricePerItem)}}% from last year</div>
          </div>
          <div class="sub-header stat-date" style="padding-left: 12px;"> {{this.formatDate(dateStart)}} - {{this.formatDate(dateEnd)}}</div>
        </div>
        <div class="stat-box">
          <div class="stat-subheader">Average Items Per Sale</div>
          <h2 style="padding-left: 12px">{{currentYearReport.averageItemsPerSale}}</h2>
          <div v-if="this.activePeriodButton !== `all`" class="sub-header stat-change">
            <vs-icon v-if="increaseFromLastYear(currentYearReport.averageItemsPerSale, lastYearReport.averageItemsPerSale) < 0" id="iconAverageSale" color="red" icon="arrow_drop_down" class="stat-change-icon"/> <!-- decrease icon -->
            <vs-icon v-else color="green" icon="arrow_drop_up" class="stat-change-icon"/> <!-- increase icon -->
            <div>{{increaseFromLastYear(currentYearReport.averageItemsPerSale, lastYearReport.averageItemsPerSale)}}% from last year</div>
          </div>
          <div class="sub-header stat-date"> {{this.formatDate(dateStart)}} - {{this.formatDate(dateEnd)}}</div>
        </div>
        <div class="stat-box">
          <div class="stat-subheader">Total Sale Value</div>
          <h2 style="padding-left: 12px">{{currency + currentYearReport.totalSaleValue}}</h2>
          <div v-if="this.activePeriodButton !== `all`" class="sub-header stat-change">
            <vs-icon v-if="increaseFromLastYear(currentYearReport.totalSaleValue, lastYearReport.totalSaleValue) < 0" id="iconAverageSale" color="red" icon="arrow_drop_down" class="stat-change-icon"/> <!-- decrease icon -->
            <vs-icon v-else color="green" icon="arrow_drop_up" class="stat-change-icon"/> <!-- increase icon -->
            <div>{{increaseFromLastYear(currentYearReport.totalSaleValue, lastYearReport.totalSaleValue)}}% from last year</div>
          </div>
          <div class="sub-header stat-date" style="padding-left: 12px;"> {{this.formatDate(dateStart)}} - {{this.formatDate(dateEnd)}}</div>
        </div>
        <div class="stat-box">
          <div class="stat-subheader">Total Items Sold</div>
          <h2 style="padding-left: 12px">{{currentYearReport.totalItems}}</h2>
          <div v-if="this.activePeriodButton !== `all`" class="sub-header stat-change">
            <vs-icon v-if="increaseFromLastYear(currentYearReport.totalItems, lastYearReport.totalItems) < 0" id="iconAverageSale" color="red" icon="arrow_drop_down" class="stat-change-icon"/> <!-- decrease icon -->
            <vs-icon v-else color="green" icon="arrow_drop_up" class="stat-change-icon"/> <!-- increase icon -->
            <div>{{increaseFromLastYear(currentYearReport.totalItems, lastYearReport.totalItems)}}% from last year</div>
          </div>
          <div class="sub-header stat-date" style="padding-left: 12px;"> {{this.formatDate(dateStart)}} - {{this.formatDate(dateEnd)}}</div>
        </div>
        <div class="stat-box">
          <div class="stat-subheader">Total Sales</div>
          <h2 style="padding-left: 12px">{{currentYearReport.totalSales}}</h2>
          <div v-if="this.activePeriodButton !== `all`" class="sub-header stat-change">
            <vs-icon v-if="increaseFromLastYear(currentYearReport.totalSales, lastYearReport.totalSales) < 0" id="iconAverageSale" color="red" icon="arrow_drop_down" class="stat-change-icon"/> <!-- decrease icon -->
            <vs-icon v-else color="green" icon="arrow_drop_up" class="stat-change-icon"/> <!-- increase icon -->
            <div>{{increaseFromLastYear(currentYearReport.totalSales, lastYearReport.totalSales)}}% from last year</div>
          </div>
          <div class="sub-header stat-date" style="padding-left: 12px;"> {{this.formatDate(dateStart)}} - {{this.formatDate(dateEnd)}}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "../Api";
import axios from "axios";
import {store} from "../store";
const moment = require('moment');

export default {
  name: "BusinessSalesReport",

  props: {
    businessId: {
      type: Number,
      default: store.actingAsBusinessId,
    }
  },

  data: function() {
    return {
      // Used to determine which setting is currently selected - prevents re-clicking, and highlights the active button.
      activePeriodButton: "",
      activeGranularityButton: "all",
      currency: "$",
      actingAsBusinessId: '',
      currentYearSalesHistory: {},
      business: [],
      salesHistory: [],
      dateStart: null,
      dateEnd: null,
      pickedStart: null,
      pickedEnd: null,
      dateGranularity: null,
      currentYearReport: {},
      lastYearReport: {},
      reportGranularity: null,
      errors: []
    }
  },


  mounted: function() {
    this.getSession();

    this.getSalesHistory();

    let currentDate = new Date();
    this.dateEnd = currentDate


  },

  methods: {
    /**
     * Updates period of report, does this by recalculating the report with a different start date
     * @param period string of the selected period.
     */
    onPeriodChange: function(period) {
      this.errors = [];
      this.activePeriodButton = period;// Changes the period button to be selected and disabled.
      switch (period) {
        case '1-d':
          this.updatePeriod(1 ,'day');
          break;
        case '1-w':
          this.updatePeriod(1 ,'week');
          break;
        case '1-m':
          this.updatePeriod(1 ,'month');
          break;
        case '6-m':
          this.updatePeriod(6 ,'month');
          break;
        case '1-y':
          this.updatePeriod(1 ,'year');
          break;
        case 'custom':
          if(this.validateDates(this.pickedStart, this.pickedEnd)) {
            this.dateStart = this.pickedStart;
            this.dateEnd = this.pickedEnd;
          }
          break;
        case 'all':
          this.dateEnd = new Date();
          this.dateStart = this.getEarliestDate();
      }

      this.updateSummary();
    },


    /**
     * Checks dates and if date is incorrect pushes error
     * @param startDate start date from input field
     * @param endDate end date from input field
     * @returns boolean, true is valid dates, false if invalid
     */
    validateDates: function(startDate, endDate) {
      if(startDate == null) {
        this.errors.push('no-start');
      }
      if (endDate == null) {
        this.errors.push('no-end');
      }
      if (moment(endDate).isBefore(moment(startDate))) {
        this.errors.push('end-before-begin');
      }
      if (moment(endDate).isBefore(moment('1970')) || moment(endDate).isAfter(moment(new Date()))) {
        this.errors.push('bad-end-date');
      }
      if (moment(startDate).isBefore(moment('1970')) || moment(startDate).isAfter(moment(new Date()))) {
        this.errors.push('bad-start-date');
      }

      if(this.errors.length > 0) {
        return false;
      }

      return true;

    },

    /**
     * Checks if error contains type
     * @param type either start or end, this will be used to filter errors to see if it contains relevant errors
     * @returns boolean, true if contains type, false otherwise
     */
    checkErrors: function(type) {
      return this.errors.filter(error => error.includes(type)).length > 0
    },


    /**
     * Returns appropriate error message depending on what is in errors
     * @param type either start or end, this will be used to show errors for start or end
     * @returns string containing appropriate error message
     */
    getError: function(type) {
      if (type == 'start') {
        if (this.errors.includes('no-start')) {
          return 'No start date'
        } else if (this.errors.includes('bad-start-date')) {
          return 'Start date must be before tomorrow and after 1970'
        }
      }

      if(type == 'end') {
        if (this.errors.includes('no-end')) {
          return 'No end date'
        } else if (this.errors.includes('bad-end-date')) {
          return 'End date must be before tomorrow and after 1970'
        } else if (this.errors.includes('end-before-begin')) {
          return 'End date must be after start date'
        }
      }
    },


    /**
     * updates period by altering dateStart
     * @param timeValue value of time to subtract from current time
     * @param unit unit of time to subtract from current time (days, months, etc.)
     */
    updatePeriod: function(timeValue, unit) {
      this.dateEnd = new Date();
      this.dateStart = moment(new Date()).subtract(timeValue, unit);
    },


    /**
     * Recomputes summary, used when date start and end changes
     */
    updateSummary: function() {
      this.calculateReport();
      this.onGranularityChange(this.activeGranularityButton);
    },

    /**
     * Helper method for getting earliest date from sales history, used with 'all' granularity as it
     * needs to know earliest date to get everything\
     * @returns earliest date from sales history
     */
    getEarliestDate: function() {
      let min = this.salesHistory[0].sold;
      for (let sale of this.salesHistory) {
        if (sale.sold < min) {
          min = sale.sold;
        }
      }
      return min;
    },


    /**
     * Helper method for getting latest date from sales history, used with 'year' granularity to figure out
     * when to stop the report
     * @returns latest date from sales history
     */
    getLatestDate: function() {
      let max = this.salesHistory[0].sold;
      for (let sale of this.salesHistory) {
        if (sale.sold > max) {
          max = sale.sold;
        }
      }
      return max;
    },



    /**
     * Something happens when this function is called. (todo: do something here).
     * @param period string of the selected granularity.
     */
    onGranularityChange: function(period) {
      let intervalDate = moment(new Date(this.dateStart))
      if (period === 'w') {
        //filter weeks
        this.dateGranularity =  intervalDate.endOf('isoWeek')
        this.granularity(this.dateGranularity, 7, 'days')
      } else if (period === 'm') {
        //filter month
        this.dateGranularity =  intervalDate.endOf('month')
        this.granularity(this.dateGranularity, 1, 'months')
      } else if (period === 'y') {
        //filter year
        this.dateGranularity =  intervalDate.add(1, 'years')
        this.granularity(this.dateGranularity, 1, 'years')
      }
      this.activeGranularityButton = period; // Changes the granularity button to be selected and disabled.
    },

    /**
     * filter granularity
     * @param intervalDate: the granularity date to filter by
     * @param amount
     * @param unit: selected granularity
     */
    granularity: function (intervalDate, amount, unit) {
      let startDate = moment(new Date(this.dateStart))
      let endDate = moment(new Date(this.dateEnd))
      let summary = []
      let finalSummary = []
      let currYear = moment(startDate).year();
      let latestYear = moment(this.getLatestDate()).year();


      if(unit == 'years') { // If unit is years, previous implementation was not working correctly with years
        while(currYear <= latestYear) {
          // Grab sales from current year and calculate summary from sales from the actual year
          summary = this.currentYearSalesHistory.filter(sale => moment(sale.sold).year() == currYear)
          finalSummary.push(this.calculateSummary(summary, String(currYear)))

          currYear += 1;
        }


      } else { // If unit is anything else
          while (startDate < endDate) {
            for (const sale of this.currentYearSalesHistory) {
              if (moment(sale.sold).isBetween(startDate, intervalDate, 'seconds', '[]')) {
                summary.push(sale)
              }
            }
            if (summary.length >= 1) {
              if (unit==='months') {
                finalSummary.push(this.calculateSummary(summary, startDate.format('MMMM YYYY')))
              } else if (unit==='days') {
                finalSummary.push(this.calculateSummary(summary, startDate.startOf('isoWeek').format('MMM DD') + " - " +
                    startDate.endOf('isoWeek').format('MMM DD YYYY')))
              }
              summary = []
            }
            startDate = startDate.add(amount, unit);
            if (unit == 'months') {
              intervalDate = startDate.clone().endOf('month');
              startDate = startDate.startOf('month')
            } else {
              intervalDate = startDate.clone().endOf('isoWeek');
              startDate = startDate.startOf('isoWeek');
            }
          }
      }

      this.reportGranularity = finalSummary
    },

    /**
     * Calculates the percentage increase from last year as a percentage
     * If last year had no sales, return 100% increase
     *
     * @param thisyear The current year's figure
     * @param lastyear The previous year's figure
     */
    increaseFromLastYear(thisYear, lastYear) {
      if (lastYear === 0) {
        return 100;
      }
      let percentage = (thisYear - lastYear) / lastYear*100;
      return Math.round(percentage)
      ;
    },

    /**
     * Calls getBusinessListingNotifications to populate the page's sales history
     */
    getSalesHistory: function () {
      api.getBusinessSales(store.actingAsBusinessId)
          .then((res) => {
            this.salesHistory = res.data
            this.dateStart = this.getEarliestDate();

            //only once we have obtained the data, calculate the variables
            this.calculateReport();
          })
          .catch(err => {
            this.$log.debug(err);
          });
    },

    formatDate: function(date) {
      return moment(new Date(date)).format('MMM DD, YYYY');
    },

    /**
     * Calls get session endpoint to get user country, if successful calls setCurrency ()
     */
    getSession: function() {
      api.checkSession()
          .then((response) => {
            this.setCurrency(response.data.homeAddress.country)
          })
          .catch(err => {
            this.$log.debug(err);
          });
    },
    /**
     * return the name of the business currently administering
     * also, sets the accting as business id to update the sales report
     */
    getBusinessName: function() {
      if (store.actingAsBusinessId !== this.actingAsBusinessId) {
        this.actingAsBusinessId = store.actingAsBusinessId;

        this.getSalesHistory();
      }

      return store.actingAsBusinessName;
    },

    /**
     * calculateReport
     * Populates the report data metrics
     * First separates the salesHistory into the selected period and that same period from the year before
     * Then it calculates the summary report from both
     *
     * Later these are used in the business metrics and % increases from last year
     */
    calculateReport: function() {
      let start = moment(new Date(this.dateStart));
      let end = moment(new Date(this.dateEnd));
      this.currentYearSalesHistory = this.salesHistory.filter(sale => moment(sale.sold).isBetween(start, end));
      start = start.subtract(1,'year');
      end = end.subtract(1,'year');
      let lastYearSalesHistory = this.salesHistory.filter(sale => moment(sale.sold).isBetween(start, end));
      this.currentYearReport = this.calculateSummary(this.currentYearSalesHistory, "Current Period's Report");
      this.lastYearReport = this.calculateSummary(lastYearSalesHistory, "Last period's Report");
    },

    /**
     * calculates the summary object given a list of sales in that period
     * @param salesHistory List of sales in the given period. ie All the sales in january
     * @param title Title of the summary ie "January 2020"
     */
    calculateSummary: function(salesHistory, title) {
      let summary= {};
      let totalPrice = 0;
      let totalQuantity = 0;
      summary.title = title
      if(salesHistory.length > 0) {
        for(let i=0;i<salesHistory.length;i++) {
          totalPrice += salesHistory[i].price;
          totalQuantity += salesHistory[i].quantity;
        }
        summary.averageSale = Number(totalPrice/salesHistory.length).toFixed(2);
        summary.averagePricePerItem = Number(totalPrice / totalQuantity).toFixed(2);
        summary.averageItemsPerSale = Number(totalQuantity / salesHistory.length).toFixed(2);
        summary.totalSaleValue = Number(totalPrice).toFixed(2);
        summary.totalItems = Number(totalQuantity).toFixed(2);
        summary.totalSales = salesHistory.length;

      } else {
        summary.averageSale = 0.00;
        summary.averagePricePerItem = 0.00;
        summary.averageItemsPerSale = 0;
        summary.totalSaleValue = 0.00;
        summary.totalItems = 0.00;
        summary.totalSales = 0;
      }
      return summary;
    },

    /**
     * Sets display currency based on the user's home country.
     */
    setCurrency: function (country) {
      axios.get(`https://restcountries.com/v2/name/${country}`)
          .then(response => {
            this.currency = response.data[0].currencies[0].symbol;
          })
          .catch(err => {
            this.$log.debug(err);
          });
    },
  },
}
</script>

<style scoped>

#report-container {
  background-color: white;
  width: 100%;
  margin: 1em auto;
}

#header-container {
  display: flex;
  margin: auto;
  padding-bottom: 0.5em;
  padding-top: 1em;
}

#title {
  font-size: 30px;
  margin-top: 4px;
  margin-right: 8px;
}
#title-business {
  font-size: 30px;
  font-weight: bold;
  margin-top: 4px;
  margin-left: 0px;
  margin-right: auto;
}

#content-container {
  display: grid;
  grid-template-columns: 1fr 3fr auto;
  grid-column-gap: 1em;
  justify-content: space-around;

}

/* === OPTIONS CONTAINER === */
#options-container {
  width: fit-content;
  height: fit-content;
}

#options-container >>> .vs-card--content {
  display: grid;
  grid-template-columns: 150px 4px 150px;
  grid-gap: 1em 2px;
  padding-bottom: 16px;
}

.options-header {
  grid-column: 1/4;
  text-align: center;
  font-weight: 500;
  font-size: 18px;
}

.options-button {
  width: 100px;
  margin: auto;
}

.active-button {
  background-color: rgb(31,116,255)!important;
  color: white;
  pointer-events: none;
}

.date-input {
  width: 136px;
  margin: auto;
  height: 100px;
}


/* MONTH/WEEK/YEAR REPORT CONTAINER */
#summary-container {
  height: 475px;
  overflow-y: scroll;
}

.row-summary-container {
  display: grid;
  grid-template-columns: 2fr 1fr 2fr 1fr;
  grid-column-gap: 1em;
  padding-bottom: 1em;
}

.summary-header {
  grid-column: 1/5;
}

.summary-subheader {
  font-size: 12px;
  text-align: right;
  color: gray;
}

/* STATS CONTAINER */
#stats-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-column-gap: 1em;
  grid-row-gap: 1em;
  height: fit-content;
}

.stat-box {
  border: 1px solid rgba(0, 0, 0, 0.16);
  border-radius: 12px;
  height: fit-content;
  width: fit-content;
  padding: 12px 12px 12px 0;

  display: flex;
  flex-direction: column;
}

.stat-change {
  display: inline-flex;
  padding-left: 8px;
}

.stat-change-icon {
  margin: auto 0;
  font-size: 20px;
}

.stat-subheader {
  font-weight: bold;
  font-size: 12px;
  padding-left: 12px;
}

.stat-date {
  padding-left: 12px;
  font-size: 10px;
}

@media screen and (max-width: 1600px) {
  #stats-container {
    grid-template-columns: 1fr;
  }
}

@media screen and (max-width: 1310px) {
  #content-container {
    grid-template-columns: auto 2fr;
  }

  #stats-container {
    grid-column: 1/3;
    grid-template-columns: repeat(auto-fit, 160px);
    justify-content: space-around;
  }
}

@media screen and (max-width: 975px) {
  #content-container {
    grid-template-columns: 1fr;
    grid-row-gap: 1em;
  }

  #options-container {
    margin: 0 auto;
  }

  #summary-container{
    grid-column: 1;
  }

  #stats-container{
    grid-column: 1;
  }
}

</style>
