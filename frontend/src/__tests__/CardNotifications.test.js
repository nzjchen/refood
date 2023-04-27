import {mount, createLocalVue } from '@vue/test-utils';
import CardNotifications from '../components/CardNotifications.vue';
import Vuesax from 'vuesax';
import api from "../Api";
import { store } from "../store";

let wrapper;
let localVue = createLocalVue();
localVue.use(Vuesax);

let $route = {
    params: {
        id: 1,
    }
}

let $vs = {
    notify: jest.fn()
}

let notificationData =[
    {"id":1105,"userId":1,"cardId":10001,"title":"Plastic Wrap","displayPeriodEnd":1626762673875,"status":"Expired"},
    {"id":1106,"userId":1,"cardId":10002,"title":"Plastic Wrap","displayPeriodEnd":1626762679077,"status":"Deleted"},
    {"id": 2, "user": { "id": 1, "firstName": "Wilma", "middleName": "Janet", "lastName": "Sails", "nickname": "Open-architected", "bio": "Profit-focused scalable moratorium", "email": "jsails0@go.com", "dateOfBirth": "1989-02-28", "phoneNumber": "+57 242 190 0153", "homeAddress": { "streetNumber": "44", "streetName": "Menomonie Way", "suburb": null, "city": "Zhashkiv", "region": null, "country": "Ukraine", "postcode": null }, "created": "2020-08-06 23:35:52", "role": "USER", "businessesAdministered": [ { "name": "Dabshots", "id": 1, "administrators": [ 1 ], "primaryAdministratorId": 1, "description": "Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.", "address": { "streetNumber": "0", "streetName": "Vernon Place", "suburb": null, "city": "Sarpang", "region": null, "country": "Bhutan", "postcode": null }, "businessType": "Charitable organisation", "created": "2020-05-18 09:06:11" }, { "name": "Layo", "id": 2, "administrators": [ { "id": 2, "firstName": "Karrie", "middleName": "Salvatore", "lastName": "Loyley", "nickname": "local area network", "bio": "Quality-focused next generation synergy", "email": "sloyley1@wordpress.com", "dateOfBirth": "1995-11-06", "phoneNumber": "+48 864 927 4819", "homeAddress": { "streetNumber": "8120", "streetName": "GroverJunction", "suburb": null, "city": "Cimarelang", "region": null, "country": "Indonesia", "postcode": null }, "created": "2020-05-01 01:25:12", "role": "USER", "businessesAdministered": [ "Layo" ] }, { "id": 9, "firstName": "Ruthe", "middleName": "Ogdan", "lastName": "Ruit", "nickname": "Open-architected", "bio": "Configurable coherent capacity", "email": "oruit8@reddit.com", "dateOfBirth": "1995-05-10", "phoneNumber": "+62 283 517 0351", "homeAddress": { "streetNumber": "9885", "streetName": "Oak Parkway", "suburb": null, "city": "Ambato", "region": null, "country": "Ecuador", "postcode": null }, "created": "2020-09-24 18:01:34", "role": "USER", "businessesAdministered": [ { "name": "Grain n Simple", "id": 5, "administrators": [ { "id": 5, "firstName": "Shermy", "middleName": "Pearle", "lastName": "Layborn", "nickname": "artificial intelligence", "bio": "Intuitive client-server standardization", "email": "playborn4@amazon.com", "dateOfBirth": "2000-12-30", "phoneNumber": "+62 527 277 7359", "homeAddress": { "streetNumber": "5", "streetName": "Hagan Avenue", "suburb": null, "city": "Chengdong", "region": null, "country": "China", "postcode": null }, "created": "2020-10-17 00:47:11", "role": "USER", "businessesAdministered": [ "Grain n Simple" ] }, 9, { "id": 8, "firstName": "Elysee", "middleName": "Maurene", "lastName": "Took", "nickname": "benchmark", "bio": "Team-oriented interactive installation", "email": "mtook7@chron.com", "dateOfBirth": "1985-11-09", "phoneNumber": "+234 186 824 2303", "homeAddress": { "streetNumber": "5", "streetName": "Utah Terrace", "suburb": null, "city": "Huangfang", "region": null, "country": "China", "postcode": null }, "created": "2020-10-31 22:39:42", "role": "USER", "businessesAdministered": [ "Grain n Simple" ] } ], "primaryAdministratorId": 5, "description": "Praesent blandit. Nam nulla.", "address": { "streetNumber": "302", "streetName": "Forest Run Place", "suburb": null, "city": "Putinci", "region": null, "country": "Serbia", "postcode": null }, "businessType": "Retail Trade", "created": "2020-05-22 20:21:22" }, "Layo" ] }, 1 ], "primaryAdministratorId": 2, "description": "Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla. Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.", "address": { "streetNumber": "95403", "streetName": "Hanover Park", "suburb": null, "city": "El Guapinol", "region": null, "country": "Honduras", "postcode": null }, "businessType": "Accommodation and Food Services", "created": "2020-08-25 10:22:19" }, { "name": "Skinder", "id": 3, "administrators": [ { "id": 3, "firstName": "Felice", "middleName": "Tabbitha", "lastName": "Jaeggi", "nickname": "intranet", "bio": "Managed foreground budgetary management", "email": "tjaeggi2@independent.co.uk", "dateOfBirth": "1976-12-06", "phoneNumber": "+1 659 270 1003", "homeAddress": { "streetNumber": "5305", "streetName": "Melrose Drive", "suburb": null, "city": "Sidon", "region": null, "country": "Lebanon", "postcode": null }, "created": "2020-12-24 17:40:59", "role": "USER", "businessesAdministered": [ "Skinder" ] }, { "id": 7, "firstName": "Papageno", "middleName": "Batholomew", "lastName": "Dolton", "nickname": "Persevering", "bio": "Advanced bi-directional flexibility", "email": "bdolton6@liveinternet.ru", "dateOfBirth": "2000-07-12", "phoneNumber": "+380 428 944 6622", "homeAddress": { "streetNumber": "01801", "streetName": "Grasskamp Lane", "suburb": null, "city": "Malhou", "region": "Santarém", "country": "Portugal", "postcode": "2380-506" }, "created": "2020-07-21 03:54:32", "role": "USER", "businessesAdministered": [ "Skinder" ] }, 1 ], "primaryAdministratorId": 3, "description": "Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.", "address": { "streetNumber": "6794", "streetName": "Jackson Way", "suburb": null, "city": "Xialaba", "region": null, "country": "China", "postcode": null }, "businessType": "Retail Trade", "created": "2020-09-11 08:50:50" } ] }, "boughtListing": { "id": 1, "buyer": 7, "product": { "id": "WAUVT64B54N722288", "business": { "name": "Dabshots", "id": 1, "administrators": [], "primaryAdministratorId": null, "description": "Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.", "address": { "streetNumber": "0", "streetName": "Vernon Place", "suburb": null, "city": "Sarpang", "region": null, "country": "Bhutan", "postcode": null }, "businessType": "Charitable organisation", "created": "2020-05-18 09:06:11" }, "name": "Pastry - Cheese Baked Scones", "description": "amet erat nulla tempus vivamus", "manufacturer": "Watties", "recommendedRetailPrice": 19.88, "created": "2021-03-05 01:36:54", "images": [], "primaryImagePath": null }, "sold": "2021-08-17T03:24:12", "listed": "2021-01-26T23:00:00", "likes": 1, "quantity": 3, "listingId": 1 }, "listing": null, "status": "Bought", "created": "2021-08-17 03:24:12" }
]

