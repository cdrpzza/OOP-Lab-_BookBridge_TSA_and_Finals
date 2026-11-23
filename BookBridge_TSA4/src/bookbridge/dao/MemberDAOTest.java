package bookbridge.dao;

import bookbridge.model.Member;
import bookbridge.model.Student;

public class MemberDAOTest {

    public static void main(String[] args) {

        MemberDAO dao = new MemberDAO();

        try {
            Member member = new Student("M001", "Ana Cruz");

            dao.addMember(member);

            System.out.println("Member saved to database!");

            for (Member m : dao.getAllMembers()) {
                System.out.println(
                        m.getMemberId() + " - "
                        + m.getMemberName() + " - "
                        + m.getMembershipType());
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
