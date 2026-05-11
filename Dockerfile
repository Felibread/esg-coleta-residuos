# ===== STAGE 1: Build =====
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar pom.xml e baixar dependências (cache de camadas Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código-fonte e compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== STAGE 2: Runtime =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Criar usuário não-root (boas práticas de segurança)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiar JAR do build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
