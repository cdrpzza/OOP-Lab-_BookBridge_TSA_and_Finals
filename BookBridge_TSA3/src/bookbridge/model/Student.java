package bookbridge.model;

public class Student extends Member {

    public Student(String memberId, String memberName) {
        super(memberId, memberName);
    }

    @Override
    public MembershipType getMembershipType() {
        return MembershipType.STUDENT;
    }
}