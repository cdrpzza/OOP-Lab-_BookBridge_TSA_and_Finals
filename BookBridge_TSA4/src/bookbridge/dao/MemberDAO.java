package bookbridge.dao;

import bookbridge.database.DatabaseConnection;
import bookbridge.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public void addMember(Member member) throws SQLException {

        String sql = "INSERT INTO members "
                + "(member_id, member_name, membership_type) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getMemberId());
            stmt.setString(2, member.getMemberName());
            stmt.setString(3, member.getMembershipType().name());

            stmt.executeUpdate();
        }
    }

    public List<Member> getAllMembers() throws SQLException {

        List<Member> members = new ArrayList<>();

        String sql = "SELECT member_id, member_name, "
                + "membership_type FROM members ORDER BY member_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String id = rs.getString("member_id");
                String name = rs.getString("member_name");

                MembershipType type = MembershipType.valueOf(
                        rs.getString("membership_type"));

                Member member = switch (type) {
                    case STUDENT -> new Student(id, name);
                    case FACULTY -> new Faculty(id, name);
                    case GUEST -> new Guest(id, name);
                };

                members.add(member);
            }
        }

        return members;
    }
    
    public boolean memberExists(String memberId) throws SQLException {

        String sql = "SELECT 1 FROM members WHERE member_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
