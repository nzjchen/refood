# ReFood

Welcome to our waste food web app!
The idea for this site is to allow businesses to advertise products that are going to be wasted, to help reduce the amount of wasted food and hopefully reduce it's impact on the environment.

For detailed information about our site and how to use it please refer to our [wiki](https://eng-git.canterbury.ac.nz/seng302-2021/team-900/-/wikis/home), which contains a user manual and our design decisions

You can find our deployed site here: [https://csse-s302g9.canterbury.ac.nz/prod](https://csse-s302g9.canterbury.ac.nz/prod/)

Sample user for demo purposes:
- Username: jsails0@go.com
- Password: t146MwLm





## Project Structure

Our website built using `gradle`, `npm`, `Spring Boot`, `Vue.js` and `Gitlab CI`.

A frontend sub-project (web GUI):

- `frontend/src` Frontend source code (Vue.js)
- `frontend/public` publicly accessible web assets (e.g., icons, images, style sheets)
- `frontend/dist` Frontend production build

A backend sub-project (business logic and persistence server):

- `backend/src` Backend source code (Java - Spring)
- `backend/out` Backend production build

## How to run locally

### Frontend / GUI

    $ cd frontend
    $ npm install
    $ npm run serve

Running on: http://localhost:9500/ by default

### Backend / server

    cd backend
    ./gradlew bootRun

Running on: http://localhost:9499/ by default

## Contributors

- SENG302 teaching team
- Ayub Momahamed
- Dan Bublik
- Jackie Chen
- Kye Oldham
- Lachlan Reynolds
- Omar Sheta
- Ronan Avery
- David Frost (Resigned)

## Technologies / Dependencies

Frontend / GUI
- Axios
- Vuejs
- Vue-Router
- Vuesax 3 UI component framework
- Apexcharts.js

Backend / server
- Spring Boot (JPA, Security, REST)
- H2 Database Engine
- MariaDB

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue docs](https://vuejs.org/v2/guide/)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=10577&section=11)
