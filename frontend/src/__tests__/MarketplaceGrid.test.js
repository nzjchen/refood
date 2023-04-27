import {mount, createLocalVue } from '@vue/test-utils';
import MarketplaceGrid from '../components/MarketplaceGrid.vue';
import Vuesax from 'vuesax';

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

let $log = {
    debug: jest.fn(),
}

let testCards = [
    {"id":11,"user":{"id":645,"firstName":"Zola","middleName":"Urbanus","lastName":"Starford","nickname":"Public-key","bio":"Nunc nisl.","email":"ustarfordhc@sbwire.com","dateOfBirth":"1990-11-20","phoneNumber":"+51 791 345 8010","homeAddress":{"streetNumber":null,"streetName":null,"suburb":null,"city":null,"region":null,"country":"China","postcode":null},"created":"2019-04-15 22:50:15","role":"USER","businessesAdministered":[{"name":"Candas","id":246,"administrators":[645],"primaryAdministratorId":645,"description":"Vestibulum quam sapien, varius ut, blandit non, interdum in, ante.","address":{"streetNumber":null,"streetName":null,"suburb":null,"city":null,"region":null,"country":"Philippines","postcode":null},"businessType":"Accommodation and Food Services","created":"2019-08-06 20:39:42"}]},"title":"Lid - 0090 Clear","description":"Nullam molestie nibh in lectus.","created":"2021-06-08 18:32:38","displayPeriodEnd":"2021-06-22 18:32:38","keywords":"lectus","section":"Wanted"},
    {"id":10,"user":{"id":2855,"firstName":"Alfonso","middleName":"Haslett","lastName":"Skipworth","nickname":"Phased","bio":"Maecenas rhoncus aliquam lacus. Morbi quis tortor id nulla ultrices aliquet.","email":"hskipworthn6@freewebs.com","dateOfBirth":"1990-12-08","phoneNumber":"+86 695 791 1499","homeAddress":{"streetNumber":"555","streetName":"Amoth","suburb":null,"city":"Gucheng","region":null,"country":"China","postcode":null},"created":"2019-12-06 14:27:19","role":"USER","businessesAdministered":[]},"title":"Pears - Bartlett","description":"Nam tristique tortor eu pede.","created":"2021-06-02 18:32:38","displayPeriodEnd":"2021-06-16 18:32:38","keywords":"accumsan","section":"Wanted"}
    ];

beforeEach(() => {
    wrapper = mount(MarketplaceGrid, {
        mocks: {$vs, $route, $log},
        stubs: {},
        methods: {},
        propsData: {
            cardData: testCards
        },
        localVue,
    })
    wrapper.vm.$emit = jest.fn();
    wrapper.vm.cards = testCards;
    wrapper.vm.selectedCard = testCards[0];
});

afterEach(() => {
    wrapper.destroy();
});

describe('Component', () => {


    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('When a card is clicked, open modal is called', async () => {
        wrapper.vm.openCardModal = jest.fn();

        let button = wrapper.find('#bOpenModal');
        button.trigger('click')
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.openCardModal).toBeCalled();
    });

    test('When openCardModal is called, the selected card is set', async () => {
        wrapper.vm.openCardModal(testCards[0]);
        expect(wrapper.vm.selectedCard).toBe(testCards[0]);
    });



});