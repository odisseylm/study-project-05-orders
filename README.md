


 - Fast build
   - `mvn install -DskipTests -Dmaven.test.skip=true`
   - `rm -f ./target/test-classes/org/mvv/mapstruct/scala/debug/*.class &&  mvn install`
   - `mvn dependency:tree`



mvn org.apache.maven.plugins:maven-dependency-plugin:2.10:resolve-plugins
mvn dependency:resolve-plugins
mvn dependency:sources -Dsilent=true
