import api from '../Api';
import {mutations, store} from '../store';

/**
 * Updates user session on every router change, this will stop the issue of having to call checksession on every single page
 * @param router Vue router that is being used
 */
function updateSessionOnRouterChange(router) {
    router.beforeEach((to, from, next) => {
        //Clears values when leaving from business page (so it doesn't stay when you come back to business page)
        if(from.path.includes('/listings/')) {
            sessionStorage.removeItem("businessesCache")
        }

        if(from.path.includes('/search')) {
            sessionStorage.removeItem("previousListing");
        }
        api.checkSession()
        .then((response) => {
          if(response.data.id != null && (to.path != '/' && to.path != '/register' && to.name != 'catchAll')){ //If logged in
                getBusinessSession();
                getNotifications(response.data.id)
            } else if (response.data.id != null && (to.path == '/' || to.path == '/register' || to.name == 'catchAll')) { //If logged in and attempting to access login or register
                sendToHome(to, next)
                getBusinessSession();
                getNotifications(response.data.id)
            } else {
                sendToLogin(to, next);
            }
        
            setStoreValues(response);
            next();

        })
  })
}

/**
 * Sets store values for user state
 * @param response response from backend that contains user information
 */
function setStoreValues(response) {
    mutations.setUserLoggedIn(response.data.id, response.data.role);
    mutations.setUserBusinesses(response.data.businessesAdministered);
    mutations.setUserName(response.data.firstName + " " + response.data.lastName);
}


/**
 * Gets user's notifications 
 * @param userId user whose notifications we are trying to receive
 */
function getNotifications(userId) {
    api.getNotifications(userId)
    .then((response) => {
        mutations.setNotifications(response.data);
    });

    //the (card) notifications from the previous call are concatenated with listing notifications
    api.getListingNotifications(userId)
    .then((response) => {
        mutations.setNotifications(store.notifications.concat(response.data));
    });
}


/**
 * Grabs user's acting as state, if they aren't acting as anything they will be acting as a user
 */
function getBusinessSession() {
    api.checkBusinessSession()
    .then((busResponse) => {
        if(busResponse.status == 200){
            mutations.setActingAsBusiness(busResponse.data.id, busResponse.data.name);
        } else {
            mutations.setActingAsUser();
        }
    });
}

/**
 * Sets next router change to login page, this will be called if the user is not logged in
 * @param to component that is next in router 
 * @param next method to go to next component in router
 */
function sendToLogin(to, next) {
    if(to.path !== '/' && to.path !== '/register') {
        next({path: '/'})
    } else {
        next();
    }
}

/**
 * Sets next router change to home page, this will be called if the user is logged in and tries to access register and login
 * @param to component that is next in router 
 * @param next method to go to next component in router
 */
function sendToHome(to, next){
    if(to.path !== '/home') {
        next({path: '/home'})
    } else {
        next();
    }
}

export { updateSessionOnRouterChange }