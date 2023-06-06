package com.example.ebankingbackend.repositories;

import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
	Page<BankAccount> findBankAccountByCustomerOrderByCreatedAt(Customer customer, Pageable pageable);
}
