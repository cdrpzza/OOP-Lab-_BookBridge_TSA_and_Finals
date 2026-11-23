package bookbridge;

import java.util.Scanner;

public class BookBridgeApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        String[] memberId = new String[5];
        String[] memberName = new String[5];
        String[] membershipType = new String[5];
        String[] isbn = new String[5];
        String[] bookTitle = new String[5];
        int[] requestedDays = new int[5];
        int[] lateDays = new int[5];
        boolean[] bookAvailability = new boolean[5];
        String[] requestStatus = new String[5];
        int[] estimatedLateCharge = new int[5];
        
        System.out.println("BookBridge Library System");
        
        for (int i = 0; i < 5; i++) {

        System.out.println();
        System.out.println("Transaction " + (i + 1) + ":");

        do {
            System.out.print("Enter Member ID: ");
            memberId[i] = scanner.nextLine().trim();

            if (memberId[i].isEmpty()) {
                System.out.println("Member ID cannot be empty.");
            }

        } while (memberId[i].isEmpty());

        do {
            System.out.print("Enter Member Name: ");
            memberName[i] = scanner.nextLine().trim();

            if (memberName[i].isEmpty()) {
                System.out.println("Member Name cannot be empty.");
            }

        } while (memberName[i].isEmpty());

        do {
            System.out.print("Enter Membership Type (STUDENT/FACULTY/GUEST): ");
            membershipType[i] = scanner.nextLine().trim().toUpperCase();

            if (!validateMembershipType(membershipType[i])) {
                System.out.println("Invalid membership type.");
            }
        } while (!validateMembershipType(membershipType[i]));

        do {
            System.out.print("Enter ISBN: ");
            isbn[i] = scanner.nextLine().trim();

            if (isbn[i].isEmpty()) {
                System.out.println("ISBN cannot be empty.");
            }

        } while (isbn[i].isEmpty());

        do {
            System.out.print("Enter Book Title: ");
            bookTitle[i] = scanner.nextLine().trim();

            if (bookTitle[i].isEmpty()) {
                System.out.println("Book Title cannot be empty.");
            }

        } while (bookTitle[i].isEmpty());

        

        do {
            System.out.print("Enter Requested Loan Days (1-30): ");

            if (scanner.hasNextInt()) {
                requestedDays[i] = scanner.nextInt();
                scanner.nextLine();

                if (requestedDays[i] < 1 || requestedDays[i] > 30) {
                    System.out.println("Loan days must be between 1 and 30.");
                }

            } else {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
                requestedDays[i] = 0;
            }

        } while (requestedDays[i] < 1 || requestedDays[i] > 30);

        String availabilityInput;

        do {
            System.out.print("Is the Book Available? (YES/NO): ");
            availabilityInput = scanner.nextLine().trim().toUpperCase();

            if (!availabilityInput.equals("YES") && !availabilityInput.equals("NO")) {
                System.out.println("Please enter YES or NO.");
            }

        } while (!availabilityInput.equals("YES") && !availabilityInput.equals("NO"));

        bookAvailability[i] = availabilityInput.equals("YES");

        
        do {
            System.out.print("Enter Late Days: ");

            if (scanner.hasNextInt()) {
                lateDays[i] = scanner.nextInt();
                scanner.nextLine();

                if (lateDays[i] < 0) {
                    System.out.println("Late days cannot be negative.");
                }

            } else {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
                lateDays[i] = -1;
            }

        } while (lateDays[i] < 0);

        
        int maximumDays = getMaximumDays(membershipType[i]);
        int dailyFine = getDailyFine(membershipType[i]);
        
        estimatedLateCharge[i] = lateDays[i] * dailyFine;
      
        requestStatus[i] = classifyRequest(bookAvailability[i], requestedDays[i], maximumDays);

        System.out.println();

        System.out.println("Member Information:");
        System.out.println("Member ID: " + memberId[i]);
        System.out.println("Member Name: " + memberName[i]);
        System.out.println("Membership Type: " + membershipType[i]);

        System.out.println();

        System.out.println("Book Information:");
        System.out.println("ISBN: " + isbn[i]);
        System.out.println("Book Title: " + bookTitle[i]);
        if(bookAvailability[i] == true){
            System.out.println("Book Available: YES");
        }
        else{
            System.out.println("Book Available: NO");
        }
        System.out.println("Requested Loan Days: " + requestedDays[i]);
        System.out.println("Late Days: " + lateDays[i]);
        System.out.println("Daily Fine: PHP " + dailyFine);
        System.out.println("Maximum Allowed Days: " + maximumDays);
        System.out.println("Estimated Late Charge: PHP " + estimatedLateCharge[i]);
        System.out.println("Request Status: " + requestStatus[i]);

        }
        printSummary(memberId, memberName, bookTitle, requestedDays, requestStatus, estimatedLateCharge);
        scanner.close();
    }
    static boolean validateMembershipType(String membershipType) {
        return membershipType.equals("STUDENT") ||
               membershipType.equals("FACULTY") ||
               membershipType.equals("GUEST");
    }
    
    static int getMaximumDays(String membershipType) {
        int maximumDays = 0;

        switch (membershipType) {
            case "STUDENT":
                maximumDays = 14;
                break;

            case "FACULTY":
                maximumDays = 30;
                break;

            case "GUEST":
                maximumDays = 7;
                break;
        }

        return maximumDays;
    }
    
    static int getDailyFine(String membershipType) {
        int dailyFine = 0;

        switch (membershipType) {
            case "STUDENT":
                dailyFine = 5;
                break;

            case "FACULTY":
                dailyFine = 2;
                break;

            case "GUEST":
                dailyFine = 10;
                break;
        }

        return dailyFine;
    }
    
    
    static String classifyRequest(boolean isAvailable, int requestedDays, int maximumDays) {
        String status;

        if (!isAvailable) {
            status = "RESERVED";
        } else if (requestedDays <= maximumDays) {
            status = "APPROVED";
        } else {
            status = "DENIED";
        }

        return status;
    }
    
    static void printSummary(String[] memberId, String[] memberName,
                         String[] bookTitle, int[] requestedDays,
                         String[] requestStatus, int[] estimatedLateCharge) {

        int approved = 0;
        int reserved = 0;
        int denied = 0;
        int totalLateCharge = 0;
        int greatestIndex = 0;

        for (int i = 0; i < 5; i++) {

            if (requestStatus[i].equals("APPROVED")) {
                approved++;
            } else if (requestStatus[i].equals("RESERVED")) {
                reserved++;
            } else {
                denied++;
            }

            totalLateCharge += estimatedLateCharge[i];

            if (requestedDays[i] > requestedDays[greatestIndex]) {
                greatestIndex = i;
            }
        }

        System.out.println();
        System.out.println("BookBridge Library System Summary");
        System.out.println("Total Transactions: 5");
        System.out.println("Approved Requests: " + approved);
        System.out.println("Reserved Requests: " + reserved);
        System.out.println("Denied Requests: " + denied);

        System.out.println();
        System.out.println("Greatest Requested Loan Days:");
        System.out.println("Member ID: " + memberId[greatestIndex]);
        System.out.println("Member Name: " + memberName[greatestIndex]);
        System.out.println("Book Title: " + bookTitle[greatestIndex]);
        System.out.println("Requested Days: " + requestedDays[greatestIndex]);

        System.out.println();
        System.out.println("Total Estimated Late Charge: PHP " + totalLateCharge);
    }

}