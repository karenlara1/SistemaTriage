# ============================================================
# Dockerfile para SistemaTriage - Backend (Spring Boot + Gradle)
# Multi-stage build: compila con Gradle, ejecuta con JRE ligero
# ============================================================

# ─────────────────────────────────────────────────────────────
# ETAPA 1: BUILD
# Usamos la imagen oficial de Eclipse Temurin (Java 21) con Gradle
# Esta imagen tiene todo lo necesario para compilar con Gradle
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

# Instalar bash (necesario para el wrapper de Gradle)
RUN apk add --no-cache bash

# Definir el directorio de trabajo dentro del contenedor
WORKDIR /app

# OPTIMIZACIÓN DE CACHÉ DE DOCKER:
# Copiamos primero los archivos de configuración de Gradle.
# Docker guarda esta capa en caché. Si build.gradle no cambia,
# en el próximo build Docker reutiliza esta capa y NO vuelve a
# descargar dependencias de internet. Esto hace los builds mucho más rápidos.
COPY gradlew .
COPY backend/gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución al wrapper de Gradle
RUN chmod +x gradlew

# Descargar dependencias en una capa separada (aprovecha el caché de Docker)
RUN ./gradlew dependencies --no-daemon -q

# Ahora copiamos el código fuente
COPY src ./src

# También necesitamos el openapi.yaml si Spring lo sirve como recurso estático
COPY backend/src/main/resources/static/openapi.yaml ./src/main/resources/openapi.yaml

# Compilar y empaquetar. -x test omite los tests en el build
# (en CI/CD los tests se corren en un paso separado)
RUN ./gradlew clean bootJar -x test --no-daemon

# ─────────────────────────────────────────────────────────────
# ETAPA 2: RUNTIME
# Imagen mínima: solo el JRE de Java 21 sobre Alpine Linux
# Alpine es una distribución Linux ultra-ligera (~5MB de base)
# Al final la imagen de producción NO contiene el JDK ni Gradle,
# solo el JAR y el JRE. Esto la hace más pequeña y segura.
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Etiquetas de metadatos (buena práctica)
LABEL maintainer="UniQuindío - Programación Avanzada"
LABEL description="Backend del Sistema de Triage - Spring Boot + Gradle"
LABEL version="0.0.1-SNAPSHOT"

# Crear un usuario no-root para ejecutar la aplicación
# NUNCA ejecutar aplicaciones de producción como root (riesgo de seguridad)
RUN addgroup -S triage && adduser -S triage -G triage

# Directorio de trabajo en runtime
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
# Con Gradle el JAR queda en build/libs/ (no en target/ como Maven)
# El comodín *.jar evita hardcodear la versión exacta
COPY --from=build /app/build/libs/*.jar triage-backend.jar

# Asignar permisos al usuario triage sobre el JAR
RUN chown triage:triage triage-backend.jar

# Cambiar al usuario no-root
USER triage

# Exponer el puerto en el que Spring Boot escucha
# NOTA: EXPOSE es solo documentación. El binding real se hace en docker-compose.yml
EXPOSE 8080

# Variables de entorno con valores por defecto seguros
# Estas se sobreescriben desde docker-compose o al correr el contenedor
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/sistema_triage
ENV SPRING_DATASOURCE_USERNAME=triage_user
ENV SPRING_DATASOURCE_PASSWORD=changeme
ENV OPENAI_API_KEY=sk-dummy
ENV OPENAI_MODEL=gpt-4o-mini
ENV IA_PROVIDER=fallback
ENV JWT_SECRET=changeme-jwt-secret-key-minimum-32-characters-long

# Comando de inicio
# Opciones JVM para entornos containerizados:
# -XX:+UseContainerSupport     → usar los límites de CPU/RAM del contenedor (no los del host)
# -XX:MaxRAMPercentage=75.0    → usar máximo el 75% de la RAM disponible en el contenedor
# -Djava.security.egd=...      → fuente de entropía más rápida para Linux (acelera el arranque)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "triage-backend.jar"]