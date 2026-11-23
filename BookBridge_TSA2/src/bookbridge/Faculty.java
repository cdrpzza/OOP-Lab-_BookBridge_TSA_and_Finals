package bookbridge;

public class Faculty extends Member {

    public Faculty(String memberId, String memberName) {
        super(memberId, memberName);
    }

    @Override
    public String getMembershipType() {
        return "FACULTY";
    }

    @Override
    public int getMaximumLoanDays() {
        return 30;
    }

    @Override
    public int getDailyFine() {
        return 2;
    }
}
