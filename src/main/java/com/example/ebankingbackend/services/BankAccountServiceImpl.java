package com.example.ebankingbackend.services;

import com.example.ebankingbackend.dtos.*;
import com.example.ebankingbackend.entities.*;
import com.example.ebankingbackend.enumes.OperationType;
import com.example.ebankingbackend.exceptions.BalanceNotSufficientException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.mappers.BankAccountMapperImpl;
import com.example.ebankingbackend.repositories.AccountOperationRepository;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import com.example.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
	private CustomerRepository customerRepository;
	private BankAccountRepository bankAccountRepository;
	private AccountOperationRepository accountOperationRepository;
	private BankAccountMapperImpl dtoMapper;

	public static String shortUUID() {
		UUID uuid = UUID.randomUUID();
		long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
		return Long.toString(l, Character.MAX_RADIX);
	}

	@Override
	public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId).orElse(null);
		if (customer == null)
			throw new CustomerNotFoundException("Customer not found");
		CurrentAccount currentAccount = new CurrentAccount();
		currentAccount.setId(shortUUID());
		currentAccount.setCreatedAt(new Date());
		currentAccount.setBalance(initialBalance);
		currentAccount.setOverDraft(overDraft);
		currentAccount.setCustomer(customer);
		CurrentAccount saveCurrentAccount = bankAccountRepository.save(currentAccount);
		return dtoMapper.fromCurrentBankAccount(saveCurrentAccount);
	}

	@Override
	public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId).orElse(null);
		if (customer == null)
			throw new CustomerNotFoundException("Customer not found");
		SavingAccount savingAccount = new SavingAccount();
		savingAccount.setId(shortUUID());
		savingAccount.setCreatedAt(new Date());
		savingAccount.setBalance(initialBalance);
		savingAccount.setInterestRate(interestRate);
		savingAccount.setCustomer(customer);
		SavingAccount saveSavingAccount = bankAccountRepository.save(savingAccount);
		return dtoMapper.fromSavingBankAccount(saveSavingAccount);
	}

	@Override
	public List<CustomerDTO> listCustomers() {
		List<Customer> customers = customerRepository.findAll();
		return customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).toList();
	}

	@Override
	public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
		return toBankAccountDTO(bankAccount);
	}

	@Override
	public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
		if (bankAccount.getBalance() < amount)
			throw new BalanceNotSufficientException("Balance not sufficient");

		AccountOperation accountOperation =   new AccountOperation();
		accountOperation.setType(OperationType.DEBIT);
		accountOperation.setAmount(amount);
		accountOperation.setDescription(description);
		accountOperation.setOperationDate(new Date());
		accountOperation.setBankAccount(bankAccount);
		accountOperationRepository.save(accountOperation);
		bankAccount.setBalance(bankAccount.getBalance()-amount);
		bankAccountRepository.save(bankAccount);
	}

	@Override
	public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));

		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.CREDIT);
		accountOperation.setAmount(amount);
		accountOperation.setDescription(description);
		accountOperation.setOperationDate(new Date());
		accountOperation.setBankAccount(bankAccount);
		accountOperationRepository.save(accountOperation);
		bankAccount.setBalance(bankAccount.getBalance()+amount);
		bankAccountRepository.save(bankAccount);
	}

	@Override
	public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
		debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
		credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
	}

	@Override
	public List<BankAccountDTO> bankAccountList() {
		List<BankAccount> bankAccounts = bankAccountRepository.findAll();
		return bankAccounts.stream().map(this::toBankAccountDTO).toList();
	}

	@Override
	public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new CustomerNotFoundException("Customer Not found"));
		return dtoMapper.fromCustomer(customer);
	}

	@Override
	public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
		log.info("Saving New Customer");
		Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
		Customer savedCustomer = customerRepository.save(customer);
		return dtoMapper.fromCustomer(savedCustomer);
	}

	@Override
	public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
		log.info("Saving new Customer");
		Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
		Customer savedCustomer = customerRepository.save(customer);
		return dtoMapper.fromCustomer(savedCustomer);
	}

	@Override
	public void deleteCustomer(Long customerId){
		customerRepository.deleteById(customerId);
	}

	@Override
	public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
		if(bankAccount == null) throw new BankAccountNotFoundException("Account not Found");
		Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
		AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
		List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).toList();
		accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
		accountHistoryDTO.setAccountId(bankAccount.getId());
		accountHistoryDTO.setBalance(bankAccount.getBalance());
		accountHistoryDTO.setCurrentPage(page);
		accountHistoryDTO.setPageSize(size);
		accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
		accountHistoryDTO.setTotalOperations((int) accountOperations.getTotalElements());
		accountHistoryDTO.setCurrentPageOperations(accountOperationDTOS.size());
		return accountHistoryDTO;
	}

	@Override
	public List<CustomerDTO> searchCustomers(String keyword) {
		List<Customer> customers = customerRepository.searchCustomer(keyword);
		return customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).toList();
	}

	@Override
	public BankAccountByCustomerDTO BANK_ACCOUNT_BY_CUSTOMER(Long customerId, int page, int size) throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer Not found"));
		BankAccountByCustomerDTO bankAccountByCustomerDTO = dtoMapper.fromBankAccountByCustomer(customer);
		Page<BankAccount> bankAccounts = bankAccountRepository.findBankAccountByCustomerOrderByCreatedAt(customer, PageRequest.of(page, size));
		List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(this::toBankAccountDTO).toList();
		bankAccountByCustomerDTO.setCurrentPage(page);
		bankAccountByCustomerDTO.setPageSize(size);
		bankAccountByCustomerDTO.setTotalPages(bankAccounts.getTotalPages());
		bankAccountByCustomerDTO.setTotalAccounts((int) bankAccounts.getTotalElements());
		bankAccountByCustomerDTO.setCurrentPageAccounts(bankAccountDTOS.size());
		bankAccountByCustomerDTO.setBankAccounts(bankAccountDTOS);
		return bankAccountByCustomerDTO;
	}

	private BankAccountDTO toBankAccountDTO(BankAccount bankAccount) {
		if (bankAccount instanceof CurrentAccount) {
			CurrentAccount currentAccount = (CurrentAccount) bankAccount;
			return dtoMapper.fromCurrentBankAccount(currentAccount);
		} else if (bankAccount instanceof SavingAccount) {
			SavingAccount savingAccount = (SavingAccount) bankAccount;
			return dtoMapper.fromSavingBankAccount(savingAccount);
		}
		return null;
	}

}
