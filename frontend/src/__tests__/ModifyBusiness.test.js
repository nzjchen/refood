import { mount, createLocalVue } from '@vue/test-utils';
import ModifyBusiness from '../components/ModifyBusiness';
import Vuesax from 'vuesax';
import {store} from "../store";
import api from "../Api";

let wrapper;
const localVue = createLocalVue();
localVue.use(Vuesax);

jest.mock("../main.js", () => ({
    eventBus: {
        $on: jest.fn(),
        $off: jest.fn(),
        $emit: jest.fn()
    }
}));

// Mock Businesess

let mockBusinessUnedited = {
    "name": "testBusiness",
    "description": "A one-stop shop for all your adventuring needs",
    "address": {
        "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "suburb": "Upper Riccarton",
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
    },
    "businessType": "Accommodation and Food Services"
}

let mockBusinessEdited = {
    "name": "realBusiness",
    "description": "fun business",
    "address": {
        "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "suburb": "Upper Riccarton",
        "city": "spain",
        "region": "china",
        "country": "New Zealand",
        "postcode": "90210"
    },
    "businessType": "Accommodation and Food Services"
}

api.getBusinessFromId = jest.fn(() => {
    return Promise.resolve({data: mockBusinessUnedited, status: 200});
});

api.updateBusiness = jest.fn(() => {
    return Promise.resolve({data: mockBusinessEdited, status: 200});
});

api.getBusinessTypes = jest.fn(() => {
    return Promise.resolve({status: 200});
});

const $route = {
    params: {
        id: 1
    },
};

let $vs = {
    notify: jest.fn()
}

let $log = {
    debug: jest.fn()
}

beforeEach(() => {
    wrapper = mount(ModifyBusiness, {
        propsData: {},
        mocks: {$vs, store, $log, $route},
        stubs: [],
        methods: {},
        localVue,
    });
});

afterEach(() => {
    wrapper.destroy();
});

describe('Form checking', () => {
    test('handles empty form', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });


    test('handles new Name', () => {
        wrapper.vm.businessName = "Business2";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.businessName).toBe("Business2");
    });


    test('handles new street address', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "Ilam road";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.streetAddress).toBe("Ilam road");
    });


    test('handles new street number', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "456";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.streetNumber).toBe("456");
    });


    test('handles new suburb', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "New Suburb";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.suburb).toBe("New Suburb");
    });


    test('handles new postcode', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "6969";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.postcode).toBe("6969");
    });


    test('handles new region', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "Sengtopia";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.region).toBe("Sengtopia");
    });


    test('handles new city', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "Dystopia";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.city).toBe("Dystopia");
    });


    test('handles new country', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "Coscopolis";
        wrapper.vm.description = "";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.country).toBe("Coscopolis");
    });


    test('handles new description', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "The spiciest, the zestiest, and the dankest business there is!";
        wrapper.vm.businessType = null;
        wrapper.vm.checkForm();
        expect(wrapper.vm.description).toBe("The spiciest, the zestiest, and the dankest business there is!");
    });

    test('handles new businessType', () => {
        wrapper.vm.businessName = "";
        wrapper.vm.streetNumber = "";
        wrapper.vm.streetAddress = "";
        wrapper.vm.suburb = "";
        wrapper.vm.postcode = "";
        wrapper.vm.city = "";
        wrapper.vm.region = "";
        wrapper.vm.country = "";
        wrapper.vm.description = "";
        wrapper.vm.businessType = "Charitable organisation";
        wrapper.vm.checkForm();
        expect(wrapper.vm.businessType).toBe("Charitable organisation");
    });
});

describe('Modifying business', () => {
    test('successful edit', async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.businessName = "edited";
        wrapper.vm.streetNumber = "12";
        wrapper.vm.streetAddress = "Vernon Place";
        wrapper.vm.suburb = "shirley";
        wrapper.vm.postcode = "8013";
        wrapper.vm.city = "christchurch";
        wrapper.vm.region = "canterbury";
        wrapper.vm.country = "new zealand";
        wrapper.vm.description = "fun business";
        wrapper.vm.businessType = "Charitable organisation";
        wrapper.vm.checkForm();
        let button = wrapper.get(".modify-button");
        await button.trigger('click');
        expect(api.updateBusiness).toBeCalled();
    });
});