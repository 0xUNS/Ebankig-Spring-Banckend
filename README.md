# Ebankig-Spring-Banckend

![](https://img.icons8.com/?size=20&id=AZOZNnY73haj) [Backend: Ebanking-Spring](https://github.com/0xUNS/Ebankig-Spring-Banckend)

![](https://img.icons8.com/?size=20&id=AZOZNnY73haj) [Frontend: Ebanking-Angular](https://github.com/0xUNS/Ebankig-Angular-Frontend)


![](https://img.icons8.com/?size=100&id=GPfHz0SM85FX) Java  |   ![](https://img.icons8.com/?size=100&id=90519) Spring Boot


# ![](https://img.icons8.com/?size=30&id=Q4PBGTVYTpx8) Schema

- CustomerDTO

| Attribute | Type |
| ----- | --------------- |
| id    | integer($int64) |
| name  | string          |
| email | string          |

# ![](https://img.icons8.com/?size=30&id=50196) Rest API

- ​	`GET` :	/customers/search?keyword=

```json
[
  {
    "id": 0,
    "name": "string",
    "email": "string"
  }
]
```

- `POST` :	/customers

```json
{
  "id": 0,
  "name": "string",
  "email": "string"
}
```

*Request body : required*

- `PUT` : 	/customers
- `GET` : 	/accounts/{accountId}
- `GET` : 	/accounts/{accountId}
- `GET` : 	/accounts/{accountId}/pageOperations
- `GET` : 	/customer-accounts/{customertId}

# ![](https://img.icons8.com/?size=30&id=50024) Example of JSON Data

- `GET` :	/customer-accounts/1?page=0&size=5

```json
{
  "id": 3,
  "name": "Mohamed",
  "currentPage": 0,
  "totalPages": 4,
  "pageSize": 5,
  "totalAccounts": 19,
  "currentPageAccounts": 5,
  "bankAccounts": [
    {
      "type": "CurrentAccount",
      "id": "qxia4qjlzo86",
      "balance": 758539.8519865578,
      "createdAt": "2023-06-21T10:32:33.887+00:00",
      "status": null,
      "overDraft": 9000
    },
    {
      "type": "SavingAccount",
      "id": "rh5hpskisgtt",
      "balance": 555275.5354078917,
      "createdAt": "2023-06-21T10:32:33.892+00:00",
      "status": null,
      "interestRate": 5.5
    }
  ]
}
```

- using :

```java
@GetMapping("/customer-accounts/{customerId}")
	public BankAccountByCustomerDTO getBankAccountsByCustomer(
			@PathVariable Long customerId,
			@RequestParam(name="page",defaultValue = "0") int page,
			@RequestParam(name="size",defaultValue = "5") int size) throws CustomerNotFoundException {
		return bankAccountService.BANK_ACCOUNT_BY_CUSTOMER(customerId, page, size);
	}
```

# ![](https://img.icons8.com/?size=30&id=cdYUlRaag9G9) Docker

Test the web application using Docker

```
docker pull 0xuns/ebanking-backend
docker run -ti -p 8080:8080 0xuns/ebanking-backend
```

Dockerfile

```dockerfile
FROM openjdk:19-jdk-alpine
WORKDIR /app
COPY ./target/ebanking-backend-0.0.1-SNAPSHOT.jar /app/ebanking-backend.jar
EXPOSE 8080
CMD ["java", "-jar", "ebanking-backend.jar"]
```



# ![](https://img.icons8.com/?size=30&id=3FDJsdSpK6cv) Deployment

[On Render :](https://ebank-backend.onrender.com/) [ebank-backend.onrender.com](https://ebank-backend.onrender.com/)	[](https://img.icons8.com/?size=20&id=ZzYf4SXUhPDd) " using Docker container "
