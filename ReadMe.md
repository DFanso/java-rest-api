# Java REST API with JAX-RS

## Build and Run

### Option 1 - Using Jetty (Recommended for Development)
```bash
mvn clean jetty:run
```

### Option 2 - Build WAR file
```bash
mvn clean package
```
The WAR file will be created in the `target` directory. Deploy it to your favorite Java application server (Tomcat, WildFly, etc.).

## Test the API

### Health Check
```bash
curl http://localhost:8000/api/health
```

### Get All Users
```bash
curl http://localhost:8000/api/users
```

### Get User by ID
```bash
curl http://localhost:8000/api/users/1
```

### Create User
```bash
curl -X POST http://localhost:8000/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

### Update User
```bash
curl -X PUT http://localhost:8000/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john.updated@example.com"}'
```

### Delete User
```bash
curl -X DELETE http://localhost:8000/api/users/1
```
`
```
mvn clean
mvn install
mvn jetty:run
```
