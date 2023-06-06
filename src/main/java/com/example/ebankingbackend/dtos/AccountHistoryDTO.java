package com.example.ebankingbackend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AccountHistoryDTO {
	private String accountId;
	private double balance;
	private int currentPage;
	private int totalPages;
	private int pageSize;
	private int totalOperations;
	private int currentPageOperations;
	private List<AccountOperationDTO> accountOperationDTOS;
}
