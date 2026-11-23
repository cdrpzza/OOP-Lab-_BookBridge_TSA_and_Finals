package bookbridge;

import bookbridge.Member;

public class Student extends Member {

    public Student(String memberId, String memberName) {
        super(memberId, memberName);
    }

    @Override
    public String getMembershipType() {
        return "STUDENT";
    }

    @Override
    public int getMaximumLoanDays() {
        return 14;
    }

    @Override
    public int getDailyFine() {
        return 5;
    }
}