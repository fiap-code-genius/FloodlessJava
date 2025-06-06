# Floodless - Sistema de Gerenciamento de Enchentes

## Equipe

 - Wesley Sena | RM: 558043
 - Vanessa Yukari | RM: 558092
 - Samara Victoria | RM: 558719

## 📋 Sobre o Projeto

O Floodless é um sistema destinado a auxiliar pessoas vítimas de enchentes, oferecendo funcionalidades para monitoramento de áreas de risco, gerenciamento de abrigos e notificações em tempo real. O sistema utiliza APIs externas para obter dados meteorológicos e geográficos, permitindo um acompanhamento preciso das condições climáticas em diferentes regiões.

## 🚀 Tecnologias Utilizadas

- Java 17
- Spring Boot 3.5.0
- Oracle Database
- Spring Data JPA
- Spring WebFlux
- Swagger/OpenAPI
- Docker

## 🔧 Dependências Principais e Seus Usos

```xml
<dependencies>
    <!-- Spring Boot Starter Data JPA -->
    <!-- Utilizado para: 
         - Mapeamento objeto-relacional (ORM)
         - Gerenciamento de entidades e repositórios
         - Implementação do padrão DAO
         - Configuração automática do EntityManager -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Spring Boot Starter Validation -->
    <!-- Utilizado para:
         - Validação de DTOs com anotações como @NotNull, @Size, etc
         - Validação de dados de entrada
         - Garantia de integridade dos dados -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Spring Boot Starter Web -->
    <!-- Utilizado para:
         - Criação de endpoints REST
         - Configuração do servidor web embutido
         - Gerenciamento de requisições HTTP
         - Serialização/deserialização JSON -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Oracle Database -->
    <!-- Utilizado para:
         - Conexão com banco de dados Oracle
         - Driver JDBC para operações no banco
         - Suporte a tipos de dados Oracle específicos -->
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc11</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Swagger/OpenAPI -->
    <!-- Utilizado para:
         - Documentação automática da API
         - Interface interativa para teste de endpoints
         - Descrição de modelos e operações
         - Geração de documentação em formato OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>

    <!-- WebFlux para chamadas assíncronas -->
    <!-- Utilizado para:
         - Chamadas não-bloqueantes às APIs externas
         - Processamento reativo de dados
         - Melhor gerenciamento de recursos em operações I/O
         - Implementação do padrão Circuit Breaker -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- Actuator para monitoramento -->
    <!-- Utilizado para:
         - Monitoramento da saúde da aplicação
         - Métricas de performance
         - Endpoints de diagnóstico
         - Informações sobre o ambiente -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

## 🛠️ Configuração do Ambiente

### Variáveis de Ambiente Necessárias

```properties
# Conexão com o banco de dados Oracle
DB_LINK=jdbc:oracle:thin:@seu_host:porta:sid
DB_USER=seu_usuario
DB_PASSWORD=sua_senha

# Configuração da porta do servidor (opcional)
PORT=8080 (padrão 8080)
```

### Executando Localmente

1. Clone o repositório
2. Configure as variáveis de ambiente no seu IDE ou sistema
3. Execute o comando: `./mvnw spring-boot:run`
4. Ou via IntelliJ ou Eclipse apenas clique no Play

### Dockerfile Explicado

```dockerfile
# Stage 1: Build
# Usa a imagem do Eclipse Temurin com JDK 17 para compilar
FROM eclipse-temurin:17-jdk-focal AS build

# Instala o Maven para build do projeto
RUN apt-get update && apt-get install -y maven

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo pom.xml para cache de dependências
COPY pom.xml .

# Baixa todas as dependências (layer de cache)
# Isso permite reutilizar o cache se só o código fonte mudar
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Executa o build do projeto
RUN mvn clean package -DskipTests

# Stage 2: Runtime
# Usa uma imagem mais leve apenas com JRE
FROM eclipse-temurin:17-jre-jammy

# Configura opções da JVM para performance
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV PORT=8080

# Expõe a porta configurada
EXPOSE ${PORT}

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o JAR gerado do stage de build
COPY --from=build /app/target/Floodless-0.0.1-SNAPSHOT.jar app.jar

