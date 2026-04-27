package repository.customer;

import database.DatabaseConnection;
import model.Customer;
import model.User;
import repository.user.UserRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryJdbc implements CustomerRepository {
    private final UserRepository userRepository;

    public CustomerRepositoryJdbc(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (name, cpf, phone, profession, birth_date, credit_limit, registered_by, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getCpf());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getProfession());
            stmt.setDate(5, Date.valueOf(customer.getBirthDate()));
            stmt.setBigDecimal(6, customer.getCreditLimit());
            stmt.setLong(7, customer.getRegisteredBy().getId());
            stmt.setTimestamp(8, Timestamp.valueOf(customer.getRegistrationDate()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                customer.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving customer: " + e.getMessage());
        }
        return customer;
    }

    @Override
    public Customer findById(long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return mapResultSetToCustomer(rs);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error finding customer: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Customer findByCpf(String cpf) {
        String sql = "SELECT * FROM customers WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1,cpf);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()){
                    return mapResultSetToCustomer(rs);
                }

        }catch (SQLException e){
                throw new RuntimeException("Error finding customer" + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                customers.add(mapResultSetToCustomer(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error finding customers" + e.getMessage());
        }

        return customers;
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE  FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Error deleting customer" + e.getMessage());
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException{
        long registeredById = rs.getLong("registered_by");
        User registeredBy = userRepository.findById(registeredById);
        Customer customer = new Customer(
                rs.getString("name"),
                rs.getString("cpf"),
                rs.getString("phone"),
                rs.getString("profession"),
                rs.getDate("birth_date").toLocalDate(),
                rs.getBigDecimal("credit_limit"),
                registeredBy);
        customer.setId(rs.getLong("id"));
        return customer;
    }
}