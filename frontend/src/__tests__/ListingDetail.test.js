import {mount, createLocalVue} from '@vue/test-utils';
import ListingDetail from "../components/ListingDetail"
import Vuesax, { wrap } from 'vuesax';
import api from "../Api";
import axios from "axios";

let wrapper;

const localVue = createLocalVue();
localVue.use(Vuesax);

let testListing = {
  "id": 1,
  "inventoryItem": {
      "id": 1,
      "product": {
          "id": "WAUVT64B54N722288",
          "business": {
              "name": "Dabshots",
              "id": 1,
              "administrators": [],
              "primaryAdministratorId": null,
              "description": "Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.",
              "address": {
                  "streetNumber": "0",
                  "streetName": "Vernon Place",
                  "suburb": null,
                  "city": "Sarpang",
                  "region": null,
                  "country": "Bhutan",
                  "postcode": null
              },
              "businessType": "Charitable organisation",
              "created": "2020-05-18 09:06:11"
          },
          "name": "Pastry - Cheese Baked Scones",
          "description": "amet erat nulla tempus vivamus",
          "manufacturer": "Watties",
          "recommendedRetailPrice": 19.88,
          "created": "2021-03-05 01:36:54",
          "images": [
              {
                  "id": "0",
                  "thumbnailFilename": "./src/main/resources/media/images/businesses/business_1/0_thumbnail.png",
                  "name": "Screen Shot 2021-07-14 at 2.10.52 PM.png",
                  "fileName": ".\\src\\main\\resources\\media\\images\\businesses\\business_1\\0.png"
              },
              {
                  "id": "1",
                  "thumbnailFilename": "./src/main/resources/media/images/businesses/business_1/1_thumbnail.jpg",
                  "name": "2D9C9C8C-F6D7-47D9-975A-B5DC86C05D93.jpeg",
                  "fileName": ".\\src\\main\\resources\\media\\images\\businesses\\business_1\\1.jpg"
              },
              {
                  "id": "2",
                  "thumbnailFilename": "./src/main/resources/media/images/businesses/business_1/2_thumbnail.jpg",
                  "name": "77C5D008-CF03-4E74-AA13-3604DA450AB3_1_201_a.jpeg",
                  "fileName": ".\\src\\main\\resources\\media\\images\\businesses\\business_1\\2.jpg"
              },
              {
                  "id": "3",
                  "thumbnailFilename": "./src/main/resources/media/images/businesses/business_1/3_thumbnail.jpg",
                  "name": "078EAAE4-42F1-47B0-B578-A098C03232C8_1_201_a.jpeg",
                  "fileName": ".\\src\\main\\resources\\media\\images\\businesses\\business_1\\3.jpg"
              }
          ],
          "primaryImagePath": "business_1\\0.png"
      },
      "quantity": 10,
      "pricePerItem": 5,
      "totalPrice": 50,
      "manufactured": "2021-01-26",
      "sellBy": "2021-05-25",
      "bestBefore": "2021-05-27",
      "expires": "2021-05-27"
  },
  "quantity": 3,
  "price": 10,
  "moreInfo": "Not negotiable.",
  "created": "2021-01-26 23:00:00",
  "closes": "2021-09-22 00:00:00",
  "likes": 0
}


let $route = {
  params: {
      businessId: 1,
      listingId: 1,
  }
}

axios.get = jest.fn(() => {
   return Promise.resolve({data: [{
        currencies: [{symbol: "€"}],
       }]}
   );
});


jest.mock("../Api.js", () => jest.fn);
api.getBusinessListings = jest.fn(() => {
    return Promise.resolve({data: testListing, status: 200});
});

api.deleteListing = jest.fn(() => {
    return Promise.resolve();
});

api.postListingNotification = jest.fn(() => {
    return Promise.resolve({status: 201});
});

const $router = {
    push: jest.fn()
}

const $vs = {
    notify: jest.fn()
}


api.addLikeToListing = jest.fn(() => {
    return Promise.resolve({status: 201});
});

api.removeLikeFromListing = jest.fn(() => {
    return Promise.resolve({status: 200});
});

api.getUserLikedListings = jest.fn(() => {
    return Promise.resolve({data:{}, status: 200});
});

let $log = {
    error: jest.fn(),
    debug: jest.fn(),
}

beforeEach(() => {
    wrapper = mount(ListingDetail, {
        mocks: {$log, api, $route, $router, $vs},
        stubs: {},
        methods: {},
        localVue,
    });
    wrapper.vm.listing = testListing

});

afterEach(() => {
  wrapper.destroy();
});

describe('Component', () => {
  test('is a Vue instance', () => {
    expect(wrapper.isVueInstance).toBeTruthy();
  });

  test("Listings data is not empty.", () => {
    expect(wrapper.vm.listing).toBeTruthy();
  });
});



