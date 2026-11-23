
package bookbridge.model;

public class Reservation implements Notifiable {

    private final Member member;
    private final LibraryBook book;
    private final int requestedDays;

    public Reservation(Member member, LibraryBook book, int requestedDays) {
        if (member == null || book == null) {
            throw new IllegalArgumentException("Member and book are required.");
        }

        if (requestedDays < 1 || requestedDays > 30) {
            throw new IllegalArgumentException(
                    "Loan days must be between 1 and 30.");
        }

        this.member = member;
        this.book = book;
        this.requestedDays = requestedDays;
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

    @Override
    public String getNotificationMessage() {
        return "Reservation recorded for "
                + member.getMemberName()
                + ": " + book.getTitle();
    }
}
