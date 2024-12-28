# Srecni Ljudi - Character Relationship Finder

A tool to explore and map the intricate relationships between characters in the popular Serbian TV show "Srećni Ljudi". This project leverages advanced algorithms and AI to provide semantic insights into character connections.

You are read more about it in the [medium article](https://medium.com/@nemnesic/discovering-connections-a-tool-to-map-relationships-in-tv-show-sre%C4%87ni-ljudi-972cb03908d8).
## Features

- Load character data and relationships from a CSV file.
- Find relationships between two characters.
- Visualize the relationship path.
- Generate explanations for the relationships using an AI assistant.
- Responsive web interface for selecting characters and displaying results.

## Technologies Used

- Kotlin
- Spring Boot
- Thymeleaf
- Bootstrap
- JGraphT
- JFreeChart
- OkHttp
- Jackson
- LangChain4j

## Getting Started

### Prerequisites

- Java 11 or higher
- Gradle

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/nemnesic/srecniljudi.git
    cd srecniljudi
    ```

2. Build the project:
    ```sh
    ./gradlew build
    ```

3. Run the application:
    ```sh
    ./gradlew bootRun
    ```

### Configuration

Add your Supabase URL and API key to the `application.properties` file:
```properties
supabase.url=https://your-supabase-url
supabase.key=your-supabase-api-key