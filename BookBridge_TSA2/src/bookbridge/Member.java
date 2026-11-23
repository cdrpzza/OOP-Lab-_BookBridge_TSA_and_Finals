
package bookbridge;
// Contains the information shared by all members
public abstract class Member {

    private String memberId;
    private String memberName;
    
    // Check if the member details are valid
    public Member(String memberId, String memberName) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty.");
        }

        if (memberName == null || memberName.trim().isEmpty()) {
            throw new IllegalArgumentException("Member Name cannot be empty.");
        }

        this.memberId = memberId.trim();
        this.memberName = memberName.trim();
    }
    
    // Each member type has its own borrowing rules
    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public abstract String getMembershipType();

    public abstract int getMaximumLoanDays();

    public abstract int getDailyFine();
}
