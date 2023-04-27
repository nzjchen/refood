import { mount, createLocalVue } from '@vue/test-utils';
import Business from '../components/Business';
import Vuesax from 'vuesax';
import api from '../Api';


let wrapper;
let localVue = createLocalVue();
localVue.use(Vuesax);


jest.mock("../main.js", () => ({
    eventBus: {
        $on: jest.fn(),
        $off: jest.fn(),
        $emit: jest.fn()
    }
}));

// Mock business admin
const mockAdmin = {
    "id": 9,
    "firstName": "Joete",
    "middleName": "YEP",
    "lastName": "Stopps",
    "nickname": "Multi-layered",
    "bio": "responsive capacity",
    "email": "jstopps7@flickr.com",
    "dateOfBirth": "1984-10-14",
    "phoneNumber": "+36 694 564 9090",
    "homeAddress": "34 Mendota Avenue",
    "created": "2021-04-03 23:29:50",
    "role": "USER",
    "businessesAdministered": [
        1
    ]
}

// Mock Business
const mockBusiness = {
        "id": 1,
        "administrators": [
        {
            "id": 9,
            "firstName": "Joete",
            "middleName": "YEP",
            "lastName": "Stopps",
            "nickname": "Multi-layered",
            "bio": "responsive capacity",
            "email": "jstopps7@flickr.com",
            "dateOfBirth": "1984-10-14",
            "phoneNumber": "+36 694 564 9090",
            "homeAddress": "34 Mendota Avenue",
            "created": "2021-04-03 23:29:50",
            "role": "USER",
            "businessesAdministered": [
                1
            ]
        }
    ],
        "name": "Business1",
        "description": "Test Business 1",
        "address": "123 Test Street",
        "businessType": "Accommodation and Food Services",
        "created": "2021-04-03 23:29:50",
        "primaryImagePath": "business_1/12.png",
        "images": [
            {
                "name": "image1",
                "id": "0",
                "fileName": "business_1/12.png"
            }
        ]
}

const wishlist = [
    {
        id: 1,
        businessId: 1,
        userId: 1,
        muted: false,
    }
]


// Mocking $route
const $route = {
    params: {
        id: 1
    }
};

const $vs = {
    notify: jest.fn(),
};

const $log = {
    error: jest.fn(),
    debug: jest.fn(),
}

api.checkSession = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200, data: {id: 1}});
});

api.getBusinessFromId = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200, data: mockBusiness});
});
api.getUserFromID = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200, data: mockAdmin});
});
api.getUsersWishlistedBusinesses = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200, data: wishlist});
});
api.removeBusinessFromWishlist = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200});
});
api.addBusinessToWishlist = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 201});
});
api.changeBusinessPrimaryImage = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200});
})
api.postBusinessImage = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 201});
})

beforeEach(() => {
    wrapper = mount(Business, {
        propsData: {},
        mocks: {$route, $vs, $log},
        stubs: ['router-link', 'router-view'],
        methods: {},
        localVue
    });
    wrapper.setData({business: mockBusiness, user: mockAdmin});
});

afterEach(() => {
    wrapper.destroy();
});


describe('Business tests', () => {
    beforeEach(() => {
        const checkSessionMethod = jest.spyOn(Business.methods, 'checkUserSession');
        checkSessionMethod.mockImplementation(() => {
            wrapper.vm.user = mockAdmin;
            wrapper.vm.business = mockBusiness;
            wrapper.vm.adminList = mockBusiness.administrators;
            wrapper.vm.images = [];
        });

        const getUserMethod = jest.spyOn(Business.methods, 'getUserInfo');
        getUserMethod.mockResolvedValue(mockAdmin);

        const getBusinessMethod = jest.spyOn(Business.methods, 'getBusiness');
        getBusinessMethod.mockResolvedValue(mockBusiness);
    });

    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('has business', () => {
        expect(wrapper.vm.business).toBeTruthy();
    });
});

describe("Successful login", () => {
    beforeEach(() => {
       wrapper.vm.user = mockAdmin;
    })

    test('business page loads successfully', () => {
        expect(wrapper.find('#business-name').exists()).toBe(true)
        expect(wrapper.find('#business-navbar').exists()).toBe(true)
    });
})

describe("Functionality tests", () => {
    test("Check session is called", () => {
        expect(api.checkSession).toBeCalled();
    });

    test("Check GET user info is called and sets data", () => {
        expect(api.getUserFromID).toBeCalled();
        expect(wrapper.vm.user).toBe(mockAdmin);
    });
});

describe("Wishlist functionality tests", () => {
   test("Business is removed from wishlist when button is pressed", async () => {
       wrapper.vm.inWishlist = true;
       let button = wrapper.find("#wishlist-button");
       expect(button).toBeTruthy();
       await button.trigger("click");
       expect(wrapper.vm.inWishlist).toBe(false);
   });

    test("Business is added to wishlist when button is pressed", async () => {
        wrapper.vm.inWishlist = false;

        let button = wrapper.find("#wishlist-button");
        expect(button).toBeTruthy();
        await button.trigger("click");
        expect(wrapper.vm.inWishlist).toBe(true);
    });
});

describe("Business images tests", () => {
    test("Filtered images function successfully filters out primary image", () => {
        wrapper.vm.images.push({"fileName": "business_1/12.png"});
        expect(wrapper.vm.images.length).toEqual(1);
        let filteredImages = wrapper.vm.filteredImages();
        expect(filteredImages.length).toEqual(0);
    });

    test("Filtered images function does not filter out non primary images", () => {
        wrapper.vm.images.push({"fileName": "business_1/1234.png"})
        wrapper.vm.images.push({"fileName": "business_1/12.png"});
        expect(wrapper.vm.images.length).toEqual(2);
        let filteredImages = wrapper.vm.filteredImages();
        expect(filteredImages.length).toEqual(1);
    });

    test("Business is retrieved and updated after updating primary image", () => {
        wrapper.vm.images = [];
        wrapper.vm.updatePrimaryImage(1);
        expect(wrapper.vm.business.images.length).toEqual(1);
    })
});
