import {mount, createLocalVue } from '@vue/test-utils';
import CardModal from '../components/CardModal.vue';
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

let $log = {
    debug: jest.fn(),
    error: jest.fn(),
}

let cardDetails = {
    id: 1,
    user: {
    id: 6077,
    firstName: "Goldy",
    middleName: "Henri",
        lastName: "Chadburn",
        nickname: "ability",
        bio: "Mauris sit amet eros.",
        email: "hchadburn1k@zdnet.com",
        dateOfBirth: "1987-06-10",
        phoneNumber: "+86 627 626 5485",
        homeAddress: {
            streetNumber: "0",
            streetName: "Briar Crest",
            suburb: null,
            city: "Qinhong",
            region: null,
            country: "China",
            postcode: null
        },
        created: "2019-01-27 00:39:30",
        role: "USER",
        businessesAdministered: []
    },
    title: "Beans - Green",
    description: "Integer ac leo.",
    created: "2021-06-01 18:32:38",
    displayPeriodEnd: "2021-06-15 18:32:38",
    keywords: "aliquam augue quam",
    section: "Wanted"
}

beforeEach(() => {
    wrapper = mount(CardModal, {
        propsData: {
            selectedCard: cardDetails
        },
        mocks: {$route, $log, $vs},
        stubs: {},
        methods: {},
        localVue,
    });

    wrapper.vm.title = "Valid Title";
    wrapper.vm.section = "ForSale";

});

afterEach(() => {
    wrapper.destroy();
});

describe('Card modal functionality', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Reset state correctly resets values', () => {
        //Setup
        wrapper.vm.message = "Hello"
        wrapper.vm.messaging = true;

        expect(wrapper.vm.message).toBe("Hello")
        expect(wrapper.vm.messaging).toBe(true);

        //Execution
        wrapper.vm.resetState()

        //Result
        expect(wrapper.vm.message).toBe("")
        expect(wrapper.vm.messaging).toBe(false);
    });

    test('Open modal correctly updates showing value', async () => {
        //Setup
        wrapper.vm.showing = false;
        expect(wrapper.vm.showing).toBe(false);

        //Execution
        await wrapper.vm.openModal()

        //Result
        expect(wrapper.vm.showing).toBe(true);
    })

    test('Open modal calls resetState', async () => {


        //Setup
        wrapper.vm.resetState = jest.fn();
        wrapper.vm.showing = false;
        expect(wrapper.vm.showing).toBe(false);

        //Execution
        await wrapper.vm.openModal()

        expect(wrapper.vm.resetState).toBeCalled()
    })
});


describe('Card modal UI', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Message button opens message box', () => {
        //Setup
        wrapper.vm.showing = true;
        let button = wrapper.find(".card-modal-message-button")

        //Execution
        button.trigger("click")

        //Result
        expect(wrapper.vm.messaging).toBeTruthy();
        expect(wrapper.find("#card-modal-message")).toBeTruthy();
    });

    test('User is not the owner - successfully shows message button', async () => {
        api.checkSession = jest.fn(() => {
           return Promise.resolve({status: 200, data: {id: 2}});
        });

        await wrapper.vm.getUserId();

        expect(wrapper.find(".card-modal-message-button")).toBeTruthy();
    });

    test('User is card owner - successfully shows edit button', async () => {
        api.checkSession = jest.fn(() => {
            return Promise.resolve({status: 200, data: {id: 1}});
        });

        await wrapper.vm.getUserId();

        expect(wrapper.find(".card-modal-edit-button")).toBeTruthy();
    });
    test('With blank keywords, Test prefills are are correctly called and keywordList array is empty', async () => {
        //setup test
        wrapper.vm.setPrefills = jest.fn();

        api.checkSession = jest.fn(() => {
            return Promise.resolve({status: 200, data: {id: 1}});
        });
        await wrapper.vm.getUserId();
        //checks that when edit button is called prefills are inserted
        let button = wrapper.find(".card-modal-edit-button");
        expect(button).toBeTruthy();

        wrapper.vm.setPrefills()
        expect(wrapper.vm.setPrefills).toBeCalled()

    })
});

