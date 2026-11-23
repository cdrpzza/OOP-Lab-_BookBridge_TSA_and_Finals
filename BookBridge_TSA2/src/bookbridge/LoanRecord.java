package bookbridge;

public class LoanRecord {
    // Used to assign a number to each loan
    private static int nextLoanNumber = 1;

    private int loanNumber;
    private Member member;
    private LibraryBook book;
    private int requestedDays;
    private int lateDays;
    private String status;
    private boolean returned;

    // Check the details before creating the loan record
    public LoanRecord(Member member, LibraryBook book,
                      int requestedDays, int lateDays, String status) {

        if (member == null || book == null) {
            throw new IllegalArgumentException(
                    "Member and book are required.");
        }

        if (requestedDays < 1 || requestedDays > 30) {
            throw new IllegalArgumentException(
                    "Loan days must be between 1 and 30.");
        }

        if (lateDays < 0) {
            throw new IllegalArgumentException(
                    "Late days cannot be negative.");
        }

        if (!"APPROVED".equals(status)
                && !"RESERVED".equals(status)
                && !"DENIED".equals(status)) {
            throw new IllegalArgumentException("Invalid request status.");
        }

        this.loanNumber = nextLoanNumber++;
        this.member = member;
        this.book = book;
        this.requestedDays = requestedDays;
        this.lateDays = lateDays;
        this.status = status;
        this.returned = false;
    }

    public int getLoanNumber() {
        return loanNumber;
    }

    public Member getMember() {
        return member;
    }

    public LibraryBook getBook() {
        return book;
    }

    public int getRequestedDays() {
        return requestedDays;
    }

    public int getLateDays() {
        return lateDays;
    }

    public String getStatus() {
        return status;
    }

    public int getEstimatedLateCharge() {
        return lateDays * member.getDailyFine();
    }

    public boolean isReturned() {
        return returned;
    }

    public void markReturned() {
        if (!status.equals("APPROVED")) {
            throw new IllegalStateException(
                    "Only approved loans can be returned.");
        }

        if (returned) {
            throw new IllegalStateException(
                    "Loan has already been returned.");
        }

        returned = true;
        book.markAvailable();
    }
}
