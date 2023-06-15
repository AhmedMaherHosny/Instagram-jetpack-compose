# Instagram App

This readme file provides an overview of the libraries used in the Instagram app developed using Jetpack Compose, Kotlin, and the MVVM (Model-View-ViewModel) architecture. Below is a list of the libraries used in the project along with their purposes.

## Libraries Used

### ConstraintLayout Compose (androidx.constraintlayout:constraintlayout-compose:1.0.1)
ConstraintLayout Compose is a library that provides a set of composable functions and modifiers for building responsive and constraint-based layouts in Jetpack Compose. It allows you to create flexible and dynamic user interfaces with ease.

### Timber (com.jakewharton.timber:timber:5.0.1)
Timber is a powerful logging library for Android. It simplifies logging statements and provides useful features such as tagging, logging levels, and integration with other logging frameworks.

### System UI Controller (com.google.accompanist:accompanist-systemuicontroller:0.27.0)
System UI Controller is a library that allows you to control and customize the system UI elements, such as the status bar and navigation bar, in your Jetpack Compose app. It provides a set of composable functions to handle system UI changes dynamically.

### Pager (com.google.accompanist:accompanist-pager:0.29.1-alpha)
Pager is a library that enables you to create paged scrolling behavior in your Jetpack Compose app. It provides a flexible and customizable pager implementation that can be used for building swipeable screens, image galleries, and more.

### Pager Indicators (com.google.accompanist:accompanist-pager-indicators:0.29.1-alpha)
Pager Indicators is a companion library for the Pager library mentioned above. It provides a set of ready-to-use indicators that can be used to display the current position and progress of a pager. These indicators can enhance the user experience and provide visual cues for navigation.

### Coil Compose (io.coil-kt:coil-compose:2.2.2)
Coil Compose is an image loading library for Jetpack Compose. It offers a simple and efficient way to load images from various sources and display them in your app. It supports features such as caching, transformations, and fetching images from remote URLs.

### Dagger Hilt (com.google.dagger:hilt-android:2.44)
Dagger Hilt is a dependency injection library for Android. It simplifies the process of managing dependencies and provides a standardized way to inject dependencies into your classes. It helps improve code modularity, testability, and maintainability.

### Hilt Navigation (androidx.hilt:hilt-navigation:1.1.0-alpha01)
Hilt Navigation is an extension library for Dagger Hilt that integrates with the Navigation component. It allows you to use Hilt's dependency injection capabilities with the Jetpack Navigation library, making it easier to inject dependencies into your navigation destinations.

### Hilt Navigation Compose (androidx.hilt:hilt-navigation-compose:1.1.0-alpha01)
Hilt Navigation Compose is another extension library for Dagger Hilt that provides integration with Jetpack Compose's navigation system. It enables you to use Hilt's dependency injection in Compose-based navigation destinations.

### Retrofit (com.squareup.retrofit2:retrofit:2.9.0)
Retrofit is a popular HTTP client library for Android. It simplifies the process of making network requests and handling API responses. Retrofit provides a declarative and type-safe way to define API endpoints and serialize/deserialize data using various formats.

### OkHttp (com.squareup.okhttp3:okhttp:5.0.0-alpha.2)
OkHttp is a powerful and efficient HTTP client

 for Android. It serves as the underlying networking library for Retrofit and provides features such as connection pooling, request/response interception, and more.

### Retrofit Gson Converter (com.squareup.retrofit2:converter-gson:2.9.0)
Retrofit Gson Converter is a converter library for Retrofit that enables JSON serialization and deserialization using the Gson library. It converts JSON responses from the server into Java/Kotlin objects and vice versa.

### OkHttp Logging Interceptor (com.squareup.okhttp3:logging-interceptor:4.5.0)
OkHttp Logging Interceptor is an interceptor for OkHttp that logs HTTP request and response information. It's useful for debugging network requests, inspecting headers, and troubleshooting network-related issues.

### Compose Destinations (io.github.raamcosta.compose-destinations:core:1.8.36-beta)
Compose Destinations is a library that provides a declarative and type-safe way to define and navigate between destinations in Jetpack Compose. It simplifies the navigation logic and allows for better code organization in large-scale Compose apps.

### Compose Destinations KSP (io.github.raamcosta.compose-destinations:ksp:1.8.36-beta)
Compose Destinations KSP is the Kotlin Symbol Processing (KSP) plugin for Compose Destinations. It enables compile-time code generation to generate the necessary navigation-related code for Compose Destinations.

### Paging (androidx.paging:paging-runtime:3.1.1)
Paging is a Jetpack library that helps you load and display large data sets efficiently. It provides components for handling data pagination, such as loading data in chunks and presenting it in a paged manner.

### Paging Compose (androidx.paging:paging-compose:1.0.0-alpha18)
Paging Compose is a companion library for Paging that integrates with Jetpack Compose. It offers a set of composable functions and utilities for displaying paged data in a Jetpack Compose UI.

### Permissions (com.google.accompanist:accompanist-permissions:0.29.1-alpha)
Permissions is a library that simplifies the process of handling runtime permissions in Jetpack Compose apps. It provides a set of composable functions for requesting, checking, and observing the status of permissions.

### Compose Cropper (com.github.SmartToolFactory:Compose-Cropper:0.2.3)
Compose Cropper is a library that enables image cropping functionality in Jetpack Compose. It provides a customizable and user-friendly interface for cropping images within your app.

### Runtime LiveData (androidx.compose.runtime:runtime-livedata:1.5.0-beta01)
Runtime LiveData is a library that allows you to observe LiveData objects from Jetpack Compose. It provides integration between the LiveData framework and Compose, enabling seamless communication between the two.

### Socket.IO Client (io.socket:socket.io-client:2.0.0)
Socket.IO Client is a library that provides a client implementation for Socket.IO, a real-time communication protocol. It allows your app to establish bidirectional communication with a Socket.IO server, enabling real-time updates and messaging.

## MVVM (Model-View-ViewModel) Architecture

The Instagram app follows the MVVM (Model-View-ViewModel) architecture pattern. MVVM separates the presentation logic (View) from the data and business logic (ViewModel), promoting better code organization and testability. Below is a brief explanation of the components in the MVVM architecture:

- **Model**: The Model represents the data and business logic of the application. It encapsulates data sources, network requests, and any other data-related operations.

- **View**: The View represents the UI components and is responsible for rendering the user interface. In the Instagram app, the View

 is implemented using Jetpack Compose, offering a modern and declarative way to build UIs.

- **ViewModel**: The ViewModel acts as a bridge between the Model and the View. It contains the presentation logic, handles data transformations, and provides data to the View. It also exposes observable states that the View can observe and react to.

The MVVM architecture promotes separation of concerns and enables easier testing, maintainability, and scalability of the app.
