# PasswordChecker test

### Check-list

| № | Check name                                | Status |
|---|-------------------------------------------|--------|
| 1 | Check correct password                    | Pass   |
| 2 | Check password if it has not enough chars | Pass   |
| 3 | Check password if it has no digits        | Pass   |
| 4 | Check password if it has no special chars | Pass   |

___

# Testing of Tallinn-Delivery API

- ***Tallinn-Delivery [URL](http://35.208.34.242) Port: 8080***

- ***API [Documentation](http://35.208.34.242:8080/swagger-ui/index.html/)***
- ***Maven, version: 3.8.5***
- ***Maven Surefire Plugin, version: 2.22.1***
- ***REST Assured, version: 5.4.0***
- ***Apache Commons Codec, version: 1.16.1***
- ***JUnit Jupiter Engine, version: 5.10.0***
- ***JUnit Jupiter Params, version: 5.10.0***
- ***Java, version: 17***

## Unit tests of /Login, /Get all orders, /Get an order by ID, /Delete an order by ID, /Create an order endpoints

### /Login

| № | Check name                                                                                | Status |
|---|-------------------------------------------------------------------------------------------|--------|
| 1 | Login with valid username and password. Check response body and response code is Ok       | Pass   |
| 2 | Login without username and password. Check response body and response code is Bad Request | Fail   |

### /Get all orders

| № | Check name                                                              | Status |
|---|-------------------------------------------------------------------------|--------|
| 1 | Get all orders and check response code is Ok                            | Pass   |
| 2 | Performance test of Get all orders endpoint. Trying 30 requests per sec | Fail   |
| 3 | Performance test of Get all orders endpoint. Trying 20 requests per sec | Pass   |

### /Get an order by ID

| № | Check name                                                                    | Status |
|---|-------------------------------------------------------------------------------|--------|
| 1 | Get order by valid id. Check matches in response body and response code is Ok | Pass   |
| 2 | Get order by invalid id and check response code is Bad Request                | Pass   |

### /Delete an order by ID

| № | Check name                                                              | Status |
|---|-------------------------------------------------------------------------|--------|
| 1 | Delete order if valid id and api key. Check response code is Ok         | Fail   |
| 2 | Delete order if invalid id and check response code is Bad Request       | Pass   |
| 3 | Delete order if invalid api key and check response code is Unauthorized | Pass   |
| 4 | Delete order if no header field and check response code is Bad Request  | Pass   |

### /Create an order

| № | Check name                                                                                             | Status |
|---|--------------------------------------------------------------------------------------------------------|--------|
| 1 | Create order with correct Json data. Check matches in response body and response code is Ok            | Pass   |
| 2 | Create order with correct Json data but without headers. Check response code is Unsupported Media Type | Pass   |
| 3 | Create order with incorrect Json data. Check response body and response code is Bad Request            | Pass   |

### /Update an order by ID

| № | Check name                                                                                         | Status |
|---|----------------------------------------------------------------------------------------------------|--------|
| 1 | Update status with valid id and api key. Check updates in response body and response code is Ok    | Fail   |
| 2 | Trying to update status with invalid api key. Check response code is Unauthorized                  | Pass   |
| 3 | Trying to update status without content type header. Check response code is Unsupported Media Type | Pass   |
| 4 | Trying to update status using invalid data in body. Check response code is Bad Request             | Fail   |
| 5 | Trying to update status with invalid id. Check response code is Order Not Found                    | Fail   |
