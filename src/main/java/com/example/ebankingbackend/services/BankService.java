package com.example.ebankingbackend.services;

import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.CurrentAccount;
import com.example.ebankingbackend.entities.SavingAccount;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankService {
	@Autowired
	private BankAccountRepository bankAccountRepository;
	public void consulter() {
		BankAccount bankAccount = bankAccountRepository.findById("2eb2cd7b-d334-406e-b5c7-8d7d3976cdcb").orElse(null);
		System.out.println(" * * * * * * * * * * * * * * * * * *");
		System.out.println(bankAccount.getId());
		System.out.println(bankAccount.getBalance());
		System.out.println(bankAccount.getCustomer().getName());
		System.out.println(bankAccount.getClass().getSimpleName());
		if (bankAccount instanceof CurrentAccount)
			System.out.println("Over Draft : " + ((CurrentAccount) bankAccount).getOverDraft());
		else if (bankAccount instanceof SavingAccount)
			System.out.println("Rate : " + ((SavingAccount) bankAccount).getInterestRate());

		bankAccount.getAccountOperations().forEach(op -> System.out.println(op.getType() +"\t"+ op.getOperationDate() +"\t"+ op.getAmount()));
	}
}
