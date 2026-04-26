package service;

import enums.InstallmentStatus;
import enums.PaymentMethod;
import exception.BusinessException;
import model.*;
import repository.PaymentRepository;
import util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;
    private InstallmentService installmentService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, InstallmentService installmentService) {
        this.paymentRepository = paymentRepository;
        this.installmentService = installmentService;
    }

        public void createPayment(Installment installment, PaymentMethod paymentMethod){
            ValidationUtils.notNull(installment, "Installment");
            ValidationUtils.notNull(paymentMethod, "PaymentMethod");

            if (installment.getStatus() == InstallmentStatus.PAID){
                throw new BusinessException("Installment already paid.");
            }

            SystemConfig systemConfig = SystemConfig.getInstance();

            long daysLate = ChronoUnit.DAYS.between(installment.getDueDate(), LocalDate.now());

            BigDecimal fine = daysLate > 0 ? installment.getValue().multiply(systemConfig.getFineRate())
                    : BigDecimal.ZERO;

            BigDecimal interest = daysLate > 0 ? installment.getValue().multiply(systemConfig.getInterestRatePerDay()).multiply(new BigDecimal(daysLate))
                    : BigDecimal.ZERO;

            BigDecimal totalPaid = installment.getValue().add(fine).add(interest);

            installment.setStatus(InstallmentStatus.PAID);

            Payment payment = new Payment(installment, installment.getValue(),fine, totalPaid, interest,paymentMethod);
            paymentRepository.save(payment);
        }

        public Payment findById(long id){
            return paymentRepository.findById(id);
        }

        public List<Payment> findByPurchase(Purchase purchase){
            return paymentRepository.findByPurchase(purchase);
        }

        public List<Payment> findByCustomer(Customer customer){
            return paymentRepository.findByCustomer(customer);
        }

        public List<Payment> findByDateRange(LocalDate start, LocalDate end){
            return paymentRepository.findByDateRange(start,end);
        }

        public List<Payment> findAll(){
            return paymentRepository.findAll();
        }

}
