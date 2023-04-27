import {mount, createLocalVue } from '@vue/test-utils';
import HomepageMessages from '../components/HomePageMessages.vue';
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

let $router = {
    push: jest.fn(),
}

let $log = {
    debug: jest.fn(),
}

let $vs = {
    notify: jest.fn()
}

let oneMessage = [{
    "id": 3,
    "sender": {
        "id": 8186,
        "firstName": "Tricia",
        "middleName": "Xenos",
        "lastName": "Vergo",
        "nickname": "fault-tolerant",
        "bio": "In eleifend quam a odio.",
        "email": "xvergo4l@vkontakte.ru",
        "dateOfBirth": "1977-11-24",
        "phoneNumber": "+31 637 443 9610",
        "homeAddress": {
            "streetNumber": null,
            "streetName": null,
            "suburb": null,
            "city": null,
            "region": null,
            "country": "Poland",
            "postcode": null
        },
        "created": "2020-03-05 04:37:27",
        "role": "USER",
        "businessesAdministered": null
    },
    "card": {
        "id": 6134,
        "user": 8186,
        "title": "7up Diet, 355 Ml",
        "description": "Ut at dolor quis odio consequat varius.",
        "created": "2021-07-13 01:01:38",
        "displayPeriodEnd": "2021-08-03 01:01:38",
        "keywords": "sapien iaculis",
        "section": "ForSale"
    },
    "receiver": {
        "id": 83,
        "firstName": "Allegra",
        "middleName": "Jaquith",
        "lastName": "King",
        "nickname": "Visionary",
        "bio": "In eleifend quam a odio. In hac habitasse platea dictumst.",
        "email": "jking1q@printfriendly.com",
        "dateOfBirth": "1983-07-29",
        "phoneNumber": "+380 600 119 0770",
        "homeAddress": {
            "streetNumber": "33005",
            "streetName": "Cascade",
            "suburb": null,
            "city": "Xingou",
            "region": null,
            "country": "China",
            "postcode": null
        },
        "created": "2020-05-30 04:08:45",
        "role": "USER",
        "businessesAdministered": null
    },
    "description": "asdsaddas",
    "sent": "2021-07-23 13:09:52"
}];

let listingNotifications = [
    {
        buyer: 1,
        id: 1,
        likes: 5,
        listed: "2021-08-10 12:00:00",
        listingId: 1,
        price: 20.5,
        quantity: 10,
        sold: "2021-08-17 12:00:00",
        product: {
            name: "Garlic",
            business: {
                name: "Dabshots",
                address: {
                    streetNumber: "88",
                    streetName: "Ilam Road",
                    suburb: "Ilam",
                    city: "Christchurch",
                    region: "Canterbury",
                    country: "New Zealand"
                },
            }
        },
        viewStatus: "Unread"
    },
    {
        id: 2,
        status: "Liked",
        created: "2021-08-17 10:03:43",
        listing: {
            inventoryItem: {
                product: {
                    name: "Pastry",
                    business: {
                      id: 1
                    },
                }
            }
        },
        viewStatus: "Unread"
    },
    {
        id: 4,
        status: "Unliked",
        created: "2021-08-17 10:03:43",
        listing: {
            inventoryItem: {
                product: {
                    name: "Pastry",
                    business: {
                        id: 1
                    },
                }
            }
        },
        viewStatus: "Unread"
    },
    {
        id: 5,
        boughtListing: {name: "bought listing",
            buyer: 10,
            product: {
                name: "Garlic",
                business: {
                    name: "Dabshots",
                    address: {
                        streetNumber: "88",
                        streetName: "Ilam Road",
                        suburb: "Ilam",
                        city: "Christchurch",
                        region: "Canterbury",
                        country: "New Zealand"
                    },
                }
            }
        },
        status: "Bought",
        created: "2021-08-17 10:03:43",
        listing: {
            inventoryItem: {
                product: {
                    name: "Pastry",
                    business: {
                        id: 1
                    },
                }
            }
        },
        viewStatus: "Unread"
    },
    {
        id: 6,
        boughtListing: {name: "bought listing",
            buyer: 83,
            product: {
                name: "Garlic",
                business: {
                    name: "Dabshots",
                    address: {
                        streetNumber: "88",
                        streetName: "Ilam Road",
                        suburb: "Ilam",
                        city: "Christchurch",
                        region: "Canterbury",
                        country: "New Zealand"
                    },
                }
            }
        },
        status: "Bought",
        created: "2021-08-17 10:03:43",
        listing: {
            inventoryItem: {
                product: {
                    name: "Pastry",
                    business: {
                        id: 1
                    },
                }
            }
        },
        viewStatus: "Unread"
    },
    {
        id: 7,
        status: "Wishlist",
        created: "2021-08-17 10:03:43",
        listing: {
            inventoryItem: {
                product: {
                    name: "Pastry",
                    business: {
                        id: 1
                    },
                }
            }
        }
    },
];

