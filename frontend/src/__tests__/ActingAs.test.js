import {createLocalVue, mount, shallowMount} from '@vue/test-utils';
import ActingAs from '../components/ActingAs';
import Vuesax from 'vuesax';
import api from "../Api";

const localVue = createLocalVue();
let wrapper;
localVue.use(Vuesax);

let business = {
    "name": "Dabshots",
    "id": 1,
    "administrators": [
        {
            "id": 1,
            "firstName": "Wilma",
            "middleName": "Janet",
            "lastName": "Sails",
            "nickname": "Open-architected",
            "bio": "Profit-focused scalable moratorium",
            "email": "jsails0@go.com",
            "dateOfBirth": "1989-02-28",
            "phoneNumber": "+57 242 190 0153",
            "homeAddress": {
                "streetNumber": "44",
                "streetName": "Menomonie Way",
                "suburb": null,
                "city": "Zhashkiv",
                "region": null,
                "country": "Ukraine",
                "postcode": null
            },
            "created": "2020-08-06 23:35:52",
            "role": "USER",
            "businessesAdministered": [
                "Dabshots",
                {
                    "name": "Layo",
                    "id": 2,
                    "administrators": [
                        {
                            "id": 2,
                            "firstName": "Karrie",
                            "middleName": "Salvatore",
                            "lastName": "Loyley",
                            "nickname": "local area network",
                            "bio": "Quality-focused next generation synergy",
                            "email": "sloyley1@wordpress.com",
                            "dateOfBirth": "1995-11-06",
                            "phoneNumber": "+48 864 927 4819",
                            "homeAddress": {
                                "streetNumber": "8120",
                                "streetName": "GroverJunction",
                                "suburb": null,
                                "city": "Cimarelang",
                                "region": null,
                                "country": "Indonesia",
                                "postcode": null
                            },
                            "created": "2020-05-01 01:25:12",
                            "role": "USER",
                            "businessesAdministered": [
                                "Layo"
                            ],
                            "images": [],
                            "primaryImagePath": null,
                            "primaryThumbnailPath": null
                        },
                        {
                            "id": 9,
                            "firstName": "Ruthe",
                            "middleName": "Ogdan",
                            "lastName": "Ruit",
                            "nickname": "Open-architected",
                            "bio": "Configurable coherent capacity",
                            "email": "oruit8@reddit.com",
                            "dateOfBirth": "1995-05-10",
                            "phoneNumber": "+62 283 517 0351",
                            "homeAddress": {
                                "streetNumber": "9885",
                                "streetName": "Oak Parkway",
                                "suburb": null,
                                "city": "Ambato",
                                "region": null,
                                "country": "Ecuador",
                                "postcode": null
                            },
                            "created": "2020-09-24 18:01:34",
                            "role": "USER",
                            "businessesAdministered": [
                                {
                                    "name": "Grain n Simple",
                                    "id": 5,
                                    "administrators": [
                                        9,
                                        {
                                            "id": 5,
                                            "firstName": "Shermy",
                                            "middleName": "Pearle",
                                            "lastName": "Layborn",
                                            "nickname": "artificial intelligence",
                                            "bio": "Intuitive client-server standardization",
                                            "email": "playborn4@amazon.com",
                                            "dateOfBirth": "2000-12-30",
                                            "phoneNumber": "+62 527 277 7359",
                                            "homeAddress": {
                                                "streetNumber": "5",
                                                "streetName": "Hagan Avenue",
                                                "suburb": null,
                                                "city": "Chengdong",
                                                "region": null,
                                                "country": "China",
                                                "postcode": null
                                            },
                                            "created": "2020-10-17 00:47:11",
                                            "role": "USER",
                                            "businessesAdministered": [
                                                "Grain n Simple"
                                            ],
                                            "images": [],
                                            "primaryImagePath": null,
                                            "primaryThumbnailPath": null
                                        },
                                        {
                                            "id": 8,
                                            "firstName": "Elysee",
                                            "middleName": "Maurene",
                                            "lastName": "Took",
                                            "nickname": "benchmark",
                                            "bio": "Team-oriented interactive installation",
                                            "email": "mtook7@chron.com",
                                            "dateOfBirth": "1985-11-09",
                                            "phoneNumber": "+234 186 824 2303",
                                            "homeAddress": {
                                                "streetNumber": "5",
                                                "streetName": "Utah Terrace",
                                                "suburb": null,
                                                "city": "Huangfang",
                                                "region": null,
                                                "country": "China",
                                                "postcode": null
                                            },
                                            "created": "2020-10-31 22:39:42",
                                            "role": "USER",
                                            "businessesAdministered": [
                                                "Grain n Simple"
                                            ],
                                            "images": [],
                                            "primaryImagePath": null,
                                            "primaryThumbnailPath": null
                                        }
                                    ],
                                    "primaryAdministratorId": 5,
                                    "description": "Praesent blandit. Nam nulla.",
                                    "address": {
                                        "streetNumber": "302",
                                        "streetName": "Forest Run Place",
                                        "suburb": null,
                                        "city": "Putinci",
                                        "region": null,
                                        "country": "Serbia",
                                        "postcode": null
                                    },
                                    "businessType": "Retail Trade",
                                    "created": "2020-05-22 20:21:22",
                                    "images": [],
                                    "primaryImagePath": null,
                                    "primaryThumbnailPath": null
                                },
                                "Layo"
                            ],
                            "images": [],
                            "primaryImagePath": null,
                            "primaryThumbnailPath": null
                        },
                        1
                    ],
                    "primaryAdministratorId": 2,
                    "description": "Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla. Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.",
                    "address": {
                        "streetNumber": "95403",
                        "streetName": "Hanover Park",
                        "suburb": null,
                        "city": "El Guapinol",
                        "region": null,
                        "country": "Honduras",
                        "postcode": null
                    },
                    "businessType": "Accommodation and Food Services",
                    "created": "2020-08-25 10:22:19",
                    "images": [],
                    "primaryImagePath": null,
                    "primaryThumbnailPath": null
                },
                {
                    "name": "Skinder",
                    "id": 3,
                    "administrators": [
                        {
                            "id": 7,
                            "firstName": "Papageno",
                            "middleName": "Batholomew",
                            "lastName": "Dolton",
                            "nickname": "Persevering",
                            "bio": "Advanced bi-directional flexibility",
                            "email": "bdolton6@liveinternet.ru",
                            "dateOfBirth": "2000-07-12",
                            "phoneNumber": "+380 428 944 6622",
                            "homeAddress": {
                                "streetNumber": "01801",
                                "streetName": "Grasskamp Lane",
                                "suburb": null,
                                "city": "Malhou",
                                "region": "Santarém",
                                "country": "Portugal",
                                "postcode": "2380-506"
                            },
                            "created": "2020-07-21 03:54:32",
                            "role": "USER",
                            "businessesAdministered": [
                                "Skinder"
                            ],
                            "images": [],
                            "primaryImagePath": null,
                            "primaryThumbnailPath": null
                        },
                        {
                            "id": 3,
                            "firstName": "Felice",
                            "middleName": "Tabbitha",
                            "lastName": "Jaeggi",
                            "nickname": "intranet",
                            "bio": "Managed foreground budgetary management",
                            "email": "tjaeggi2@independent.co.uk",
                            "dateOfBirth": "1976-12-06",
                            "phoneNumber": "+1 659 270 1003",
                            "homeAddress": {
                                "streetNumber": "5305",
                                "streetName": "Melrose Drive",
                                "suburb": null,
                                "city": "Sidon",
                                "region": null,
                                "country": "Lebanon",
                                "postcode": null
                            },
                            "created": "2020-12-24 17:40:59",
                            "role": "USER",
                            "businessesAdministered": [
                                "Skinder"
                            ],
                            "images": [],
                            "primaryImagePath": null,
                            "primaryThumbnailPath": null
                        },
                        1
                    ],
                    "primaryAdministratorId": 3,
                    "description": "Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.",
                    "address": {
                        "streetNumber": "6794",
                        "streetName": "Jackson Way",
                        "suburb": null,
                        "city": "Xialaba",
                        "region": null,
                        "country": "China",
                        "postcode": null
                    },
                    "businessType": "Retail Trade",
                    "created": "2020-09-11 08:50:50",
                    "images": [],
                    "primaryImagePath": null,
                    "primaryThumbnailPath": null
                }
            ],
            "images": [
                {
                    "id": "3",
                    "thumbnailFilename": "user_1/3_thumbnail.png",
                    "name": "Screen Shot 2021-06-07 at 11.52.28 AM.png",
                    "fileName": "user_1/3.png"
                }
            ],
            "primaryImagePath": "user_1/3.png",
            "primaryThumbnailPath": "user_1/3_thumbnail.png"
        }
    ],
    "primaryAdministratorId": 1,
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
    "created": "2020-05-18 09:06:11",
    "images": [
        {
            "id": "19",
            "thumbnailFilename": "business_1/19_thumbnail.png",
            "name": "Screen Shot 2021-06-03 at 8.24.58 PM.png",
            "fileName": "business_1/19.png"
        }
    ],
    "primaryImagePath": "business_1/19.png",
    "primaryThumbnailPath": "business_1/19_thumbnail.png"
}


