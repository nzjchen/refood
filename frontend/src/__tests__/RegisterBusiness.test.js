import { mount, createLocalVue } from '@vue/test-utils';
import Business from '../components/BusinessRegister';
import Vuesax from 'vuesax';
import {store} from "../store";
import api from "../Api";

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

api.createBusiness = jest.fn(() => {
    return Promise.resolve({data: mockBusiness, status: 200}).catch({message: "Error", status: 400});
});

api.actAsBusiness = jest.fn(() => {
    return Promise.resolve({status: 200}).reject({message: "Error", status: 400});
});

api.getUserFromID = jest.fn(() => {
    return Promise.resolve({data: mockUser, status: 200});
});

api.getBusinessTypes = jest.fn(() => {
    return Promise.resolve({status: 200});
});


let $vs = {
    notify: jest.fn()
}

let $log = {
    debug: jest.fn()
}

//Mock user
const mockUser = {
    "id": 5,
    "firstName": "Rayna",
    "middleName": "YEP",
    "lastName": "Dalgety",
    "nickname": "Universal",
    "bio": "zero tolerance task-force",
    "email": "rdalgety3@ocn.ne.jp",
    "dateOfBirth": "1999-02-28",
    "phoneNumber": "+7 684 622 5902",
    "homeAddress": "44 Ramsey Court",
    "created": "2021-04-05 00:11:04",
    "role": "USER",
    "businessesAdministered": []
}

const $router = {
    push: jest.fn()
}
beforeEach(() => {
    wrapper = mount(Business, {
        propsData: {},
        mocks: {$vs, store, $router, $log},
        stubs: [],
        methods: {},
        localVue,
    });
});
const mockBusiness = {
    "businessId": 1,
    "administrators": [
        {
            "id": 5,
            "firstName": "Rayna",
            "middleName": "YEP",
            "lastName": "Dalgety",
            "nickname": "Universal",
            "bio": "zero tolerance task-force",
            "email": "rdalgety3@ocn.ne.jp",
            "dateOfBirth": "1999-02-28",
            "phoneNumber": "+7 684 622 5902",
            "homeAddress": "44 Ramsey Court",
            "created": "2021-04-05 00:11:04",
            "role": "USER",
            "businessesAdministered": [1]
        }
    ],
    "name": "Refood's Pizzas",
    "description": "We make Uni's cheapest pizza",
    "address": "420 Main South Road",
    "businessType": "Accommodation and Food Services",
    "created": "2021-04-03 23:29:50"
}
let mockBusinessAddress = {
    streetNumber: 420,
    streetName: "Main South Road",
    suburb: "Hornby",
    city: "Christchurch",
    region: "Canterbury",
    country: "New Zealand",
    postcode: 6969,
};

afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Business Register error checking', () => {

    test('Handles empty Register', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
    });

    test('Handles only name', () => {
        wrapper.vm.businessName = 'bestBusiness';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    })
    test('Handles only a type', () => {
        wrapper.vm.businessType = 'Charitable Organisation';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(2);
    })

    test('Handles old enough user', () => {
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
    })

    test('Handles young users', () => {
        wrapper.vm.businessName = 'bestBusiness';
        wrapper.vm.businessType = 'Charitable Organisation';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
        expect(wrapper.vm.$vs.notify).toBeCalled();
    })


    test('Handles long descriptions', () => {
        wrapper.vm.businessName = 'bestBusiness';
        wrapper.vm.businessType = 'Charitable Organisation';
        wrapper.vm.store.userDateOfBirth = "1990-01-01"
        wrapper.vm.description = 'EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.includes("description")).toBe(true);
        expect(wrapper.vm.$vs.notify).toBeCalled();
    })
});

describe('Business Register user age checking', () => {
    test('Handles too young user', () => {
        wrapper.vm.store.userDateOfBirth = "2010-01-01"
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.includes('dob')).toBe(true);
    })
});

describe('Business registration method checking', () => {
    test("Country is successfully set", () => {
        wrapper.vm.setCountry("Australia");
        expect(wrapper.vm.country).toBe("Australia");
        expect(wrapper.vm.suggestCountries).toBe(false);
    });

    test("City is successfully set", () => {
        wrapper.vm.setCity("Christchurch");
        expect(wrapper.vm.city).toBe("Christchurch");
        expect(wrapper.vm.suggestCities).toBe(false);
    });
});

describe('Checking valid age to register a business', () => {
    test("Test empty date of birth", () => {
        wrapper.vm.store.userDateOfBirth = "";
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('dob')).toBe(true);
    });

    test("Test successful age older than 16", () => {
        wrapper.vm.store.userDateOfBirth = "1980-01-01";
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('dob')).toBe(false);
    });

    test("Test unsuccessful age younger than 16", () => {
        wrapper.vm.store.userDateOfBirth = "2020-01-01";
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('dob')).toBe(true);
    });

    test("Test date of birth of now to be invalid", () => {
        wrapper.vm.store.userDateOfBirth = Date.now().toString();
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('dob')).toBe(true);
    });
});

describe('Check user sessions', () => {
   test("Not logged in (no userId in store)", () => {
       wrapper.vm.store.loggedInUserId = null;
       wrapper.vm.checkLoggedIn();
       expect(wrapper.vm.$vs.notify).toBeCalled();
       expect(wrapper.vm.$router.push).toBeCalled();
   });
});

describe('Creating business', () => {
   test("Successful", async () => {
       await wrapper.vm.createBusinessInfo();
       expect(wrapper.vm.$router.push).toBeCalled();
   });

   test("Unsuccessful", async () => {
       await wrapper.vm.createBusinessInfo();
       expect(wrapper.vm.$log.debug).toBeCalled();
   });
});

describe("Get user info", () => {
   test("Successful", async () => {
       await wrapper.vm.getUserInfo(5);
       expect(wrapper.vm.user).toEqual(mockUser);
   })
});