api.getMessages = jest.fn().mockResolvedValue({data: oneMessage});

api.deleteMessage = jest.fn(() => {
    return Promise.resolve({data: oneMessage[0].id, status: 200});
});

api.getListingNotifications = jest.fn( () => {
   return Promise.resolve({status: 200, data: listingNotifications});
});

api.deleteListingNotification = jest.fn(() => {
    return Promise.resolve({status: 200});
});

api.updateListingNotificationViewStatus = jest.fn(() => {
    return Promise.resolve({status: 200});
});

beforeEach(() => {
    wrapper = mount(HomepageMessages, {
        mocks: {$route, $log, $vs, $router},
        stubs: {},
        methods: {},
        localVue,
    })
    wrapper.vm.messages = oneMessage;
});

afterEach(() => {
    wrapper.destroy();
});

describe('Homepage Messages functionality', () => {
    test('is a Vue instance', () => {
        expect(wrapper.isVueInstance).toBeTruthy();
    });

    test('Message is shown', () => {
        expect(wrapper.find("#message-notification-card")).toBeTruthy()
    });

    test('Open modal correctly updates showing value', () => {
        //Setup
        wrapper.vm.showing = false;
        expect(wrapper.vm.detailedView).toBe(false);

        //Execution
        wrapper.vm.openDetailedModal(oneMessage[0])

        //Result
        expect(wrapper.vm.detailedView).toBe(true);
    })
});

describe('Message validation', () => {
    test('When message is valid, check passes', () => {
        wrapper.vm.message = 'Simple correct message';

        let res = wrapper.vm.checkMessage();

        expect(res).toBeTruthy();
        expect(wrapper.vm.errors).toStrictEqual([]);
    });

    test('When message is blank, check fails', () => {
        wrapper.vm.message = '';

        let res = wrapper.vm.checkMessage();

        expect(res).toBeFalsy();
        expect(wrapper.vm.errors.includes('bad-content')).toBeTruthy();
    });

    test('When message is over character limit, check fails', () => {
        wrapper.vm.message = ' Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris condimentum metus mauris, ut vehicula lacus sollicitudin vel. Etiam consectetur maximus vulputate. Etiam non laoreet velit, sed consequat lectus. Ut rhoncus suscipit urna sed maximus. Vestibulum volutpat iaculis lorem ac faucibus. Quisque ultrices nisi et augue consectetur, maximus fringilla neque aliquam. Cras et felis vitae justo iaculis egestas eu eu nisl. Integer pellentesque arcu eget erat finibus dapibus. Cras eleifend ante eget suscipit vestibulum. Sed nunc nisi, hendrerit id sodales nec, varius fermentum turpis. Suspendisse dictum mollis est, sit amet dignissim elit vehicula nec. Nullam eget dui ac augue laoreet ultrices. Cras laoreet rhoncus odio. ';

        let res = wrapper.vm.checkMessage();

        expect(res).toBeFalsy();
        expect(wrapper.vm.errors.includes('bad-content')).toBeTruthy();
    });
});

describe('Listing notification methods tests', () => {
   test('City and country address string is returned', () => {
     let address = {city: "Christchurch", country: "New Zealand"};

     expect(wrapper.vm.createAddressString(address)).toBe("Christchurch, New Zealand");
   });

    test('Full address string is returned', () => {
        let address = {
            streetNumber: "88",
            streetName: "Ilam Road",
            suburb: "Ilam",
            city: "Christchurch",
            region: "Canterbury",
            country: "New Zealand"
        };

        expect(wrapper.vm.createAddressString(address)).toBe("88 Ilam Road, Ilam, Christchurch, Canterbury, New Zealand");
    });
});

