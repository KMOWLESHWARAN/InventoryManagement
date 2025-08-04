package src;
import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/iv";
    private static final String USER = "root";   
    private static final String PASS = "MOULY@2005";

    private Connection connection;
    private Scanner scanner;

    public Main() {
        try {
            // Load the JDBC driver
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
            scanner = new Scanner(System.in);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("Database connection failed. Check your DB_URL, USER, PASS, and ensure MySQL is running.");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println("JDBC Driver not found. Make sure mysql-connector-j-x.x.x.jar is in your classpath.");
        }
    }

    public void start() throws SQLException {
        if (connection == null) {
            System.err.println("Application cannot start without a database connection.");
            return;
        }

        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                     addProduct();
                    break;
                case 2:
                    viewAllProducts();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    delete();
                    break;
                case 5:
                    System.out.println("Exiting Inventory Application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        } while (choice != 5);

        closeResources();
    }

    private void displayMenu() {
        System.out.println("--- Simple Inventory Application ---");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Update Product Quantity/Price");
        System.out.println("4. Delete Product");
        System.out.println("5. Exit");
    }

    //AddProducts.
    public void addProduct() throws SQLException{
    System.out.println("Enter product name: ");
    String name=scanner.nextLine();

    System.out.println("Enter quantity: ");
    int quantity=scanner.nextInt();

    System.out.println("Enter the price: ");
    double price = scanner.nextDouble();

    String query ="Insert into products (name,quantity,price) values(?,?,?)";
    try(PreparedStatement pst = connection.prepareStatement(query)){
        pst.setString(1, name);
        pst.setInt(2, quantity);
        pst.setDouble(3, price);
        
        int rows = pst.executeUpdate();
        if(rows>0){
            System.out.println("Updated");
        }else{
            System.out.println("Not Updated");
        }
    }catch(SQLException e){
        e.printStackTrace();
    }

    }

    public void viewAllProducts() throws SQLException{
        String query="Select id,name,quantity,price from products";
        try(Statement st = connection.createStatement(); 
        ResultSet rs = st.executeQuery(query)){

                while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                Double price = rs.getDouble("price");
               
                 System.out.printf("ID: %d, Name: %s, Quantity: %d, Price: %.2f%n", id, name, quantity, price);
            }
        }
         catch(SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(){
        System.out.println("Update Product");
        System.out.println("Enter the product ID to update:");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new price: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE products SET quantity = ?, price = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, newQuantity);
            pst.setDouble(2, newPrice);
            pst.setInt(3, id);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Updated");
            } else {
                System.out.println("Not update.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void delete(){
        System.out.println("Id to delete");
        int id=scanner.nextInt();
        scanner.nextLine();

       String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("deleted successfully.");
            } else {
                System.out.println("No product found.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // Method to close resources
    private void closeResources() {
        try {
            if (scanner != null) scanner.close();
            if (connection != null) connection.close();
            System.out.println("Resources closed successfully.");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        Main app = new Main();
        app.start();
    }
}
