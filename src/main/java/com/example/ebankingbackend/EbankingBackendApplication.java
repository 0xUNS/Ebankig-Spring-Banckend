package com.example.ebankingbackend;

import com.example.ebankingbackend.dtos.CustomerDTO;
import com.example.ebankingbackend.entities.*;
import com.example.ebankingbackend.enumes.AccountStatus;
import com.example.ebankingbackend.enumes.OperationType;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.repositories.AccountOperationRepository;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import com.example.ebankingbackend.repositories.CustomerRepository;
import com.example.ebankingbackend.services.BankAccountService;
import com.example.ebankingbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}

	// @Bean
	CommandLineRunner start(
			CustomerRepository customerRepository,
			BankAccountRepository bankAccountRepository,
			AccountOperationRepository accountOperationRepository
			) {
		return args -> {

			Stream.of("Hassan", "Imane", "Mohamed").forEach(name -> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				customerRepository.save(customer);
			});

			customerRepository.findAll().forEach(customer -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 1651);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(customer);
				currentAccount.setOverDraft(15220);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random() * 435);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(customer);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});

			bankAccountRepository.findAll().forEach(account -> {
				for (int i = 0; i < 10; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(account);
					accountOperationRepository.save(accountOperation);
				}
			});
		};
	}

	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
		return args -> {
			Stream.of("Hassan", "Imane", "Mohamed").forEach(name->{

				CustomerDTO customerDTO = new CustomerDTO();
				customerDTO.setName(name);
				customerDTO.setEmail(name+"@gmail.com");

				bankAccountService.saveCustomer(customerDTO);
			});

			bankAccountService.listCustomers().forEach(customer->{

				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}
			});

			List<BankAccount> bankAccounts = bankAccountService.bankAccountList();

			for (BankAccount bankAccount:bankAccounts){
				for (int i = 0; i < 10; i++) {
					String accountId;
//					if (bankAccount instanceof SavingBankAccount){
//						accountId=((SavingBankAccount) bankAccount).getId();
//					} else {
//						accountId = ((CurrentBankAccount) bankAccount).getId();
//					}
					accountId = bankAccount.getId();
					bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
					bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
				}
			}
		};
	}

}
