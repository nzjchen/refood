Feature: UCM4 Card expiry

  Scenario: An existing card is expiring, I get notified if I want to delete or extend it for a further period
    Given An existing card has just expired
    When I choose to extend it
    Then The expiry date for the card is extended and the card is still there