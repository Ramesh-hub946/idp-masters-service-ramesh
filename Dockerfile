
# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml first (cache dependencies)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build
RUN mvn -q -DskipTests package

# -------- RUNTIME STAGE --------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 2001
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]
