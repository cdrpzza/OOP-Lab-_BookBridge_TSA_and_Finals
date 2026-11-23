1. Project Description

BookBridge is a Java-based library management system that was developed throughout the four Technical Summative Assessments. The system allows users to add books, register members, borrow and return books, manage reservations, and view circulation reports. It uses Java Swing for the graphical user interface and JDBC with MariaDB to store and manage the records.

2. Software Requirements

The following software is needed to run the project:

- Apache NetBeans IDE
- Java Development Kit (JDK) 17 or later
- XAMPP with MariaDB
- MariaDB JDBC Driver
- Web browser for phpMyAdmin

3. Database Setup

The project uses a MariaDB database named bookbridge_tsa4. To set up the database, follow these steps:

1. Open XAMPP Control Panel and start Apache and MySQL.
2. Open a browser and go to http://localhost/phpmyadmin.
3. Create a new database named bookbridge_tsa4.
4. Click the database and select Import.
5. Choose the provided schema.sql file and click Import.
6. Check if the four tables (books, members, loans, and reservations) were successfully created.

The database connection is handled by DatabaseConnection.java, which connects the Java application to MariaDB using JDBC.

4. Project Setup

1. Open Apache NetBeans.
2. Click File → Open Project.
3. Find and open the BookBridge_Final project.
4. Make sure that the MariaDB JDBC Driver is added to the project's Libraries.
5. Check if the database settings in DatabaseConnection.java match the database in XAMPP.
6. Set bookbridge.ui.BookBridgeFrame as the main class.

5. Building and Running the Project

1. Make sure MySQL is running in XAMPP.
2. Open the BookBridge_Final project in NetBeans.
3. Right-click the project and select Clean and Build.
4. Wait until the Output window displays BUILD SUCCESSFUL.
5. Right-click the project again and select Run.
6. The BookBridge application should open and display the five tabs: Catalog, Members, Circulation, Reservations, and Reports.

6. Troubleshooting

If the program cannot connect to the database, check if MySQL is running in XAMPP and if the database settings are correct.

If there is an error related to JDBC, check if the MariaDB JDBC Driver is added to the project's Libraries.

If the application does not open, make sure that BookBridgeFrame is selected as the main class and try cleaning and building the project again.

7. Testing and Verification

I tested the system using 15 test cases to check if its different features work properly. These include adding books, registering members, borrowing and returning books, managing reservations, and handling invalid inputs.

I also tested the system by closing and reopening it to check if the records were still saved in the database. The test results and screenshots are included in the Final Test Report.
