

### Build and launch
 - `mvn clean install`
 - `docker build --tag mvv.bank.account.soa ./`
 - Path from WSL `/mnt/c/Users/Volod/Projects/study/study-project-01`
 - Run locally:
   - `mvn clean package spring-boot:repackage`
   - As JAR file:
     - `java -jar mvv.bank.account.soa/target/mvv.bank.account.soa.jar`
     - `java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000 -jar mvv.bank.account.soa/target/mvv.bank.account.soa.jar`
   - Using mvn:
     - `cd account-soa`
     - `mvn spring-boot:run`
     - `mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:8000"`
   - As Docker:
     - `docker run mvv.bank.account.soa`
     - `docker run -d mvv.bank.account.soa` (in background)
     - `docker run --publish 8095:8080/tcp mvv.bank.account.soa`
     - `docker stop $(docker ps -q --filter ancestor=registry.mvv/mvv.bank.account.soa)`
     - Using io.fabric8:docker-maven-plugin:
         - `mvn17 docker:start -Ddocker.follow`
     - Using com.dkanejs.maven.plugins:docker-compose-maven-plugin:
         - `mvn17 docker-compose:up`
         - `mvn17 docker-compose:down`
     - Using exec-maven-plugin (which directly calls docker-compose):
         - `mvn17 exec:exec@docker-compose-up`
         - `mvn17 exec:exec@docker-compose-down`
     - `docker login ...`
   - As local kubernetes/k8s:
     - `kubectl src/main/kubernetes/.preprocessed/account-soa.yaml`
     - `mvn k8s-light:minikube-k8s-apply  -pl account-soa`
     - `mvn k8s-light:minikube-k8s-apply  -Dk8sConfigFiles=multi-containers/account-soa.yaml -Dk8sNamespace=test123 -pl account-soa`
     - `mvn k8s-light:minikube-k8s-delete -Dk8sConfigFiles=multi-containers/account-soa.yaml -Dk8sNamespace=test123 -pl account-soa`
 - Build and run:
   - `mvn17 clean install && mvn17 spring-boot:run`
   - `mvn17 clean install -DskipTests && mvn17 spring-boot:run`
   - `mvn17 install -DskipTests -DskipITs -Ddocker.skip.run=true && mvn17 docker:start -Ddocker.follow`

 - Show logs
  - `kubectl logs -l projectArtifactId=account-soa --prefix=true --all-containers=true --follow`
  - `kubectl logs -l projectArtifactId=account-soa --prefix=true --all-containers=true --namespace test123 --follow`

### Launch app

`java -jar target/mvv.bank.account.soa.jar`

`%JAVA_HOME_17%/bin/java -jar target/mvv.bank.account.soa.jar`

### Links

#### Users
 - account-soa-test-user/account-soa-test-user-psw

 - http://localhost:8080/account-soa/
 - http://localhost:8080/account-soa/ping
 - http://localhost:8080/account-soa/index.html
 - http://localhost:8080/account-soa/api/swagger.json
 - http://localhost:8080/account-soa/swagger-ui/index.html
 - http://localhost:8080/account-soa/webjars/springfox-swagger-ui/index.html
 - http://localhost:8080/account-soa/api/application.wadl
 - http://localhost:8095/account-soa/api/temp/users/1
 - http://localhost:8080/account-soa/api/temp/account/0000000000000002
 - https://192.168.59.102:30095/account-soa/api/account/all/client/client1
 - http://192.168.99.1:8095/account-soa/swagger-ui/
 - http://localhost:8095/account-soa/swagger-ui/
 - http://localhost:8096/account-soa/swagger-ui/
 
#### API
 - http://localhost:8080/account-soa/api/account/aacount1
 - http://localhost:8080/account-soa/api/settings

#### Database connection
 - Postgres
   - postgres/psw
