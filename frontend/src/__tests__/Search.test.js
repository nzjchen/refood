import { createLocalVue, shallowMount } from '@vue/test-utils';
import Search from '../components/Search.vue';
import Vuesax from 'vuesax';
import api from "../Api";

let wrapper;

//Mock response data from search request
const mockUsersFromSearch = {
        "content": [
            {
                "id": 2001,
                "firstName": "Aaren",
                "middleName": "Ronald",
                "lastName": "Wain",
                "nickname": "Diverse",
                "bio": "Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.",
                "email": "rwainr8@examiner.com",
                "dateOfBirth": "1989-03-28",
                "phoneNumber": "+267 898 773 7001",
                "homeAddress": {
                    "streetNumber": "2549",
                    "streetName": "Tennyson",
                    "suburb": null,
                    "city": "Nizhniy Tsasuchey",
                    "region": null,
                    "country": "Russia",
                    "postcode": "674480"
                },
                "created": "2019-04-08 13:31:19",
                "role": "USER",
                "businessesAdministered": []
            }
        ],
        "pageable": {
            "sort": {
                "sorted": true,
                "unsorted": false,
                "empty": false
            },
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 10,
            "paged": true,
            "unpaged": false
        },
        "last": false,
        "totalElements": 9418,
        "totalPages": 942,
        "number": 0,
        "size": 10,
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "first": true,
        "numberOfElements": 10,
        "empty": false
    }

const mockBusinessesFromSearch = {
    "content": [
        {
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
                    "businessesAdministered": null
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
            "created": "2020-05-18 09:06:11"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 505,
    "totalPages": 51,
    "number": 0,
    "size": 10,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "first": true,
    "numberOfElements": 10,
    "empty": false
}

let $vs = {
    loading: jest.fn(),
    notify: jest.fn()
}

let $log = {
    debug: jest.fn(),
    error: jest.fn()
}

const localVue = createLocalVue();
localVue.use(Vuesax);

beforeEach(() => {
    wrapper = shallowMount(Search, {
        localVue,
        propsData: {},
        mocks: {$vs, $log},
        stubs: ['router-link', 'router-view'],
        methods: {},
        data () {
            return {
                users: mockUsersFromSearch.content
            }
        }
    });
    expect(wrapper).toBeTruthy();
    api.searchUsersQuery = jest.fn(() => {
        return Promise.resolve({data: mockUsersFromSearch, status: 200}).finally();
    });

    api.searchBusinessesWithTypeQuery = jest.fn(() => {
        return Promise.resolve({data: mockBusinessesFromSearch, status: 200}).finally();
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Search page tests', () => {
    test('Table exists when valid data is in response', () => {
        expect(wrapper.find('.data-table').exists()).toBe(true);
    })

    test('Mobile mode not on when window is normal size', () => {
        global.innerWidth = 500;
        global.dispatchEvent(new Event('resize'));
        expect(wrapper.vm.mobileMode).toBe(false)
    })
    

    test('Mobile mode active when window size is reduced', () => {
        global.innerWidth = 499;
        global.dispatchEvent(new Event('resize'));

        expect(wrapper.vm.mobileMode).toBe(true)

    })
});

describe("Test searching without query", () => {
   test("Successful search for users - No query", async () => {
      wrapper.vm.searchbarUser = "";
      wrapper.vm.sortString = "";
      await wrapper.vm.searchUsers(1);
      expect(wrapper.vm.$vs.loading).not.toBeCalled();
   });


    test("Successful search for businesses - No query", async () => {
        wrapper.vm.searchbarBusiness = "";
        await wrapper.vm.searchBusiness(1);
        expect(wrapper.vm.$vs.loading).toBeCalled();
    });

});


describe("Test searching with query", () => {
    test("Successful user search - with query", async () => {
        wrapper.vm.$vs.loading.close = jest.fn();
        wrapper.vm.searchbarUser = "Something";
        wrapper.vm.sortString = null;
        wrapper.vm.tableLoaded = true;
        await wrapper.vm.searchUsers();
        expect(wrapper.vm.$vs.loading).toBeCalled();
    });
    test("Successful business search - with query", async () => {
        wrapper.vm.$vs.loading.close = jest.fn();
        wrapper.vm.searchbarBusiness = "Something";
        wrapper.vm.tableLoaded = true;
        await wrapper.vm.searchBusiness();
        expect(wrapper.vm.$vs.loading).toBeCalled();
    });
});
