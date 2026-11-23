
package bookbridge;

public class PolymorphismTest {

    public static void main(String[] args) {

        // Test 6: Member reference using a student object
        Member member = new Student("101", "Cedrick");

        System.out.println("Polymorphism Test 6 - Student");
        System.out.println("Membership Type: " + member.getMembershipType());
        System.out.println("Maximum Loan Days: " + member.getMaximumLoanDays());
        System.out.println("Daily Fine: PHP " + member.getDailyFine());

        System.out.println();

        // Test 7: Member reference using a guest object
        member = new Guest("103", "Sabrina");

        System.out.println("Polymorphism Test 7 - Guest");
        System.out.println("Membership Type: " + member.getMembershipType());
        System.out.println("Maximum Loan Days: " + member.getMaximumLoanDays());
        System.out.println("Daily Fine: PHP " + member.getDailyFine());
    }
}
