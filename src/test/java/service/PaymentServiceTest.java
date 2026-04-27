package service;

import enums.InstallmentStatus;
import enums.PaymentMethod;
import enums.UserType;
import exception.BusinessException;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.payment.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    InstallmentService installmentService;

    @InjectMocks
    PaymentServiceImpl paymentService;

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

    // Comportment Tests
    @Test
    void shouldNotApplyFineAmountWhenPaidOnTime(){

        paymentService.createPayment(installment,PaymentMethod.CASH);

        verify(paymentRepository, times(1)).save(argThat(payment ->
                payment.getFineAmount().compareTo(BigDecimal.ZERO) == 0 &&
                payment.getInterestAmount().compareTo(BigDecimal.ZERO) == 0 &&
                payment.getPaidAmount().compareTo(new BigDecimal("50.00")) == 0
        ));
    }

    @Test
    void shouldCalculatorCorrectFineAndInterestWhenLate(){
        Installment lateInstallment = new Installment(
                purchase,
                new BigDecimal("50.00"),
                LocalDate.now().minusDays(10)
        );

        paymentService.createPayment(lateInstallment, PaymentMethod.CASH);

        verify(paymentRepository, times(1)).save(argThat(payment ->
                payment.getFineAmount().compareTo(new BigDecimal("1.00")) == 0 &&
                payment.getInterestAmount().compareTo(new BigDecimal("0.50")) == 0 &&
                payment.getPaidAmount().compareTo(new BigDecimal("51.50")) == 0
        ));
    }

    @Test
    void shouldThrowExceptionWhenInstallmentAlreadyPaid(){
        Installment paidInstallment = new Installment(
                purchase,
                new BigDecimal("50.00"),
                LocalDate.now()
        );

        paidInstallment.setStatus(InstallmentStatus.PAID);

        BusinessException exception = assertThrows(BusinessException.class, () ->{
            paymentService.createPayment(paidInstallment, PaymentMethod.CASH);
        });

        assertEquals("Installment already paid.", exception.getMessage());
    }

    @Test
    void shouldUpdateInstallmentStatusToPaidAfterPayment(){
        paymentService.createPayment(installment, PaymentMethod.CASH);
        assertEquals(InstallmentStatus.PAID, installment.getStatus());
    }
}
