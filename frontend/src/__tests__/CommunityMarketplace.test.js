import { mount, createLocalVue } from '@vue/test-utils';
import CommunityMarketplace from '../components/CommunityMarketplace.vue';

import Vuesax from 'vuesax';
import api from "../Api";

let wrapper;
let localVue = createLocalVue();
localVue.use(Vuesax);


// will get jest specific errors if there is no full user object
let cards = {
    "content": [
        {
            "id": 9028,
            "user": {
                "id": 7538,
                "firstName": "Audre",
                "middleName": "Romain",
                "lastName": "Tower",
                "nickname": "pricing structure",
                "bio": "Aenean auctor gravida sem. Praesent id massa id nisl venenatis lacinia.",
                "email": "rtowered@google.cn",
                "dateOfBirth": "1994-01-25",
                "phoneNumber": "+51 299 332 0265",
                "homeAddress": {
                    "streetNumber": null,
                    "streetName": null,
                    "suburb": null,
                    "city": null,
                    "region": null,
                    "country": "Russia",
                    "postcode": null
                },
                "created": "2020-09-14 06:35:19",
                "role": "USER",
                "businessesAdministered": []
            },
            "title": "Pastry - Choclate Baked",
            "description": "Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo.",
            "created": "2021-05-13 18:47:50",
            "displayPeriodEnd": "2021-06-25 18:47:50",
            "keywords": "congue risus semper",
            "section": "ForSale"
        },
        {
            "id": 9072,
            "user": {
                "id": 9584,
                "firstName": "Ragnar",
                "middleName": "Mariel",
                "lastName": "Northfield",
                "nickname": "Team-oriented",
                "bio": "Nulla ac enim.",
                "email": "mnorthfieldfn@weather.com",
                "dateOfBirth": "1981-05-22",
                "phoneNumber": "+86 256 685 8481",
                "homeAddress": {
                    "streetNumber": "7091",
                    "streetName": "Quincy",
                    "suburb": null,
                    "city": "Al QadmÅ«s",
                    "region": null,
                    "country": "Syria",
                    "postcode": null
                },
                "created": "2019-07-24 05:50:57",
                "role": "USER",
                "businessesAdministered": []
            },
            "title": "Truffle Shells - Semi - Sweet",
            "description": "Aliquam quis turpis eget elit sodales scelerisque.",
            "created": "2021-05-13 18:47:50",
            "displayPeriodEnd": "2021-06-22 18:47:50",
            "keywords": "hac",
            "section": "ForSale"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "offset": 0,
        "pageSize": 2,
        "pageNumber": 0,
        "unpaged": false,
        "paged": true
    },
    "last": false,
    "totalElements": 3300,
    "totalPages": 1650,
    "size": 2,
    "number": 0,
    "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
    },
    "first": true,
    "numberOfElements": 2,
    "empty": false
}

jest.mock("../Api.js", () => jest.fn);
api.getCardsBySection = jest.fn(() => {
    return Promise.resolve({data: cards, status: 200});
});

api.checkSession = jest.fn(() => {
    return Promise.resolve({status: 200});
});

let $vs = {
    loading: jest.fn(),
}

beforeEach(() => {
    wrapper = mount(CommunityMarketplace, {
        propsData: {},
        mocks: {$vs},
        stubs: ['router-link', 'router-view', 'CardModal'],
        methods: {},
        localVue,
    });

    wrapper.vm.$vs.loading.close = jest.fn();
});

afterEach(() => {
    wrapper.destroy();
});


describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

describe('CommunityMarketplace toggle tests', () => {
    beforeEach(() => {
        wrapper.vm.displaytype = false;
    });

    test('Grid view is shown at first', () => {
        const gridContainer = wrapper.find("#grid-container")
        expect(gridContainer.exists()).toBe(true);
    });

    test('Table view is shown when slider toggled', async () => {
        const displayTypeButton = wrapper.find("#list-button");
        displayTypeButton.trigger("click");
        await wrapper.vm.$nextTick();

        const tableContainer = wrapper.find("#tableContainer");
        expect(tableContainer.exists()).toBe(true);
    });
});

describe('Method tests', () => {
   test('Cards is successfully set.', async () => {
       await wrapper.vm.getSectionCards('forSale');
       expect(wrapper.vm.cards).toStrictEqual(cards.content);
   });

   test('Switching tab to Wanted', () => {
       expect(wrapper.vm.tabIndex).toStrictEqual(0);
       wrapper.vm.onSuccess("Wanted");
       expect(wrapper.vm.tabIndex).toStrictEqual(1);
   });

   test('Switching tab to Exchange', () => {
        expect(wrapper.vm.tabIndex).toStrictEqual(0);
        wrapper.vm.onSuccess("Exchange");
        expect(wrapper.vm.tabIndex).toStrictEqual(2);
    });


    test('Switching tab to non-existing tab', () => {
        expect(wrapper.vm.tabIndex).toStrictEqual(0);
        wrapper.vm.onSuccess("Bababooey");
        expect(wrapper.vm.tabIndex).toStrictEqual(0);
    });
});