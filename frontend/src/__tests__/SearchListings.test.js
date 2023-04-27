import { createLocalVue, mount } from '@vue/test-utils';
import SearchListings from '../components/SearchListings.vue';
import Vuesax from 'vuesax';
import api from "../Api";
import axios from "axios";

let wrapper;

//Mock response data from search request
const mockListingsFromSearch = {
    "content": [
        {
            "id": 8101,
            "inventoryItem": {
                "id": 3625,
                "product": {
                    "id": "WAUVT68E95A621381",
                    "business": {
                        "name": "Bluezoom",
                        "id": 371,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": "Pellentesque at nulla.",
                        "address": {
                            "streetNumber": "96",
                            "streetName": "Veith",
                            "suburb": null,
                            "city": "Okulovka",
                            "region": null,
                            "country": "Russia",
                            "postcode": "174350"
                        },
                        "businessType": "Electricity, Gas, Water and Waste Services",
                        "created": "2019-04-12 09:28:16"
                    },
                    "name": "Longos - Chicken Cordon Bleu",
                    "description": "Suspendisse accumsan tortor quis turpis.",
                    "manufacturer": "Tempor Est Foundation",
                    "recommendedRetailPrice": 98.53,
                    "created": "2021-02-03 02:50:51",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 99,
                "pricePerItem": 98.4,
                "totalPrice": 9741.6,
                "manufactured": "2021-03-02",
                "sellBy": "2021-09-04",
                "bestBefore": "2021-01-17",
                "expires": "2022-10-30"
            },
            "quantity": 44,
            "price": 4329.6,
            "moreInfo": "Morbi non quam nec dui luctus rutrum.",
            "created": "2021-03-01 09:06:18",
            "closes": "2022-10-31 08:12:42",
            "likes": 0
        },
        {
            "id": 2009,
            "inventoryItem": {
                "id": 5628,
                "product": {
                    "id": "5UXZW0C56D0221636",
                    "business": {
                        "name": "Meemm",
                        "id": 305,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": "Nunc nisl.",
                        "address": {
                            "streetNumber": null,
                            "streetName": null,
                            "suburb": null,
                            "city": null,
                            "region": null,
                            "country": "Greece",
                            "postcode": null
                        },
                        "businessType": "Mining",
                        "created": "2019-12-21 12:22:34"
                    },
                    "name": "Beer - Original Organic Lager",
                    "description": null,
                    "manufacturer": "Quam Quis Diam Institute",
                    "recommendedRetailPrice": 20.21,
                    "created": "2021-03-24 00:02:32",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 46,
                "pricePerItem": 19.27,
                "totalPrice": 886.42,
                "manufactured": "2021-03-13",
                "sellBy": "2021-10-09",
                "bestBefore": "2020-06-10",
                "expires": "2022-10-30"
            },
            "quantity": 5,
            "price": 96.3,
            "moreInfo": "Proin leo odio, porttitor id, consequat in, consequat ut, nulla.",
            "created": "2021-05-27 12:16:12",
            "closes": "2022-10-31 06:57:10",
            "likes": 0
        },
        {
            "id": 4775,
            "inventoryItem": {
                "id": 7304,
                "product": {
                    "id": "WTU-433YPJ",
                    "business": {
                        "name": "Abatz",
                        "id": 234,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": "Sed ante.",
                        "address": {
                            "streetNumber": "310",
                            "streetName": "Buell",
                            "suburb": null,
                            "city": "Liuhuang",
                            "region": null,
                            "country": "China",
                            "postcode": null
                        },
                        "businessType": "Electricity, Gas, Water and Waste Services",
                        "created": "2020-01-18 02:14:41"
                    },
                    "name": "Cleaner - Pine Sol",
                    "description": "Nam ultrices, libero non mattis pulvinar, nulla pede ullamcorper augue, a suscipit nulla elit ac nulla.",
                    "manufacturer": "Nulla Institute",
                    "recommendedRetailPrice": 3.84,
                    "created": "2021-01-15 00:18:04",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 20,
                "pricePerItem": -0.4,
                "totalPrice": -8,
                "manufactured": null,
                "sellBy": "2021-09-25",
                "bestBefore": null,
                "expires": "2022-10-30"
            },
            "quantity": 4,
            "price": -1.6,
            "moreInfo": "Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo.",
            "created": "2021-06-06 04:32:43",
            "closes": "2022-10-31 06:33:05",
            "likes": 0
        },
        {
            "id": 783,
            "inventoryItem": {
                "id": 7710,
                "product": {
                    "id": "5FRYD3H99GB136715",
                    "business": {
                        "name": "Abata",
                        "id": 75,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "1",
                            "streetName": "Mandrake",
                            "suburb": null,
                            "city": "Kinmparana",
                            "region": null,
                            "country": "Mali",
                            "postcode": null
                        },
                        "businessType": "Construction",
                        "created": "2020-04-06 10:13:49"
                    },
                    "name": "Pork Casing",
                    "description": "Duis ac nibh.",
                    "manufacturer": "Lobortis Ultrices Ltd",
                    "recommendedRetailPrice": 26.06,
                    "created": "2021-01-24 10:43:15",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 33,
                "pricePerItem": 25.48,
                "totalPrice": 840.84,
                "manufactured": null,
                "sellBy": "2021-12-19",
                "bestBefore": null,
                "expires": "2022-10-30"
            },
            "quantity": 15,
            "price": 382.2,
            "moreInfo": "Pellentesque at nulla.",
            "created": "2021-04-09 20:06:00",
            "closes": "2022-10-31 05:05:38",
            "likes": 0
        },
        {
            "id": 7261,
            "inventoryItem": {
                "id": 9582,
                "product": {
                    "id": "WAULFAFH1EN811277",
                    "business": {
                        "name": "Livepath",
                        "id": 318,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "647",
                            "streetName": "Mccormick",
                            "suburb": null,
                            "city": "Sikka",
                            "region": null,
                            "country": "Indonesia",
                            "postcode": null
                        },
                        "businessType": "Charitable organisation",
                        "created": "2019-02-20 22:31:12"
                    },
                    "name": "Mop Head - Cotton, 24 Oz",
                    "description": "Quisque arcu libero, rutrum ac, lobortis vel, dapibus at, diam.",
                    "manufacturer": "Cum Sociis Corp.",
                    "recommendedRetailPrice": 14.36,
                    "created": "2021-01-17 20:34:31",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 4,
                "pricePerItem": 14.02,
                "totalPrice": 56.08,
                "manufactured": "2020-11-17",
                "sellBy": "2021-11-04",
                "bestBefore": "2021-03-08",
                "expires": "2022-10-30"
            },
            "quantity": 2,
            "price": 28,
            "moreInfo": "Duis at velit eu est congue elementum.",
            "created": "2021-05-16 20:42:28",
            "closes": "2022-10-31 04:52:15",
            "likes": 0
        },
        {
            "id": 2912,
            "inventoryItem": {
                "id": 9785,
                "product": {
                    "id": "RFO-226IW7",
                    "business": {
                        "name": "Quaxo",
                        "id": 440,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "8663",
                            "streetName": "Sachtjen",
                            "suburb": null,
                            "city": "Del Rosario",
                            "region": null,
                            "country": "Philippines",
                            "postcode": "5408"
                        },
                        "businessType": "Information Media and Telecommunication",
                        "created": "2021-01-08 10:23:31"
                    },
                    "name": "Beer - Corona",
                    "description": null,
                    "manufacturer": null,
                    "recommendedRetailPrice": 41.93,
                    "created": "2021-05-25 00:51:20",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 10,
                "pricePerItem": 42.13,
                "totalPrice": 421.3,
                "manufactured": null,
                "sellBy": "2021-03-02",
                "bestBefore": null,
                "expires": "2022-10-30"
            },
            "quantity": 2,
            "price": 84.3,
            "moreInfo": "Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.",
            "created": "2021-04-30 09:50:01",
            "closes": "2022-10-31 04:16:53",
            "likes": 0
        },
        {
            "id": 5016,
            "inventoryItem": {
                "id": 6027,
                "product": {
                    "id": "2C3CDXEJ1FH737616",
                    "business": {
                        "name": "Twiyo",
                        "id": 419,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "685",
                            "streetName": "Algoma",
                            "suburb": null,
                            "city": "Mranggen",
                            "region": null,
                            "country": "Indonesia",
                            "postcode": null
                        },
                        "businessType": "Wholesale Trade",
                        "created": "2020-04-17 07:01:03"
                    },
                    "name": "Mace Ground",
                    "description": "Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.",
                    "manufacturer": "Non PC",
                    "recommendedRetailPrice": 71.71,
                    "created": "2021-02-20 05:30:58",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 89,
                "pricePerItem": 71.27,
                "totalPrice": 6343.03,
                "manufactured": null,
                "sellBy": "2021-07-18",
                "bestBefore": null,
                "expires": "2022-10-30"
            },
            "quantity": 21,
            "price": 1496.7,
            "moreInfo": "Nulla ac enim.",
            "created": "2021-03-28 14:39:02",
            "closes": "2022-10-31 03:37:35",
            "likes": 0
        },
        {
            "id": 8707,
            "inventoryItem": {
                "id": 5372,
                "product": {
                    "id": "QNL-4241JO",
                    "business": {
                        "name": "Jabbertype",
                        "id": 392,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "0",
                            "streetName": "Florence",
                            "suburb": null,
                            "city": "Wugong",
                            "region": null,
                            "country": "China",
                            "postcode": null
                        },
                        "businessType": "Accommodation and Food Services",
                        "created": "2019-03-26 21:19:14"
                    },
                    "name": "Longan",
                    "description": null,
                    "manufacturer": null,
                    "recommendedRetailPrice": 84.36,
                    "created": "2021-04-05 17:28:40",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 5,
                "pricePerItem": 84.89,
                "totalPrice": 424.45,
                "manufactured": "2021-04-05",
                "sellBy": "2021-11-12",
                "bestBefore": "2021-04-27",
                "expires": "2022-10-30"
            },
            "quantity": 2,
            "price": 169.8,
            "moreInfo": "Aenean fermentum.",
            "created": "2021-03-20 16:19:11",
            "closes": "2022-10-31 03:25:17",
            "likes": 0
        },
        {
            "id": 7550,
            "inventoryItem": {
                "id": 7189,
                "product": {
                    "id": "VYI-366QA8",
                    "business": {
                        "name": "Aibox",
                        "id": 176,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "0771",
                            "streetName": "Bellgrove",
                            "suburb": null,
                            "city": "La Jicaral",
                            "region": null,
                            "country": "Nicaragua",
                            "postcode": null
                        },
                        "businessType": "Transport, Postal and Warehousing",
                        "created": "2020-05-14 22:40:23"
                    },
                    "name": "Soup - Cream Of Broccoli",
                    "description": "Phasellus id sapien in sapien iaculis congue.",
                    "manufacturer": "Nulla Institute",
                    "recommendedRetailPrice": 6.29,
                    "created": "2021-02-18 22:54:59",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 83,
                "pricePerItem": 4.95,
                "totalPrice": 410.85,
                "manufactured": "2020-11-05",
                "sellBy": "2021-04-30",
                "bestBefore": "2021-05-16",
                "expires": "2022-10-30"
            },
            "quantity": 19,
            "price": 94,
            "moreInfo": null,
            "created": "2021-04-12 04:33:39",
            "closes": "2022-10-31 01:22:48",
            "likes": 0
        },
        {
            "id": 9449,
            "inventoryItem": {
                "id": 6027,
                "product": {
                    "id": "2C3CDXEJ1FH737616",
                    "business": {
                        "name": "Twiyo",
                        "id": 419,
                        "administrators": [],
                        "primaryAdministratorId": null,
                        "description": null,
                        "address": {
                            "streetNumber": "685",
                            "streetName": "Algoma",
                            "suburb": null,
                            "city": "Mranggen",
                            "region": null,
                            "country": "Indonesia",
                            "postcode": null
                        },
                        "businessType": "Wholesale Trade",
                        "created": "2020-04-17 07:01:03"
                    },
                    "name": "Mace Ground",
                    "description": "Nulla neque libero, convallis eget, eleifend luctus, ultricies eu, nibh.",
                    "manufacturer": "Non PC",
                    "recommendedRetailPrice": 71.71,
                    "created": "2021-02-20 05:30:58",
                    "images": [],
                    "primaryImagePath": null
                },
                "quantity": 89,
                "pricePerItem": 71.27,
                "totalPrice": 6343.03,
                "manufactured": null,
                "sellBy": "2021-07-18",
                "bestBefore": null,
                "expires": "2022-10-30"
            },
            "quantity": 24,
            "price": 1710.5,
            "moreInfo": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.",
            "created": "2021-04-19 01:16:08",
            "closes": "2022-10-31 01:15:54",
            "likes": 0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "offset": 0,
        "pageSize": 10,
        "pageNumber": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 597,
    "totalElements": 5961,
    "size": 10,
    "number": 0,
    "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
    },
    "first": true,
    "numberOfElements": 10,
    "empty": false
}

const mockUser = {
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
            "created": "2020-05-18 09:06:11"
        },
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
                    ]
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
                                    ]
                                },
                                9,
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
                                    ]
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
                            "created": "2020-05-22 20:21:22"
                        },
                        "Layo"
                    ]
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
            "created": "2020-08-25 10:22:19"
        },
        {
            "name": "Skinder",
            "id": 3,
            "administrators": [
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
                    ]
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
                        "region": "SantarÃ©m",
                        "country": "Portugal",
                        "postcode": "2380-506"
                    },
                    "created": "2020-07-21 03:54:32",
                    "role": "USER",
                    "businessesAdministered": [
                        "Skinder"
                    ]
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
            "created": "2020-09-11 08:50:50"
        }
    ]
}

