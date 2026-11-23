package bookbridge.dao;

import bookbridge.database.DatabaseConnection;
import bookbridge.model.LoanRecord;

import java.sql.*;

import bookbridge.model.LibraryBook;
import bookbridge.model.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoanDAO {

    public void saveLoan(LoanRecord loan) throws SQLException {

        String insertLoan = "INSERT INTO loans "
                + "(loan_id, member_id, isbn, requested_days, "
                + "loan_date, due_date, return_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String updateBook = "UPDATE books SET status = 'CHECKED_OUT' "
                + "WHERE isbn = ? AND status = 'AVAILABLE'";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement loanStmt =
                    conn.prepareStatement(insertLoan);
                 PreparedStatement bookStmt =
                    conn.prepareStatement(updateBook)) {

                bookStmt.setString(1, loan.getBook().getIsbn());

                int updated = bookStmt.executeUpdate();

                if (updated != 1) {
                    throw new SQLException(
                            "Book is not available in the database.");
                }

                loanStmt.setString(1, loan.getLoanId());
                loanStmt.setString(2, loan.getMember().getMemberId());
                loanStmt.setString(3, loan.getBook().getIsbn());
                loanStmt.setInt(4, loan.getRequestedDays());
                loanStmt.setDate(5, Date.valueOf(loan.getLoanDate()));
                loanStmt.setDate(6, Date.valueOf(loan.getDueDate()));
                loanStmt.setNull(7, Types.DATE);

                loanStmt.executeUpdate();

                conn.commit();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public void returnLoan(LoanRecord loan) throws SQLException {

        String updateLoan = "UPDATE loans SET return_date = ? "
                + "WHERE loan_id = ? AND return_date IS NULL";

        String updateBook = "UPDATE books SET status = 'AVAILABLE' "
                + "WHERE isbn = ? AND status = 'CHECKED_OUT'";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement loanStmt =
                    conn.prepareStatement(updateLoan);
                 PreparedStatement bookStmt =
                    conn.prepareStatement(updateBook)) {

                loanStmt.setDate(1, Date.valueOf(loan.getReturnDate()));
                loanStmt.setString(2, loan.getLoanId());

                int updatedLoan = loanStmt.executeUpdate();

                if (updatedLoan != 1) {
                    throw new SQLException(
                            "Active loan not found in database.");
                }

                bookStmt.setString(1, loan.getBook().getIsbn());

                int updatedBook = bookStmt.executeUpdate();

                if (updatedBook != 1) {
                    throw new SQLException(
                            "Book status could not be updated.");
                }

                conn.commit();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
       
    public List<LoanRecord> getAllLoans(
            Map<String, Member> members,
            Map<String, LibraryBook> books) throws SQLException {

        List<LoanRecord> loans = new ArrayList<>();

        String sql = "SELECT loan_id, member_id, isbn, "
                + "requested_days, loan_date, return_date "
                + "FROM loans ORDER BY loan_date, loan_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String loanId = rs.getString("loan_id");
                String memberId = rs.getString("member_id");
                String isbn = rs.getString("isbn");

                Member member = members.get(memberId);
                LibraryBook book = books.get(isbn);

                if (member == null || book == null) {
                    throw new SQLException(
                            "Missing member or book for loan: " + loanId);
                }

                int days = rs.getInt("requested_days");

                java.time.LocalDate loanDate =
                        rs.getDate("loan_date").toLocalDate();

                LoanRecord loan = new LoanRecord(
                        loanId, member, book, days, loanDate);

                Date returnDate = rs.getDate("return_date");

                if (returnDate != null) {
                    loan = loan.withReturnDate(
                            returnDate.toLocalDate());
                }

                loans.add(loan);
            }
        }

        return loans;
    }
    
    public void processReservedLoan(LoanRecord loan)
            throws SQLException {

        String updateBook = "UPDATE books SET status = 'CHECKED_OUT' "
                + "WHERE isbn = ? AND status = 'AVAILABLE'";

        String insertLoan = "INSERT INTO loans "
                + "(loan_id, member_id, isbn, requested_days, "
                + "loan_date, due_date, return_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String deleteReservation = "DELETE FROM reservations "
                + "WHERE reservation_id = ("
                + "SELECT reservation_id FROM ("
                + "SELECT reservation_id FROM reservations "
                + "ORDER BY reservation_id LIMIT 1"
                + ") AS next_reservation) "
                + "AND member_id = ? AND isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement bookStmt =
                    conn.prepareStatement(updateBook);
                 PreparedStatement loanStmt =
                    conn.prepareStatement(insertLoan);
                 PreparedStatement reservationStmt =
                    conn.prepareStatement(deleteReservation)) {

                bookStmt.setString(1, loan.getBook().getIsbn());

                if (bookStmt.executeUpdate() != 1) {
                    throw new SQLException(
                            "Reserved book is not available.");
                }

                loanStmt.setString(1, loan.getLoanId());
                loanStmt.setString(2, loan.getMember().getMemberId());
                loanStmt.setString(3, loan.getBook().getIsbn());
                loanStmt.setInt(4, loan.getRequestedDays());
                loanStmt.setDate(5, Date.valueOf(loan.getLoanDate()));
                loanStmt.setDate(6, Date.valueOf(loan.getDueDate()));
                loanStmt.setNull(7, Types.DATE);

                if (loanStmt.executeUpdate() != 1) {
                    throw new SQLException("Loan could not be saved.");
                }

                reservationStmt.setString(
                        1, loan.getMember().getMemberId());
                reservationStmt.setString(
                        2, loan.getBook().getIsbn());

                if (reservationStmt.executeUpdate() != 1) {
                    throw new SQLException(
                            "Next reservation could not be removed.");
                }

                conn.commit();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }


}
