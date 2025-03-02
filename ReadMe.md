## Java REST API


### Steps
- build
- run

### Build
```bash
mvn clean install
```

### Run
```bash
mvn exec:java -Dexec.mainClass="com.example.server.SimpleHttpServer"
```

### Test
```bash
curl http://localhost:8000/api/health
```


### if somethin fuckedup with the server and port is in use

```powershell
netstat -ano | findstr :8000
```
```powershell
taskkill /PID <PID> /F
```