describe('Card editing', () => {
   test('Edited card title is too long', async () => {
      wrapper.vm.title = "OverFiftyCharactersLong".repeat(4);
      await wrapper.vm.$nextTick();

      expect(wrapper.vm.editErrors.title.error).toBeTruthy();
      expect(wrapper.vm.editErrors.title.message).toBe("Card title is too long");
   });

    test('Edited card title is too short', async () => {
        wrapper.vm.title = ""; // No longer valid.
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.editErrors.title.error).toBeTruthy();
        expect(wrapper.vm.editErrors.title.message).toBe("A valid card title is required");
    });

    test('Edited card title is invalid', async () => {
        wrapper.vm.title = "  ";
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.editErrors.title.error).toBeTruthy();
        expect(wrapper.vm.editErrors.title.message).toBe("A valid card title is required");
    });


    test('Edited card title is of valid length', async () => {
        wrapper.vm.title = "Valid";
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.editErrors.title.error).toBeFalsy();
    });

    test('Edited card has missing section', async () => {
        wrapper.vm.section = "";
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.editErrors.section.error).toBeTruthy();
    });

    test('Edited card has valid section', async () => {
        wrapper.vm.section = "Wanted";
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.editErrors.section.error).toBeFalsy();
    });

    test('Card edit is invalid - bad title', async () => {
        wrapper.vm.title = "OverFiftyCharactersLong".repeat(4);
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.validateCardEdit()).toBeFalsy();
    });

    test('Card edit is invalid - bad section', async () => {
        wrapper.vm.section = ""
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.validateCardEdit()).toBeFalsy();
    });

    test('Card edit is valid', async () => {
        wrapper.vm.title = "New Edited Title";
        wrapper.vm.section = "Exchange";
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.validateCardEdit()).toBeTruthy();
    });
});

describe('Messaging', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Check Prefills accurate', () => {
        wrapper.vm.setPrefills();

        expect(wrapper.vm.editing).toBeTruthy();
        expect(wrapper.vm.title).toBe(wrapper.vm.selectedCard.title);
        expect(wrapper.vm.description).toBe(wrapper.vm.selectedCard.description);
        expect(wrapper.vm.section).toBe(wrapper.vm.selectedCard.section);
        expect(wrapper.vm.keywordList).toStrictEqual(["aliquam", "augue", "quam"]);
    });


    test('Message box shows with input', () => {
        //Setup
        wrapper.vm.showing = true;
        wrapper.vm.messaging = true;

        let button = wrapper.find(".card-modal-message-button")
        button.trigger("click")

        //Result
        expect(wrapper.vm.messaging).toBeTruthy();
        expect(wrapper.find("#card-modal-message")).toBeTruthy();
        expect(wrapper.find("#message-input")).toBeTruthy();
        expect(wrapper.find("#card-modal-message-send")).toBeTruthy();
    });


    test('Message is checked after send', () => {
        api.postMessage = jest.fn(() => {
            return Promise.resolve({status: 201, data: {id: 1}});
        });

        wrapper.vm.checkMessage = jest.fn(() => {
            return true;
        });

        wrapper.vm.message = "message desc";
        wrapper.vm.selectedCard.user.id = 1;
        wrapper.vm.selectedCard.id = 1;

        wrapper.vm.sendMessage(wrapper.vm.selectedCard, wrapper.vm.message);

        expect(wrapper.vm.checkMessage).toBeCalled();
    });

    test('When user inputs failed data, Message is checked and user is notified', () => {
        api.postMessage = jest.fn(() => {
            return Promise.resolve({status: 201, data: {id: 1}});
        });

        wrapper.vm.message = "message desc";
        wrapper.vm.errors = [];
        wrapper.vm.selectedCard.userId = "asdf"; //server returns the user's id instead of the object
        wrapper.vm.selectedCard.id = "asdf";

        wrapper.vm.checkMessage();

        expect(wrapper.vm.errors.includes('invalid-card')).toBeTruthy();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });

    test('User is card owner - successfully shows delete button', async () => {
        api.checkSession = jest.fn(() => {
            return Promise.resolve({status: 200, data: {id: 1}});
        });

        await wrapper.vm.getUserId();

        expect(wrapper.find(".card-modal-delete-button")).toBeTruthy();

    });

    test('After sent success, Message clears and user is notified', () => {
        api.postMessage = jest.fn(() => {
            return Promise.resolve({status: 201, data: {messageId: 1}});
        });

        wrapper.vm.sendPostMessage(7, 12, "message");

        expect(wrapper.vm.message).toBe("");
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });


    test('On null recipient, User is notified and message is not cleared', () => {
        wrapper.vm.message = "message";
        wrapper.vm.recipient = null;
        wrapper.vm.selectedCard.user.id = null;
        wrapper.vm.selectedCard.userId = null;

        wrapper.vm.checkMessage();

        expect(wrapper.vm.message).toBe("message");
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });

    test('On blank message, User is notified', () => {
        wrapper.vm.message = "";
        wrapper.vm.recipient = 7;

        wrapper.vm.checkMessage();
        expect(wrapper.vm.errors.includes('bad-content')).toBeTruthy();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });

    test('On Long message, User is notified', () => {
        wrapper.vm.message = " Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis " +
            "egestas. Pellentesque sed tortor id lacus finibus aliquet at vitae nulla. Duis leo tortor, hendrerit at " +
            "auctor non, convallis et enim. Ut aliquam consequat diam. Cras leo metus, tempus et dignissim ac, tempus " +
            "quis purus. Etiam ornare, quam ac porttitor laoreet, arcu odio ullamcorper tellus, sit amet aliquam nisi " +
            "enim ut nibh. Nulla vitae feugiat arcu, sit amet semper nisl. Vestibulum ac faucibus dolor, quis cursus " +
            "dolor. Aliquam sagittis risus orci, eu aliquam orci feugiat in. Aliquam nec orci tortor. Duis id rutrum felis. " +
            "Sed eget lacus porta, malesuada orci a, porttitor nisl. In turpis nisi, sagittis eu pulvinar vitae, finibus ac mi. ";
        wrapper.vm.recipient = 7;

        wrapper.vm.checkMessage();
        expect(wrapper.vm.errors.includes('bad-content')).toBeTruthy();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });

    test('On null card, User is notified', () => {
        wrapper.vm.message = "";
        wrapper.vm.errors = [];

        wrapper.vm.recipient = null;
        wrapper.vm.selectedCard.id = null;
        wrapper.vm.selectedCard.user.id = null;
        wrapper.vm.selectedCard.userId = null;

        wrapper.vm.checkMessage();

        expect(wrapper.vm.errors);
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });
});

