import { mount, createLocalVue } from '@vue/test-utils';
import Register from '../components/Register';
import Vuesax from 'vuesax';
import api from "../Api";

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

// Mocking $route
const $route = {
    params: {
        id: 1
    },
};

const $router = {
    push: jest.fn(),
}

let $log = {
    debug: jest.fn(),
}

beforeEach(() => {
    wrapper = mount(Register, {
        propsData: {},
        mocks: {$route, $log, $router},
        stubs: ['router-link', 'router-view'],
        methods: {},

        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Register error checking', () => {
    test('Handles empty register', () => {
        wrapper.vm.firstname = '';
        wrapper.vm.lastname = '';
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.email = '';
        wrapper.vm.password = '';
        wrapper.vm.confirm_password = '';
        wrapper.vm.dateofbirth = '';
        wrapper.vm.phonenumber = '';
        wrapper.vm.country = '';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(7);
    });

    test('Handles only email', () => {
        wrapper.vm.email = 'test@email.com';
        wrapper.vm.firstname = '';
        wrapper.vm.lastname = '';
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.password = '';
        wrapper.vm.confirm_password = '';
        wrapper.vm.dateofbirth = '';
        wrapper.vm.phonenumber = '';
        wrapper.vm.country = '';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(6);
    })
    test('Handles only password', () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.email = '';
        wrapper.vm.firstname = '';
        wrapper.vm.lastname = '';
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.dateofbirth = '';
        wrapper.vm.phonenumber = '';
        wrapper.vm.country = '';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors.length).toBe(5);
    })

    test('Handles bad email', () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.firstname = 'bob';
        wrapper.vm.lastname = 'steve';
        wrapper.vm.dateofbirth = '15/09/0145';
        wrapper.vm.country = 'New Zealand';
        wrapper.vm.email = 'thisisnotaemail.com'
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        wrapper.vm.bio = '';
        wrapper.vm.phonenumber = '027254871';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors).toStrictEqual(['thisisnotaemail.com'])
    })

    test('Handles large bio too large', () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.firstname = 'bob';
        wrapper.vm.lastname = 'steve';
        wrapper.vm.dateofbirth = '15/09/0145';
        wrapper.vm.country = 'New Zealand';
        wrapper.vm.email = 'thisis@email.com'
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        let info = "some info";
        wrapper.vm.bio = info.repeat(10); // 90 characters.
        wrapper.vm.phonenumber = '027254871';
        const registerBtn = wrapper.find('.register-button')
        registerBtn.trigger('click');
        expect(wrapper.vm.errors).toStrictEqual([wrapper.vm.bio])
    })

    test('Successful login', async () => {
        wrapper.vm.password = 'Potato123!';
        wrapper.vm.confirm_password = 'Potato123!';
        wrapper.vm.firstname = 'bob';
        wrapper.vm.lastname = 'steve';
        wrapper.vm.dateofbirth = '2000-01-01';
        wrapper.vm.country = 'New Zealand';
        wrapper.vm.email = 'thisis@email.com'
        wrapper.vm.middlename = '';
        wrapper.vm.nickname = '';
        let info = "some info";
        wrapper.vm.phonenumber = '027254871';
        await wrapper.vm.createUserInfo();
        expect(wrapper.vm.errors.length).toBe(0);
    })
});

describe('Method Checking', () => {
    test("Country is successfully set", () => {
       wrapper.vm.setCountry("New Zealand");
       expect(wrapper.vm.country).toBe("New Zealand");
       expect(wrapper.vm.suggestCountries).toBe(false);
    });

    test("City is successfully set", () => {
        wrapper.vm.setCity("Christchurch");
        expect(wrapper.vm.city).toBe("Christchurch");
        expect(wrapper.vm.suggestCities).toBe(false);
    });
});

