package bookbridge;

public class Guest extends Member {

    public Guest(String memberId, String memberName) {
        super(memberId, memberName);
    }

    @Override
    public String getMembershipType() {
        return "GUEST";
    }

    @Override
    public int getMaximumLoanDays() {
        return 7;
    }

    @Override
    public int getDailyFine() {
        return 10;
    }
}
