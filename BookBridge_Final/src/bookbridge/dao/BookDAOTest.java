package bookbridge.dao;

import bookbridge.model.LibraryBook;

public class BookDAOTest {

    public static void main(String[] args) {

        BookDAO dao = new BookDAO();

        try {
            LibraryBook book =
                    new LibraryBook("978001", "Java Programming");

            dao.addBook(book);

            System.out.println("Book saved to database!");

            for (LibraryBook b : dao.getAllBooks()) {
                System.out.println(
                        b.getIsbn() + " - "
                        + b.getTitle() + " - "
                        + b.getStatus());
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
