package bookbridge.model;

public class LibraryBook {

    private final String isbn;
    private final String title;
    private final BookStatus status;

    public LibraryBook(String isbn, String title) {
        this(isbn, title, BookStatus.AVAILABLE);
    }

    public LibraryBook(String isbn, String title, BookStatus status) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }

        if (status == null) {
            throw new IllegalArgumentException("Book status is required.");
        }

        this.isbn = isbn.trim();
        this.title = title.trim();
        this.status = status;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public BookStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public LibraryBook withStatus(BookStatus newStatus) {
        return new LibraryBook(isbn, title, newStatus);
    }
}