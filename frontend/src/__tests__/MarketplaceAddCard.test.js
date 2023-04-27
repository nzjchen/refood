import {mount, createLocalVue } from '@vue/test-utils';
import MarketplaceAddCard from '../components/MarketplaceAddCard.vue';
import Vuesax from 'vuesax';
import api from "../Api";

let wrapper;
let localVue = createLocalVue();
localVue.use(Vuesax);
let $route = {
    params: {
        id: 1,
    }
}

let $vs = {
    notify: jest.fn()
}

let mockCard = {
    "creatorId": 1,
    "section": "ForSale",
    "title": "1982 Lada Samara",
    "description": "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
    "keywordIds": [
        [
            20,
            15,
            600
        ]
    ]
}
let $log = {
    debug: jest.fn(),
}

api.checkSession = jest.fn().mockResolvedValue({data: {id: 1}});
api.createCard = jest.fn(() => {
    return Promise.resolve({data: mockCard, status: 200});
});
beforeEach(() => {
    wrapper = mount(MarketplaceAddCard, {
        mocks: {$vs, $route, $log},
        stubs: {},
        methods: {},
        localVue,
    })
    wrapper.vm.$emit = jest.fn();
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('No section error', () => {
        wrapper.vm.section = null;
        wrapper.vm.title = "Weee";
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('no-section')).toBeTruthy();
    });

    test('No title error', () => {
        wrapper.vm.section = "Wanted";
        wrapper.vm.title = "";
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('no-title')).toBeTruthy();
    });

    test('Too long title error', () => {
        wrapper.vm.section = "Wanted";
        wrapper.vm.title = "The quick brown fox jumped over the lazy orange dog";
        wrapper.vm.checkForm();
        expect(wrapper.vm.errors.includes('long-title')).toBeTruthy();
    });

    test('Card title is only spaces error',  () => {
        wrapper.vm.title = "   ";
        wrapper.vm.checkForm();
        expect(wrapper.vm.titleError).toBeTruthy();
    });

    test('Valid card', () => {
       wrapper.vm.section = "Wanted";
       wrapper.vm.title = "Volkswagen Golf GTi mk5";
       expect(wrapper.vm.checkForm()).toBeTruthy();
    });

    test('Open modal', () => {
        wrapper.vm.openModal();
        expect(wrapper.vm.showing).toBeTruthy();
    });

    test('Reset values', () => {
        wrapper.vm.openModal();
        expect(wrapper.vm.section).toEqual(null);
        expect(wrapper.vm.title.length).toEqual(0);
        expect(wrapper.vm.description.length).toEqual(0);
    });

    test('Close modal after successful submission', () => {
        wrapper.vm.section = "Wanted";
        wrapper.vm.title = "Volkswagen Golf GTi mk5";
        wrapper.vm.id = 7;
        expect(wrapper.vm.checkForm()).toBeTruthy();
        wrapper.vm.addToMarketplace();
        expect(wrapper.vm.showing).toBeFalsy();
    });
});

describe("Card creation", () => {
   test("Succesful creation", async () => {
       await wrapper.vm.addToMarketplace();
       expect(wrapper.vm.$vs.notify).toBeCalled();
   });
});