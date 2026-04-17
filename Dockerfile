# ── Etapa 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copia solo el wrapper y el pom primero (cache de dependencias)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Dar permisos de ejecución al wrapper (necesario cuando viene de Windows)
RUN chmod +x mvnw

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

EXPOSE 8080

# Las flags JVM se configuran desde JAVA_TOOL_OPTIONS en Railway Variables
# para poder cambiarlas sin redeploy.
# Solo forzamos el perfil de Spring aquí.
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
