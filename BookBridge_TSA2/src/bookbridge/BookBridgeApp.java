package bookbridge;

import java.util.Scanner;

public class BookBridgeApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryService service = new LibraryService();

        System.out.println("BookBridge Library System");

        for (int i = 0; i < 5; i++) {
            System.out.println();
            System.out.println("Transaction " + (i + 1) + ":");

            String memberId = readRequired(scanner,
                    "Enter Member ID: ", "Member ID cannot be empty.");

            String memberName = readRequired(scanner,
                    "Enter Member Name: ", "Member Name cannot be empty.");

            String membershipType;

            do {
                System.out.print("Enter Membership Type (STUDENT/FACULTY/GUEST): ");
                membershipType = scanner.nextLine().trim().toUpperCase();

                if (!validateMembershipType(membershipType)) {
                    System.out.println("Invalid membership type.");
                }
            } while (!validateMembershipType(membershipType));

            Member member = createMember(memberId, memberName, membershipType);

            String isbn = readRequired(scanner,
                    "Enter ISBN: ", "ISBN cannot be empty.");

            String title = readRequired(scanner,
                    "Enter Book Title: ", "Book Title cannot be empty.");

            int requestedDays = readInt(scanner,
                    "Enter Requested Loan Days (1-30): ", 1, 30,
                    "Loan days must be between 1 and 30.");

            String availability;

            do {
                System.out.print("Is the Book Available? (YES/NO): ");
                availability = scanner.nextLine().trim().toUpperCase();

                if (!availability.equals("YES") && !availability.equals("NO")) {
                    System.out.println("Please enter YES or NO.");
                }
            } while (!availability.equals("YES") && !availability.equals("NO"));

            LibraryBook book = new LibraryBook(isbn, title,
                    availability.equals("YES"));

            int lateDays = readInt(scanner,
                    "Enter Late Days: ", 0, Integer.MAX_VALUE,
                    "Late days cannot be negative.");

            LoanRecord record = service.processRequest(
                    member, book, requestedDays, lateDays);

            printTransaction(record, availability.equals("YES"));
        }

        printSummary(service);
        scanner.close();
    }

    static boolean validateMembershipType(String type) {
        return type.equals("STUDENT") ||
               type.equals("FACULTY") ||
               type.equals("GUEST");
    }

    static Member createMember(String id, String name, String type) {
        switch (type) {
            case "STUDENT":
                return new Student(id, name);
            case "FACULTY":
                return new Faculty(id, name);
            case "GUEST":
                return new Guest(id, name);
            default:
                throw new IllegalArgumentException("Invalid membership type.");
        }
    }

    static String readRequired(Scanner scanner, String prompt, String error) {
        String value;

        do {
            System.out.print(prompt);
            value = scanner.nextLine().trim();

            if (value.isEmpty()) {
                System.out.println(error);
            }
        } while (value.isEmpty());

        return value;
    }

    static int readInt(Scanner scanner, String prompt,
                       int minimum, int maximum, String error) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(text);

                if (value >= minimum && value <= maximum) {
                    return value;
                }

                System.out.println(error);

            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static void printTransaction(LoanRecord record, boolean originallyAvailable) {
        Member member = record.getMember();
        LibraryBook book = record.getBook();

        System.out.println();
        System.out.println("Member Information:");
        System.out.println("Member ID: " + member.getMemberId());
        System.out.println("Member Name: " + member.getMemberName());
        System.out.println("Membership Type: " + member.getMembershipType());

        System.out.println();
        System.out.println("Book Information:");
        System.out.println("ISBN: " + book.getIsbn());
        System.out.println("Book Title: " + book.getTitle());
        System.out.println("Book Available: " + (originallyAvailable ? "YES" : "NO"));
        System.out.println("Requested Loan Days: " + record.getRequestedDays());
        System.out.println("Late Days: " + record.getLateDays());
        System.out.println("Daily Fine: PHP " + member.getDailyFine());
        System.out.println("Maximum Allowed Days: " + member.getMaximumLoanDays());
        System.out.println("Estimated Late Charge: PHP " + record.getEstimatedLateCharge());
        System.out.println("Request Status: " + record.getStatus());
    }

    static void printSummary(LibraryService service) {
        LoanRecord greatest = service.getGreatestRequestedDaysRecord();

        System.out.println();
        System.out.println("BookBridge Library System Summary");
        System.out.println("Total Transactions: " + service.getTransactionCount());
        System.out.println("Approved Requests: " + service.countStatus("APPROVED"));
        System.out.println("Reserved Requests: " + service.countStatus("RESERVED"));
        System.out.println("Denied Requests: " + service.countStatus("DENIED"));

        System.out.println();
        System.out.println("Greatest Requested Loan Days:");
        System.out.println("Member ID: " + greatest.getMember().getMemberId());
        System.out.println("Member Name: " + greatest.getMember().getMemberName());
        System.out.println("Book Title: " + greatest.getBook().getTitle());
        System.out.println("Requested Days: " + greatest.getRequestedDays());

        System.out.println();
        System.out.println("Total Estimated Late Charge: PHP "
                + service.getTotalEstimatedLateCharge());
    }
}
