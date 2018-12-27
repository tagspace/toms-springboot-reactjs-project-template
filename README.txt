

To setup environment:
 - Download and Install JDK 8
 	- add this to .bash_profile: export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
 - Check that everything builds: ./gradlew stage
 - Install Postgresapp and 'create database mydatabase'

To run locally:
 - VM Options: -Dspring.profiles.active=development
 - ./scripts/webpack-dev-server.sh

