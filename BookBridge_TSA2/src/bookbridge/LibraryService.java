package bookbridge;

public class LibraryService {

    private LoanRecord[] records;
    private Reservation[] reservations;
    private int transactionCount;
    private int reservationCount;

    public LibraryService() {
        records = new LoanRecord[5];
        reservations = new Reservation[5];
        transactionCount = 0;
        reservationCount = 0;
    }

    public LoanRecord processRequest(Member member, LibraryBook book,
                                     int requestedDays, int lateDays) {

        if (transactionCount >= records.length) {
            throw new IllegalStateException(
                    "Maximum number of transactions reached.");
        }

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

        String status;

        if (!book.isAvailable()) {
            status = "RESERVED";
        } else if (requestedDays <= member.getMaximumLoanDays()) {
            status = "APPROVED";
        } else {
            status = "DENIED";
        }

        LoanRecord record = new LoanRecord(
                member, book, requestedDays, lateDays, status);

        if (status.equals("RESERVED")) {
            Reservation reservation = new Reservation(member, book);
            reservations[reservationCount] = reservation;
            reservationCount++;
        } else if (status.equals("APPROVED")) {
            book.markBorrowed();
        }

        records[transactionCount] = record;
        transactionCount++;

        return record;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int countStatus(String status) {
        int count = 0;

        for (int i = 0; i < transactionCount; i++) {
            if (records[i].getStatus().equals(status)) {
                count++;
            }
        }

        return count;
    }

    public LoanRecord getGreatestRequestedDaysRecord() {
        if (transactionCount == 0) {
            throw new IllegalStateException(
                    "No transactions available.");
        }

        LoanRecord greatest = records[0];

        for (int i = 1; i < transactionCount; i++) {
            if (records[i].getRequestedDays()
                    > greatest.getRequestedDays()) {
                greatest = records[i];
            }
        }

        return greatest;
    }

    public int getTotalEstimatedLateCharge() {
        int total = 0;

        for (int i = 0; i < transactionCount; i++) {
            total += records[i].getEstimatedLateCharge();
        }

        return total;
    }

    public int getReservationCount() {
        return reservationCount;
    }
}
