package repository;

import model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {
    private List<Customer> customers = new  ArrayList<>();
    private long nextId = 1;

    public void save(Customer customer){
        customer.setId(nextId++);
        customers.add(customer);
    }

    public Customer findById(long id){
        for(Customer c : customers){
            if(c.getId() == id){
                return c;
            }
        }
        return null;
    }

    public List<Customer> findAll(){
            return new ArrayList<>(customers);
    }

    public void delete(long id){
        Customer customer = findById(id);
        if (customer != null){
            customers.remove(customer);
        }
    }

    public Customer findByCpf(String cpf){
        for (Customer c : customers){
            if (cpf.equals(c.getCpf())){
                return c;
            }
        }
        return null;
    }

}
