package com.accounting.cubeia.cubeia.repository;

import com.accounting.cubeia.cubeia.entity.Account;
import com.accounting.cubeia.cubeia.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {
}
