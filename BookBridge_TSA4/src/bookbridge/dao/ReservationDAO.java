package bookbridge.dao;

import bookbridge.database.DatabaseConnection;
import bookbridge.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import bookbridge.model.Member;
import bookbridge.model.LibraryBook;
import java.util.Map;

public class ReservationDAO {

    public void addReservation(Reservation reservation)
            throws SQLException {

        String sql = "INSERT INTO reservations "
                + "(member_id, isbn, requested_days) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1,
                    reservation.getMember().getMemberId());
            stmt.setString(2,
                    reservation.getBook().getIsbn());
            stmt.setInt(3, reservation.getRequestedDays());

            stmt.executeUpdate();
        }
    }

    public void cancelReservation(String memberId, String isbn)
            throws SQLException {

        String sql = "DELETE FROM reservations "
                + "WHERE member_id = ? AND isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, memberId);
            stmt.setString(2, isbn);

            int deleted = stmt.executeUpdate();

            if (deleted == 0) {
                throw new SQLException(
                        "Reservation not found in database.");
            }
        }
    }

    public List<String> getReservationQueue()
            throws SQLException {

        List<String> reservations = new ArrayList<>();

        String sql = "SELECT reservation_id, member_id, isbn, "
                + "requested_days FROM reservations "
                + "ORDER BY reservation_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reservations.add(
                        rs.getInt("reservation_id") + " - "
                        + rs.getString("member_id") + " - "
                        + rs.getString("isbn") + " - "
                        + rs.getInt("requested_days"));
            }
        }

        return reservations;
    }
    
    public List<Reservation> getAllReservations(
            Map<String, Member> members,
            Map<String, LibraryBook> books) throws SQLException {

        List<Reservation> results = new ArrayList<>();

        String sql = "SELECT member_id, isbn, requested_days "
                + "FROM reservations ORDER BY reservation_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String memberId = rs.getString("member_id");
                String isbn = rs.getString("isbn");

                Member member = members.get(memberId);
                LibraryBook book = books.get(isbn);

                if (member == null || book == null) {
                    throw new SQLException(
                            "Reservation references missing data.");
                }

                int days = rs.getInt("requested_days");

                results.add(new Reservation(member, book, days));
            }
        }

        return results;
    }

}
