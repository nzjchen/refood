/**
 * Functions common the MaretplaceGrid and MarketplaeTable
 * Placed here to avoid code duplication
 */

import api from "../Api";

export default {

    /**
     * Converts the space separated keywords to a JSON object recognized by the keywordWrapper
     *
     * @param keywords  space separated keywords
     * @returns {[]}    JSON object
     */

    getKeywords: function(keywords) {
        keywords = keywords.trimEnd().split(/\s+/);
        let tmpKeywords = [];

        for(let i=0;i<keywords.length;i++) {
            let keyword = {};
            keyword.key = i;
            keyword.name = keywords[i];
            tmpKeywords.push(keyword);
        }
        return tmpKeywords;
    },

    /**
     * Translates the user's address into a general address string, listing only the country, city and suburb.\
     *
     * @param homeAddress   User's home address
     * @returns String      General address string
     */

    getGeneralAddress: function (homeAddress) {
        let addressStr = "";
        if(homeAddress.country) {
            addressStr += homeAddress.country;
        }

        if(homeAddress.city) {
            addressStr += ", " + homeAddress.city;
        }

        if(homeAddress.suburb) {
            addressStr += ", " + homeAddress.suburb;
        }

        return addressStr
    },

    /**
     * Checks the data integrity of the list of cards
     * ie every card includes a user
     * if not, it will get one from the database and update the card list
     *
     * assumes the user id attribute is not null.
     *
     */
    checkCardList: function(cards) {
        cards.forEach(function (card, index) {
            if (!card.user.firstName) {
                api.getUserFromID(card.user)
                    .then((response) => {
                        cards[index].user = response.data;
                    }).catch((err) => {
                    if (err.response.status === 406) {
                        this.$vs.notify({title:'User not found: '+card.user, text:'This user does not exist.', color:'danger'});
                    }
                    throw new Error(`Error trying to get user info from id: ${err}`);
                });

            }
        });
        return cards;
    }
}