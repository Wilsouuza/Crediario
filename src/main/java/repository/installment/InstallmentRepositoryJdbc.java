package repository.installment;

import database.DatabaseConnection;
import enums.InstallmentStatus;
import model.Customer;
import model.Installment;
import model.Purchase;
import repository.purchase.PurchaseRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InstallmentRepositoryJdbc implements InstallmentRepository {
    private final PurchaseRepository purchaseRepository;

    public InstallmentRepositoryJdbc(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Installment save(Installment installment){
        String sql = "INSERT INTO installments (purchase_id, value, due_date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1,installment.getPurchase().getId());
            stmt.setBigDecimal(2,installment.getValue());
            stmt.setDate(3, Date.valueOf(installment.getDueDate()));
            stmt.setString(4,installment.getStatus().name());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
                installment.setId(rs.getLong(1));
            }


        }catch (SQLException e){
            throw new RuntimeException("Error saving installment: " + e.getMessage());
        }
        return installment;
    }

    @Override
    public Installment findById(long id){
        String sql = "SELECT * FROM installments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return mapResultToInstallment(rs);
            }

        }catch (SQLException e){
            throw new RuntimeException("Error saving installment: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Installment> findByPurchase(Purchase purchase){
        String sql = "SELECT * FROM installments WHERE purchase_id = ?";
        List<Installment> installments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,purchase.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                installments.add(mapResultToInstallment(rs));
            }

        } catch (SQLException e){
            throw new RuntimeException("Error finding installments: " + e.getMessage());
        }
        return installments;
    }

    @Override
    public List<Installment> findByCustomer(Customer customer){
        String sql = "SELECT i.* FROM installments i join purchases p on i.purchase_id = p.id WHERE p.customer_id = ?";
        List<Installment> installments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,customer.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                installments.add(mapResultToInstallment(rs));
            }
        } catch (SQLException e){
            throw new RuntimeException("Error finding installments: " + e.getMessage());
        }
        return installments;

    }

    @Override
    public List<Installment> findByStatus(InstallmentStatus status){
        String sql = "SELECT * FROM installments WHERE status = ?";
        List<Installment> installments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1,status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                installments.add(mapResultToInstallment(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding installments: " + e.getMessage());
        }
        return installments;
    }

    @Override
    public List<Installment> findByCustomerAndStatus(Customer customer, InstallmentStatus status){
        String sql = "SELECT i.* FROM installments i join purchases p on i.purchase_id = p.id WHERE p.customer_id = ? and status = ?";
        List<Installment> installments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customer.getId());
            stmt.setString(2,status.name());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                installments.add(mapResultToInstallment(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding installments: " + e.getMessage());
        }
        return installments;
    }

    @Override
    public List<Installment> findAll(){
        String sql = "SELECT * FROM installments";
        List<Installment> installments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                installments.add(mapResultToInstallment(rs));
            }

        }catch (SQLException e){
            throw new RuntimeException("Error finding installment: " + e.getMessage());
        }
        return installments;
    }

    @Override
    public void updateStatus(long id, InstallmentStatus status){
        String sql = "UPDATE installments SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1,status.name());
            stmt.setLong(2,id);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Error deleting installment: " + e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM installments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1,id);
            stmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException("Error deleting installment: " + e.getMessage());
        }

    }

    private Installment mapResultToInstallment(ResultSet rs) throws SQLException{
        long purchaseId = rs.getLong("purchase_id");
        Purchase purchase = purchaseRepository.findById(purchaseId);

        Installment installment = new Installment(
                purchase,
                rs.getBigDecimal("value"),
                rs.getDate("due_date").toLocalDate()
        );
        installment.setId(rs.getLong("id"));
        installment.setStatus(InstallmentStatus.valueOf(rs.getString("status")));
        return installment;
    }
}
