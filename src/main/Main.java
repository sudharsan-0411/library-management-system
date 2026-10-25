package main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import library.Book;

public class Main {
    private static final Path DATA_FILE = Paths.get("data", "books.csv");
    private static final List<Book> books = new ArrayList<>();

    public static void main(String[] args) {
        try {
            loadBooks();
        } catch (IOException e) {
            System.out.println("Could not load saved books: " + e.getMessage());
        }

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Choose an option: ");
                String choice = sc.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> addBook(sc);
                        case "2" -> listBooks();
                        case "3" -> searchBooks(sc);
                        case "4" -> removeBook(sc);
                        case "5" -> borrowBook(sc);
                        case "6" -> returnBook(sc);
                        case "0" -> running = false;
                        default -> System.out.println("Please choose a valid menu option.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Could not save book data: " + e.getMessage());
                }
                if (running) {
                    System.out.println();
                }
            }
            try {
                saveBooks();
                System.out.println("Book data saved. Goodbye!");
            } catch (IOException e) {
                System.out.println("Could not save book data: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("==================================");
        System.out.println("      LIBRARY MANAGEMENT SYSTEM");
        System.out.println("==================================");
        System.out.println("1. Add a book");
        System.out.println("2. List all books");
        System.out.println("3. Search books");
        System.out.println("4. Remove a book");
        System.out.println("5. Borrow a book");
        System.out.println("6. Return a book");
        System.out.println("0. Save and exit");
        System.out.println("----------------------------------");
    }

    private static void addBook(Scanner sc) throws IOException {
        System.out.print("Book title: ");
        String title = nonEmpty(sc.nextLine(), "Book title cannot be empty.");
        System.out.print("Author: ");
        String author = nonEmpty(sc.nextLine(), "Author cannot be empty.");
        System.out.print("ISBN (unique): ");
        String isbn = nonEmpty(sc.nextLine(), "ISBN cannot be empty.").toUpperCase(Locale.ROOT);
        if (findByIsbn(isbn) != null) {
            throw new IllegalArgumentException("A book with that ISBN already exists.");
        }

        System.out.print("Publication year: ");
        int publicationYear;
        try {
            publicationYear = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Publication year must be a number.");
        }
        if (publicationYear < 1 || publicationYear > Year.now().getValue()) {
            throw new IllegalArgumentException(
                    "Enter a publication year from 1 to " + Year.now().getValue() + ".");
        }

        books.add(new Book(title, author, isbn, publicationYear));
        saveBooks();
        System.out.println("Book added successfully.");
    }

    private static void listBooks() {
        if (books.isEmpty()) {
            System.out.println("No books found. Add a book first.");
            return;
        }
        System.out.println("----- Library Catalog (" + books.size() + ") -----");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.printf("%d. %s | %s | ISBN: %s | %d | %s%n",
                    i + 1, book.getTitle(), book.getAuthor(), book.getIsbn(),
                    book.getPublicationYear(), book.isBorrowed() ? "Borrowed" : "Available");
        }
    }

    private static void searchBooks(Scanner sc) {
        System.out.print("Search by title, author, or ISBN: ");
        String query = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty.");
        }

        List<Book> matches = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase(Locale.ROOT).contains(query)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(query)
                    || book.getIsbn().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(book);
            }
        }
        if (matches.isEmpty()) {
            System.out.println("No books matched \"" + query + "\".");
            return;
        }
        System.out.println("----- Search Results (" + matches.size() + ") -----");
        for (Book book : matches) {
            book.displayBookInfo();
            System.out.println();
        }
    }

    private static void removeBook(Scanner sc) throws IOException {
        System.out.print("Enter ISBN of the book to remove: ");
        Book book = findByIsbn(sc.nextLine());
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        books.remove(book);
        saveBooks();
        System.out.println("Book removed successfully.");
    }

    private static void borrowBook(Scanner sc) throws IOException {
        System.out.print("Enter ISBN of the book to borrow: ");
        Book book = findByIsbn(sc.nextLine());
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (book.isBorrowed()) {
            System.out.println("This book is already borrowed.");
            return;
        }
        book.borrow();
        saveBooks();
        System.out.println("Book borrowed successfully.");
    }

    private static void returnBook(Scanner sc) throws IOException {
        System.out.print("Enter ISBN of the book to return: ");
        Book book = findByIsbn(sc.nextLine());
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (!book.isBorrowed()) {
            System.out.println("This book is already available.");
            return;
        }
        book.returnToLibrary();
        saveBooks();
        System.out.println("Book returned successfully.");
    }

    private static Book findByIsbn(String isbn) {
        String normalizedIsbn = isbn.trim();
        for (Book book : books) {
            if (book.getIsbn().equalsIgnoreCase(normalizedIsbn)) {
                return book;
            }
        }
        return null;
    }

    private static String nonEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static void loadBooks() throws IOException {
        if (!Files.exists(DATA_FILE)) {
            return;
        }
        int invalidRows = 0;
        for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
            String[] fields = line.split(",", -1);
            if (fields.length != 5) {
                invalidRows++;
                continue;
            }
            try {
                Book book = new Book(decode(fields[0]), decode(fields[1]), decode(fields[2]),
                        Integer.parseInt(fields[3]));
                if (!fields[4].equals("A") && !fields[4].equals("B")) {
                    invalidRows++;
                    continue;
                }
                if (fields[4].equals("B")) {
                    book.borrow();
                }
                if (findByIsbn(book.getIsbn()) != null) {
                    invalidRows++;
                    continue;
                }
                books.add(book);
            } catch (IllegalArgumentException e) {
                invalidRows++;
            }
        }
        if (invalidRows > 0) {
            System.out.println("Warning: skipped " + invalidRows + " invalid or duplicate saved book record(s).");
        }
    }

    private static void saveBooks() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        List<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(encode(book.getTitle()) + "," + encode(book.getAuthor()) + ","
                    + encode(book.getIsbn()) + "," + book.getPublicationYear() + ","
                    + (book.isBorrowed() ? "B" : "A"));
        }
        Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
