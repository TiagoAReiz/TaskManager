# Multi-stage build para otimizar tamanho da imagem final
# Estágio 1: Build da aplicação
FROM maven:3.9-openjdk-17-slim AS build

# Define diretório de trabalho
WORKDIR /app

# Copia apenas os arquivos de configuração do Maven primeiro para otimizar cache
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixa dependências (esta camada será cacheada se o pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Compila a aplicação e gera o JAR
RUN mvn clean package -DskipTests

# Estágio 2: Runtime da aplicação
FROM eclipse-temurin:17-jre-jammy AS runtime

# Cria usuário não-root para segurança
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Define diretório de trabalho
WORKDIR /app

# Cria diretório para logs com permissões corretas
RUN mkdir -p /app/logs && chown spring:spring /app/logs

# Copia o JAR compilado do estágio de build
COPY --from=build /app/target/taskManager-*.jar app.jar

# Altera proprietário do arquivo para o usuário spring
RUN chown spring:spring app.jar

# Muda para usuário não-root
USER spring

# Expõe a porta da aplicação
EXPOSE 8080

# Configurações da JVM para otimização em container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Health check para verificar se a aplicação está funcionando
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