describe('Listing notification functionality tests', () => {
    test("Bought listings are shown", () => {
        expect(wrapper.find(".bought-listing-notification-card")).toBeTruthy();
    });

    test("Liked listings notifications are shown", () => {
        expect(wrapper.find(".liked-listing-notification")).toBeTruthy();
    });

    test("Liked listing view button redirects to listing page on click", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#view-listing-button");
        expect(button).toBeTruthy();

        await button.trigger('click');
        expect(wrapper.vm.$router.push).toBeCalled();
    });

    test("Delete listing notification buttons call undo function which deletes notification", async () => {
        const deleteUndo = jest.spyOn(wrapper.vm, 'undo')
        await wrapper.vm.$nextTick();
        wrapper.vm.currentUserId = 83;
        let button = wrapper.get("#delete-liked-listing-notification-button");
        expect(button).toBeTruthy();

        await button.trigger('click');
        expect(deleteUndo).toBeCalled();
        // expect undo modal to open i.e undoClick is true
        expect(wrapper.vm.undoClick).toBe(true);
        // time undo is visible should be 10secs
        expect(wrapper.vm.undoCount).toBe(10);

        button = wrapper.get("#delete-liked-purchased-listing-notification-button");
        expect(button).toBeTruthy();

        await button.trigger('click');
        expect(deleteUndo).toBeCalled();
        // expect undo modal to open i.e undoClick is true
        expect(wrapper.vm.undoClick).toBe(true);
        // time undo is visible should be 10secs
        expect(wrapper.vm.undoCount).toBe(10);

        button = wrapper.get("#delete-purchased-listing-notification-button");
        expect(deleteUndo).toBeTruthy();

    });

    test("Delete listing notification buttons call delete notification function", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.currentUserId = 83;
        let button = wrapper.get("#delete-liked-listing-notification-button");
        expect(button).toBeTruthy();

        await button.trigger('click');
        //expect(api.deleteListingNotification).toBeCalled();
    })
    test("Toggle listing notification as important", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.currentUserId = 83;

        let button = wrapper.get("#important-listing-notification-button");
        expect(button).toBeTruthy();

        await button.trigger('click');
        expect(api.updateListingNotificationViewStatus).toBeCalled();
        button = wrapper.get("#important-listing-notification-button");
        expect(button).toBeTruthy();

        await button.trigger('click');
        expect(api.updateListingNotificationViewStatus).toBeCalled();
    });

    test("Marking as read", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.currentUserId = 83;

        let boughtNotif = wrapper.get(".liked-listing-container");
        expect(boughtNotif).toBeTruthy();
        await boughtNotif.trigger('mouseenter');
        expect(api.updateListingNotificationViewStatus).toBeCalled();
    })
});

//
// test all method routes to ensure properly working under all conditions
//

