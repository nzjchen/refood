import { mount, createLocalVue } from '@vue/test-utils';
import BusinessAdministrators from "../components/BusinessAdministrators";
import Vuesax from 'vuesax';

const localVue = createLocalVue();
localVue.use(Vuesax);
let wrapper;

// Prop data
let adminList = [
    {
        "id": 1,
        "firstName": "Wilma",
        "middleName": "Janet",
        "lastName": "Sails",
    },
    {
        "id": 2,
        "firstName": "Karrie",
        "middleName": "Salvatore",
        "lastName": "Loyley",
    },
];

let store = {
    loggedInUserId: 1,
}

let api = {
    removeUserAsBusinessAdmin: jest.fn(),
}

let $router = {
    push: jest.fn(),
}

beforeEach(() => {
    wrapper = mount(BusinessAdministrators, {
        mocks: {store, api, $router},
        stubs: {},
        methods: {},
        localVue,
        propsData: {
            admins: adminList,
            pAdminId: adminList[0].id,
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

    test('Props data loads correctly.', () => {
       expect(wrapper.vm.admins.length).toBe(2);
       expect(wrapper.vm.pAdminId).toBe(1);
    });
});

describe('UI components render successfully', () => {
    test('Correct number of admin cards generate', () => {
        expect(wrapper.findAll(".admin-chip").length).toBe(adminList.length);
    });
});

describe('Checking method functions', () => {
    test('Router pushes', async () => {
        let admin = wrapper.find(".admin-label");
        expect(admin).toBeTruthy();

        await admin.trigger('click');
        expect(wrapper.vm.$router.push).toBeCalled();
    });
});