const busTypes = {
    "data": [
        "Accommodation and Food Services",
        "Retail Trade",
        "Charitable organisation",
        "Non-profit organisation",
        "Administrative and Support services",
        "Agriculture, Forestry and Fishing",
        "Arts and Recreation Services",
        "Construction",
        "Education and Training",
        "Electricity, Gas, Water and Waste Services",
        "Financial and Insurance Services",
        "Health Care and Social Assistance",
        "Information Media and Telecommunication",
        "Manufacturing",
        "Mining",
        "Professional, Scientific and Technical Services",
        "Public Administration and Safety",
        "Rental, Hiring and Real Estate Services",
        "Transport, Postal and Warehousing",
        "Wholesale Trade",
        "Other Services"
    ],
    "status": 200,
    "statusText": "",
    "headers": {
        "cache-control": "no-cache, no-store, max-age=0, must-revalidate",
        "content-length": "628",
        "content-type": "application/json",
        "expires": "0",
        "pragma": "no-cache"
    },
    "config": {
        "url": "/businesses/types",
        "method": "get",
        "headers": {
            "Accept": "application/json, text/plain, */*"
        },
        "baseURL": "http://localhost:9499",
        "transformRequest": [
            null
        ],
        "transformResponse": [
            null
        ],
        "timeout": 10000,
        "withCredentials": true,
        "xsrfCookieName": "XSRF-TOKEN",
        "xsrfHeaderName": "X-XSRF-TOKEN",
        "maxContentLength": -1,
        "maxBodyLength": -1
    },
    "request": {}
}

