Feature: U1 Registering and logging into an individual account

  Scenario: Successful Registration
  Given User attempts to register
  When They input the firstname "Jeff", lastname "Goldstein", email "jeffgold@hotmail.com", DoB "1999-04-27", and password "Potato1!"
  Then They successfully register and are redirected to their homepage

  Scenario: Registration with a taken email
  Given User attempts to register with an email that is already registered
  When They input the firstname "Jeff", lastname "Goldstein", email "jeffgold@hotmail.com", DoB "1999-04-27", and password "Potato1!"
  Then They are notified that the email they tried to register with is already registered

  Scenario: Registration with no names
  Given User attempts to register
  When They input the firstname "", lastname "", email "jeffgold@hotmail.com", DoB "1999-04-27", and password "Potato1!"
  Then They are notified that they need to enter firstname and lastname as they are mandatory fields

  Scenario: Registration with an invalid email
  Given User attempts to register
  When They input the firstname "Kyle", lastname "Jonathan", email "kyle@@b0..com", DoB "1999-04-27", and password "Potato1!"
  Then They are notified that they need to enter a valid email

  Scenario: Registration with a blank email
  Given User attempts to register
  When They input the firstname "Kyle", lastname "Jonathan", email "", DoB "1999-04-27", and password "Potato1!"
  Then They are notified that they need to enter a valid email

  Scenario: Underage registration
  Given User attempts to register
  When They input the firstname "Jeff", lastname "Goldstein", email "jeffgold@hotmail.com", DoB "2018-04-27", and password "Potato1!"
  Then They are notified that the email they need to be at least 13 years old to register

  Scenario: Password storage
  Given User attempts to register
  When They successfully register with the password "Potato1!"
  Then Password is hashed and not stored in plain text
