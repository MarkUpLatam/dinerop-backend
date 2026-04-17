# ── Etapa 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copia solo el wrapper y el pom primero (cache de dependencias)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Descarga dependencias sin compilar el código fuente (se cachea si pom no cambia)
RUN ./mvnw dependency:go-offline -B

# Ahora copia el código fuente y compila
COPY src ./src
RUN ./mvnw package -B -DskipTests

# ── Etapa 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copia solo el JAR final
COPY --from=builder /app/target/*.jar app.jar

# Configuración JVM optimizada para contenedores con poca RAM (Railway free tier)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:MaxGCPauseMillis=200 \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=prod"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
