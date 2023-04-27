import { shallowMount, createLocalVue } from '@vue/test-utils';
import AddToInventory from '../components/AddToInventory';
import Vuesax from 'vuesax';
import api from "../Api";


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
]

const mockProduct = {
    "id": "W04GP5EC0B1798680",
    "name": "Compound - Mocha",
    "description": "vel ipsum praesent blandit lacinia erat vestibulum sed magna at nunc",
    "manufacturer": "Nestle",
    "recommendedRetailPrice": 88.93,
    "created": "2021-01-11 20:54:46",
    "images": [],
    "primaryImagePath": null
};

api.createInventory = jest.fn(() => {
    return Promise.resolve({data: mockInventory, status: 200});
});

// api.getBusinessProducts = jest.fn(() => {
//     return Promise.resolve({data: mockProducts, status: 200});
// });

const localVue = createLocalVue();
localVue.use(Vuesax);

let wrapper;

let $route = {
    params: {
        id: 1,
    }
}

let $log = {
    debug: jest.fn(),
}

beforeEach(() => {
    wrapper = shallowMount(AddToInventory, {
        mocks: {$route, $log},
        stubs: {},
        methods: {},
        localVue,
    });
    wrapper.setData({product: mockProduct});
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Invalid ID error', () => {
        wrapper.vm.invenForm.prodId = '#@!%##@$%';
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors.includes('invalid-chars')).toBeTruthy();
    });

    test('Invalid PricePerItem error', () => {
        wrapper.vm.invenForm.pricePerItem = 'B';
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors.includes('pricePerItem')).toBeTruthy();
    });

    test('No date error', () => {
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors.includes('no-dates')).toBeTruthy();
        expect(wrapper.vm.errors.includes('past-expiry')).toBeTruthy();
    });

    test('Bad price per item error', () => {
        wrapper.vm.invenForm.pricePerItem = -50;
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("pricePerItem");
    });

    test('Total price is negative', () => {
        wrapper.vm.invenForm.totalPrice = -50;
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("totalPrice");
    });

    test('Total price is null', () => {
        wrapper.vm.invenForm.totalPrice = null;
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain("totalPrice");
    });

    test('Invalid quantity error', () => {
        wrapper.vm.invenForm.quantity = -50;
        expect(wrapper.vm.checkForm()).toBeFalsy();
        expect(wrapper.vm.errors).toContain(wrapper.vm.invenForm.quantity);
    });

    test('Past best before date error', () => {
        wrapper.vm.invenForm.bestBefore = '01-01-2000';
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('past-date')).toBeTruthy();
        expect(wrapper.vm.errors.includes('past-best')).toBeTruthy();
    });

    test('Past list expiry date error', () => {
        wrapper.vm.invenForm.listExpiry = '01-01-2000';
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('past-date')).toBeTruthy();
        expect(wrapper.vm.errors.includes('past-expiry')).toBeTruthy();
    });

    test('Future manufacture date error', () => {
        wrapper.vm.invenForm.manufactureDate = '01-01-2200';
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('future-date')).toBeTruthy();
    });

    test('Past sell by date error', () => {
        wrapper.vm.invenForm.sellBy = '01-01-2000';
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('past-date')).toBeTruthy();
        expect(wrapper.vm.errors.includes('past-sell')).toBeTruthy();
    });

    test('Best Before date is after expiry date error', () => {
        wrapper.vm.invenForm.bestBefore = '2021-12-12';
        wrapper.vm.invenForm.listExpiry = '2021-12-10';
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('best-before-expiry')).toBeTruthy();
    });

    test('Best Before date is before expiry date success', () => {
        wrapper.vm.invenForm.bestBefore = '2021-12-10';
        wrapper.vm.invenForm.listExpiry = '2021-12-12';
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('best-before-expiry')).toBeFalsy();
    });

    test('Successful inventory addition', async () => {
        wrapper.vm.invenForm.prodId = "W04GP5EC0B1798680";
        wrapper.vm.invenForm.quantity = 7;
        wrapper.vm.invenForm.pricePerItem = 3;
        wrapper.vm.invenForm.totalPrice = 88.93;
        wrapper.vm.invenForm.manufactureDate = '2020-01-27';
        wrapper.vm.invenForm.sellBy = null;
        wrapper.vm.invenForm.bestBefore = "2022-08-27";
        wrapper.vm.invenForm.listExpiry = "2022-08-27";
        wrapper.vm.addNewInv = true;
        await wrapper.vm.addInventory();
        expect(wrapper.vm.addNewInv).toBeFalsy();
    });

    test('Modal opens and sets info correctly',  () => {
        expect(wrapper.vm.addNewInv).toBeFalsy();

        wrapper.vm.open(mockProduct);
        expect(wrapper.vm.errors).toStrictEqual([]);
        expect(wrapper.vm.product).toBe(mockProduct);
    });
});
