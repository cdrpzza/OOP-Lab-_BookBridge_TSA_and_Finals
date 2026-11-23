package bookbridge;

public class Reservation implements Notifiable {

    private Member member;
    private LibraryBook book;
    private boolean active;

    public Reservation(Member member, LibraryBook book) {
        if (member == null || book == null) {
            throw new IllegalArgumentException(
                    "Member and book are required.");
        }

        this.member = member;
        this.book = book;
        this.active = true;
    }

    public Member getMember() {
        return member;
    }

    public LibraryBook getBook() {
        return book;
    }

    public boolean isActive() {
        return active;
    }

    public void cancelReservation() {
        active = false;
    }

    @Override
    public String getNotificationMessage() {
        if (active) {
            return "Reservation recorded for "
                    + member.getMemberName()
                    + ": " + book.getTitle();
        }

        return "Reservation has been cancelled.";
    }
}