describe('Testing methods', () => {
    test('Get listing images returns correct amount of images', () => {
        let images = wrapper.vm.listing.inventoryItem.product.images;
        let listImages = wrapper.vm.getListingImages(images);
        expect(listImages.length).toBe(images.length)
    })

    test('Get listing images returns correct amount of images with no images', () => {
        let listImages = wrapper.vm.getListingImages([]);
        expect(listImages.length).toBe(0)
        })

        test('Get listing images returns correct amount of images with random data', () => {
        let listImages = wrapper.vm.getListingImages([{fileName: 'aaa'}, {fileName: 'aadsadsaa'}, {fileName: 'aabbbbb'}]);
        expect(listImages.length).toBe(3)
    })


    test('Get primary image returns correct image', () => {
        wrapper.vm.listing = testListing;
        let images = wrapper.vm.listing.inventoryItem.product.images;
        let primaryImagePath = wrapper.vm.listing.inventoryItem.product.primaryImagePath
        let listImages = wrapper.vm.getListingImages(images);

        let primaryImage = wrapper.vm.getPrimaryImage(listImages, wrapper.vm.listing);

        expect(primaryImage).toBe(".\\src\\main\\resources\\media\\images\\businesses\\" + primaryImagePath)
        })

        test('Filter listing response returns correct listing', () => {
        let fakeResponse = [{id: 1}, {id: 2}, {id: 3}];

        let fakeListing = wrapper.vm.filterListingFromListingsResponse(fakeResponse, 1);

        expect(fakeListing).toBe(fakeResponse[0]);
    })


    test('Filter listing response returns nothing with bad values', () => {
        let fakeResponse = [{id: 2}, {id: 3}, {id: 4}];

        let fakeListing = wrapper.vm.filterListingFromListingsResponse(fakeResponse, 1);

        expect(fakeListing).toBe(undefined);
        })

        test('Filter listing response with duplicated id returns first occurrence', () => {
        let fakeResponse = [{id: 1}, {id: 2}, {id: 2}];

        let fakeListing = wrapper.vm.filterListingFromListingsResponse(fakeResponse, 2);

        expect(fakeListing).toBe(fakeResponse[1]);
    })


    test('nextImage goes to next image in list', () => {
        let images = wrapper.vm.listing.inventoryItem.product.images;
        let listImages = wrapper.vm.getListingImages(images);
        let primaryImage = wrapper.vm.getPrimaryImage(listImages, wrapper.vm.listing);
        wrapper.vm.listingImages = listImages;
        wrapper.vm.currentImage = primaryImage;
        wrapper.vm.nextImage(wrapper.vm.currentImage);

        expect(wrapper.vm.currentImage).toBe(listImages[1])
    })

    test('nextImage resets to first image at end of list', () => {
        let images = wrapper.vm.listing.inventoryItem.product.images;
        let listImages = wrapper.vm.getListingImages(images);
        let primaryImage = wrapper.vm.getPrimaryImage(listImages, wrapper.vm.listing);
        wrapper.vm.listingImages = listImages;
        wrapper.vm.currentImage = listImages[listImages.length - 1];
        wrapper.vm.nextImage(wrapper.vm.currentImage);

        expect(wrapper.vm.currentImage).toBe(listImages[0])
    })



    test('previousImage goes to previous image', () => {
        let images = wrapper.vm.listing.inventoryItem.product.images;
        let listImages = wrapper.vm.getListingImages(images);
        let primaryImage = wrapper.vm.getPrimaryImage(listImages, wrapper.vm.listing);
        wrapper.vm.listingImages = listImages;
        wrapper.vm.currentImage = listImages[2];
        wrapper.vm.previousImage(wrapper.vm.currentImage);

        expect(wrapper.vm.currentImage).toBe(listImages[1]);
    })


    test('previousImage goes to end of list when at start', () => {
        let images = wrapper.vm.listing.inventoryItem.product.images;
        let listImages = wrapper.vm.getListingImages(images);
        let primaryImage = wrapper.vm.getPrimaryImage(listImages, wrapper.vm.listing);
        wrapper.vm.listingImages = listImages;
        wrapper.vm.currentImage = primaryImage;
        wrapper.vm.previousImage(wrapper.vm.currentImage);

        expect(wrapper.vm.currentImage).toBe(listImages[listImages.length - 1]);
    })

    test('Test buy item', async () => {
        await wrapper.vm.buy();
        await expect(wrapper.vm.$vs.notify).toBeCalled();
        await expect(wrapper.vm.$router.push).toBeCalled();
    })
})

describe('Liking items', () => {
    test('Liking an item', async () => {
        expect(wrapper.vm.likedListingsIds.length).toEqual(0);
        await wrapper.vm.sendLike(8101, "Longos - Chicken Cordon Bleu");
        expect(wrapper.vm.likedListingsIds[0]).toEqual(8101);
    })

    test('Unliking an item', async () => {
        await wrapper.vm.sendLike(8101, "Longos - Chicken Cordon Bleu");
        expect(wrapper.vm.likedListingsIds[0]).toEqual(8101);
        await wrapper.vm.deleteLike(8101, "Longos - Chicken Cordon Bleu");
        expect(wrapper.vm.likedListingsIds.length).toEqual(0);
    })
});
