package service;

import enums.InstallmentStatus;
import enums.UserType;
import exception.BusinessException;
import model.Customer;
import model.Installment;
import model.Purchase;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CustomerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    UserService userService;

    @Mock
    InstallmentService installmentService;

    @InjectMocks
    CustomerServiceImpl customerService;

    private User registeredBy;
    private Customer customer;
    private Purchase purchase;

    @BeforeEach
    void setup(){
        registeredBy = new User("login","password",UserType.SELLER);

        customer = new Customer("João",
                "12345678900",
                "75912345678",
                "Developer",
                LocalDate.of(1990, 1, 15),
                new BigDecimal("500.00"),
                registeredBy
        );

        purchase = new Purchase(
                customer,
                new BigDecimal("100.00"),
                LocalDate.now(),
                2,
                "Purchase Teste"
        );
    }

    // Validation Tests
    @Test
    void shouldThrowExceptionWhenCpfIsBlank(){
        BusinessException exception = assertThrows(BusinessException.class, () ->{
            customerService.createCustomer(
                    "Joao",
                    "",  // empty cpf
                    "75912345678",
                    "Developer",
                    LocalDate.of(1990, 1, 15),
                    registeredBy
            );
        });

        assertEquals("CPF cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank(){
        BusinessException exception =  assertThrows(BusinessException.class, () -> {
            customerService.createCustomer(
                    "",              // empty name
                    "12345678900",
                    "75912345678",
                    "Developer",
                    LocalDate.of(1990, 1, 15),
                    registeredBy
            );
        });
        assertEquals("Name cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsBlank(){
        BusinessException exception = assertThrows(BusinessException.class, () ->{
            customerService.createCustomer(
                    "Joao",
                    "12345678900",
                    "",  // empty phone
                    "Developer",
                    LocalDate.of(1990, 1, 15),
                    registeredBy
            );
        });
        assertEquals("Phone cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProfessionIsBlank(){
        BusinessException exception = assertThrows(BusinessException.class, () ->{
           customerService.createCustomer(
                   "Joao",
                   "12345678900",
                   "75912345678",
                   "", // empty profession
                   LocalDate.of(1990, 1, 15),
                   registeredBy
           );
        });
        assertEquals("Profession cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBirthDateIsNull(){
        BusinessException exception = assertThrows(BusinessException.class, () ->{
           customerService.createCustomer(
                   "Joao",
                   "12345678900",
                   "75912345678",
                   "Developer", // empty profession
                   null,
                   registeredBy
           );
        });
        assertEquals("Birth date cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRegisteredByIsNull(){
        BusinessException exception = assertThrows(BusinessException.class, () ->{
           customerService.createCustomer(
                   "Joao",
                   "12345678900",
                   "75912345678",
                   "Developer", // empty profession
                   LocalDate.of(1990, 1, 15),
                   null
           );
        });
        assertEquals("Registered BY cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists(){

        when(customerRepository.findByCpf("12345678900")).thenReturn(customer);

        assertThrows(BusinessException.class,()->{
            customerService.createCustomer("Wilson", "12345678900", "75912345678", "Developer", LocalDate.of(1990, 1, 15), registeredBy);
        });
    }

    //behaviors Tests

    @Test
    void shouldCreateCustomerSuccessfully(){
        String cpf = "12345678900";
        User registeredBy = new User("login", "password", UserType.SELLER);

        when(customerRepository.findByCpf(cpf)).thenReturn(null);

        Customer customer = customerService.createCustomer(
                "Fulano",
                cpf,
                "75912345678",
                "Developer",
                LocalDate.of(1999,1,16),
                registeredBy);

        assertNotNull(customer);
        assertEquals(cpf, customer.getCpf());
    }

    @Test
    void shouldAddCommissionWhenCustomerIsCreated(){
        when(customerRepository.findByCpf("12345678900")).thenReturn(null);

        customerService.createCustomer(
                "Joao",
                "12345678900",
                "75912345678",
                "Developer", // empty profession
                LocalDate.of(1990, 1, 15),
                registeredBy
        );

        verify(userService, times(1)).addCommission(
                registeredBy.getId(),new BigDecimal("4.00")
        );
    }

    //findByCpf tests

    @Test
    void shouldReturnCustomerWhenCpfExists(){

        when(customerRepository.findByCpf("12345678900")).thenReturn(customer);

        Customer result = customerService.findByCpf(customer.getCpf());

        assertNotNull(customer);
        assertEquals(customer.getCpf(), result.getCpf());
    }

    @Test
    void shouldThrowExceptionWhenCpfNotFound(){

        when(customerRepository.findByCpf("12345678900")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () ->{
            customerService.findByCpf("12345678900");
        });

        assertEquals("Customer not found.", exception.getMessage());
    }

    //getAvailableLimit Tests

    @Test
    void shouldReturnFullLimitWhenNoInstallments(){
        when(installmentService.findByCustomerAndStatus(any(),eq(InstallmentStatus.PENDING))).thenReturn(new ArrayList<>());
        when(installmentService.findByCustomerAndStatus(any(),eq(InstallmentStatus.LATE))).thenReturn(new ArrayList<>());
        when(customerRepository.findByCpf(customer.getCpf())).thenReturn(customer);

        BigDecimal result = customerService.getAvailableLimit(customer.getCpf());

        assertEquals(0, new BigDecimal("500.00").compareTo(result));
    }

    @Test
    void shouldReturnReducedLimitWhenHasOpenInstallments(){
        List<Installment> pendingInstallments = new ArrayList<>();
        Installment installment = new Installment(
                purchase,
                new BigDecimal("50.00"),
                LocalDate.now().minusDays(10)
        );

        pendingInstallments.add(installment);

        when(installmentService.findByCustomerAndStatus(any(),eq(InstallmentStatus.PENDING))).thenReturn(new ArrayList<>(pendingInstallments));
        when(installmentService.findByCustomerAndStatus(any(),eq(InstallmentStatus.LATE))).thenReturn(new ArrayList<>());
        when(customerRepository.findByCpf(customer.getCpf())).thenReturn(customer);

        BigDecimal result = customerService.getAvailableLimit(customer.getCpf());

        assertEquals(0, new BigDecimal("450.00").compareTo(result));
    }

    // hasLateInstallments Tests
    @Test
    void shouldReturnTrueWhenCustomerHasLateInstallments() {
        when(customerRepository.findByCpf(customer.getCpf())).thenReturn(customer);
        when(installmentService.hasLateInstallments(customer)).thenReturn(true);

        boolean result = customerService.hasLateInstallments(customer.getCpf());

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenCustomerHasNoLateInstallments() {
        when(customerRepository.findByCpf(customer.getCpf())).thenReturn(customer);
        when(installmentService.hasLateInstallments(customer)).thenReturn(false);

        boolean result = customerService.hasLateInstallments(customer.getCpf());

        assertFalse(result);
    }

}
