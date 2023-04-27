import { mount, createLocalVue } from '@vue/test-utils';
import Login from '../components/Login';
import Vuesax from 'vuesax';
import api from '../Api';

let wrapper;
let store;
let actions;
let mutations;
let state;
const localVue = createLocalVue();
localVue.use(Vuesax);

const $router = {
    push: jest.fn()
}

const $vs = {
    notify: jest.fn()
}

api.login = jest.fn(() => {
   return Promise.resolve({data: {userId: 1}, status: 200});
});

beforeEach(() => {
    actions = {
        someAction: jest.fn()
    };
    mutations = {
        someMutation: jest.fn()
    };
    state = {
        key: {}
    };
    wrapper = mount(Login, {
        propsData: {},
        mocks: {$router, $vs},
        stubs: {},
        methods: {},
        store,
        localVue,    
    });
});

afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Login error checking', () => {
    test('Handles empty login', () => {
        const loginBtn = wrapper.find('.loginButton')
        loginBtn.trigger('click');
        expect(wrapper.vm.errors.message).toBeTruthy();
        expect(wrapper.vm.errors.hasErrors).toBe(true);
    });
    
    test('Handles only email', () => {
        wrapper.vm.email = 'test@email.com';
        const loginBtn = wrapper.find('.loginButton')
        loginBtn.trigger('click');
        expect(wrapper.vm.errors.message).toBeTruthy();
        expect(wrapper.vm.errors.hasErrors).toBe(true);
    })
    test('Handles only password', () => {
        wrapper.vm.password = 'test';
        const loginBtn = wrapper.find('.loginButton')
        loginBtn.trigger('click');
        expect(wrapper.vm.errors.message).toBeTruthy();
        expect(wrapper.vm.errors.hasErrors).toBe(true);
    })

    test('Handles bad email', () => {
        wrapper.vm.password = 'test';
        wrapper.vm.email = 'thisisnotaemail.com'
        const loginBtn = wrapper.find('.loginButton')
        loginBtn.trigger('click');
        expect(wrapper.vm.errors.message).toBeTruthy();
        expect(wrapper.vm.errors.hasErrors).toBe(true);
    })
  
});

describe("Logging in", () => {
    test("Successful login", async () => {
       wrapper.vm.email = "jeff@mail.com";
       wrapper.vm.password = "123456!Skux";
       await wrapper.vm.loginSubmit();
       expect(wrapper.vm.$router.push).toBeCalled();
    });
    test("Unsuccessful login - Bad request", async () => {
        api.login = jest.fn(() => {
            return Promise.reject({response: {message: "Bad request", status: 400}});
        });
        await wrapper.vm.loginSubmit();
        expect(wrapper.vm.email).toEqual("");
        expect(wrapper.vm.password).toEqual("");
    });
})
