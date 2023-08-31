package com.accounting.cubeia.cubeia.repository;

import com.accounting.cubeia.cubeia.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByIdIn(List<Long> ids);
}
