Project Description

BookBridge_TSA4 is a library management system that I developed using Java Swing in Apache NetBeans. The program allows users to add books, register members, borrow and return books, manage reservations, and view reports. I also connected the program to MariaDB using JDBC so that the records can be saved and accessed even after closing the application.

Software Requirements

The following software is needed to run the program:

Java Development Kit (JDK)

Apache NetBeans IDE

Apache Ant

XAMPP

MariaDB Connector/J

Database Setup

To set up the database, open XAMPP and start MySQL. After that, open phpMyAdmin through http://localhost/phpmyadmin and create a database named bookbridge_tsa4. Import the schema.sql file to create the four tables, which are books, members, loans, and reservations.

Make sure that MariaDB Connector/J is added to the project's Libraries and that the connection settings in DatabaseConnection.java match the database configuration.

How to Run

First, open the BookBridge_TSA4 project in Apache NetBeans and make sure MySQL is running in XAMPP. After that, right-click the project and select Clean and Build to check if there are any errors. Once the build is successful, run BookBridgeFrame.java to open the program.

The program will automatically load the saved records from the database.

Program Features

The program has five tabs with different functions:

Catalog: Allows users to add, search, and view books.

Members: Allows users to register, search, and view members.

Circulation: Handles borrowing and returning books.

Reservations: Allows users to create, cancel, and process reservations.

Reports: Displays the available books, active loans, pending reservations, estimated fines, and collection consistency.

Program Structure

I organized the program into different classes based on their functions. BookBridgeFrame.java handles the user interface, while LibraryService.java handles the validation and borrowing rules. The DAO classes are used to save and retrieve records from MariaDB.

I also used SwingWorker to run database operations in the background so that the program remains responsive. Synchronization was also used to help prevent problems when accessing shared data.

Testing

I tested the program using ten test cases to check if the different features were working properly. These included adding books, registering members, issuing and returning loans, managing reservations, handling invalid inputs, and testing the database connection.

The results and screenshots are included in the Abana_BookBridge_TSA4.pdf file.

Additional Notes

The project uses Java with Ant, so it uses build.xml instead of pom.xml.

The schema.sql file only contains the database structure and does not include the records used during testing. MySQL must also be running in XAMPP for the program to connect to the database.
