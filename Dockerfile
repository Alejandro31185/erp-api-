# Etapa de compilación con JDK 17
FROM eclipse-temurin:17-jdk-alpine as build

# Instalar herramientas necesarias
RUN apk add --no-cache bash curl unzip

# Directorio de trabajo
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Asegurar permisos para el wrapper de Gradle
RUN chmod +x ./gradlew

# Compilar el proyecto y generar el JAR "fat" (sin correr tests)
RUN ./gradlew clean shadowJar -x test --stacktrace --info

# -------------------------------
# Etapa de ejecución (imagen liviana)
# -------------------------------
FROM eclipse-temurin:17-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR generado desde la etapa anterior
COPY --from=build /app/build/libs/erp-api-ktor-all.jar .

# Exponer puerto por defecto usado por Ktor
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "erp-api-ktor-all.jar"]
