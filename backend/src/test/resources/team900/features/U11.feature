Feature: U11 Modify Businesses
  Scenario: Owner updates business
    Given there is a business with an owner "Wilma"
    When the user modifies the business with valid data
    Then the business is updated

  Scenario: Bad name doesn't update business
    Given there is a business with an owner "Wilma"
    When the user modifies the business to have a name ""
    Then a bad request is returned
    Then the business is not updated

  Scenario: No country doesn't update business
    Given there is a business with an owner "Wilma"
    When the user modifies the business to have a country ""
    Then a bad request is returned
    Then the business is not updated