describe('Messaging returns', () => {
    test('When a modified card is saved, User is notified, card closes and delete event', async () => {
        api.modifyCard = jest.fn(() => {
            return Promise.resolve({status: 201, data: {messageId: 1}});
        });
        wrapper.vm.validateCardEdit = jest.fn(() => {
            return true;
        });
        wrapper.vm.keywordList = ["key1", "key2"]

        await wrapper.vm.saveCardEdit();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.showing).toBeFalsy();
        expect(wrapper.vm.keywords).toBe('key1 key2 ');
    });

    test('When a modified card is not saved (400), User is notified', async () => {
        api.modifyCard = jest.fn(() => {
            return Promise.reject({response: { status: 400}});
        });

        await wrapper.vm.saveCardEdit();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.keywords).toBe('');
    });


    test('When a modified card is not saved (401/403), User is notified', async () => {
        api.modifyCard = jest.fn(() => {
            return Promise.reject({response: {message: "Bad request", status: 403}});
        });

        await wrapper.vm.saveCardEdit();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.keywords).toBe('');
    });

    test('When a modified card is not saved with an unspecified error (500), User is notified', async () => {
        api.modifyCard = jest.fn(() => {
            return Promise.reject({response: {status: 500}});
        });

        await wrapper.vm.saveCardEdit();

        expect(wrapper.vm.$vs.notify).toBeCalled();
    });


    test('When a modified card includes errors, User is notified', () => {
        api.modifyCard = jest.fn(() => {
            return Promise.resolve({status: 201, data: {messageId: 1}});
        });
        wrapper.vm.validateCardEdit = jest.fn(() => {
            return false;
        });

        wrapper.vm.showing = true;

        wrapper.vm.saveCardEdit();

        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.showing).toBeTruthy();
    });
});