# Configura o comando de inicialização
# Usa shell form para permitir expansão de variáveis
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
```

### Executando com Docker

```bash
# Build da imagem
docker build -t floodless .

# Executando o container
docker run -p 8080:8080 \
  -e DB_LINK=seu_link_banco \
  -e DB_USER=seu_usuario \
  -e DB_PASSWORD=sua_senha \
  floodless
```

## 📦 Estrutura do Projeto

### Camadas da Aplicação

1. **Controllers** (`/controller`)
   - Responsáveis pelo tratamento das requisições HTTP
   - Implementam a API REST
   - Documentados com Swagger/OpenAPI
   - Validação de entrada de dados
   - Conversão de DTOs para entidades e vice-versa
   - Gerenciamento de respostas HTTP

2. **Services** (`/service`)
   - Contém a lógica de negócio
   - Gerencia transações
   - Integração com APIs externas
   - Implementação de regras de negócio
   - Validações complexas
   - Orquestração de operações

3. **Repositories** (`/repositories`)
   - Interface com o banco de dados
   - Implementa operações CRUD
   - Queries personalizadas
   - Gerenciamento de entidades
   - Cache de dados

4. **Models** (`/model`)
   - Entidades JPA
   - Enums e classes auxiliares
   - Mapeamentos objeto-relacional

5. **DTOs** (`/dto`)
   - **Request DTOs**: Objetos de transferência para entrada de dados
     - Validações com Bean Validation
     - Documentação Swagger
     - Exemplo: `UsuarioRequestDTO`
       ```java
       public record UsuarioRequestDTO(
           @NotBlank String nome,
           @Email String email,
           @NotBlank String senha,
           String telefone,
           Boolean receberNotificacoes,
           Boolean receberAlertas,
           Long regiaoId
       ) {}
       ```
   
   - **Response DTOs**: Objetos de transferência para saída de dados
     - Mapeamento de entidades para DTOs
     - Ocultação de dados sensíveis
     - Exemplo: `UsuarioResponseDTO`
       ```java
       public record UsuarioResponseDTO(
           Long id,
           String nome,
           String email,
           String telefone,
           Boolean receberNotificacoes,
           Boolean receberAlertas,
           RegiaoResponseDTO regiao
       ) {
           public UsuarioResponseDTO(Usuario usuario) {
               this(
                   usuario.getId(),
                   usuario.getNome(),
                   usuario.getEmail(),
                   usuario.getTelefone(),
                   usuario.getReceberNotificacoes(),
                   usuario.getReceberAlertas(),
                   usuario.getRegiao() != null ? 
                       new RegiaoResponseDTO(usuario.getRegiao()) : 
                       null
               );
           }
       }
       ```

   - **Benefícios do uso de DTOs**:
     - Separação clara entre camada de API e domínio
     - Controle preciso dos dados expostos
     - Validação específica para cada operação
     - Versionamento facilitado da API
     - Documentação mais clara no Swagger

## 🔍 Monitoramento

### Endpoints do Actuator

1. **Health Check**
```
GET /floodless/actuator/health
```
- Status da aplicação
- Estado do banco de dados
- Conexão com APIs externas

2. **Info**
```
GET /floodless/actuator/info
```
- Versão da aplicação
- Informações de build
- Dados do ambiente

## 🌐 Configuração do WebClient

O projeto utiliza o WebClient do Spring WebFlux para realizar chamadas HTTP não-bloqueantes às APIs externas. A configuração é centralizada na classe `WebClientConfig`:

```java
@Configuration
public class WebClientConfig {
    private HttpClient createHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Bean
    public WebClient nominatimWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "Floodless/1.0")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();
    }

    @Bean
    public WebClient openMeteoWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .baseUrl("https://api.open-meteo.com")
                .defaultHeader("User-Agent", "Floodless/1.0")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();
    }
}
```

### Características da Configuração:
- **Timeout**: 60 segundos para conexão e operações
- **Keep-Alive**: Habilitado para reutilização de conexões
- **Headers Padrão**: User-Agent, Accept e Accept-Language configurados
- **Circuit Breaker**: Implementado para falhas consecutivas
- **Rate Limiting**: Controle de taxa de requisições
- **Retry Mechanism**: Backoff exponencial com máximo de 3 tentativas

## 🌐 APIs Externas Utilizadas

### 1. Nominatim OpenStreetMap
- **Objetivo**: Geocodificação de endereços
- **Base URL**: https://nominatim.openstreetmap.org
- **Endpoints Utilizados**:
  - `/search`: Converte endereços em coordenadas
- **Parâmetros**:
  - `q`: Endereço completo
  - `format`: json
  - `limit`: 1
- **Rate Limiting**: 1 requisição a cada 2 segundos
- **Implementação**: 
  - Classe: `ClimaService`
  - Método: `getCoordenadas`
  - Cache implementado para reduzir chamadas
  - Circuit breaker para falhas
- **Exemplo de Uso**:
```java
String endereco = String.format("%s, %s, %s", regiao.getBairro(), regiao.getCidade(), regiao.getEstado());
JsonNode locationData = nominatimWebClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/search")
        .queryParam("q", endereco)
        .queryParam("format", "json")
        .queryParam("limit", 1)
        .build())
    .retrieve()
    .bodyToMono(JsonNode.class)
    .block();
