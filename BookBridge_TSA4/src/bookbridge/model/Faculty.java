package bookbridge.model;

public class Faculty extends Member {

    public Faculty(String memberId, String memberName) {
        super(memberId, memberName);
    }

    @Override
    public MembershipType getMembershipType() {
        return MembershipType.FACULTY;
    }
}