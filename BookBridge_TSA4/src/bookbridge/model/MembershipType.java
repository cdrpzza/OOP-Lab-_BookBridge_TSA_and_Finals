package bookbridge.model;

public enum MembershipType {
    STUDENT(14, 5),
    FACULTY(30, 2),
    GUEST(7, 10);

    private final int maximumDays;
    private final int dailyFine;

    MembershipType(int maximumDays, int dailyFine) {
        this.maximumDays = maximumDays;
        this.dailyFine = dailyFine;
    }

    public int getMaximumDays() {
        return maximumDays;
    }

    public int getDailyFine() {
        return dailyFine;
    }
}