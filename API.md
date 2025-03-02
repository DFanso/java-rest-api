# API Documentation

Base URL: `http://localhost:8000`

## Health Check

### GET /api/health
Check if the API is running.

```bash
curl http://localhost:8000/api/health
```

Response:
```json
{
    "status": "UP"
}
```

## Users API

### GET /api/users
Get all users.

```bash
curl http://localhost:8000/api/users
```

Response:
```json
[
    {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com"
    },
    {
        "id": 2,
        "name": "Jane Smith",
        "email": "jane@example.com"
    }
]
```

### GET /api/users/{id}
Get a specific user by ID.

```bash
curl http://localhost:8000/api/users/1
```

Response:
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
}
```

### POST /api/users
Create a new user.

```bash
curl -X POST \
  http://localhost:8000/api/users \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
}'
```

Response:
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
}
```

### PUT /api/users/{id}
Update an existing user.

```bash
curl -X PUT \
  http://localhost:8000/api/users/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com"
}'
```

Response:
```json
{
    "id": 1,
    "name": "John Updated",
    "email": "john.updated@example.com"
}
```

### DELETE /api/users/{id}
Delete a user.

```bash
curl -X DELETE http://localhost:8000/api/users/1
```

Response: Empty response with status code 204

## Error Responses

### Not Found (404)
```json
{
    "error": "User not found"
}
```

### Bad Request (400)
```json
{
    "error": "Invalid email format"
}
```

### Method Not Allowed (405)
```json
{
    "error": "Method not allowed"
}
```

### Unsupported Media Type (415)
```json
{
    "error": "Content-Type must be application/json"
}
```

## PowerShell Examples

If you're using PowerShell, here are equivalent commands:

### GET Request
```powershell
Invoke-RestMethod -Uri "http://localhost:8000/api/users" -Method Get
```

### POST Request
```powershell
$body = @{
    name = "John Doe"
    email = "john@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8000/api/users" -Method Post -Body $body -ContentType "application/json"
```

### PUT Request
```powershell
$body = @{
    name = "John Updated"
    email = "john.updated@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8000/api/users/1" -Method Put -Body $body -ContentType "application/json"
```

### DELETE Request
```powershell
Invoke-RestMethod -Uri "http://localhost:8000/api/users/1" -Method Delete
```