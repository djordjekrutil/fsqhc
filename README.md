# Foursquare Health Check
This is a simple Android application demonstrating integration with the Foursquare API. It allows users to search for nearby venues based on their current location and selected filters.

## Getting Started
Before running the project, make sure to add your Foursquare API key to the `local.properties` file:
FOURSQUARE_API_KEY=your_api_key_here
Without this key, the application will not be able to fetch venue data.

## Architecture

The project is structured using a clean architecture approach. It includes:

- **ViewModel layer** for handling UI state
- **Use Cases** to abstract business logic (although in this particular case, use cases are simple wrappers around repository methods)
- **Repository layer** for data access
- **Remote Data Source** for interacting with the Foursquare API

Given the scope and simplicity of the project, the use of use cases might be considered redundant, as they currently only delegate calls to the repository without adding any real business logic. Removing them could simplify the codebase without any downside.

## Improvements

There are several areas where the project can be improved:

- **Test Coverage**: Unit test coverage should be increased, especially for the ViewModel and data layers.
- **CI/CD Integration**: Adding a CI/CD pipeline (e.g., GitHub Actions) would help automate testing and improve code quality.
- **Code Simplification**: The use of use cases may be unnecessary in this context and could be removed to reduce boilerplate.
- **Error Handling**: Expand and improve error handling, including specific feedback for network failures or API issues.
- **UI Architecture**: Instead of passing entire `ViewModel` instances to the composables, consider exposing only the `uiState` and `actions`. This approach would improve composable testability and make UI components more reusable and decoupled from the framework layer.

This project serves as a lightweight example of how to work with third-party APIs in a modern Android application using Kotlin, Jetpack libraries, and a modular architecture.

