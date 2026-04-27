package repository.customer;

import model.Customer;

import java.util.List;

public interface CustomerRepository {
    Customer save(Customer customer);
    Customer findById(long id);
    List<Customer> findAll();
    void delete(long id);
    Customer findByCpf(String cpf);
}
