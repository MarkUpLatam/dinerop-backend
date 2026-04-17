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

# Flags JVM optimizadas para contenedores Railway (1 vCPU / 1 GB RAM)
# - UseContainerSupport: respeta los límites del contenedor
# - MaxRAMPercentage=70: usa hasta 70% de la RAM asignada para el heap
# - TieredStopAtLevel=1: arranca MÁS RÁPIDO (sin JIT completo al inicio)
# - SerialGC: menos overhead que G1GC para contenedores pequeños
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=70.0 \
               -XX:InitialRAMPercentage=30.0 \
               -XX:TieredStopAtLevel=1 \
               -XX:+UseSerialGC \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=prod"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
