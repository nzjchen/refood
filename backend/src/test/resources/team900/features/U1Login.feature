Feature: U1 Registering and logging into an individual account
  Scenario: Successful login
    Given User attempts to login
    When They enter with email "johnsmith@yahoo.com" and password "Potato1!"
    Then They are redirected to their profile

  Scenario: Invalid email format inputted
    Given User attempts to login
    When They enter an email with an invalid format, such that "bababooey@@somemail..com"
    Then They are given a warning that their email format is invalid

  Scenario: Non-existent email
    Given User attempts to login
    When They enter an email with the valid format "ghostman@gmail.com" and password "HelpMe69!"
    Then They are given a warning that either their password or email is incorrect

  Scenario: Incorrect password
    Given User attempts to login
    When They enter an email with the valid format "johnsmith@yahoo.com" and password "Ligma420!"
    Then They are given a warning that either their password or email is incorrect

  Scenario: Email is blank
    Given User attempts to login
    When They enter only the password "UserIsDead420!"
    Then They are given a warning that the email field is empty

  Scenario: Password is blank
    Given User attempts to login
    When They enter only the email "johnsmith@yahoo.com"
    Then They are given a warning that the password field is empty

  Scenario: Both email and password are blank
    Given User attempts to login
    When They enter nothing, but attempt to logging anyway
    Then They are given a warning that both email and password fields are empty

  Scenario: Logging out
    Given User is logged in
    When They press log out
    Then They successfully logout and their token session disappears
