package bookbridge.service;

import bookbridge.model.*;
import bookbridge.exception.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class LibraryService {

    private final Map<String, LibraryBook> books = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, LoanRecord> activeLoans = new HashMap<>();

    private final Queue<Reservation> reservations = new LinkedList<>();
    private final Set<String> borrowedBooks = new HashSet<>();
    private final List<LoanRecord> history = new ArrayList<>();

    private int nextLoanNumber = 1;

    // Register a new library member
    public synchronized void registerMember(Member member)
            throws DuplicateEntityException {

        if (member == null) {
            throw new IllegalArgumentException("Member is required.");
        }

        String id = member.getMemberId();

        if (members.containsKey(id)) {
            throw new DuplicateEntityException("Member ID already exists.");
        }

        members.put(id, member);
    }

    // Add a book to the catalog
    public synchronized void addBook(LibraryBook book)
            throws DuplicateEntityException {

        if (book == null) {
            throw new IllegalArgumentException("Book is required.");
        }

        if (!book.isAvailable()) {
            throw new IllegalArgumentException("New books must be available.");
        }

        String isbn = book.getIsbn();

        if (books.containsKey(isbn)) {
            throw new DuplicateEntityException("ISBN already exists.");
        }

        books.put(isbn, book);
    }

    // Check if the member and book exist
    private Member requireMember(String memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID is required.");
        }

        Member member = members.get(memberId.trim());

        if (member == null) {
            throw new IllegalArgumentException("Member not found.");
        }

        return member;
    }

    private LibraryBook requireBook(String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("ISBN is required.");
        }

        LibraryBook book = books.get(isbn.trim());

        if (book == null) {
            throw new IllegalArgumentException("Book not found.");
        }

        return book;
    }

    // Check the borrowing limit
    private void checkLoanDays(Member member, int days)
            throws LoanLimitExceededException {

        if (days < 1 || days > 30) {
            throw new IllegalArgumentException(
                    "Loan days must be between 1 and 30.");
        }

        if (days > member.getMaximumLoanDays()) {
            throw new LoanLimitExceededException(
                    "Loan denied. Maximum days: "
                    + member.getMaximumLoanDays());
        }
    }

    // Borrow an available book
    public synchronized LoanRecord issueLoan(String memberId,
                                             String isbn, int days)
            throws ItemUnavailableException, LoanLimitExceededException {

        Member member = requireMember(memberId);
        LibraryBook book = requireBook(isbn);

        if (!book.isAvailable()
                || activeLoans.containsKey(book.getIsbn())
                || borrowedBooks.contains(book.getIsbn())) {
            throw new ItemUnavailableException("Book is not available.");
        }

        checkLoanDays(member, days);

        // A reserved book must go to the next member in line
        for (Reservation reservation : reservations) {
            if (reservation.getBook().getIsbn().equals(book.getIsbn())) {
                throw new ItemUnavailableException(
                        "Book has a pending reservation.");
            }
        }

        return createLoan(member, book, days);
    }

    // Update the collections after validation
    private LoanRecord createLoan(Member member, LibraryBook book, int days) {

        String loanId = String.format("L%04d", nextLoanNumber);

        LoanRecord loan = new LoanRecord(
                loanId, member, book, days, LocalDate.now());

        LibraryBook updatedBook = book.withStatus(BookStatus.CHECKED_OUT);

        books.put(book.getIsbn(), updatedBook);
        activeLoans.put(book.getIsbn(), loan);
        borrowedBooks.add(book.getIsbn());
        history.add(loan);

        nextLoanNumber++;

        return loan;
    }

    // Return a borrowed book
    public synchronized LoanRecord returnBook(String isbn) {

        LibraryBook book = requireBook(isbn);
        LoanRecord loan = activeLoans.get(book.getIsbn());

        if (loan == null || !borrowedBooks.contains(book.getIsbn())
                || book.isAvailable()) {
            throw new IllegalStateException("No active loan for this book.");
        }

        LoanRecord returnedLoan = loan.withReturnDate(LocalDate.now());

        int index = history.indexOf(loan);

        if (index < 0) {
            throw new IllegalStateException("Loan history is inconsistent.");
        }

        books.put(book.getIsbn(), book.withStatus(BookStatus.AVAILABLE));
        activeLoans.remove(book.getIsbn());
        borrowedBooks.remove(book.getIsbn());
        history.set(index, returnedLoan);

        return returnedLoan;
    }

    // Add a member to the reservation queue
    public synchronized Reservation reserveBook(String memberId,
                                                String isbn, int days)
            throws ItemUnavailableException, LoanLimitExceededException {

        Member member = requireMember(memberId);
        LibraryBook book = requireBook(isbn);

        checkLoanDays(member, days);

        if (book.isAvailable()) {
            throw new ItemUnavailableException(
                    "Book is available. You can borrow it instead.");
        }

        for (Reservation reservation : reservations) {
            if (reservation.getMember().getMemberId().equals(memberId.trim())
                    && reservation.getBook().getIsbn().equals(book.getIsbn())) {
                throw new IllegalArgumentException(
                        "You already reserved this book.");
            }
        }

        Reservation reservation = new Reservation(member, book, days);
        reservations.add(reservation);

        return reservation;
    }

    // Process the first reservation when its book is available
    public synchronized LoanRecord processNextReservation()
            throws ItemUnavailableException, LoanLimitExceededException {

        Reservation reservation = reservations.peek();

        if (reservation == null) {
            return null;
        }

        LibraryBook book = requireBook(reservation.getBook().getIsbn());
        Member member = requireMember(reservation.getMember().getMemberId());

        if (!book.isAvailable()) {
            throw new ItemUnavailableException(
                    "The next reserved book is still checked out.");
        }

        checkLoanDays(member, reservation.getRequestedDays());

        if (activeLoans.containsKey(book.getIsbn())
                || borrowedBooks.contains(book.getIsbn())) {
            throw new IllegalStateException("Book records are inconsistent.");
        }

        LoanRecord loan = createLoan(
                member, book, reservation.getRequestedDays());

        reservations.remove();

        return loan;
    }

    // Generic search method
    private <T> Result<T> findByKey(Map<String, T> map, String key) {

        if (key == null || key.isBlank()) {
            return new Result<>(null, "Search value is required.", false);
        }

        T item = map.get(key.trim());

        if (item == null) {
            return new Result<>(null, "No record found.", false);
        }

        return new Result<>(item, "Record found.", true);
    }

    public synchronized Result<LibraryBook> findBook(String isbn) {
        return findByKey(books, isbn);
    }

    public synchronized Result<Member> findMember(String memberId) {
        return findByKey(members, memberId);
    }

    // Search books using part of their title
    public synchronized List<LibraryBook> searchTitle(String keyword) {

        List<LibraryBook> results = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) {
            return results;
        }

        for (LibraryBook book : books.values()) {
            if (book.getTitle().toLowerCase()
                    .contains(keyword.trim().toLowerCase())) {
                results.add(book);
            }
        }

        results.sort(Comparator.comparing(
                LibraryBook::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(LibraryBook::getIsbn));

        return results;
    }

    // Filter books by their status
    public synchronized List<LibraryBook> filterBooks(BookStatus status) {

        List<LibraryBook> results = new ArrayList<>();

        for (LibraryBook book : books.values()) {
            if (book.getStatus() == status) {
                results.add(book);
            }
        }

        results.sort(Comparator.comparing(LibraryBook::getIsbn));

        return results;
    }

    // Filter members by membership type
    public synchronized List<Member> filterMembers(MembershipType type) {

        List<Member> results = new ArrayList<>();

        for (Member member : members.values()) {
            if (member.getMembershipType() == type) {
                results.add(member);
            }
        }

        results.sort(Comparator.comparing(Member::getMemberId));

        return results;
    }

    // Return sorted copies instead of the original collections
    public synchronized List<LibraryBook> listBooks() {

        List<LibraryBook> results = new ArrayList<>(books.values());
        results.sort(Comparator.comparing(LibraryBook::getIsbn));

        return results;
    }

    public synchronized List<Member> listMembers() {

        List<Member> results = new ArrayList<>(members.values());
        results.sort(Comparator.comparing(Member::getMemberId));

        return results;
    }

    public synchronized List<Reservation> listReservations() {
        return new ArrayList<>(reservations);
    }

    public synchronized List<LoanRecord> listHistory() {
        return new ArrayList<>(history);
    }

    // Overdue first, then due date, then loan ID
    public synchronized List<LoanRecord> listActiveLoans() {

        List<LoanRecord> results = new ArrayList<>(activeLoans.values());
        LocalDate today = LocalDate.now();

        results.sort(
                Comparator.comparing(
                        (LoanRecord loan) -> !loan.isOverdue(today))
                        .thenComparing(LoanRecord::getDueDate)
                        .thenComparing(LoanRecord::getLoanId)
        );

        return results;
    }

    // Show active loans for a particular member
    public synchronized List<LoanRecord> findLoansByMember(String memberId) {

        requireMember(memberId);

        List<LoanRecord> results = new ArrayList<>();

        for (LoanRecord loan : listActiveLoans()) {
            if (loan.getMember().getMemberId().equals(memberId.trim())) {
                results.add(loan);
            }
        }

        return results;
    }

    // Count books and calculate estimated fines
    public synchronized CirculationSummary getSummary() {

        int available = 0;
        int checkedOut = 0;
        long totalFines = 0;

        Map<MembershipType, Long> loansByType = new HashMap<>();

        for (MembershipType type : MembershipType.values()) {
            loansByType.put(type, 0L);
        }

        for (LibraryBook book : books.values()) {
            if (book.isAvailable()) {
                available++;
            } else {
                checkedOut++;
            }
        }

        LocalDate today = LocalDate.now();

        for (LoanRecord loan : activeLoans.values()) {
            MembershipType type = loan.getMember().getMembershipType();

            loansByType.put(type, loansByType.get(type) + 1);
            totalFines += loan.getEstimatedLateCharge(today);
        }

        return new CirculationSummary(
                available,
                checkedOut,
                activeLoans.size(),
                reservations.size(),
                loansByType,
                totalFines
        );
    }

    // Check that the active loan collections match
    public synchronized boolean isConsistent() {

        if (!activeLoans.keySet().equals(borrowedBooks)) {
            return false;
        }

        for (LibraryBook book : books.values()) {
            boolean borrowed = borrowedBooks.contains(book.getIsbn());

            if (borrowed != (book.getStatus() == BookStatus.CHECKED_OUT)) {
                return false;
            }
        }

        for (Map.Entry<String, LoanRecord> entry : activeLoans.entrySet()) {
            LoanRecord loan = entry.getValue();

            if (!entry.getKey().equals(loan.getBook().getIsbn())
                    || loan.isReturned()
                    || !history.contains(loan)) {
                return false;
            }
        }

        return true;
    }
}