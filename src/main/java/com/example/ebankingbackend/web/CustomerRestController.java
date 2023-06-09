package com.example.ebankingbackend.web;

import com.example.ebankingbackend.dtos.BankAccountByCustomerDTO;
import com.example.ebankingbackend.dtos.CustomerDTO;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
	private BankAccountService bankAccountService;
	@GetMapping("/")
	public String home() {
		return "<a href='swagger-ui.html'>Swagger</a>";
	}
	@GetMapping("/customers")
	private List<CustomerDTO> customers() {
		return bankAccountService.listCustomers();
	}
	@GetMapping("/customers/search")
	public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword){
		return bankAccountService.searchCustomers("%"+keyword+"%");
	}
	@GetMapping("/customers/{id}")
	public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
		return bankAccountService.getCustomer(customerId);
	}
	@PostMapping("/customers")
	public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
		return bankAccountService.saveCustomer(customerDTO);
	}
	@PutMapping("/customers/{id}")
	public CustomerDTO updateCustomer(@PathVariable(name = "id") Long customerId , @RequestBody CustomerDTO customerDTO){
		customerDTO.setId(customerId);
		return bankAccountService.updateCustomer(customerDTO);
	}
	@DeleteMapping("/customers/{id}")
	public void deleteCustomer(@PathVariable Long id){
		bankAccountService.deleteCustomer(id);
	}

	@GetMapping("/customer-accounts/{customerId}")
	public BankAccountByCustomerDTO getBankAccountsByCustomer(
			@PathVariable Long customerId,
			@RequestParam(name="page",defaultValue = "0") int page,
			@RequestParam(name="size",defaultValue = "5") int size) throws CustomerNotFoundException {
		return bankAccountService.BANK_ACCOUNT_BY_CUSTOMER(customerId, page, size);
	}
}
