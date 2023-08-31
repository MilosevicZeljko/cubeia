package com.accounting.cubeia.cubeia.repository;

import com.accounting.cubeia.cubeia.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
}
