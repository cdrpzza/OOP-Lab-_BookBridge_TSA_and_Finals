
package bookbridge.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanRecord {

    private final String loanId;
    private final Member member;
    private final LibraryBook book;
    private final int requestedDays;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;

    public LoanRecord(String loanId, Member member, LibraryBook book,
                      int requestedDays, LocalDate loanDate) {
        this(loanId, member, book, requestedDays, loanDate, null);
    }

    private LoanRecord(String loanId, Member member, LibraryBook book,
                       int requestedDays, LocalDate loanDate,
                       LocalDate returnDate) {

        if (loanId == null || loanId.isBlank()) {
            throw new IllegalArgumentException("Loan ID is required.");
        }

        if (member == null || book == null || loanDate == null) {
            throw new IllegalArgumentException("Loan details are required.");
        }

        if (requestedDays < 1 || requestedDays > member.getMaximumLoanDays()) {
            throw new IllegalArgumentException("Invalid loan days.");
        }

        if (returnDate != null && returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("Invalid return date.");
        }

        this.loanId = loanId;
        this.member = member;
        this.book = book;
        this.requestedDays = requestedDays;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(requestedDays);
        this.returnDate = returnDate;
    }

    public String getLoanId() {
        return loanId;
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

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }

    public LoanStatus getStatus() {
        if (isReturned()) {
            return LoanStatus.RETURNED;
        }

        if (isOverdue(LocalDate.now())) {
            return LoanStatus.OVERDUE;
        }

        return LoanStatus.ACTIVE;
    }

    public long getLateDays(LocalDate today) {
        LocalDate endDate = isReturned() ? returnDate : today;

        if (!endDate.isAfter(dueDate)) {
            return 0;
        }

        return ChronoUnit.DAYS.between(dueDate, endDate);
    }

    public long getEstimatedLateCharge(LocalDate today) {
        return getLateDays(today) * member.getDailyFine();
    }

    public LoanRecord withReturnDate(LocalDate date) {
        if (isReturned()) {
            throw new IllegalStateException("Loan already returned.");
        }

        return new LoanRecord(loanId, member, book,
                requestedDays, loanDate, date);
    }
}