let $log = {
    debug: jest.fn(),
}



beforeEach(() => {
    wrapper = mount(CardNotifications, {
        propsData: {
},
        mocks: {$route, $log, store, $vs},
        stubs: {},
        methods: {},
        localVue,
    })

    wrapper.vm.notifications = notificationData;

});

afterEach(() => {
    wrapper.destroy();
});

describe('Card modal functionality', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('If notifications exist list is shown', async () => {
        let button = wrapper.find('#dropdownButton');

        button.trigger('click')

        await wrapper.vm.$nextTick();

        expect(wrapper.find("#cardList").exists()).toBeTruthy();
    })

    test('If no notifications exist list is not shown', async () => {
        wrapper.vm.notifications = [];


        let button = wrapper.find('#dropdownButton');
        button.trigger('click')

        await wrapper.vm.$nextTick();
        expect(wrapper.find('#noCards').exists()).toBeTruthy();
    })
});

describe('Notification checks', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('When a notification is from a listing another user bought, validPurchaseNotification returns true', () => {
        wrapper.vm.userId = 1;
        let res = wrapper.vm.validPurchaseNotification(notificationData[2]);
        expect(res).toBeTruthy();
    });
    test('When a notification is from a listing the current user bought, validPurchaseNotification returns false', () => {
        wrapper.vm.userId = 7;
        let res = wrapper.vm.validPurchaseNotification(notificationData[2]);
        expect(res).toBeFalsy();
    });

    test('When a card is extended (by notification), the user is notified with an appropriate message and notifications are refreshed', async() => {
        api.extendCardDisplayPeriod = jest.fn(() => {
            return Promise.resolve({status: 201, data: {messageId: 1}});
        });
        wrapper.vm.getNotifications = jest.fn();

        wrapper.vm.extendCard(1, "Test Card");

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.getNotifications).toBeCalled();
    });

    test('When a card is NOT extended (by notification), the user is notified with an appropriate message', async() => {
        api.extendCardDisplayPeriod = jest.fn(() => {
            return Promise.reject({response: {message:"Error", status: 500}});
        });
        wrapper.vm.getNotifications = jest.fn();

        wrapper.vm.extendCard(1, "Test Card");

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$vs.notify).toBeCalled();
    });


    test('When a card is deleted (by notification), the user is notified with an appropriate message and notifications are refreshed', async() => {
        api.deleteCard = jest.fn(() => {
            return Promise.resolve({status: 201, data: {messageId: 1}});
        });
        wrapper.vm.getNotifications = jest.fn();

        wrapper.vm.deleteCard(1, "Test Card");

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.getNotifications).toBeCalled();
    });

    test('When a card is NOT deleted (by notification), the user is notified with an appropriate message', async() => {
        api.deleteCard = jest.fn(() => {
            return Promise.reject({status: 500, data: {messageId: 1}});
        });
        wrapper.vm.getNotifications = jest.fn();

        wrapper.vm.deleteCard(1, "Test Card");

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$vs.notify).toBeCalled();
    });
});