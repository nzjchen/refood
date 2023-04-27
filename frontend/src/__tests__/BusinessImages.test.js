import { mount, createLocalVue } from '@vue/test-utils';
import BusinessImages from '../components/BusinessImages';
import Vuesax from 'vuesax';
import api from '../Api';


let wrapper;
let localVue = createLocalVue();
localVue.use(Vuesax);

const mockBusiness = {
    "id": 1,
    "administrators": [
        {
            "id": 9,
            "firstName": "Joete",
            "middleName": "YEP",
            "lastName": "Stopps",
            "nickname": "Multi-layered",
            "bio": "responsive capacity",
            "email": "jstopps7@flickr.com",
            "dateOfBirth": "1984-10-14",
            "phoneNumber": "+36 694 564 9090",
            "homeAddress": "34 Mendota Avenue",
            "created": "2021-04-03 23:29:50",
            "role": "USER",
            "businessesAdministered": [
                1
            ]
        }
    ],
    "name": "Business1",
    "description": "Test Business 1",
    "address": "123 Test Street",
    "businessType": "Accommodation and Food Services",
    "created": "2021-04-03 23:29:50",
    "primaryImagePath": "business_1/12.png",
    "images": [
        {
            "name": "image1",
            "id": "0",
            "fileName": "business_1/12.png"
        }
    ]
}

const $vs = {
    notify: jest.fn(),
    loading: jest.fn(),
};

const store = {
    actingAsBusinessId: 1
}

const $log = {
    error: jest.fn(),
    debug: jest.fn(),
}

const $refs = {

}

const $emit = jest.fn();

const location = jest.fn();

api.changeBusinessPrimaryImage = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200});
})

api.deleteBusinessImage = jest.fn().mockImplementation(() => {
    return Promise.resolve({status: 200});
})

beforeEach(() => {
    wrapper = mount(BusinessImages, {
        propsData: {},
        mocks: {store, $vs, $log, $refs, $emit, location},
        stubs: ['router-link', 'router-view'],
        methods: {},
        localVue
    });
    wrapper.setProps({business: mockBusiness, images: mockBusiness.images});
});

afterEach(() => {
    wrapper.destroy();
});

describe("Business images tests", () => {
    test("Primary image updated successfully", () => {
        wrapper.vm.$vs.loading.close = jest.fn();
        wrapper.vm.updatePrimaryImage(1);
        expect(wrapper.vm.$vs.loading).toBeCalled();
    })

    test("Delete image called successfully", () => {
        wrapper.vm.$vs.loading.close = jest.fn();
        wrapper.vm.deleteImage(1);
        expect(wrapper.vm.$vs.loading).toBeCalled();
    })
});