# Library Management System (Java)

A terminal-based Java application for maintaining a small library catalog.

## Features

- Add books with a title, author, ISBN, and publication year
- List all books and view their availability
- Search by title, author, or ISBN
- Borrow and return books
- Remove books from the catalog by ISBN
- Save catalog records locally in `data/books.csv`, including borrow status
- Reject duplicate ISBNs and invalid publication years

## Requirements

- Java JDK 17 or newer
- Terminal / Command Prompt

Check Java is installed:

```text
java -version
javac -version
```

## Run in VS Code or a terminal

Open the project folder, then run the following from its root directory.

### Windows

```bat
javac -d out src\library\Book.java src\main\Main.java
java -cp out main.Main
```

### macOS / Linux

```bash
javac -d out src/library/Book.java src/main/Main.java
java -cp out main.Main
```

The data folder and saved file are created automatically when the application saves records. Any pre-existing data files remain untouched.

## Project structure

```text
library-management-system/
├── src/
│   ├── main/
│   │   └── Main.java
│   └── library/
│       └── Book.java
├── data/                 # Created automatically at runtime
├── .gitignore
└── README.md
```

## Data and privacy

Book records are stored locally in `data/books.csv`. The application does not connect to a server or upload data.