let user = {
    "id": 1,
    "firstName": "Wilma",
    "middleName": "Janet",
    "lastName": "Sails",
    "nickname": "Open-architected",
    "bio": "Profit-focused scalable moratorium",
    "email": "jsails0@go.com",
    "dateOfBirth": "1989-02-28",
    "phoneNumber": "+57 242 190 0153",
    "homeAddress": {
        "streetNumber": "44",
        "streetName": "Menomonie Way",
        "suburb": null,
        "city": "Zhashkiv",
        "region": null,
        "country": "Ukraine",
        "postcode": null
    },
    "created": "2020-08-06 23:35:52",
    "role": "USER",
    "businessesAdministered": [
        {
            "name": "Dabshots",
            "id": 1,
            "administrators": [
                1
            ],
            "primaryAdministratorId": 1,
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
            "created": "2020-05-18 09:06:11",
            "images": [
                {
                    "id": "19",
                    "thumbnailFilename": "business_1/19_thumbnail.png",
                    "name": "Screen Shot 2021-06-03 at 8.24.58 PM.png",
                    "fileName": "business_1/19.png"
                }
            ],
            "primaryImagePath": "business_1/19.png",
            "primaryThumbnailPath": "business_1/19_thumbnail.png"
        },
        {
            "name": "Layo",
            "id": 2,
            "administrators": [
                1,
                {
                    "id": 9,
                    "firstName": "Ruthe",
                    "middleName": "Ogdan",
                    "lastName": "Ruit",
                    "nickname": "Open-architected",
                    "bio": "Configurable coherent capacity",
                    "email": "oruit8@reddit.com",
                    "dateOfBirth": "1995-05-10",
                    "phoneNumber": "+62 283 517 0351",
                    "homeAddress": {
                        "streetNumber": "9885",
                        "streetName": "Oak Parkway",
                        "suburb": null,
                        "city": "Ambato",
                        "region": null,
                        "country": "Ecuador",
                        "postcode": null
                    },
                    "created": "2020-09-24 18:01:34",
                    "role": "USER",
                    "businessesAdministered": [
                        {
                            "name": "Grain n Simple",
                            "id": 5,
                            "administrators": [
                                9,
                                {
                                    "id": 5,
                                    "firstName": "Shermy",
                                    "middleName": "Pearle",
                                    "lastName": "Layborn",
                                    "nickname": "artificial intelligence",
                                    "bio": "Intuitive client-server standardization",
                                    "email": "playborn4@amazon.com",
                                    "dateOfBirth": "2000-12-30",
                                    "phoneNumber": "+62 527 277 7359",
                                    "homeAddress": {
                                        "streetNumber": "5",
                                        "streetName": "Hagan Avenue",
                                        "suburb": null,
                                        "city": "Chengdong",
                                        "region": null,
                                        "country": "China",
                                        "postcode": null
                                    },
                                    "created": "2020-10-17 00:47:11",
                                    "role": "USER",
                                    "businessesAdministered": [
                                        "Grain n Simple"
                                    ],
                                    "images": [],
                                    "primaryImagePath": null,
                                    "primaryThumbnailPath": null
                                },
                                {
                                    "id": 8,
                                    "firstName": "Elysee",
                                    "middleName": "Maurene",
                                    "lastName": "Took",
                                    "nickname": "benchmark",
                                    "bio": "Team-oriented interactive installation",
                                    "email": "mtook7@chron.com",
                                    "dateOfBirth": "1985-11-09",
                                    "phoneNumber": "+234 186 824 2303",
                                    "homeAddress": {
                                        "streetNumber": "5",
                                        "streetName": "Utah Terrace",
                                        "suburb": null,
                                        "city": "Huangfang",
                                        "region": null,
                                        "country": "China",
                                        "postcode": null
                                    },
                                    "created": "2020-10-31 22:39:42",
                                    "role": "USER",
                                    "businessesAdministered": [
                                        "Grain n Simple"
                                    ],
                                    "images": [],
                                    "primaryImagePath": null,
                                    "primaryThumbnailPath": null
                                }
                            ],
                            "primaryAdministratorId": 5,
                            "description": "Praesent blandit. Nam nulla.",
                            "address": {
                                "streetNumber": "302",
                                "streetName": "Forest Run Place",
                                "suburb": null,
                                "city": "Putinci",
                                "region": null,
                                "country": "Serbia",
                                "postcode": null
                            },
                            "businessType": "Retail Trade",
                            "created": "2020-05-22 20:21:22",
                            "images": [],
                            "primaryImagePath": null,
                            "primaryThumbnailPath": null
                        },
                        "Layo"
                    ],
                    "images": [],
                    "primaryImagePath": null,
                    "primaryThumbnailPath": null
                },
                {
                    "id": 2,
                    "firstName": "Karrie",
                    "middleName": "Salvatore",
                    "lastName": "Loyley",
                    "nickname": "local area network",
                    "bio": "Quality-focused next generation synergy",
                    "email": "sloyley1@wordpress.com",
                    "dateOfBirth": "1995-11-06",
                    "phoneNumber": "+48 864 927 4819",
                    "homeAddress": {
                        "streetNumber": "8120",
                        "streetName": "GroverJunction",
                        "suburb": null,
                        "city": "Cimarelang",
                        "region": null,
                        "country": "Indonesia",
                        "postcode": null
                    },
                    "created": "2020-05-01 01:25:12",
                    "role": "USER",
                    "businessesAdministered": [
                        "Layo"
                    ],
                    "images": [],
                    "primaryImagePath": null,
                    "primaryThumbnailPath": null
                }
            ],
            "primaryAdministratorId": 2,
            "description": "Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla. Sed vel enim sit amet nunc viverra dapibus. Nulla suscipit ligula in lacus.",
            "address": {
                "streetNumber": "95403",
                "streetName": "Hanover Park",
                "suburb": null,
                "city": "El Guapinol",
                "region": null,
                "country": "Honduras",
                "postcode": null
            },
            "businessType": "Accommodation and Food Services",
            "created": "2020-08-25 10:22:19",
            "images": [],
            "primaryImagePath": null,
            "primaryThumbnailPath": null
        },
        {
            "name": "Skinder",
            "id": 3,
            "administrators": [
                1,
                {
                    "id": 3,
                    "firstName": "Felice",
                    "middleName": "Tabbitha",
                    "lastName": "Jaeggi",
                    "nickname": "intranet",
                    "bio": "Managed foreground budgetary management",
                    "email": "tjaeggi2@independent.co.uk",
                    "dateOfBirth": "1976-12-06",
                    "phoneNumber": "+1 659 270 1003",
                    "homeAddress": {
                        "streetNumber": "5305",
                        "streetName": "Melrose Drive",
                        "suburb": null,
                        "city": "Sidon",
                        "region": null,
                        "country": "Lebanon",
                        "postcode": null
                    },
                    "created": "2020-12-24 17:40:59",
                    "role": "USER",
                    "businessesAdministered": [
                        "Skinder"
                    ],
                    "images": [],
                    "primaryImagePath": null,
                    "primaryThumbnailPath": null
                },
                {
                    "id": 7,
                    "firstName": "Papageno",
                    "middleName": "Batholomew",
                    "lastName": "Dolton",
                    "nickname": "Persevering",
                    "bio": "Advanced bi-directional flexibility",
                    "email": "bdolton6@liveinternet.ru",
                    "dateOfBirth": "2000-07-12",
                    "phoneNumber": "+380 428 944 6622",
                    "homeAddress": {
                        "streetNumber": "01801",
                        "streetName": "Grasskamp Lane",
                        "suburb": null,
                        "city": "Malhou",
                        "region": "Santarém",
                        "country": "Portugal",
                        "postcode": "2380-506"
                    },
                    "created": "2020-07-21 03:54:32",
                    "role": "USER",
                    "businessesAdministered": [
                        "Skinder"
                    ],
                    "images": [],
                    "primaryImagePath": null,
                    "primaryThumbnailPath": null
                }
            ],
            "primaryAdministratorId": 3,
            "description": "Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.",
            "address": {
                "streetNumber": "6794",
                "streetName": "Jackson Way",
                "suburb": null,
                "city": "Xialaba",
                "region": null,
                "country": "China",
                "postcode": null
            },
            "businessType": "Retail Trade",
            "created": "2020-09-11 08:50:50",
            "images": [],
            "primaryImagePath": null,
            "primaryThumbnailPath": null
        }
    ],
    "images": [
        {
            "id": "3",
            "thumbnailFilename": "user_1/3_thumbnail.png",
            "name": "Screen Shot 2021-06-07 at 11.52.28 AM.png",
            "fileName": "user_1/3.png"
        }
    ],
    "primaryImagePath": "user_1/3.png",
    "primaryThumbnailPath": "user_1/3_thumbnail.png"
}

