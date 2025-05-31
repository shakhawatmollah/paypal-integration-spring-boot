package com.shakhawat.paypalintegration.repository;

import com.shakhawat.paypalintegration.model.Payment;
import com.shakhawat.paypalintegration.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByPaymentId(String paymentId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.payerEmail = :email ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByPayerEmail(String email);

    boolean existsByPaymentIdAndStatus(String paymentId, PaymentStatus status);
}