```

### 2. Open-Meteo
- **Objetivo**: Dados meteorológicos
- **Base URL**: https://api.open-meteo.com
- **Endpoints Utilizados**:
  - `/v1/forecast`: Previsão do tempo e dados atuais
- **Parâmetros**:
  - `latitude`: Latitude da região
  - `longitude`: Longitude da região
  - `current`: temperature_2m,precipitation,rain,showers,weathercode
  - `hourly`: precipitation_probability,precipitation
  - `forecast_days`: 1
- **Implementação**:
  - Classe: `ClimaService`
  - Método: `buscarDadosMeteorologicosSincrono`
  - Retry mechanism implementado
  - Fallback para dados padrão
- **Exemplo de Uso**:
```java
String openMeteoUrl = String.format("/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m,precipitation,rain,showers,weathercode&hourly=precipitation_probability,precipitation&forecast_days=1", lat, lon);
JsonNode weatherData = openMeteoWebClient.get()
    .uri(openMeteoUrl)
    .retrieve()
    .bodyToMono(JsonNode.class)
    .block();
```

## 📡 Endpoints da API Detalhados

### Regiões

#### `GET /api/regioes`
- **Descrição**: Lista todas as regiões cadastradas
- **Parâmetros**: Nenhum
- **Resposta**: Lista de `RegiaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 500: Erro interno

#### `POST /api/regioes`
- **Descrição**: Cria uma nova região
- **Corpo**: `RegiaoRequestDTO`
- **Validações**:
  - Nome: Obrigatório, máx 100 caracteres
  - Estado: Obrigatório, 2 letras maiúsculas
  - Cidade: Obrigatório, máx 50 caracteres
  - CEP: Formato 00000-000
- **Resposta**: `RegiaoResponseDTO`
- **Códigos de Status**:
  - 201: Criado com sucesso
  - 400: Dados inválidos
  - 500: Erro interno

#### `GET /api/regioes/{id}`
- **Descrição**: Busca região por ID
- **Parâmetros**:
  - id: ID da região (path)
- **Resposta**: `RegiaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 404: Não encontrado

#### `PUT /api/regioes/{id}`
- **Descrição**: Atualiza uma região existente
- **Parâmetros**:
  - id: ID da região (path)
- **Corpo**: `RegiaoRequestDTO`
- **Resposta**: `RegiaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 400: Dados inválidos
  - 404: Não encontrado

#### `DELETE /api/regioes/{id}`
- **Descrição**: Remove uma região
- **Parâmetros**:
  - id: ID da região (path)
- **Códigos de Status**:
  - 204: Removido com sucesso
  - 404: Não encontrado

### Usuários

