package com.example.ebankingbackend.services;

import com.example.ebankingbackend.dtos.*;
import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.CurrentAccount;
import com.example.ebankingbackend.entities.Customer;
import com.example.ebankingbackend.entities.SavingAccount;
import com.example.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
	CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
	SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
	List<CustomerDTO> listCustomers();
	BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
	void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
	void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
	void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;
	List<BankAccountDTO> bankAccountList();

	CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

	CustomerDTO saveCustomer(CustomerDTO customerDTO);

	CustomerDTO updateCustomer(CustomerDTO customerDTO);

	void deleteCustomer(Long customerId);

	List<AccountOperationDTO> accountHistory(String accountId);

	AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException/*
	@Override
	public List<CustomerDTO> searchCustomers(String keyword) {
		List<Customer> customers=customerRepository.searchCustomer(keyword);
		List<CustomerDTO> customerDTOS = customers.stream().map(cust -> dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
		return customerDTOS;
	}*/;

	List<CustomerDTO> searchCustomers(String keyword);
}
