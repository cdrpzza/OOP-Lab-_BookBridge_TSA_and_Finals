package bookbridge.model;

public class Guest extends Member {

    public Guest(String memberId, String memberName) {
        super(memberId, memberName);
    }

    @Override
    public MembershipType getMembershipType() {
        return MembershipType.GUEST;
    }
}