#### `GET /api/usuarios`
- **Descrição**: Lista todos os usuários
- **Parâmetros**: Nenhum
- **Resposta**: Lista de `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 500: Erro interno

#### `POST /api/usuarios`
- **Descrição**: Cadastra novo usuário
- **Corpo**: `UsuarioRequestDTO`
- **Validações**:
  - Nome: Obrigatório
  - Email: Obrigatório, formato válido
  - Senha: Mínimo 8 caracteres, maiúscula, minúscula, número e caractere especial
  - Telefone: 10 ou 11 dígitos
- **Resposta**: `UsuarioResponseDTO`
- **Códigos de Status**:
  - 201: Criado com sucesso
  - 400: Dados inválidos

#### `GET /api/usuarios/alertas`
- **Descrição**: Lista usuários que aceitam alertas
- **Parâmetros**: Nenhum
- **Resposta**: Lista de `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso

#### `GET /api/usuarios/{id}`
- **Descrição**: Busca usuário por ID
- **Parâmetros**:
  - id: ID do usuário (path)
- **Resposta**: `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 404: Não encontrado

#### `PUT /api/usuarios/{id}`
- **Descrição**: Atualiza dados do usuário
- **Parâmetros**:
  - id: ID do usuário (path)
- **Corpo**: `UsuarioRequestDTO`
- **Validações**:
  - Nome: Obrigatório
  - Email: Obrigatório, formato válido
  - Senha: Mínimo 8 caracteres, maiúscula, minúscula, número e caractere especial
  - Telefone: 10 ou 11 dígitos
- **Resposta**: `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 400: Dados inválidos
  - 404: Não encontrado

#### `DELETE /api/usuarios/{id}`
- **Descrição**: Remove um usuário
- **Parâmetros**:
  - id: ID do usuário (path)
- **Códigos de Status**:
  - 204: Removido com sucesso
  - 404: Não encontrado

#### `GET /api/usuarios/regiao/{regiaoId}`
- **Descrição**: Lista usuários de uma região específica
- **Parâmetros**:
  - regiaoId: ID da região (path)
- **Resposta**: Lista de `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 400: Parâmetros inválidos

#### `GET /api/usuarios/notificacoes/regiao/{regiaoId}`
- **Descrição**: Lista usuários que aceitam notificações em uma região
- **Parâmetros**:
  - regiaoId: ID da região (path)
- **Resposta**: Lista de `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 400: Parâmetros inválidos

#### `GET /api/usuarios/alertas/regiao/{regiaoId}`
- **Descrição**: Lista usuários que aceitam alertas em uma região
- **Parâmetros**:
  - regiaoId: ID da região (path)
- **Resposta**: Lista de `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 400: Parâmetros inválidos

#### `GET /api/usuarios/notificacoes`
- **Descrição**: Lista todos os usuários que aceitam notificações
- **Resposta**: Lista de `UsuarioResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso

#### `PATCH /api/usuarios/{id}/preferencias-notificacao`
- **Descrição**: Atualiza preferências de notificação do usuário
- **Parâmetros**:
  - id: ID do usuário (path)
  - receberNotificacoes: Boolean (query, opcional)
  - receberAlertas: Boolean (query, opcional)
- **Códigos de Status**:
  - 204: Sucesso
  - 404: Usuário não encontrado

#### `POST /api/usuarios/{id}/registrar-login`
- **Descrição**: Registra um novo acesso do usuário
- **Parâmetros**:
  - id: ID do usuário (path)
- **Códigos de Status**:
  - 204: Sucesso
  - 404: Usuário não encontrado

### Notificações

#### `GET /api/notificacoes`
- **Descrição**: Lista todas as notificações
- **Ordenação**: Por urgência e data de criação
- **Resposta**: Lista de `NotificacaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso

#### `GET /api/notificacoes/nao-lidas`
- **Descrição**: Lista notificações não lidas
- **Ordenação**: Por urgência e data
- **Resposta**: Lista de `NotificacaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso

#### `POST /api/notificacoes`
- **Descrição**: Cria nova notificação
- **Corpo**: `NotificacaoRequestDTO`
- **Validações**:
  - Título: Obrigatório
  - Mensagem: Obrigatório
  - Tipo: Enum válido
  - RegiaoId: Obrigatório
- **Resposta**: `NotificacaoResponseDTO`
- **Códigos de Status**:
  - 201: Criado com sucesso
  - 400: Dados inválidos

#### `GET /api/notificacoes/regiao/{regiaoId}`
- **Descrição**: Lista notificações de uma região específica
- **Parâmetros**:
  - regiaoId: ID da região (path)
