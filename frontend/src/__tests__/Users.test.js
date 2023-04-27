import {createLocalVue, mount} from '@vue/test-utils';
import Users from '../components/Users';
import {store} from '../store';
import Vuesax from 'vuesax';
import VueRouter from 'vue-router';
import api from "../Api";


let wrapper;

// Mock User for testing
const mockUser = {
    "id": 5,
    "firstName": "Rayna",
    "middleName": "YEP",
    "lastName": "Dalgety",
    "nickname": "Universal",
    "bio": "zero tolerance task-force",
    "email": "rdalgety3@ocn.ne.jp",
    "dateOfBirth": "2006-03-30",
    "phoneNumber": "+7 684 622 5902",
    "homeAddress": "44 Ramsey Court",
    "images": [],
    "created": "2021-04-05 00:11:04",
    "role": "USER",
    "businessesAdministered": [
        2
    ]
}

const mockBusinesses = [
    {
        "id": 2,
        "administrators": [
            {
                "id": 5,
                "firstName": "Rayna",
                "middleName": "YEP",
                "lastName": "Dalgety",
                "nickname": "Universal",
                "bio": "zero tolerance task-force",
                "email": "rdalgety3@ocn.ne.jp",
                "dateOfBirth": "2006-03-30",
                "phoneNumber": "+7 684 622 5902",
                "homeAddress": "44 Ramsey Court",
                "created": "2021-04-07 01:09:35",
                "role": "USER",
                "businessesAdministered": [
                    2
                ]
            }
        ],
        "name": "Business2",
        "primaryAdministratorId": 5,
        "description": "Test Business 2",
        "address": "23 Testing Avenue",
        "businessType": "Retail Trade",
        "created": "2021-04-07 01:09:35"
    }
];

jest.mock("../main.js", () => ({
    eventBus: {
        $on: jest.fn(),
        $off: jest.fn(),
        $emit: jest.fn()
    }
}));

let mockCard = {
    id: 1,
    user: {},
    title: "Beans - Green",
    description: "Integer ac leo.",
    created: "2021-06-01 18:32:38",
    displayPeriodEnd: "2021-06-15 18:32:38",
    keywords: "aliquam augue quam",
    section: "Wanted"
}


api.makeUserBusinessAdmin = jest.fn(() => {
    return Promise.resolve({status: 200});
});

api.getUserFromID = jest.fn(() => {
    return Promise.resolve({data: mockUser, status: 200});
});

api.getUserCards = jest.fn(() => {
   return Promise.resolve({data: [mockCard], status: 200});
});

const $route = {
    params: {
        id: 5
    }
};

let $vs = {
    loading: jest.fn(),
}

let $log = {
    error: jest.fn(),
    debug: jest.fn(),
}

const localVue = createLocalVue();
localVue.use(Vuesax);
localVue.use(VueRouter);
const router = new VueRouter();

beforeEach(() => {
    wrapper = mount(Users, {
        localVue,
        router,
        propsData: {},
        mocks: {store, $vs, $log},
        stubs: ['router-link', 'router-view', 'CardModal'],
        methods: {},
        
    });
    wrapper.setData({user: mockUser})
    wrapper.setData({userViewingBusinesses: ['test', 'test']})
    const getUserMethod = jest.spyOn(Users.methods, 'getUserInfo');
    getUserMethod.mockResolvedValue(mockUser);
    wrapper.setData({businesses: mockBusinesses})

    wrapper.vm.$vs.loading.close = jest.fn();
});

afterEach(() => {
    wrapper.destroy();
});

describe('User profile page tests', () => {
    test('Check calculateDuration is called', () => {
        wrapper.vm.calculateDuration = jest.fn();
        expect(typeof(wrapper.vm.calculateDuration)).toBeCalled;
    });

    test('User info loads successfully', () => {
        expect(wrapper.find('#container').exists()).toBe(true)
    });

    test('addUserToBusiness function is called', async () => {
        wrapper.vm.userViewingBusinesses = ["test"];

        wrapper.vm.addUserToBusiness = jest.fn();
        const openModalMethod = jest.spyOn(Users.methods, 'openModal');
        openModalMethod.mockImplementation(() => {
            wrapper.vm.showModal = true;
        });

        await wrapper.vm.$nextTick();
        expect(wrapper.find('#option-add-to-business').exists()).toBe(true);
        wrapper.find('#option-add-to-business').trigger('click');
        wrapper.vm.showModal = true;
        // await wrapper.vm.$nextTick(); // lets the wrapper go through the v-for loop.
    });

    test('Card modal successfully opens', async () => {
        expect(wrapper.vm.showMarketModal).toBeFalsy();

        let button = wrapper.find("#option-view-cards");
        expect(button).toBeTruthy();

        button.trigger('click');
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.showMarketModal).toBeTruthy();
        expect(wrapper.find('#market-card-modal')).toBeTruthy();
    });

    test('User cards is retrieved and set', async () => {
       expect(wrapper.vm.cards).toStrictEqual([]);
       await wrapper.vm.getUserCards();
       await wrapper.vm.$nextTick();
       expect(wrapper.vm.cards).toStrictEqual([mockCard]);
    });
});

describe('Business link tests with businesses', () =>  {
    beforeEach(() => {
        wrapper.setData({businesses: mockBusinesses});
    });

    test('Business names load successfully', () => {
        expect(wrapper.find('.card').exists()).toBe(true);
    });

    test('goToBusinessPage function gets called',  () => {
        wrapper.vm.goToBusinessPage = jest.fn();
        wrapper.find('.card').trigger('click');
        expect(wrapper.vm.goToBusinessPage).toBeCalled();
    })
});

describe('Business link tests without businesses', () =>  {
    test('Business names don\'t appear', () => {
        expect(wrapper.find('#card').exists()).toBe(false)
    });
});

describe('User details tests', () =>  {
    beforeEach(() => {
        wrapper.setData({businesses: []});
        wrapper.setData({selectedBusiness: mockBusinesses});
    });

    test('Successful addition to business', async () => {
        expect(wrapper.vm.businesses.length).toEqual(0);
        await wrapper.vm.addUserToBusiness();
        expect(wrapper.vm.businesses.length).toEqual(1);
    });

    test('User already an admin', async () => {
        let testdata = await wrapper.vm.getUserInfo(5);
        expect(testdata).toEqual(mockUser);
    });
})