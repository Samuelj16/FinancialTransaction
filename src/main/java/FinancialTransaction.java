import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FinancialTransaction {
    // created a transaction File
    private   static final String TRANSACTION_FILE= "transactions.csv";
    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        boolean quit = false;

        // Print out a prompt
        //Enter display for the home screen
        while (!quit) {
            System.out.println(" Welcome to Financial Transactions!: ");
            System.out.println(" Enter D for Deposit: ");
            System.out.println(" Enter P for Payment: ");
            System.out.println(" Enter L for ledger: ");
            System.out.println(" Enter X for Exit: ");

            System.out.println (" Enter Your Choice: ");

            String choice = keyboard.next().toUpperCase();
            switch (choice) {
                case "D": addTransaction();
                break;
                case "P": subTransaction();
                break;
                case "L": ledgerMenu();
                break;
                case "X": System.out.println("Exiting");
                    quit = true;
                break;
                default:System.out.println("Invalid choice: ");


            }

        }

    }
    private static void addTransaction() {
        System.out.println("Enter the deposit amount: ");
        Scanner keyboard = new Scanner(System.in);
        double amount = keyboard.nextDouble();
        keyboard.nextLine();
        System.out.println("Enter description: ");
        String description = keyboard.nextLine();
        System.out.println("Enter Vendor: ");
        String vendor = keyboard.nextLine();
        saveTransaction(description, vendor, amount, "Deposit");
    }
    private static void subTransaction() {
        System.out.println("Enter the debit amount: ");
        Scanner keyboard = new Scanner(System.in);
        double amount = keyboard.nextDouble();
        keyboard.nextLine();
        System.out.println("Enter description: ");
        String description = keyboard.nextLine();
        System.out.println("Enter Vendor: ");
        String vendor = keyboard.nextLine();
        amount = -amount;
        saveTransaction(description, vendor, amount, "Payment");

    }
    private static void saveTransaction(String description,String vendor, double amount, String type) {
        //  Date time for transaction file
        try( FileWriter fw = new FileWriter(TRANSACTION_FILE,true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
            String date = now.format(dateFormat);
            String time = now.format(timeFormat);

            fw.append(String.format("%s|%s|%s|%s|%.2f|%s\n",date, time, description, vendor,  amount, type));

        }
        //  Block of code to handle errors
        catch( IOException e ){
            System.out.println("Error Writing.transaction.csv");
        }
    }
    // display for ledger
    private static void ledgerMenu(){
        boolean back = false;
        while(!back) {
            System.out.println("Enter A for display all entries: ");
            System.out.println("Enter D for display only the entries that are deposits into the account: ");
            System.out.println("Enter P for display only the negative entries: ");
            System.out.println("Enter R for display a new screen that allows the user to run Pre-defined  reports or to run a custom search: ");
            System.out.println("Enter H to go back to the home page: ");
            System.out.println("Enter Your Choice: ");
            Scanner keyboard = new Scanner(System.in);

            String choice = keyboard.next().toUpperCase();
            switch (choice) {

                case "A":
                    DisplayEntries(null);
                    break;
                case "D":
                    DisplayEntries("Deposit");
                    break;
                case "P":
                    DisplayEntries("Payment");
                    break;
                case "R":
                    ReportMenu();
                    break;
                case "H":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.: ");


            }
        }

    }
        // custom search in ledger
        private static void ReportMenu(){
            boolean back = false;
            while(!back) {
                System.out.println("Report menu: ");
                System.out.println("Enter 1 for Month To Date: ");
                System.out.println("Enter 2 for Previous Month: ");
                System.out.println("Enter 3 for Year To Date: ");
                System.out.println("Enter 4 for Previous Year: ");
                System.out.println("Enter 5 for Search by Vendor: ");
                System.out.println("Enter 0 for Back: ");

                System.out.println("Enter Your Choice: ");
                Scanner keyboard = new Scanner(System.in);

                int choice = keyboard.nextInt();
                switch (choice) {

                    case 1:
                        displayReport(Period.ofMonths(1), true);
                        break;
                    case 2:
                        displayReport(Period.ofMonths(1), false);
                        break;
                    case 3:
                        displayReport(Period.ofYears(1), true);
                        break;
                    case 4:
                        displayReport(Period.ofYears(1), false);
                        break;
                    case 5:
                        searchbyVendor();
                        break;
                    case 0:
                        back = true;
                        break;
                    default: System.out.println("Invalid choice.: ");


                }

            }
        }
        private static void DisplayEntries(String type) {
        System.out.println("Date|Time|Description|Vendor|Amount|Type  : ");
        System.out.println("--------------------------------------- : ");
        try {
            List< String > lines = Files.readAllLines(Paths.get(TRANSACTION_FILE));
            Collections.reverse(lines);
            for(String line: lines){
                String[] details = line.split("\\|");
                if(type == null || type.equalsIgnoreCase(details[5])){
                    System.out.println(String.join(" | ", details));
                }
            }
        }
catch(IOException e){
            System.out.println("Errors reading from the transaction files: ");
}
        }
        private static void searchbyVendor(){
        Scanner keybord = new Scanner(System.in);
        keybord.nextLine();
        String vendorname = keybord.nextLine();
        System.out.println("Transaction for "+vendorname);
        try {
            List< String > lines = Files.readAllLines(Paths.get(TRANSACTION_FILE));
            for (String line: lines){
                String[] details = line.split("\\|");
                if (details[3].equalsIgnoreCase(vendorname)) {
                    System.out.println(String.join(" | ", details));
                }


            }
        }
        catch(IOException e){
            System.out.println("Errors reading from the transaction files: ");
        }
        }
        private static void displayReport(Period period,boolean isCurrent){
            LocalDate now = LocalDate.now();
            LocalDate startPeriod;
            if(isCurrent){
                startPeriod = now.minus(period);

            }
            else {
                startPeriod = now.minus(period).minus(period);
                now = startPeriod.plus(period);
            }
        System.out.println(" Transaction from "+ startPeriod+" To "+now);
            displayEntriesInPeriod(startPeriod,now);
        }
        private static void displayEntriesInPeriod(LocalDate start, LocalDate end){
        try {
            List< String > lines = Files.readAllLines(Paths.get(TRANSACTION_FILE));
            for (String line: lines){
                String[] details = line.split("\\|");
                LocalDate date = LocalDate.parse(details[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if ((date.isAfter(start) || date.equals(start)) && (date.isBefore(end)|| date.equals(end))){
                    System.out.println(String.join(" | ", details));
                }
            }
        }
        catch(IOException e){
            System.out.println("Errors reading from the transaction files: ");
        }
        }
}