- **Ordenação**: Por data de criação
- **Resposta**: Lista de `NotificacaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 404: Região não encontrada

#### `GET /api/notificacoes/usuario/{usuarioId}`
- **Descrição**: Lista notificações de um usuário específico
- **Parâmetros**:
  - usuarioId: ID do usuário (path)
- **Ordenação**: Por data de criação
- **Resposta**: Lista de `NotificacaoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 404: Usuário não encontrado

#### `PATCH /api/notificacoes/usuario/{usuarioId}/marcar-todas-como-lidas`
- **Descrição**: Marca todas as notificações do usuário como lidas
- **Parâmetros**:
  - usuarioId: ID do usuário (path)
- **Códigos de Status**:
  - 204: Sucesso
  - 404: Usuário não encontrado

#### `DELETE /api/notificacoes/{id}`
- **Descrição**: Remove uma notificação
- **Parâmetros**:
  - id: ID da notificação (path)
- **Códigos de Status**:
  - 204: Removida com sucesso
  - 404: Notificação não encontrada

#### `PATCH /api/notificacoes/{id}/marcar-como-lida`
- **Descrição**: Marca uma notificação específica como lida
- **Parâmetros**:
  - id: ID da notificação (path)
- **Códigos de Status**:
  - 204: Sucesso
  - 404: Notificação não encontrada

### Abrigos

#### `GET /api/abrigos`
- **Descrição**: Lista todos os abrigos
- **Resposta**: Lista de `AbrigoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso

#### `GET /api/abrigos/disponiveis`
- **Descrição**: Lista abrigos com vagas
- **Resposta**: Lista de `AbrigoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso

#### `POST /api/abrigos`
- **Descrição**: Cadastra novo abrigo
- **Corpo**: `AbrigoRequestDTO`
- **Validações**:
  - Nome: Obrigatório
  - Endereço: Obrigatório
  - Capacidade: Maior que zero
  - RegiaoId: Existente
- **Resposta**: `AbrigoResponseDTO`
- **Códigos de Status**:
  - 201: Criado com sucesso
  - 400: Dados inválidos

#### `POST /api/abrigos/{id}/entrada`
- **Descrição**: Registra entrada de pessoas
- **Parâmetros**:
  - id: ID do abrigo (path)
  - quantidade: Número de pessoas (query)
- **Validações**:
  - Quantidade positiva
  - Capacidade disponível
- **Códigos de Status**:
  - 204: Sucesso
  - 400: Quantidade inválida
  - 404: Abrigo não encontrado

#### `POST /api/abrigos/{id}/saida`
- **Descrição**: Registra saída de pessoas
- **Parâmetros**:
  - id: ID do abrigo (path)
  - quantidade: Número de pessoas (query)
- **Validações**:
  - Quantidade positiva
  - Não exceder ocupação atual
- **Códigos de Status**:
  - 204: Sucesso
  - 400: Quantidade inválida
  - 404: Abrigo não encontrado

#### `GET /api/abrigos/{id}`
- **Descrição**: Busca abrigo por ID
- **Parâmetros**:
  - id: ID do abrigo (path)
