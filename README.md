# Robotic Arm Material Analyzer

## Description

The **Robotic Arm Material Analyzer** is an Android application designed to visualize and manage data collected by a robotic arm. The app fetches material data from a Firebase Firestore database and presents it in a user-friendly interface. Users can view the data in a detailed table or through a series of intuitive charts, providing insights into the materials being processed.



---

## Features

* **Welcome Screen**: A friendly introduction to the application, crediting the project's authors.
* **Real-time Data Visualization**: Fetches and displays material data in real-time from a Firebase Firestore collection.
* **Tabular Data View**: Presents a detailed table of materials, showing properties like color, weight, category, and whether the material is metal. Long-press on an item to delete it.
* **Graphical Analysis**: Displays four different charts for a comprehensive overview:
    * **Metal vs. Non-Metal Pie Chart**: Shows the proportion of metallic vs. non-metallic items.
    * **Color Distribution Pie Chart**: Visualizes the distribution of materials by color.
    * **Category Bar Chart**: Compares the quantity of materials across different categories.
    * **Weight Line Chart**: Plots the weight of each material item sequentially.
* **Data Management**: Allows users to delete individual material entries or clear all records from the database.
* **Offline Handling**: Provides clear feedback to the user when there is no internet connection.

---

## Technologies Used

* **Kotlin**: The primary programming language used for the application.
* **Jetpack Compose**: The modern UI toolkit for building native Android UI.
* **Firebase Firestore**: Used as the real-time, cloud-hosted NoSQL database to store and sync material data.
* **Accompanist**: For UI components like animated navigation.
* **YCharts**: A library for creating the bar, pie, and line charts.

---

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio
* A Firebase project with Firestore enabled.

### Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/christianregla/brazo_robot.git
    ```

2.  **Firebase Configuration:**
    * Go to your Firebase project console.
    * Add a new Android app to your project.
    * Follow the setup instructions to download the `google-services.json` file.
    * Place the `google-services.json` file in the `app/` directory of the project.

3.  **Build and Run:**
    * Open the project in Android Studio.
    * Let Gradle sync the dependencies.
    * Run the app on an emulator or a physical device.

---

## Author:

* **Christian Josue Regla Andrade** - *Developer*