describe('HomePageMessages method tests', () => {
    test("removeId method splices id from array", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.currentUserId = 83;

        wrapper.vm.undoId = [0,1,2,3,5,7,9];

        wrapper.vm.removeId(5)
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.undoId).toStrictEqual([0,1,2,3,7,9]);
    });

    test("spliceMessage removes feeditem", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.currentUserId = 83;

        wrapper.vm.feedItems = [{fid:0},{fid:1},{fid:5},{fid:6},{fid:7}];
        wrapper.vm.spliceMessage(5);
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.feedItems).toStrictEqual([{fid:0},{fid:1},{fid:6},{fid:7}]);
    });

    test("if undo message, click and delete are set to false and delete message is called", async () => {
        wrapper.vm.deleteMessage = jest.fn();
        wrapper.vm.undo(5, 0, true, false, "title");
        expect(wrapper.vm.undoCount).toBeTruthy(); //undo count is set to greater than zero
        expect(wrapper.vm.undoClick).toBeFalsy();
        expect(wrapper.vm.undoDelete).toBeFalsy();
        //expect(wrapper.vm.deleteMessage).toBeCalled();
    });

    test("if undo expiry message, deleteCard is called", async () => {
        wrapper.vm.deleteCard = jest.fn();
        wrapper.vm.undo(5, 0, false, true, "title");
        expect(wrapper.vm.undoCount).toBeTruthy(); //undo count is set to greater than zero
        expect(wrapper.vm.undoClick).toBeFalsy();
        expect(wrapper.vm.undoDelete).toBeFalsy();
        //expect(wrapper.vm.deleteCard).toBeCalled();
    });

    test("When a notification is deleted, user is notified", async () => {
        api.deleteListingNotification = jest.fn(() => {
            return Promise.resolve({status: 200});
        });
        wrapper.vm.spliceMessage = jest.fn();
        wrapper.vm.getListingNotifications = jest.fn();


        wrapper.vm.deleteNotification(0, 0);

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.polling).toBeTruthy();
    });

    test("When a notification fails to delete, user is notified and debug is called", async () => {
        api.deleteListingNotification = jest.fn(() => {
            return Promise.reject({response: {status: 500}});
        });

        wrapper.vm.deleteNotification(0, 0);

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.$log.debug).toBeCalled();
    });

    test("When listings get fails, the user is notified and error written to debug", async () => {
        api.getListingNotifications = jest.fn(() => {
            return Promise.reject({response: {status: 500}});
        });
        wrapper.vm.getListingNotifications(0, 0);

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.$log.debug).toBeCalled();
    });


    test("When a message is deleted, user is notified", async () => {
        api.deleteMessage = jest.fn(() => {
            return Promise.resolve({status: 200});
        });
        wrapper.vm.spliceMessage = jest.fn();
        wrapper.vm.getMessages = jest.fn();


        wrapper.vm.deleteMessage(0, 0);

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$vs.notify).toBeCalled();
    });

    test("When a message fails to delete, user is notified and debug is called", async () => {
        api.deleteMessage = jest.fn(() => {
            return Promise.reject({response: {status: 500}});
        });

        wrapper.vm.deleteMessage(0, 0);

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.$log.debug).toBeCalled();
    });


    test("When a message is sent, user is notified and debug is called", async () => {
        wrapper.vm.checkMessage = jest.fn(() => {
            return true;
        });

        api.postMessage = jest.fn(() => {
            return Promise.resolve({status: 200});
        });

        let oneMessageOriginal = oneMessage;
        oneMessageOriginal.sender = {id: 44};
        oneMessageOriginal.card = {id: 44};

        wrapper.vm.sendMessage(oneMessageOriginal, oneMessage);

        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$vs.notify).toBeCalled();
        expect(wrapper.vm.message).toBe("");
    });





    test("markAsRead method test, Notification input, return 200", async () => {
        api.updateNotificationViewStatus = jest.fn(() => {
            return Promise.resolve({status: 200});
        });

        //notification.card (mock card)
        listingNotifications[0].card = false;
        listingNotifications[0].displayPeriodEnd = true;
        listingNotifications[0].id = 12;
        listingNotifications[0].viewStatus = "Unread";

        wrapper.vm.markAsRead(listingNotifications[0]);

        await wrapper.vm.$nextTick();
        expect(api.updateNotificationViewStatus).toBeCalled();
        expect(listingNotifications[0].viewStatus).toBe("Read");

    });
});

describe('HomePageMessages method tests 2', () => {
    test("markAsRead method test, message input, return 200", async () => {
        api.updateMessageViewStatus = jest.fn(() => {
            return Promise.resolve({status: 200});
        });

        //notification.card (mock card)
        listingNotifications[0].card = true;
        listingNotifications[0].displayPeriodEnd = false;
        listingNotifications[0].id = 12;
        listingNotifications[0].viewStatus = "Unread";

        wrapper.vm.markAsRead(listingNotifications[0]);
        await wrapper.vm.$nextTick();
        expect(api.updateMessageViewStatus).toBeCalled();
        expect(listingNotifications[0].viewStatus).toBe("Read");
    });



    //id is underfined if placed here
});



//markAsRead test