let store = {
    loggedInUserId: 1,
    role: "USER",
    userName: "Wileen Tisley",
    userBusinesses: [business],
    actingAsBusinessId: 1,
    actingAsBusinessName: "Dabshots"
}

jest.mock("../Api.js", () => jest.fn);
api.getUserFromID = jest.fn(() => {
    return Promise.resolve({data:user, status: 200});
});

api.getBusinessFromId = jest.fn(() => {
    return Promise.resolve({data:business, status: 200});
});

let $router;
let mutations;
let sessionStorage;

let $log = {
    debug: jest.fn()
}


describe('User acting as tests', () => {
    beforeEach(() => {
        wrapper = shallowMount(ActingAs, {
            propsData: {},
            mocks: {store, $log, $router, mutations, sessionStorage},
            stubs: ['router-link', 'router-view'],
            methods: {},
            localVue,
        });
    
        wrapper.vm.businesses = [business];
        wrapper.vm.user = user;
    
    
        mutations = {
            setActingAsBusiness: jest.fn(),
            setActingAsUser: jest.fn()
        };
    
        sessionStorage = {
            getItem: jest.fn(),
            removeItem: jest.fn()
        };
    
        $router = {
            push: jest.fn()
        }
    
        const getUserRoleMethod = jest.spyOn(ActingAs.methods, 'getUserRole')
        getUserRoleMethod.mockImplementation(() => {
            wrapper.vm.role = "USER";
            return wrapper.vm.role;
        });
    
        const getUserNameMethod = jest.spyOn(ActingAs.methods, 'getUserName');
        getUserNameMethod.mockImplementation(() => {
            return store.userName;
        });
    });
    
    afterEach(() => {
        wrapper.destroy();
    });


    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Check username is shown', () => {
        expect(wrapper.find('.user').exists()).toBe(true);
    });

    test('Check username is displaying the correct username and role', () =>  {
        wrapper.vm.store.userName = "Wileen Tisley";
        wrapper.vm.store.loggedInUserId = 22;
        expect(wrapper.vm.getUserName()).toBe('Wileen Tisley');
    });

    test('Check user primary businesses list is rendered', () =>  {
        expect(wrapper.find('#businessList').exists()).toBe(true);
    });

    test('Check selecting business in dropdown calls setActingAsBusinessId method', () => {
        wrapper.vm.setActingAsBusinessId = jest.fn();
        const busLi = wrapper.find('#businessList');
        busLi.trigger('click');
        expect(wrapper.vm.setActingAsBusinessId).toBeCalled;
    });

    test('Check selecting user calls setActingAsUser method', () => {
        //Select business
        wrapper.vm.setActingAsBusinessId = jest.fn();
        const busLi = wrapper.find('#businessList');
        busLi.trigger('click');
        const userName = wrapper.find('.user-menu');
        wrapper.vm.setActingAsUser = jest.fn();
        userName.trigger('click');
        expect(wrapper.vm.setActingAsUser).toBeCalled;
    })
});

