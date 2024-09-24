Planification Trello : https://trello.com/b/vLKKodCh
Presentation : https://www.canva.com/design/DAF8kW55_cU/qrMlVUhpbsSdrvGvOT5Kiw/edit?utm_content=DAF8kW55_cU&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

# BatiCuisine Application

BatiCuisine is a Java application for managing construction projects, particularly focusing on kitchen renovations. It allows users to manage clients, projects, components (materials and labor), and quotes.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- PostgreSQL database

## Setup

1. Clone the repository or download the source code.

2. Set up the PostgreSQL database:
   - Create a new database named `baticuisine`
   - Update the database connection details in `com.baticuisine.db.DatabaseConnection.java`

3. Compile the Java files:
   ```
   javac -d bin src/com/baticuisine/*.java src/com/baticuisine/*/*.java
   ```

4. Create a JAR file:
   ```
   jar cvfm BatiCuisine.jar manifest.txt -C bin .
   ```

## Running the Application

To run the application, use the following command:

```
java -jar BatiCuisine.jar
```

## Usage

The application provides a console-based user interface with the following main options:

1. Manage Clients
2. Manage Projects
3. Manage Components
4. Manage Quotes

Each option leads to a sub-menu where you can perform various operations such as adding, displaying, modifying, and deleting entities.

### Managing Clients

- Add new clients (professional or individual)
- View all clients
- Modify client information
- Delete clients

### Managing Projects

- Create new projects
- View all projects
- Modify project details
- Delete projects
- Add components to projects

### Managing Components

- Add new materials
- Add new labor
- View all components
- Modify component details
- Delete components

### Managing Quotes

- Create new quotes for projects
- View all quotes
- Modify quote details
- Delete quotes
- Accept quotes
- Extend quote validity

## Note

This application uses a console-based user interface. Follow the on-screen prompts to navigate through the application and perform various operations.

## Contributing

Contributions to improve BatiCuisine are welcome. Please feel free to submit pull requests or open issues to discuss potential improvements.

## License

This project is licensed under the MIT License.
