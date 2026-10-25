package library;

import java.time.Year;

public class Book {
    private final String title;
    private final String author;
    private final String isbn;
    private final int publicationYear;
    private boolean borrowed;

    public Book(String title, String author, String isbn, int publicationYear) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty.");
        }
        if (publicationYear < 1 || publicationYear > Year.now().getValue()) {
            throw new IllegalArgumentException("Publication year is out of range.");
        }
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.publicationYear = publicationYear;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void borrow() {
        borrowed = true;
    }

    public void returnToLibrary() {
        borrowed = false;
    }

    public void displayBookInfo() {
        System.out.println("----- Book Information -----");
        System.out.println("Title  : " + title);
        System.out.println("Author : " + author);
        System.out.println("ISBN   : " + isbn);
        System.out.println("Year   : " + publicationYear);
        System.out.println("Status : " + (borrowed ? "Borrowed" : "Available"));
    }
}
