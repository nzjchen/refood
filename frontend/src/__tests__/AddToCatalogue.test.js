import { mount, createLocalVue } from '@vue/test-utils';
import AddToCatalogue from '../components/AddToCatalogue';
import Vuesax from 'vuesax';
import api from "../Api";

let wrapper;

const localVue = createLocalVue();
localVue.use(Vuesax);

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
    "homeAddress": {
        "streetNumber": "5",
        "streetName": "Hagan Avenue",
        "suburb": null,
        "city": "Chengdong",
        "region": null,
        "country": "China",
        "postcode": null
    },
}

// const mockProduct = {
//
// }

let $log = {
    debug: jest.fn(),
    error: jest.fn(),
}

let $router = {
    push: jest.fn()
}

let $vs = {
    notify: jest.fn()
}

let store = {
    loggedInUserId: 22,
    role: "USER",
    userName: "Wileen Tisley",
    userPrimaryBusinesses: [],
    actingAsBusinessId: 1,
    actingAsBusinessName: "Dabshots"
}


beforeEach(() => {
    wrapper = mount(AddToCatalogue, {
        propsData: {},
        mocks: {store, $log, $router, $vs},
        stubs: ['router-link', 'router-view'],
        methods: {},
        // components: CurrencyInput,
        localVue,
    });

    const checkSessionMethod = jest.spyOn(AddToCatalogue.methods, 'checkUserSession');
    checkSessionMethod.mockImplementation(() => {
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
describe('Add To Catalogue form error checking', () => {


    beforeEach(() => {
        wrapper.vm.user = mockUser;
        const getUserMethod = jest.spyOn(AddToCatalogue.methods, 'getUserInfo');
        getUserMethod.mockImplementation(() =>{
            wrapper.vm.user = mockUser;
            wrapper.vm.currencyCode = "NZD";
            wrapper.vm.currencySymbol = "$"
        });
    });

    test('Handles empty Register', () => {
        const addBtn = wrapper.find('.add-button');
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(4);
    });

    test('Handles no product name', () => {
        wrapper.vm.productName = "";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no product id', () => {
        wrapper.vm.productName = "Big Tyre";
        wrapper.vm.productId = "";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = 22;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no rrp', () => {
        wrapper.vm.productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = "";

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles negative rrp', () => {
        wrapper.vm.productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Good product";
        wrapper.vm.manufacturer = "Bob tyres";
        wrapper.vm.rrp = -2;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });

    test('Handles no manufacturer', () => {
        wrapper.vm.productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "";
        wrapper.vm.manufacturer = "";
        wrapper.vm.rrp = 2;

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(1);
    });
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('check form function checking', () => {


    beforeEach(() => {
        wrapper.vm.user = mockUser;
    });

    test('When NaN rrp, long product name and long description is input, user is notified and errors are pushed', () => {
        wrapper.vm.productName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris condimentum metus mauris, ut vehicula lacus sollicitudin vel. Etiam consectetur maximus vulputate. Etiam non laoreet velit, sed consequat lectus. Ut rhoncus suscipit urna sed maximus. Vestibulum volutpat iaculis lorem ac faucibus. Quisque ultrices nisi et augue consectetur, maximus fringilla neque aliquam. Cras et felis vitae justo iaculis egestas eu eu nisl. Integer pellentesque arcu eget erat finibus dapibus. Cras eleifend ante eget suscipit vestibulum. Sed nunc nisi, hendrerit id sodales nec, varius fermentum turpis.";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris condimentum metus mauris, ut vehicula lacus sollicitudin vel. Etiam consectetur maximus vulputate. Etiam non laoreet velit, sed consequat lectus. Ut rhoncus suscipit urna sed maximus. Vestibulum volutpat iaculis lorem ac faucibus. Quisque ultrices nisi et augue consectetur, maximus fringilla neque aliquam. Cras et felis vitae justo iaculis egestas eu eu nisl. Integer pellentesque arcu eget erat finibus dapibus. Cras eleifend ante eget suscipit vestibulum. Sed nunc nisi, hendrerit id sodales nec, varius fermentum turpis.";
        wrapper.vm.manufacturer = "Ling Long";
        wrapper.vm.rrp = "asdfksdj";

        const addBtn = wrapper.find('.add-button')
        addBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(3);
        expect(wrapper.vm.errors).toStrictEqual(["invalid-rrp", "long-prodName", "long-desc"]);
    });

    test('When a new product is created, results are written to debug and user is redirected to the business producct page', async () => {
        api.createProduct = jest.fn(() => {
            return Promise.resolve({status: 201, data: {id: 1}});
        });

        wrapper.vm.productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "";
        wrapper.vm.manufacturer = "Ling Long";
        wrapper.vm.rrp = 2;

        wrapper.vm.createItem();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$log.debug).toBeCalled();
        expect(wrapper.vm.$router.push).toBeCalled();
    });

    test('When a new product is not created (promise reject), results are written to debug and user is notified', async () => {
        api.createProduct = jest.fn(() => {
            return Promise.reject({response: {status: 400, message: "unspecified error"}});
        });

        wrapper.vm.productName = "Big Tyre";
        wrapper.vm.productId = "BB";
        wrapper.vm.description = "";
        wrapper.vm.manufacturer = "Ling Long";
        wrapper.vm.rrp = 2;

        wrapper.vm.createItem();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$log.debug).toBeCalled();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });

    test('When getUserInfo is called, the user variable is set', async () => {
        api.getUserFromID = jest.fn(() => {
            return Promise.resolve({status: 201, data: mockUser});
        });
        wrapper.vm.setCurrency = jest.fn(() => {
            return true;
        });
        wrapper.vm.user = null;

        wrapper.vm.getUserInfo();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.user).toBeTruthy();
        //expect(wrapper.vm.setCurrency).toBeCalled();
    });

    test('When getUserInfo returns unauthorized (401), the user is notified and then redirected ', async() => {
        api.getUserFromID = jest.fn(() => {
            return Promise.reject({response: {status: 401, message: "unspecified error"}});
        });
        wrapper.vm.setCurrency = jest.fn(() => {
            return true;
        });
        wrapper.vm.user = null;

        wrapper.vm.getUserInfo();

        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.$router.push).toBeCalled();
    });
});
