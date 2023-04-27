import { mount, createLocalVue } from '@vue/test-utils';
import Modal from '../components/Modal';
import Vuesax from 'vuesax';
import api from '../Api';

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

api.login = jest.fn(() => {
   return Promise.resolve({data: {userId: 1}, status: 200});
});

beforeEach(() => {
    wrapper = mount(Modal, {
        propsData: {},
        mocks: {},
        stubs: {},
        methods: {},
        localVue,
    });
});


afterEach(() => {
    wrapper.destroy();
});

//TESTS TO CHECK LOGIN ERROR HANDLING
describe('Modal emit', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Modal emits close event', () => {
        wrapper.vm.close() // emitFirstName('hello') // We emit data
        expect(wrapper.emitted()).toEqual({"close": [[]]}) // Data is emitted with expected value

    });


});