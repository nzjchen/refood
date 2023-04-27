import {mount, createLocalVue } from '@vue/test-utils';
import BusinessSalesHistory from '../components/BusinessSalesHistory.vue';
import Vuesax from 'vuesax';
import api from "../Api";
import axios from "axios";

let wrapper;
let localVue = createLocalVue();
localVue.use(Vuesax);

const mockUser = {
    "id": 5,
    "homeAddress": "44 Ramsey Court",
}

const mockSale = [    {
        "id": 1,
        "buyer": 3,
        "product": {
            "id": "W04GP5EC0B1798680",
            "business": {
                "name": "Dabshots",
                "id": 1,
                "administrators": [],
                "primaryAdministratorId": null,
                "description": "Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.",
                "address": {
                    "streetNumber": "0",
                    "streetName": "Vernon Place",
                    "suburb": null,
                    "city": "Sarpang",
                    "region": null,
                    "country": "Bhutan",
                    "postcode": null
                },
                "businessType": "Charitable organisation",
                "created": "2020-05-18 09:06:11"
            },
            "name": "Compound - Mocha",
            "description": "vel ipsum praesent blandit lacinia erat vestibulum sed magna at nunc",
            "manufacturer": "Nestle",
            "recommendedRetailPrice": 88.93,
            "created": "2021-01-11 07:54:46",
            "images": [],
            "primaryImagePath": null
        },
        "sold": "2021-09-16 13:05:28",
        "listed": "2021-02-03 11:00:00",
        "likes": 0,
        "quantity": 2,
        "listingId": 5,
        "price": 5.99
    },
    {
        "id": 2,
        "buyer": 3,
        "product": {
            "id": "W04GP5EC0B1798680",
            "business": {
                "name": "Dabshots",
                "id": 1,
                "administrators": [],
                "primaryAdministratorId": null,
                "description": "Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.",
                "address": {
                    "streetNumber": "0",
                    "streetName": "Vernon Place",
                    "suburb": null,
                    "city": "Sarpang",
                    "region": null,
                    "country": "Bhutan",
                    "postcode": null
                },
                "businessType": "Charitable organisation",
                "created": "2020-05-18 09:06:11"
            },
            "name": "Compound - Mocha",
            "description": "vel ipsum praesent blandit lacinia erat vestibulum sed magna at nunc",
            "manufacturer": "Nestle",
            "recommendedRetailPrice": 88.93,
            "created": "2021-01-11 07:54:46",
            "images": [],
            "primaryImagePath": null
        },
        "sold": "2021-09-16 13:05:37",
        "listed": "2021-02-02 11:00:00",
        "likes": 0,
        "quantity": 1,
        "listingId": 4,
        "price": 15.5
    }];

let $route = {
    params: {
        businessId: 1,
        listingId: 1,
    }
}

jest.mock("../Api.js", () => jest.fn);
api.checkSession = jest.fn(() => {
    return Promise.resolve({data: mockUser, status: 200});
});

api.getBusinessSales = jest.fn(() => {
    return Promise.resolve({data: mockSale, status: 200}).catch({response: {message: "Bad request", status: 400}});
});

axios.get = jest.fn(() => {
    return Promise.resolve({data: [{
            currencies: [{symbol: "€"}],
        }]}
    );
});

beforeEach(() => {
    wrapper = mount(BusinessSalesHistory, {
       mocks: {$route},
       stubs: {},
       methods: {},
       localVue,
    });
    const setNotifications = jest.spyOn(BusinessSalesHistory.methods, 'getSalesHistory');
    setNotifications.mockImplementation(() => {
        wrapper.vm.notifications = [];
    });
})

afterEach(() => {
    wrapper.destroy();
});

describe('Method Tests', () => {
    test("Session is checked and currency is set to euros", async () => {
       await wrapper.vm.getSession();

       expect(wrapper.vm.currency).toBe("€");
    });
});
