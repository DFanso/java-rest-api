# Java REST API

## Steps

- install
- build
- run

## Install

```bash
mvn clean install
```

## Build

```bash
mvn clean package
```

## Run

### Option 1 - Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.example.server.SimpleHttpServer"
```

### Option 2 - Using JAR (recommended)

```bash
java -jar target/simple-rest-api-1.0-SNAPSHOT.jar
```

### Test

```bash
curl http://localhost:8000/api/health
```

### If something's wrong with the server and port is in use

```powershell
netstat -ano | findstr :8000
```

```powershell
taskkill /PID <PID> /F
```
