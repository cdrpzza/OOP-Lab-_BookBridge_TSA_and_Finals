package bookbridge.app;

import bookbridge.model.*;
import bookbridge.service.*;
import bookbridge.exception.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import java.sql.SQLException;

public class BookBridgeApp {

    static Scanner input = new Scanner(System.in);
    static LibraryService service = new LibraryService();

    public static void main(String[] args) {

        int choice;

        do {
            showMenu();
            choice = readInt("Enter choice: ", 0, 13);

            try {
                switch (choice) {
                    case 1 -> registerMember();
                    case 2 -> addBook();
                    case 3 -> issueLoan();
                    case 4 -> returnBook();
                    case 5 -> reserveBook();
                    case 6 -> processReservation();
                    case 7 -> searchMenu();
                    case 8 -> viewBooks(service.listBooks());
                    case 9 -> viewMembers(service.listMembers());
                    case 10 -> viewLoans(service.listActiveLoans());
                    case 11 -> viewLoans(service.listHistory());
                    case 12 -> viewReservations();
                    case 13 -> viewReports();
                    case 0 -> System.out.println("Thank you for using BookBridge!");
                    default -> System.out.println("Invalid choice.");
                }

            } catch (DuplicateEntityException ex) {
                System.out.println("Error: " + ex.getMessage());

            } catch (ItemUnavailableException ex) {
                System.out.println("Unavailable: " + ex.getMessage());

            } catch (LoanLimitExceededException ex) {
                System.out.println("Loan denied: " + ex.getMessage());

            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            catch (SQLException ex) {
                System.out.println("Database error: " + ex.getMessage());
            }
            

        } while (choice != 0);

        input.close();
    }

    static void showMenu() {
        System.out.println();
        System.out.println("BookBridge Library System Menu:");
        System.out.println("1. Register Member");
        System.out.println("2. Add Book");
        System.out.println("3. Issue Loan");
        System.out.println("4. Return Book");
        System.out.println("5. Reserve Book");
        System.out.println("6. Process Next Reservation");
        System.out.println("7. Search Books and Members");
        System.out.println("8. View Books");
        System.out.println("9. View Members");
        System.out.println("10. View Active Loans");
        System.out.println("11. View Loan History");
        System.out.println("12. View Reservations");
        System.out.println("13. View Reports");
        System.out.println("0. Exit");
    }

    static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();

            try {
                int value = Integer.parseInt(text);

                if (value >= min && value <= max) {
                    return value;
                }

                System.out.println("Enter a number from "
                        + min + " to " + max + ".");

            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static MembershipType readMembershipType() {
        while (true) {
            String type = readRequired(
                    "Membership Type (STUDENT/FACULTY/GUEST): ");

            try {
                return MembershipType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid membership type.");
            }
        }
    }

    static BookStatus readBookStatus() {
        while (true) {
            String status = readRequired(
                    "Book Status (AVAILABLE/CHECKED_OUT): ");

            try {
                return BookStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid book status.");
            }
        }
    }

    static void registerMember()
        throws DuplicateEntityException, SQLException {

        System.out.println("\n--- REGISTER MEMBER ---");

        String id = readRequired("Member ID: ");
        String name = readRequired("Member Name: ");
        MembershipType type = readMembershipType();

        Member member = switch (type) {
            case STUDENT -> new Student(id, name);
            case FACULTY -> new Faculty(id, name);
            case GUEST -> new Guest(id, name);
        };

        service.registerMember(member);
        System.out.println("Member registered successfully.");
    }

    static void addBook()
        throws DuplicateEntityException, SQLException {

        System.out.println("\n--- ADD BOOK ---");

        String isbn = readRequired("ISBN: ");
        String title = readRequired("Book Title: ");

        LibraryBook book = new LibraryBook(isbn, title);

        service.addBook(book);
        System.out.println("Book added successfully.");
    }

    static void issueLoan()
            throws ItemUnavailableException, LoanLimitExceededException, SQLException {

        System.out.println("\n--- ISSUE LOAN ---");

        String memberId = readRequired("Member ID: ");
        String isbn = readRequired("ISBN: ");
        int days = readInt("Requested Loan Days (1-30): ", 1, 30);

        LoanRecord loan = service.issueLoan(memberId, isbn, days);

        System.out.println("Loan approved.");
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("Due Date: " + loan.getDueDate());
    }

    static void returnBook() throws SQLException {

        System.out.println("\n--- RETURN BOOK ---");

        String isbn = readRequired("ISBN: ");

        LoanRecord loan = service.returnBook(isbn);

        System.out.println("Book returned successfully.");
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("Late Fine: PHP "
                + loan.getEstimatedLateCharge(LocalDate.now()));
    }

    static void reserveBook()
            throws ItemUnavailableException, LoanLimitExceededException, SQLException {

        System.out.println("\n--- RESERVE BOOK ---");

        String memberId = readRequired("Member ID: ");
        String isbn = readRequired("ISBN: ");
        int days = readInt("Requested Loan Days (1-30): ", 1, 30);

        Reservation reservation = service.reserveBook(memberId, isbn, days);

        System.out.println(reservation.getNotificationMessage());
    }

    static void processReservation()
            throws ItemUnavailableException, LoanLimitExceededException, SQLException {

        System.out.println("\n--- PROCESS RESERVATION ---");

        LoanRecord loan = service.processNextReservation();

        if (loan == null) {
            System.out.println("Reservation queue is empty.");
            return;
        }

        System.out.println("Reservation processed successfully.");
        System.out.println("Loan ID: " + loan.getLoanId());
        System.out.println("Member: " + loan.getMember().getMemberName());
        System.out.println("Book: " + loan.getBook().getTitle());
    }

    static void searchMenu() {

        System.out.println("\n--- SEARCH AND FILTER ---");
        System.out.println("1. Search Book by ISBN");
        System.out.println("2. Search Member by ID");
        System.out.println("3. Search Book by Title");
        System.out.println("4. Filter Books by Status");
        System.out.println("5. Filter Members by Type");
        System.out.println("6. Search Active Loans by Member");

        int choice = readInt("Enter choice: ", 1, 6);

        switch (choice) {
            case 1 -> {
                String isbn = readRequired("Enter ISBN: ");
                Result<LibraryBook> result = service.findBook(isbn);

                if (result.isFound()) {
                    viewBooks(List.of(result.getData()));
                } else {
                    System.out.println(result.getMessage());
                }
            }

            case 2 -> {
                String id = readRequired("Enter Member ID: ");
                Result<Member> result = service.findMember(id);

                if (result.isFound()) {
                    viewMembers(List.of(result.getData()));
                } else {
                    System.out.println(result.getMessage());
                }
            }

            case 3 -> {
                String keyword = readRequired("Enter Title Keyword: ");
                viewBooks(service.searchTitle(keyword));
            }

            case 4 -> {
                BookStatus status = readBookStatus();
                viewBooks(service.filterBooks(status));
            }

            case 5 -> {
                MembershipType type = readMembershipType();
                viewMembers(service.filterMembers(type));
            }

            case 6 -> {
                String id = readRequired("Enter Member ID: ");
                viewLoans(service.findLoansByMember(id));
            }
        }
    }

    static void viewBooks(List<LibraryBook> books) {

        System.out.println("\n--- BOOK LIST ---");

        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        for (LibraryBook book : books) {
            System.out.println("ISBN: " + book.getIsbn());
            System.out.println("Title: " + book.getTitle());
            System.out.println("Status: " + book.getStatus());
            System.out.println();
        }
    }

    static void viewMembers(List<Member> members) {

        System.out.println("\n--- MEMBER LIST ---");

        if (members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }

        for (Member member : members) {
            System.out.println("ID: " + member.getMemberId());
            System.out.println("Name: " + member.getMemberName());
            System.out.println("Type: " + member.getMembershipType());
            System.out.println("Maximum Days: " + member.getMaximumLoanDays());
            System.out.println("Daily Fine: PHP " + member.getDailyFine());
            System.out.println();
        }
    }

    static void viewLoans(List<LoanRecord> loans) {

        System.out.println("\n--- LOAN RECORDS ---");

        if (loans.isEmpty()) {
            System.out.println("No loan records found.");
            return;
        }

        for (LoanRecord loan : loans) {
            System.out.println("Loan ID: " + loan.getLoanId());
            System.out.println("Member: " + loan.getMember().getMemberName());
            System.out.println("Book: " + loan.getBook().getTitle());
            System.out.println("Loan Date: " + loan.getLoanDate());
            System.out.println("Due Date: " + loan.getDueDate());
            System.out.println("Status: " + loan.getStatus());
            System.out.println("Estimated Fine: PHP "
                    + loan.getEstimatedLateCharge(LocalDate.now()));
            System.out.println();
        }
    }

    static void viewReservations() {

        System.out.println("\n--- RESERVATION QUEUE ---");

        List<Reservation> reservations = service.listReservations();

        if (reservations.isEmpty()) {
            System.out.println("No pending reservations.");
            return;
        }

        for (Reservation reservation : reservations) {
            System.out.println(reservation.getNotificationMessage());
            System.out.println("Requested Days: "
                    + reservation.getRequestedDays());
            System.out.println();
        }
    }

    static void viewReports() {

        System.out.println("\n--- CIRCULATION REPORT ---");

        CirculationSummary summary = service.getSummary();

        System.out.println("Available Books: " + summary.availableBooks());
        System.out.println("Checked-Out Books: " + summary.checkedOutBooks());
        System.out.println("Active Loans: " + summary.activeLoans());
        System.out.println("Pending Reservations: "
                + summary.pendingReservations());

        System.out.println("\nActive Loans by Membership Type:");

        for (MembershipType type : MembershipType.values()) {
            System.out.println(type + ": "
                    + summary.loansByType().get(type));
        }

        System.out.println("\nTotal Estimated Fines: PHP "
                + summary.totalEstimatedFines());

        System.out.println("Collections Consistent: "
                + (service.isConsistent() ? "YES" : "NO"));
    }
}