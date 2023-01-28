Feature: U34 Business wishlist notification
  Scenario: AC1: When a business on my business wishlist adds a new sale listing, a notification will appear on my feed.
    Given I have a business wishlisted
    When The business posts a new listing
    Then I, and all other users who have the business wishlisted, receive a notification