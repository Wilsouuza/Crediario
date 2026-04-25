package repository;

import model.Customer;

import java.util.List;

public interface CustomerRepository {
    void save(Customer customer);
    Customer findById(long id);
    List<Customer> findAll();
    void delete(long id);
    Customer findByCpf(String cpf);
}
