import { mount, createLocalVue } from '@vue/test-utils';
import ProductCatalogue from "../components/ProductCatalogue";
import Vuesax from 'vuesax';
import {store} from "../store";
import api from "../Api";
import axios from "axios";

let wrapper;

const localVue = createLocalVue();
localVue.use(Vuesax);

store.loggedInUserId = 7;

//Mock user
const mockUser = {
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
        // Remove unnecessary fields.
        "country": "France",
    },
};

const mockBusiness =
    {
        "id": 7,
        "administrators": [
            22
        ],
        "name": "Skinder",
        "primaryAdministratorId": 1,
        "description": "Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.",
        "address": {
            "streetNumber": "0",
            "streetName": "Vernon Place",
            "city": "Sarpang",
            "region": null,
            "country": "Bhutan",
            "postcode": null
        },
        "businessType": "Charitable organisation",
        "created": "2020-05-18 21:06:11"
    };

const mockProducts = [
    {
        "id": "WATT-420-BEANS",
        "name": "Watties Baked Beans - 420g can",
        "description": "Baked Beans as they should be.",
        "manufacturer": "Heinz Wattie's Limited",
        "recommendedRetailPrice": 2.2,
        "created": "2021-05-24T11:44:21.028Z",
        "images": [
            {
                "id": 1234,
                "filename": "/media/images/23987192387509-123908794328.png",
                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    },
    {
        "id": "SPK-123",
        "name": "Sour Patch Kids",
        "description": "They're even sourer and sweeter when expired.",
        "manufacturer": "Food Things Ltd.",
        "recommendedRetailPrice": 2.49,
        "created": "2021-05-22T11:44:21.028Z",
        "images": [
            {
                "id": 1235,
                "filename": "/media/images/23987192387509-123908794328.png",
                "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
            }
        ]
    }
];

axios.get = jest.fn(() => {
    return Promise.resolve({data: [{
            currencies: [{symbol: "€", code: "EUR"}],
        }]}
    );
});

jest.mock("../Api.js", () => jest.fn);
api.checkSession =  jest.fn(() => {
    return Promise.resolve({
        data: {
            id: 7,
        }
    });
});

api.getBusinessProducts = jest.fn(() => {
    return Promise.resolve({
        data: mockProducts
    });
});

api.getUserFromID = jest.fn(() => {
    return Promise.resolve({
        data: mockUser,
    });
});


let $log = {
    debug: jest.fn(),
    error: jest.fn()
}

let $route = {
    params: {
        id: 7,
    }
}

let $router = {
    push: jest.fn(),
}


beforeEach(() => {
    wrapper = mount(ProductCatalogue, {
        propsData: {},
        mocks: {store, $log, $route, $router},
        stubs: ['router-link', 'router-view'],
        methods: {},
        localVue,
        data () {
            return {
                business: mockBusiness,
                actingAsBusinessId: null
            }
        }
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

describe('UI tests', () => {
   test("Correct number of grid cards is displayed", () => {
       let items = wrapper.findAll(".grid-item");
       expect(items.length).toBe(mockProducts.length);
   });

    test('uploadImage is called when new file uploaded', async () => {
        wrapper.vm.uploadImage = jest.fn();

        expect(wrapper.find('#fileUpload').exists()).toBe(true);
        await wrapper.find('#fileUpload').trigger('change');

        expect(wrapper.vm.uploadImage).toBeCalled();
    });

});


describe('Functionality tests', () => {
    test("Data is initialized and set properly.",  () => {
        expect(wrapper.vm.userId).toBe(7);
        expect(wrapper.vm.products).toBe(mockProducts);
        expect(wrapper.vm.filteredProducts).toBe(mockProducts);
        expect(api.checkSession).toBeCalled();
        expect(api.getBusinessProducts).toBeCalled();

        expect(wrapper.vm.user).toBe(mockUser);
        expect(wrapper.vm.currencySymbol).toBe("€");
    });

    test("Currency is set properly", async () => {
        await wrapper.vm.setCurrency("France");
        expect(wrapper.vm.currencySymbol).toBe("€");
    });
});