let $log = {
    debug: jest.fn(),
    error: jest.fn()
}

const localVue = createLocalVue();
localVue.use(Vuesax);

beforeEach(() => {
    wrapper = mount(SearchListings, {
        localVue,
        propsData: {},
        mocks: {$log},
        stubs: ['router-link', 'router-view'],
        methods: {},
        data () {
            return {
                listings: mockListingsFromSearch.content,
                businessTypes: busTypes.data
            }
        }
    });
    expect(wrapper).toBeTruthy();

    axios.get = jest.fn(() => {
        return Promise.resolve({data: [{
                currencies: [{symbol: "€"}],
            }]}
        );
    });

    api.checkSession = jest.fn(() => {
        return Promise.resolve({data: mockUser, status: 200});
    });

    api.filterListingsQuery = jest.fn(() => {
        return Promise.resolve({data: mockListingsFromSearch, status: 200}).finally();
    });

    api.addLikeToListing = jest.fn(() => {
        return Promise.resolve({status: 201});
    });

    api.getBusinessTypes = jest.fn(() => {
        return Promise.resolve({status: 200});
    });

    api.removeLikeFromListing = jest.fn(() => {
        return Promise.resolve({status: 200});
    });

    api.getUserLikedListings = jest.fn(() => {
        return Promise.resolve({data:{}, status: 200});
    });

    api.getBusinessTypes = jest.fn(() => {
        return Promise.resolve({status: 200});
    });


    wrapper.vm.filterListings = jest.fn();
});

