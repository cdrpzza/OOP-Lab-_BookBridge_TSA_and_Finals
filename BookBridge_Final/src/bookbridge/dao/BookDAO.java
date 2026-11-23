package bookbridge.dao;

import bookbridge.database.DatabaseConnection;
import bookbridge.model.BookStatus;
import bookbridge.model.LibraryBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void addBook(LibraryBook book) throws SQLException {

        String sql = "INSERT INTO books (isbn, title, status) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getStatus().name());

            stmt.executeUpdate();
        }
    }

    public List<LibraryBook> getAllBooks() throws SQLException {

        List<LibraryBook> books = new ArrayList<>();

        String sql = "SELECT isbn, title, status "
                + "FROM books ORDER BY isbn";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String isbn = rs.getString("isbn");
                String title = rs.getString("title");

                BookStatus status = BookStatus.valueOf(
                        rs.getString("status"));

                LibraryBook book =
                        new LibraryBook(isbn, title, status);

                books.add(book);
            }
        }

        return books;
    }
    
    public boolean bookExists(String isbn) throws SQLException {

        String sql = "SELECT 1 FROM books WHERE isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
