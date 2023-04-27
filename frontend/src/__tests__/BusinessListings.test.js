import {mount, createLocalVue} from '@vue/test-utils';
import BusinessListings from "../components/BusinessListings";
import Vuesax from 'vuesax';
import api from "../Api";
import axios from "axios";

let wrapper;

const localVue = createLocalVue();
localVue.use(Vuesax);

let listingData = [
    {
        "id": 1,
        "inventoryItem": {
            "id": 101,
            "product": {
                "id": "WATT-420-BEANS",
                "name": "Watties Baked Beans - 420g can",
                "images": [
                    {
                        "id": 1234,
                        "filename": "/media/images/23987192387509-123908794328.png",
                        "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                    }
                ],
                "primaryImagePath": "business_2/image_1.jpg"
            },
        },
        "quantity": 3,
        "price": 17.99,
        "moreInfo": "Seller may be willing to consider near offers",
        "created": "2021-07-14T11:44:00Z",
        "closes": "2021-07-21T23:59:00Z",
        "productName": "Watties Baked Beans - 420g can",
    },
    {
        "id": 2,
        "inventoryItem": {
            "id": 102,
            "product": {
                "id": "Doritos",
                "name": "Doritos - The spicy purple one",
                "images": [
                    {
                        "id": 12345,
                        "filename": "/media/images/23987192387509-123908794328.png",
                        "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
                    }
                ]
            },
        },
        "quantity": 5,
        "price": 5.99,
        "moreInfo": "Seller may be willing to consider near offers",
        "created": "2021-07-15T11:44:00Z",
        "closes": "2021-07-20T23:59:00Z",
        "productName": "Doritos - The spicy purple one",
    }
]

axios.get = jest.fn(() => {
   return Promise.resolve({data: [{
        currencies: [{symbol: "€"}],
       }]}
   );
});


jest.mock("../Api.js", () => jest.fn);
api.getBusinessListings = jest.fn(() => {
    return Promise.resolve({data: listingData, status: 200});
});

let $log = {
    error: jest.fn(),
    debug: jest.fn(),
}

beforeEach(() => {
    wrapper = mount(BusinessListings, {
        propsData: {
            businessId: 1,
        },
        mocks: {$log, api},
        stubs: {},
        methods: {},
        localVue,
    });
    wrapper.vm.listings = listingData;

});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test("Listings data is not empty.", () => {
        expect(wrapper.vm.listings).toBeTruthy();
    });
});

describe("UI tests", () => {
   test("Table/grid view switch changes successfully", async () => {
        expect(wrapper.vm.tableView).toBeFalsy();
        wrapper.vm.tableView = true;
        await wrapper.vm.$nextTick();
        expect(wrapper.find("#table-view")).toBeTruthy()
   });

   test("Grid sort order switches successfully", async () => {
        let sortButton = wrapper.find("#sort-button");
        expect(sortButton).toBeTruthy();
        await sortButton.trigger("click");

        expect(wrapper.vm.sortDirection).toBe("asc");
   });

});

describe("Functionality tests", () => {
   test("Sort direction switches successfully", () => {
       expect(wrapper.vm.sortDirection).toBe("desc");
       wrapper.vm.changeSortDirection();
       expect(wrapper.vm.sortDirection).toBe("asc");

       wrapper.vm.changeSortDirection();
       expect(wrapper.vm.sortDirection).toBe("desc");
   });

   test("Currency is set properly", () => {
      wrapper.vm.setCurrency("France");
      expect(wrapper.vm.currencySymbol).toBe("€");
   });

    test("Sort direction successfully changes", async () => {
        expect(wrapper.vm.sortDirection).toBe("desc");
        let sortButton = wrapper.find("#sort-button");
        expect(sortButton).toBeTruthy();

        await sortButton.trigger("click");
        expect(wrapper.vm.sortDirection).toBe("asc");
    });

});