describe( 'Backend error checking tests', () => {
    beforeEach(() => {
        wrapper = shallowMount(ActingAs, {
            propsData: {},
            mocks: {store, $log, $router, mutations, sessionStorage},
            stubs: ['router-link', 'router-view'],
            methods: {},
            localVue,
        });

        wrapper.vm.businesses = [business];
        wrapper.vm.user = user;

    

        sessionStorage = {
            getItem: jest.fn(),
            removeItem: jest.fn()
        };

        $router = {
            push: jest.fn()
        }

    });

    afterEach(() => {
        wrapper.destroy();
    });



    test('When setActingAsBusinessId returns 200, the cache is refreshed and the user acts as a business', async () => {
        api.actAsBusiness = jest.fn(() => {
            return Promise.resolve({status: 201, data: {id: 1}});
        });
        wrapper.vm.refreshCachedItems = jest.fn();
        wrapper.vm.setActingAsBusinessId(store.actingAsBusinessId, store.actingAsBusinessName);


        await wrapper.vm.$nextTick();

        expect(wrapper.vm.refreshCachedItems).toBeCalled();
    });

    test('When setActingAsBusinessId returns an unspecified error (500), the problem is printed to debug', async () => {
        api.actAsBusiness = jest.fn(() => {
            return Promise.reject({response: {status: 500, message: "unspecified error"}});
        });

        wrapper.vm.setActingAsBusinessId(store.actingAsBusinessId, store.actingAsBusinessName);

        await wrapper.vm.$nextTick();

        expect(api.actAsBusiness).toBeCalled()
        expect(wrapper.vm.$log.debug).toBeCalled();
    });

    test('When setActingAsUser returns 200, the cache is refreshed and the user acts as a business', async () => {
        api.actAsBusiness = jest.fn(() => {
            return Promise.resolve({status: 201, data: {id: 1}});
        });
        wrapper.vm.refreshCachedItems = jest.fn();
        wrapper.vm.setActingAsUser();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.refreshCachedItems).toBeCalled();
    });

    test('When setActingAsUser returns an unspecified error (500), the problem is printed to debug', async () => {
        api.actAsBusiness = jest.fn(() => {
            return Promise.reject({response: {status: 500, message: "unspecified error"}});
        });

        wrapper.vm.setActingAsBusinessId(store.actingAsBusinessId, store.actingAsBusinessName);

        await wrapper.vm.$nextTick();

        expect(api.actAsBusiness).toBeCalled()
        expect(wrapper.vm.$log.debug).toBeCalled();
    });



});