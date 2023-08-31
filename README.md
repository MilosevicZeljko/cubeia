# Accounting Transactions API

The Accounting Transactions API is a simple application designed to manage accounting transactions between accounts. It provides endpoints for creating and retrieving transactions, as well as checking the balance of accounts.

## Getting Started

To run the project, you have two options:

### Using Docker Compose

1. Make sure you have Docker Compose installed.
2. Navigate to cubeia directory in your terminal
3. Run the following command to build and start the project:

```console
docker-compose up --build 
```

#### Connecting to mysql running inside of docker
1. Navigate to cubeia directory in your terminal
2. Run the following commands

```console
docker exec -it mysql bash
mysql -h 127.0.0.1 -P 3306 -u root -p 
```

### Running Locally

1. Make sure you have MySQL running locally.
2. Execute the following command to start the application:

```console
mvn spring-boot:run 
```

### There is postman collection inside of postman folder, import it in Postman application

## Endpoints

### Open an Account

- **URL:** `/api/accounts`
- **Method:** `POST`
- **Description:** Open a new account with a specified starting balance and account holder.
- **Request Body::**
  ```json
  {
    "startingBalance": 100.0,
    "accountHolder": "John Doe"
  }
  ``` 
- **Restrictions** *startingBalance* must be present and positive; *accountHolder* must be present

#### Successful Response

- **Status Code:** `201 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "message": "uccessfully created account with id: 1 with starting amount: 123.456"
  }
  ```

### Get Balance of an Account

- **URL:** `/api/accounts/{accountId}`
- **Method:** `GET`
- **Description:** Retrieve the balance of a specific account.
- **Parameters:**
    - `accountId`: ID of the account to retrieve the balance for.

#### Successful Response

- **Status Code:** `200 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "message": "Account with id: 1 has balance of: 123.456",
    "amount": 123.456,
    "success": true
  }


#### Unsuccessful Response

- **Status Code:** `404 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "message": "Account with id: 1 is not present",
    "amount": -1.0,
    "success": false
  }

### Get Transactions for an Account

- **URL** `/api/transactions/{accountId}`
- **Method** `GET`
- **Description** Retrieve a list of transactions for a specific account.
- **Parameters**
- `accountId`: ID of the account to retrieve transactions for.

#### Successful Response

- **Status Code:** `200 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "transactions": [
        {
            "id": 1,
            "amount": 1.1,
            "fromAccountId": 1,
            "toAccountId": 2,
            "timestamp": "2023-08-30T20:47:02.388+00:00"
        },
        {
            "id": 2,
            "amount": 1.1,
            "fromAccountId": 2,
            "toAccountId": 1,
            "timestamp": "2023-08-30T20:47:08.488+00:00"
        },
        {
            "id": 3,
            "amount": 1.1,
            "fromAccountId": 2,
            "toAccountId": 1,
            "timestamp": "2023-08-30T20:47:13.826+00:00"
        }
    ],
    "message": "Successfully retrieved transactions for account with id: 1",
    "size": 3
  }
#### Unsuccessful Response

- **Status Code:** `404 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "transactions": [],
    "message": "Account with id: 1 is not present",
    "size": 0
  }

### Create a New Transaction

- **URL:** `/api/accounts`
- **Method:** `POST`
- **Description:** Create a new accounting transaction.
- **Request Body::**
  ```json
  {
    "amount": 1.1,
    "fromAccountId": 2,
    "toAccountId": 1
  }
  ```
- **Restrictions** *amount* must be present and positive; *fromAccountId* must be present; ; *toAccountId* must be present

#### Successful Response

- **Status Code:** `201 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "message" : "Successfully transferred 1.1 from account with id: 2 to account with id: 1 with transaction id: 1",
    "success" : true
  }

#### Unsuccessful Response when account cant be found

- **Status Code:** `404 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "message" : "Sender or receiver account not found",
    "success" : false
  }

#### Unsuccessful Response when sender does not have enough funds

- **Status Code:** `400 OK`
- **Content Type:** `application/json`
- **Body:**
  ```json
  {
    "message" : "Sending account with does not have enough on balance to complete transaction",
    "success" : false
  }

## Contact

For questions or support, please contact me at zeljko.milosevic.1996@gmail.com
