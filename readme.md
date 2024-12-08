# JWT Design and Workflows in 'com.kv' apps

### 1. JWT Authentication (Login)
    - Used for first time login with username & password, which then authenticates and generates a JWT.
    -

### 2. JWT Authorization using just the SECRET (No Spring-Security features)
    - This Authorization is purely used for only allowing valid users to perform API calls.
    - This uses the SECRET to decode the JWT to validate the incoming request.
    - The SECRET is shared between both 'Login service' and 'Other Apps using JWT-Libraries'.

### 2. JWT Authorization with Spring-Security(require DB) - Implementation is Pending.
    - WIP