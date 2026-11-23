package bookbridge.model;

public abstract class Member {

    private final String memberId;
    private final String memberName;

    public Member(String memberId, String memberName) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty.");
        }

        if (memberName == null || memberName.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }

        this.memberId = memberId.trim();
        this.memberName = memberName.trim();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public abstract MembershipType getMembershipType();

    public int getMaximumLoanDays() {
        return getMembershipType().getMaximumDays();
    }

    public int getDailyFine() {
        return getMembershipType().getDailyFine();
    }
}