afterEach(() => {
    wrapper.destroy();
});

describe('Button clicks', () => {
    test('Clicking search button calls correct method', async ()  => {
        let button = wrapper.find('#main-search-btn')
        button.trigger('click')
        await wrapper.vm.$nextTick();
        expect(api.filterListingsQuery).toBeCalled();
    });

    test('Clicking sort button calls correct method', async ()  => {
        let button = wrapper.find('#sort-button')
        button.trigger('click')
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.filterListings).toBeCalled();
    });


    test('Clicking pagination button calls correct method', async ()  => {
        let button = wrapper.find('.vs-pagination--nav')
        button.trigger('change')
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.filterListings).toBeCalled();
    });


});

describe('Filter validation', () => {
    test('No filters passes validation', async ()  => {
        wrapper.vm.businessQuery = null;
        wrapper.vm.productQuery = null;
        wrapper.vm.addressQuery = null;
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = null;
        wrapper.vm.maxPrice = null;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = null;
        wrapper.vm.maxClosingDate = null;
        expect(wrapper.vm.checkForm()).toBe(true);
    })

    test('Normal values passes validation', async ()  => {
        wrapper.vm.errors = [];
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = 5;
        wrapper.vm.maxPrice = 15;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = "2022-08-18T16:47";
        wrapper.vm.maxClosingDate = "2022-10-30T16:46";
        expect(wrapper.vm.checkForm()).toBe(true);
    })

    test('Invalid min price fails validation', async ()  => {
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = -5;
        wrapper.vm.maxPrice = 15;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = "2021-08-18T16:47";
        wrapper.vm.maxClosingDate = "2021-10-30T16:46";
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("invalid-minprice");
    })

    test('Invalid max price fails validation', async ()  => {
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = 5;
        wrapper.vm.maxPrice = -15;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = "2021-12-18T16:47";
        wrapper.vm.maxClosingDate = "2021-12-30T16:46";
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("invalid-maxprice");
    })

    test('Max price less than min price', async ()  => {
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = 5;
        wrapper.vm.maxPrice = 2;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = "2021-12-18T16:47";
        wrapper.vm.maxClosingDate = "2021-12-30T16:46";
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("invalid-maxprice");
    })

    test('Min closing date in past', async ()  => {
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = 5;
        wrapper.vm.maxPrice = 2;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = "2021-08-15T17:10";
        wrapper.vm.maxClosingDate = "2021-12-30T16:46";
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("past-min-date");
    })

    test('Max closing date in past', async ()  => {
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = 5;
        wrapper.vm.maxPrice = 2;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = null;
        wrapper.vm.maxClosingDate = "2021-08-15T17:10";
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("past-max-date");
    })

    test('Max closing date before min closing date', async ()  => {
        wrapper.vm.businessQuery = "Dab";
        wrapper.vm.productQuery = "Pa";
        wrapper.vm.addressQuery = "France";
        wrapper.vm.sortBy = "closes";
        wrapper.vm.minPrice = 5;
        wrapper.vm.maxPrice = 2;
        wrapper.vm.selectedTypes = [];
        wrapper.vm.minClosingDate = "2021-12-17T17:10";
        wrapper.vm.maxClosingDate = "2021-12-09T17:13";
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("past-max-date");
    })
});

describe('Listings search page tests', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Listings are loaded into their cards', () => {
        expect(wrapper.find('.grid-container').exists()).toBe(true);
    });

    test('Clicking on a listing redirects to details page', async () => {
        wrapper.vm.viewListing = jest.fn();
        let clickableImg = wrapper.find('#img-wrap')
        clickableImg.trigger('click');
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.viewListing).toBeCalled();
    });
});

describe('Liking items', () => {
    test('Liking an item', async () => {
        expect(wrapper.vm.likedListingsIds.length).toEqual(0);
        await wrapper.vm.sendLike(8101, "Longos - Chicken Cordon Bleu");
        expect(wrapper.vm.likedListingsIds[0]).toEqual(8101);
    })

    test('Unliking an item', async () => {
        await wrapper.vm.sendLike(8101, "Longos - Chicken Cordon Bleu");
        expect(wrapper.vm.likedListingsIds[0]).toEqual(8101);
        await wrapper.vm.deleteLike(8101, "Longos - Chicken Cordon Bleu");
        expect(wrapper.vm.likedListingsIds.length).toEqual(0);
    })
});