- **Resposta**: `AbrigoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 404: Não encontrado

#### `PUT /api/abrigos/{id}`
- **Descrição**: Atualiza dados do abrigo
- **Parâmetros**:
  - id: ID do abrigo (path)
- **Corpo**: `AbrigoRequestDTO`
- **Validações**:
  - Nome: Obrigatório
  - Endereço: Obrigatório
  - Capacidade: Maior que zero
  - RegiaoId: Existente
- **Resposta**: `AbrigoResponseDTO`
- **Códigos de Status**:
  - 200: Sucesso
  - 400: Dados inválidos
  - 404: Não encontrado

#### `PATCH /api/abrigos/{id}/ocupacao`
- **Descrição**: Atualiza a quantidade de pessoas no abrigo
- **Parâmetros**:
  - id: ID do abrigo (path)
  - ocupacao: Número de ocupantes (query)
- **Validações**:
  - Ocupação não pode exceder capacidade máxima
  - Ocupação não pode ser negativa
- **Códigos de Status**:
  - 204: Sucesso
  - 400: Quantidade inválida
  - 404: Abrigo não encontrado

## 📊 Swagger/OpenAPI

O Swagger UI está disponível em:
```
http://localhost:8080/floodless/swagger-ui/index.html
```

Documentação completa da API com:
- Descrição de todos os endpoints
- Modelos de request/response
- Exemplos de uso
- Códigos de status
- Schemas

## 🕒 Tarefas Agendadas

O sistema possui tarefas automáticas que são executadas periodicamente:

### Atualização de Dados Climáticos
- **Frequência**: A cada 1 hora
- **Implementação**: `RegiaoService.atualizarTodasRegioes()`
- **Funcionalidade**: 
  - Atualiza dados meteorológicos de todas as regiões
  - Intervalo mínimo entre atualizações: 2 minutos
  - Retry automático em caso de falhas
  - Circuit breaker para proteção das APIs

## 🌡️ Sistema de Monitoramento Climático

### Níveis de Risco
- **BAIXO**: Precipitação até 25mm/h
- **MODERADO**: Precipitação entre 25-45mm/h
- **ALTO**: Precipitação entre 45-65mm/h
- **CRÍTICO**: Precipitação acima de 65mm/h

### Cálculo de Risco
O sistema considera diversos fatores para determinar o nível de risco:
- Precipitação atual
- Previsão para próximas 24h
- Probabilidade de chuva
- Código meteorológico
- Histórico da região

### Mecanismos de Resiliência
1. **Cache de Coordenadas**
   - Armazenamento de coordenadas geográficas
   - Validade: 7 dias
   - Reduz chamadas à API de geocodificação

2. **Circuit Breaker**
   - Ativação: 3 falhas consecutivas
   - Tempo de reset: 15 minutos
   - Fallback: Mantém últimos dados conhecidos

3. **Rate Limiting**
   - Nominatim API: 1 requisição a cada 2 segundos
   - Implementado com Guava RateLimiter

4. **Retry Pattern**
   - Backoff exponencial
   - Máximo de 3 tentativas
   - Delay inicial: 10 segundos
   - Delay máximo: 30 segundos

## 📊 Métricas e Monitoramento

### Actuator Endpoints
```
GET /floodless/actuator/health
GET /floodless/actuator/info
```

### Logs Estruturados
O sistema utiliza SLF4J para logging com diferentes níveis:
- **INFO**: Operações normais, atualizações de dados
- **WARN**: Situações inesperadas, fallbacks
- **ERROR**: Falhas em APIs, erros de processamento
- **DEBUG**: Informações detalhadas para troubleshooting

### Métricas Disponíveis
- Status da aplicação
- Conexão com banco de dados
- Integrações com APIs externas
- Versão e build da aplicação

## 🔐 Segurança e Validações

### Validações de Dados
1. **Usuário**
   - Email: Formato válido e único
   - Senha: Mínimo 8 caracteres
   - Telefone: 10 ou 11 dígitos

2. **Abrigo**
   - Capacidade: Maior que zero
   - Ocupação: Não pode exceder capacidade
   - Remoção: Apenas se não estiver vazio

3. **Região**
   - CEP: Formato válido
   - Estado: 2 letras maiúsculas
   - Coordenadas: Validação de existência

### Transações
- Todas operações críticas são transacionais
- Rollback automático em caso de erro
- Isolamento de dados garantido

## 🔄 Ciclo de Vida de Notificações

### Tipos de Notificação
1. **Mudança de Nível de Risco**
   - Automática quando o risco da região muda
   - Inclui comparativo com nível anterior
   - Orientações baseadas na gravidade

2. **Evacuação**
   - Gerada em situações críticas
   - Prioridade máxima
   - Indica abrigos próximos

### Estados e Transições
- Criação: Sempre não lida
- Marcação como lida: Individual ou em lote
- Ordenação: Por urgência e data
- Filtros: Por região, usuário, status

## Links

- Aprensentação da solução: https://youtu.be/8OtP5Eza9uE
- Deploy no Render: https://floodless.onrender.com/floodless/swagger-ui/swagger-ui/index.html#/ (Pode demorar um pouco para abrir a interface)
