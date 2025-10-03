import re
from playwright.sync_api import sync_playwright, expect

def run_verification(playwright):
    """
    This script verifies the fix for the search input bug.
    It mocks the API response for suggestions and waits for the request
    to ensure the dropdown's visibility is tested correctly.
    """
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context()
    page = context.new_page()

    try:
        # Define the mock suggestion data
        mock_suggestions = [{
            "riotId": "TestPlayer#123",
            "profileIconId": 123,
            "summonerLevel": 30,
            "profileIconUrl": "http://example.com/icon.png"
        }]

        # Intercept the API call and provide a mock response
        page.route(
            re.compile(".*suggestions.*"),
            lambda route: route.fulfill(status=200, json=mock_suggestions)
        )

        # --- Step 1: Navigate and trigger the search ---
        page.goto("http://localhost:8080/")

        search_input = page.get_by_placeholder("Enter Riot ID (e.g., Name#TAG)")
        expect(search_input).to_be_visible()

        # --- Step 2: Wait for the mocked request and verify the UI ---
        # Start waiting for the request BEFORE the action that triggers it
        with page.expect_request(re.compile(".*suggestions.*")) as request_info:
            # Type into the input to trigger the debounced API call
            search_input.type("Test", delay=100) # Add a small delay

        # Now that the request has been made and responded to, check for visibility
        suggestions_container = page.locator(".suggestions-dropdown")
        expect(suggestions_container).to_be_visible(timeout=5000)

        # Assert that the mocked suggestion is present
        expect(suggestions_container.get_by_text("TestPlayer#123")).to_be_visible()

        # Take a screenshot for visual verification
        page.screenshot(path="jules-scratch/verification/verification.png")

        print("Verification script completed successfully.")

    except Exception as e:
        print(f"An error occurred during verification: {e}")
        page.screenshot(path="jules-scratch/verification/error.png")

    finally:
        browser.close()

if __name__ == "__main__":
    with sync_playwright() as playwright:
        run_verification(playwright)