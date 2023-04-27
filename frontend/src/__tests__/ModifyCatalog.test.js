import { mount, createLocalVue } from '@vue/test-utils';
import ModifyCatalogue from '../components/ModifyCatalog';
import Vuesax from 'vuesax';
import {store} from "../store";
import api from "../Api";

let wrapper;

const localVue = createLocalVue();
localVue.use(Vuesax);


let $vs = {
    notify: jest.fn()
}

// Mocking $route
const $route = {
    params: {
        id: 1,
        productId: 'test'
    }
};

let $log = {
    debug: jest.fn(),
    error: jest.fn()
}

let $router = {
    push: jest.fn()
}

store.loggedInUserId = 5;

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
};

const mockProduct = {
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
};


beforeEach(() => {
    wrapper = mount(ModifyCatalogue, {
        propsData: {},
        mocks: {store, $log, $route, $router, $vs},
        stubs: ['router-link', 'router-view'],
        methods: {},
        // components: CurrencyInput,
        localVue,
    });

    let getProduct = jest.spyOn(ModifyCatalogue.methods, 'getProduct');

    getProduct.mockImplementation(() => {})

    const checkSessionMethod = jest.spyOn(ModifyCatalogue.methods, 'checkUserSession');
    checkSessionMethod.mockImplementation(() => {
        wrapper.vm.user = mockUser;
        wrapper.vm.currencyCode = "NZD";
        wrapper.vm.currencySymbol = "$"
    });

    const getUserMethod = jest.spyOn(ModifyCatalogue.methods, 'getUserInfo');
    getUserMethod.mockImplementation(() =>{
        wrapper.vm.user = mockUser;
        wrapper.vm.currencyCode = "NZD";
        wrapper.vm.currencySymbol = "$"
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

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Modify Catalogue form error checking', () => {
    beforeEach(() => {
        // const createItemMethod = jest.spyOn(AddToCatalogue.methods, 'createItem');
        // createItemMethod.mockResolvedValue(mockUsers);
        //wrapper.vm.checkForm = jest.fn();
    });


    test('Handles no product name modification', () => {
        wrapper.vm.productName = "";
        wrapper.vm.productId = "BB";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.description = "Good product";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no product id modification', () => {
        wrapper.vm. productName = "Big Tyre";
        wrapper.vm.productId = "";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.description = "Good product";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no manufacturer modification', () => {
        wrapper.vm. productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no rrp modification', () => {
        wrapper.vm. productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = "";

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles negative rrp modification', () => {
        wrapper.vm. productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = -2;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });
});


describe('Add To Catalogue form error checking', () => {
    beforeEach(() => {
    });

    test('When the input is invalid and too long (product name, id, rrp, description and manufacturer), all errors are pushed and the user is notified', async () => {
        let longInput = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris condimentum metus mauris, ut vehicula lacus sollicitudin vel. Etiam consectetur maximus vulputate. Etiam non laoreet velit, sed consequat lectus. Ut rhoncus suscipit urna sed maximus. Vestibulum volutpat iaculis lorem ac faucibus. Quisque ultrices nisi et augue consectetur, maximus fringilla neque aliquam. Cras et felis vitae justo iaculis egestas eu eu nisl. Integer pellentesque arcu eget erat finibus dapibus. Cras eleifend ante eget suscipit vestibulum. Sed nunc nisi, hendrerit id sodales nec, varius fermentum turpis.";
        let invalidChars = "$#%^&$%*&%*(*^)(&*^%$;";

        wrapper.vm.productName = invalidChars + longInput;
        wrapper.vm.productId = invalidChars + longInput;
        wrapper.vm.description = longInput + longInput + longInput;
        wrapper.vm.manufacturer = invalidChars;
        wrapper.vm.rrp = "asdfkkld";

        wrapper.vm.checkForm();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.errors.length).toBe(7);
        expect(wrapper.vm.errors).toStrictEqual(["invalid-chars", "invalid-chars", "invalid-chars", "long-name", "long-id", "long-desc", "invalid-rrp"]);
    });

    test('When the ModifyItem call succeeds, result is  written to debug and user is redirected', async () => {
        api.modifyProduct = jest.fn(() => {
            return Promise.resolve({status: 201, data: {}});
        });

        wrapper.vm.ModifyItem();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$log.debug).toBeCalled();
        expect(wrapper.vm.$router.push).toBeCalled();
    });

    test('When the ModifyItem call fails (400), result is  written to debug and user is notified', async () => {
        api.modifyProduct = jest.fn(() => {
            return Promise.reject({response: {status: 400, message: "unspecified error"}});
        });

        wrapper.vm.ModifyItem();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$log.debug).toBeCalled();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });
});

