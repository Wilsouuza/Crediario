package service;

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
import repository.purchase.PurchaseRepository;
import service.customer.CustomerService;
import service.installment.InstallmentService;
import service.purchase.PurchaseServiceImpl;
import service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock
    PurchaseRepository purchaseRepository;

    @Mock
    CustomerService customerService;

    @Mock
    InstallmentService installmentService;

    @Mock
    UserService userService;

    @InjectMocks
    PurchaseServiceImpl purchaseService;

    private User registeredBy;
    private Customer customer;
    private Purchase purchase;
    private Installment installment;

    @BeforeEach
    void setup(){
        registeredBy = new User("login","password", UserType.SELLER);

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

        installment = new Installment(
                purchase,
                new BigDecimal("50.00"),
                LocalDate.now()
        );
    }

    @Test
    void shouldThrowExceptionWhenCustomerHasLateInstallments(){
        when(customerService.hasLateInstallments(customer.getCpf())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->{
            purchaseService.createPurchase(
                    customer,
                    new BigDecimal("100.00"),
                    LocalDate.now(),
                    2,
                    "Purchase Teste"
            );
        });

        assertEquals("Customer has late installments.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientLimit(){
        when(customerService.hasLateInstallments(customer.getCpf())).thenReturn(false);
        when(customerService.getAvailableLimit(customer.getCpf())).thenReturn(new BigDecimal("50.00"));

        BusinessException exception = assertThrows(BusinessException.class, () ->{
            purchaseService.createPurchase(
                    customer,
                    new BigDecimal("100.00"),
                    LocalDate.now(),
                    2,
                    "Purchase Teste"
            );
        });

        assertEquals("Insufficient limit.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenExceedsMaxInstallments(){
        BusinessException exception = assertThrows(BusinessException.class, () ->{
            purchaseService.createPurchase(
                    customer,
                    new BigDecimal("100.00"),
                    LocalDate.now(),
                    7,
                    "Purchase Teste"
            );
        });

        assertEquals("Max installments exceeded.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueBelowMinimum(){
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            purchaseService.createPurchase(
                    customer,
                    new BigDecimal("5.00"),
                    LocalDate.now(),
                    6,
                    "Purchase Teste"
            );
        });

        assertEquals("Value below the minimum purchase.", exception.getMessage());
    }

    @Test
    void shouldCreatePurchaseSuccessfully(){
        when(customerService.hasLateInstallments(customer.getCpf())).thenReturn(false);
        when(customerService.getAvailableLimit(customer.getCpf())).thenReturn(new BigDecimal("150.00"));
        when(purchaseRepository.findByCustomer(customer)).thenReturn(new ArrayList<>());

        purchaseService.createPurchase(
                customer,
                new BigDecimal("100.00"),
                LocalDate.now(),
                6,
                "Purchase Teste"
        );

        verify(purchaseRepository, times(1)).save(any(Purchase.class));
        verify(installmentService, times(1)).generateInstallments(any(Purchase.class));
    }

    @Test
    void shouldAddBonusCommissionWhenFirstPurchaseWithin24Hours(){
        when(customerService.hasLateInstallments(customer.getCpf())).thenReturn(false);
        when(customerService.getAvailableLimit(customer.getCpf())).thenReturn(new BigDecimal("150.00"));
        when(purchaseRepository.findByCustomer(customer)).thenReturn(new ArrayList<>());

        purchaseService.createPurchase(
                customer,
                new BigDecimal("100.00"),
                LocalDate.now(),
                6,
                "Purchase Teste"
        );

        verify(userService, times(1)).addCommission(registeredBy.getId(), new BigDecimal("10.00"));
    }
}
