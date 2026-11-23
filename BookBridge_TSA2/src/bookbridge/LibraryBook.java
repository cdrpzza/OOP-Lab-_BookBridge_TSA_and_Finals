package bookbridge;

public class LibraryBook {

    private String isbn;
    private String title;
    private boolean available;

    public LibraryBook(String isbn, String title, boolean available) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book Title cannot be empty.");
        }

        this.isbn = isbn.trim();
        this.title = title.trim();
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return available;
    }

    public void markBorrowed() {
        if (!available) {
            throw new IllegalStateException("Book is not available.");
        }

        available = false;
    }

    public void markAvailable() {
        available = true;
    }
}
