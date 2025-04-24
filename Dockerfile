# Imagen base con JDK 17
FROM eclipse-temurin:17-jdk-alpine as build

# Instalar dependencias básicas
RUN apk add --no-cache bash curl unzip

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Dar permisos al wrapper de Gradle
RUN chmod +x ./gradlew

# Build sin tests (puedes sacarlo si querés que los ejecute)
RUN ./gradlew clean shadowJar -x test --stacktrace --info

# ----------------------
# Etapa final: contenedor liviano
# ----------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el .jar desde la etapa de build
COPY --from=build /app/build/libs/erp-api-ktor.jar .

# Puerto que expone Ktor por defecto
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "erp-api-ktor.jar"]
