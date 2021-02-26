FROM alpine/git
WORKDIR /dependency
RUN git clone https://github.com/superuserz/ucp-service-common.git 
WORKDIR /app
RUN git clone https://github.com/superuserz/ucp-service-notification.git

FROM maven:3.5-jdk-8-alpine as builder
WORKDIR /dependency
COPY --from=0 /dependency/ucp-service-common /dependency
RUN mvn -q clean install -Dmaven.test.skip=true
WORKDIR /app
COPY --from=0 /app/ucp-service-notification /app
RUN mvn -q clean package -Dmaven.test.skip=true

FROM openjdk:8-jdk-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]