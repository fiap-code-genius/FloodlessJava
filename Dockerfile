# Usando uma imagem base do Eclipse Temurin para construção (mais otimizada para Java)
FROM eclipse-temurin:17-jdk-focal AS build

# Instalando o Maven
RUN apt-get update && apt-get install -y maven

# Definindo o diretório de trabalho para a construção
WORKDIR /app

# Copiando o arquivo pom.xml
COPY pom.xml .

# Baixando todas as dependências (cache layer)
RUN mvn dependency:go-offline

# Copiando o código-fonte do projeto
COPY src ./src

# Executando a construção do Maven
RUN mvn clean package -DskipTests

# Usando uma imagem base mais leve para a execução
FROM eclipse-temurin:17-jre-jammy

# Definindo variáveis de ambiente para a JVM
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV PORT=8080

# Expondo a porta definida pela variável de ambiente
EXPOSE ${PORT}

# Definindo o diretório de trabalho
WORKDIR /app

# Copiando o arquivo JAR gerado para a imagem final
COPY --from=build /app/target/Floodless-0.0.1-SNAPSHOT.jar app.jar

# Configurando o ponto de entrada com as opções da JVM e passando a porta como argumento
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"] 