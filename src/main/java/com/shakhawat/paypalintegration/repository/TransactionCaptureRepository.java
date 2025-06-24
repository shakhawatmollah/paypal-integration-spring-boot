package com.shakhawat.paypalintegration.repository;

import com.shakhawat.paypalintegration.model.TransactionCapture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionCaptureRepository extends JpaRepository<TransactionCapture, Long> {
    Optional<TransactionCapture> findByCaptureId(String captureId);
}

