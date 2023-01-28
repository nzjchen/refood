Feature: UCM5 Find My Cards
  Scenario: User looks at all of own cards
    Given A user named "Bob" exists with 2 cards
    When The user retrieves his own cards
    Then The 2 cards are shown
    And They are owned by the user "Bob"




