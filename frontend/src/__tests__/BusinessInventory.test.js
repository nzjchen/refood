import { mount, createLocalVue } from '@vue/test-utils';
import BusinessInventory from '../components/BusinessInventory';
import Vuesax from 'vuesax';


const mockInventory = [
    {
        "id": 2,
        "product": {
            "id": "W04GP5EC0B1798680",
            "name": "Compound - Mocha",
            "description": "vel ipsum praesent blandit lacinia erat vestibulum sed magna at nunc",
            "manufacturer": "Nestle",
            "recommendedRetailPrice": 88.93,
            "created": "2021-01-11 20:54:46",
            "images": [],
            "primaryImagePath": null
        },
        "quantity": 7,
        "pricePerItem": 3,
        "totalPrice": 80,
        "manufactured": "2020-01-27",
        "sellBy": null,
        "bestBefore": "2021-08-27",
        "expires": "2021-08-27",
        "productName": "Compound - Mocha",
        "productId": "W04GP5EC0B1798680"
    }
];

const localVue = createLocalVue();
localVue.use(Vuesax);
let wrapper;

let $log = {
    debug: jest.fn()
}

let $route = {
    params: {
        id: 1,
    }
}

beforeEach(() => {
   wrapper = mount(BusinessInventory, {
       mocks: {$log, $route},
       stubs: ["ModifyInventory"],
       methods: {},
       localVue,
   });

   const getBusinessInventory = jest.spyOn(BusinessInventory.methods, "getBusinessInventory");
   getBusinessInventory.mockResolvedValue([]);

   const getSession = jest.spyOn(BusinessInventory.methods, 'getSession');
   //TODO: Setup this properly since this is temp fix so it can be merged
   const getBusinessProducts = jest.spyOn(BusinessInventory.methods, "getProducts");

   getSession.mockResolvedValue(() => {
       wrapper.vm.currency = "$";
   });
   getBusinessProducts.mockResolvedValue([]);
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });
});

describe('New sale listing modal tests', () => {
   beforeEach(async () => {
       wrapper.vm.newListingPopup = true;
       await wrapper.vm.$nextTick();
       expect(wrapper.find(".new-listing-modal")).toBeTruthy();
   });

   test("No changes to input fields - reject form", () => {
       expect(wrapper.vm.validateNewListing()).toBeFalsy();
       expect(wrapper.vm.newListingErrors.price.error).toBe(true);
       expect(wrapper.vm.newListingErrors.quantity.error).toBe(false);
       expect(wrapper.vm.newListingErrors.closes.error).toBe(true);
   });

    test("Invalid form with negative price", () => {
        wrapper.vm.price = -1.50;

        expect(wrapper.vm.validateNewListing()).toBeFalsy();
        expect(wrapper.vm.newListingErrors.price.error).toBe(true);
        expect(wrapper.vm.newListingErrors.quantity.error).toBe(false);
        expect(wrapper.vm.newListingErrors.closes.error).toBe(true);
    });

    test("Invalid form with bad date", () => {
        wrapper.vm.closes = "";
        expect(wrapper.vm.validateNewListing()).toBeFalsy();
        expect(wrapper.vm.newListingErrors.closes.error).toBe(true);

        wrapper.vm.closes = "2020-05-31T14:00";
        expect(wrapper.vm.validateNewListing()).toBeFalsy();
    });

    test("Successfully creates a new form", () => {
        wrapper.vm.listingQuantity = 5;
        wrapper.vm.price = 5.5;
        wrapper.vm.closes = "2022-01-01T15:00";
        expect(wrapper.vm.validateNewListing()).toBeTruthy();
        expect(wrapper.vm.newListingErrors.quantity.error).toBe(false);
        expect(wrapper.vm.newListingErrors.closes.error).toBe(false);
        expect(wrapper.vm.newListingErrors.price.error).toBe(false);
    });

    test("New Listing closes on button click", async () => {
        let closeButton = wrapper.find("#cancel-button");
        expect(closeButton).toBeTruthy();
        await closeButton.trigger("click");

        expect(wrapper.vm.newListingPopup).toBe(false);
        expect(wrapper.vm.newListingErrors.quantity.error).toBe(false);
        expect(wrapper.vm.newListingErrors.closes.error).toBe(false);
        expect(wrapper.vm.newListingErrors.price.error).toBe(false);
    });

    test("Form autocomplete calculates product attribute fields", () => {
        let invItem = mockInventory[0];
        wrapper.vm.openNewListingModal(invItem);

        expect(wrapper.vm.price).toBe(invItem.pricePerItem);
        expect(wrapper.vm.listingQuantityMax).toBe(invItem.quantity);
        expect(wrapper.vm.closes).toBe(invItem.expires + 'T00:00');
    });

    test("Full product info modal appears when clicking image", async () => {
        wrapper.vm.inventory = mockInventory;
        await wrapper.vm.$nextTick();
        let image = wrapper.find('.inventory-image');
        expect(image).toBeTruthy();

        await image.trigger('click');

        expect(wrapper.vm.showFullProduct).toBeTruthy();
        expect(wrapper.find('#full-product-modal'))

    });

});
