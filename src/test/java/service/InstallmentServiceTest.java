package service;

import enums.InstallmentStatus;
import enums.UserType;
import model.Customer;
import model.Installment;
import model.Purchase;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.installment.InstallmentRepository;
import service.installment.InstallmentServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class InstallmentServiceTest {


    @Mock
    InstallmentRepository installmentRepository;

    @InjectMocks
    InstallmentServiceImpl installmentService;

    private User registeredBy;
    private Customer customer;
    private Purchase purchase;

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
    }

    @Test
    void shouldGenerateCorrectNumberOfInstallments(){
        installmentService.generateInstallments(purchase);

        verify(installmentRepository, times(2)).save(any(Installment.class));
    }

    @Test
    void shouldGenerateCorrectInstallmentValue(){
        installmentService.generateInstallments(purchase);

        verify(installmentRepository, times(2)).save(argThat(instalment ->
                instalment.getValue().compareTo(new BigDecimal("50.00")) == 0
        ));
    }

    @Test
    void shouldGenerateCorrectDueDates(){
        installmentService.generateInstallments(purchase);

        ArgumentCaptor<Installment> captor = ArgumentCaptor.forClass(Installment.class);

        verify(installmentRepository, times(2)).save(captor.capture());

        List<Installment> savedInstallments = captor.getAllValues();

        assertEquals(LocalDate.now().plusMonths(1), savedInstallments.get(0).getDueDate());
        assertEquals(LocalDate.now().plusMonths(2), savedInstallments.get(1).getDueDate());
    }

    @Test
    void shouldUpdateStatusToLateWhenOverdue(){
        Installment lateInstallment = new Installment(
                purchase,
                new BigDecimal("50.00"),
                LocalDate.now().minusDays(10)
        );
        lateInstallment.setStatus(InstallmentStatus.PENDING);
        List<Installment> lateInstallments = new ArrayList<>();
        lateInstallments.add(lateInstallment);

        when(installmentRepository.findAll()).thenReturn(lateInstallments);

        installmentService.updateOverdueInstallments();

        assertEquals(InstallmentStatus.LATE, lateInstallment.getStatus());
    }
}
