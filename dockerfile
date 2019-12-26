# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine

# Copy files into container
COPY ./bin/ /bin

# Expose access port
EXPOSE 1337

# Run Delve Server
WORKDIR /bin
ENTRYPOINT ["java", "DelveServer"]