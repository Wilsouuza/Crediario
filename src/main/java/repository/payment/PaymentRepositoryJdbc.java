package repository.payment;

import database.DatabaseConnection;
import enums.PaymentMethod;
import model.Customer;
import model.Installment;
import model.Payment;
import model.Purchase;
import repository.installment.InstallmentRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepositoryJdbc implements PaymentRepository {
    private final InstallmentRepository installmentRepository;

    public PaymentRepositoryJdbc(InstallmentRepository installmentRepository) {
        this.installmentRepository = installmentRepository;
    }

    public Payment save(Payment payment){
        String sql = "INSERT INTO payments (installment_id, date, original_amount, fine_amount, paid_amount, interest_amount, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1,payment.getInstallment().getId());
            stmt.setDate(2, Date.valueOf(payment.getDate().toLocalDate()));
            stmt.setBigDecimal(3,payment.getOriginalAmount());
            stmt.setBigDecimal(4,payment.getFineAmount());
            stmt.setBigDecimal(5,payment.getPaidAmount());
            stmt.setBigDecimal(6,payment.getInterestAmount());
            stmt.setString(7,payment.getPaymentMethod().name());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
                payment.setId(rs.getLong(1));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error saving payment: " + e.getMessage() );
        }
        return payment;

    }

    public Payment findById(long id){
        String sql = "SELECT * FROM payments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return mapResultToPayment(rs);
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding payment: " + e.getMessage());
        }
        return null;
    }

    public List<Payment> findByPurchase(Purchase purchase){
        String sql = "SELECT pay.* FROM payments pay JOIN installments i ON pay.installment_id = i.id WHERE i.purchase_id = ?";
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,purchase.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                payments.add(mapResultToPayment(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding payment: " + e.getMessage());
        }
        return payments;

    }

    public List<Payment> findByCustomer(Customer customer){
        String sql = "SELECT pay.* FROM payments pay JOIN installments i ON pay.installment_id = i.id JOIN purchases p ON i.purchase_id = p.id WHERE p.customer_id = ?";
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,customer.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                payments.add(mapResultToPayment(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding payment: " + e.getMessage());
        }
        return payments;
    }

    public List<Payment> findAll(){
        String sql = "SELECT * FROM payments";
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                payments.add(mapResultToPayment(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error finding payment: " + e.getMessage());
        }
        return payments;
    }

    public List<Payment> findByDateRange(LocalDate start, LocalDate end){
        String sql = "SELECT * FROM payments WHERE date BETWEEN ? AND ?";
        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                payments.add(mapResultToPayment(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding payment: " + e.getMessage());
        }
        return payments;
    }

    public void delete(long id){
        String sql = "DELETE FROM payments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Error deleting payment: " + e.getMessage());
        }
    }

    private Payment mapResultToPayment(ResultSet rs) throws SQLException{
        long installmentId = rs.getLong("installment_id");
        Installment installment = installmentRepository.findById(installmentId);

        Payment payment = new Payment(
                installment,
                rs.getBigDecimal("original_amount"),
                rs.getBigDecimal("fine_amount"),
                rs.getBigDecimal("paid_amount"),
                rs.getBigDecimal("interest_amount"),
                PaymentMethod.valueOf(rs.getString("payment_method"))
        );
        payment.setId(rs.getLong("id"));

        return payment;
    }
}
