<template>
  <div>
    <div id="logo-container">
      <img :src="`${publicPath}refood-logo-recycle.png`" alt="refood-logo" id="logo"/>
    </div>
    <vs-card id="main">
      <p id="sign">Sign In</p>
      <form>
        <vs-input id="email" type="text"
                  class="form-control"
                  v-model="email"
                  label="Enter Email"
                  :danger="this.errors.message != null"
                  :danger-text="this.errors.message"
                  required/>
        <vs-input id="password" type="password"
                  class="form-control"
                  v-model="password"
                  label="Enter Password"
                  :danger="this.errors.message != null"
                  :danger-text="this.errors.message"
                  required/>
        <vs-button class="loginButton" @click="checkForm(); loginSubmit()">Sign in</vs-button>
      </form>
    </vs-card>
  </div>
</template>

<script>
import api from "../Api";
import {mutations} from "../store"

const Login = {
  name: "Login",
  data: function () {
    return {
      errors: [],
      email: "",
      password: "",

      publicPath: process.env.BASE_URL
    };
  },
  methods: {
    validEmail: function (email) {
      const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },
    /**
     * Checks if the username and password match on what is stored in the backend.
     */
    checkForm: function() {
      this.errors = {
        hasErrors: false,
        message: null
      };

      if (this.email.length === 0) {
        this.errors.message = "Email required.";
        this.errors.hasErrors = true;
      }
      else if (!this.validEmail(this.email)) {
        this.errors.message = "Incorrect email/password";
        this.errors.hasErrors = true;
      }
      if (this.password.length === 0) {
        this.errors.message = "Incorrect email/password";
        this.errors.hasErrors = true;
      }
    },

    /**
     * Sends the login request to the backend by calling the login function from the API.
     */
    loginSubmit: function() {
      if(!this.errors.hasErrors){
        api.login(this.email, this.password)
          .then((response) => {
            //LOAD USER HOME PAGE, USING ROUTER
            mutations.setUserLoggedIn(response.data.userId, response.data.role);
            this.$router.push({path: `/home`});
          })
          .catch(err => {
            if(err.response.status === 400) { // Catch 400 Bad Request
              this.email = this.password = "";
              this.errors.message = "";
              this.$vs.notify({title:'Login Failed', text:'Email or password is incorrect.', color:'danger'});

            }
            else { // Catch anything else.
              this.$vs.notify({title:'Error Logging In', text:`Status Code ${err.response.status}`, color:'danger'});

            }
        });
      }
    }
  },

}
export default Login;
</script>

<style scoped>

#main {
  max-width: 400px;
  margin: 1em auto;
  padding: 0.5em 0 0.5em 0;
}

#main >>> .vs-card--content {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: auto auto;
  grid-row-gap: 1em;
}

/* First Header Row */
#sign {
  grid-row: 1;
  grid-column: 1;

  margin: 0;
  padding: 0.5em 0;

  color: #1F74FF;
  font-weight: bold;
  font-size: 24px;
  text-align: center;
}

form {
  grid-row: 2;
  grid-column: 1;

  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(3, auto);
  grid-row-gap: 1em;
}

.form-control {
  margin: auto;
}

#email {
  grid-row: 1;
  grid-column: 1;
  margin: auto;
  background-color: black;
}

#password {
  grid-row: 2;
  grid-column: 1;
}

.loginButton {
  grid-row: 3;
  grid-column: 1;

  width: 200px;
  margin: 1em auto;
}

#logo-container {
  margin-top: 8px;
  display: flex;
  justify-content: center;
}

#logo {
  width: 15%;
}

@media screen and (max-width: 800px) {
  #logo {
    width: 30%;
  }
}


</style>