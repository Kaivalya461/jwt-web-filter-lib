# JWT Design and Workflows in 'com.kv' apps


---
## 1. Authentication
#### Generating New JWT Tokens using Login API
    - Used for first time login with username & password, which then authenticates and generates a JWT.
    - Interacts with Database to authenticate user login details.


---
## 2. Authorization
#### 2.1 JWT Authorization using just the SECRET (No Database interaction & No Spring-Security features)
    - This Authorization is purely used for only allowing valid users to perform API calls.
    - This uses the SECRET to decode the JWT to validate the incoming request.
    - The SECRET is shared between both 'Login service' and 'Other Apps using JWT-Libraries'.

#### 2.2 JWT Authorization with Spring-Security(require DB) - Implementation is Pending.
    - Implementaion WIP


---
## 3. Invalidating Tokens
    - Short lived JWT tokens(15 Mins) with Refresh Tokens(7 Days)
