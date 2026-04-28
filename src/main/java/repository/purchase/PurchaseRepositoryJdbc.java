package repository.purchase;

import database.DatabaseConnection;
import model.Customer;
import model.Purchase;
import repository.customer.CustomerRepository;
import repository.user.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRepositoryJdbc implements PurchaseRepository{
    private final CustomerRepository customerRepository;

    public PurchaseRepositoryJdbc(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Purchase save(Purchase purchase){


        String sql = "INSERT INTO purchases (customer_id, value, date, qty_installments, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1,purchase.getCustomer().getId());
            stmt.setBigDecimal(2,purchase.getValue());
            stmt.setDate(3, Date.valueOf(purchase.getDate()));
            stmt.setInt(4, purchase.getQtyInstallments());
            stmt.setString(5,purchase.getDescription());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
                purchase.setId(rs.getLong(1));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error saving purchase: " + e.getMessage());
        }
        return purchase;
    }

    public Purchase findById(long id){
        String sql = "SELECT * FROM purchases WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return mapResultToPurchase(rs);
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Error finding purchase: " + e.getMessage());
        }
      return null;
    };

    public List<Purchase> findByCustomer(Customer customer){
        String sql = "SELECT * FROM purchases WHERE customer_id = ?";
        List<Purchase> purchases = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customer.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                purchases.add(mapResultToPurchase(rs)) ;
            }

        } catch (SQLException e){
            throw new RuntimeException("Error finding purchases: " + e.getMessage());
        }
        return purchases;
    }

    public List<Purchase> findAll(){
        String sql = "SELECT * FROM purchases";
        List<Purchase> purchases = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                purchases.add(mapResultToPurchase(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding purchases: " + e.getMessage());
        }
        return purchases;
    }

    public void delete(long id){
        String sql = "DELETE FROM purchases WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,id);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Error deleting purchases: " + e.getMessage());
        }

    }

    private Purchase mapResultToPurchase(ResultSet rs) throws SQLException{
        long customerId = rs.getLong("customer_id");
        Customer customer = customerRepository.findById(customerId);

        Purchase purchase = new Purchase(
                customer,
                rs.getBigDecimal("value"),
                rs.getDate("date").toLocalDate(),
                rs.getInt("qty_installments"),
                rs.getString("description")
        );
        purchase.setId(rs.getLong("id"));
        return purchase;
    }
}
