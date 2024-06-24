# Story Master

Story Master is an Android app developed in Kotlin, integrating the Dicoding Story API to manage and share user stories.

## Screenshots

<div align="center">
  <img src="https://github.com/Chlunidia/asclepius/assets/115222445/bf159426-67e0-4375-9cff-929c626e5c4e" alt="App Screenshot">
</div>

## Features
1. Users can log in with their email and password.
2. New users can register with their name, email, and password.
3. Session Management
    - Stores session data and token in preferences.
    - Automatically navigates to the main page if logged in, otherwise goes to the login page.
4. Logout button clears the session and token data.
5. Fetches and displays a list of stories from the API.
6. Displays details of a story when an item is clicked.
7. Allows users to add new stories.
    - Photo file
    - Story description
    - Upload buttonuploads data to the server and returns to the story list with the new story at the top.
8. Includes Property Animation, Motion Animation, or Shared Element animation to enhance the user experience.
9. Displays a map with markers or icons for stories that include location data. Fetch stories with location data using the endpoint: `https://story-api.dicoding.dev/v1/stories?location=1`.
10. Implements Paging 3 to display stories efficiently.
11. Includes unit tests for the ViewModel functions that fetch paged story data.
    - Ensures data is loaded successfully and is not null.
    - Confirms the number of items matches expectations.
    - Verifies the first item returned is correct.
    - Handles scenarios where no data is returned, ensuring a count of zero.

## API Documentation
The Dicoding Story API allows users to share stories similar to Instagram posts but specific to Dicoding. Documentation is available at:
[Dicoding Story API Documentation](https://story-api.dicoding.dev/v1/)

## Installation

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/Chlunidia/story-master.git
    ```
2. **Open in Android Studio**: 
    - Open the cloned repository in Android Studio.
    - Sync the project with Gradle files.

## Notes
- Maximum file upload size is 1 MB; compress files if necessary.
- Store the login token for subsequent requests.
- Data is automatically cleared after one hour for a clean environment.
- Avoid using the guest endpoint for real application usage.
