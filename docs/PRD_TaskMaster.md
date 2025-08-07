PRD & Guia de Implementação: TaskMaster
Versão: 1.0
Data: 05 de Agosto de 2025
Autor: Gemini (Baseado na solicitação de TiagoReiz)

Parte 1: Product Requirements Document (PRD)
1.1. Visão Geral e Objetivo
O TaskMaster é uma aplicação web para gerenciamento de tarefas pessoais. O objetivo é permitir que usuários cadastrados criem, gerenciem e organizem suas tarefas de forma eficiente. O projeto servirá como uma peça de portfólio para demonstrar competências full-stack, implementando um backend robusto com Java e Spring Boot (seguindo a Clean Architecture), um frontend reativo com Angular, e práticas de deploy com Docker e Azure.

1.2. Persona de Usuário
Persona Principal: Alex, 22 anos, estudante de Sistemas de Informação e estagiário de TI.

Necessidades: Precisa de uma ferramenta centralizada para organizar suas tarefas da faculdade e do estágio. Deseja categorizar tarefas, definir prioridades e visualizar seu progresso de forma clara.

Motivação: Busca uma solução simples e focada, sem a complexidade e o custo de ferramentas de gerenciamento de projetos de grande porte.

1.3. Épicos e Histórias de Usuário
Épico 1: Gerenciamento de Usuários e Autenticação

EU1.1: Como um visitante, quero poder me cadastrar na plataforma fornecendo nome, e-mail e senha, para que eu possa ter uma conta pessoal.

EU1.2: Como um usuário cadastrado, quero poder fazer login com meu e-mail e senha para acessar minhas tarefas de forma segura.

EU1.3: Como um usuário logado, quero poder fazer logout para encerrar minha sessão com segurança.

Épico 2: Gerenciamento de Tarefas (CRUD)

EU2.1: Como um usuário logado, quero poder criar uma nova tarefa com um título e uma descrição.

EU2.2: Como um usuário logado, quero poder visualizar uma lista de todas as minhas tarefas.

EU2.3: Como um usuário logado, quero poder editar o título e a descrição de uma tarefa existente.

EU2.4: Como um usuário logado, quero poder excluir uma tarefa que não é mais necessária.

Épico 3: Funcionalidades Avançadas de Tarefas

EU3.1: Como um usuário logado, quero poder marcar uma tarefa como "Concluída" ou "Pendente".

EU3.2: Como um usuário logado, quero poder definir uma prioridade (Baixa, Média, Alta) para cada tarefa.

EU3.3: Como um usuário logado, quero poder filtrar minhas tarefas por status (todas, pendentes, concluídas).

Parte 2: Guia de Implementação Passo a Passo
Este guia descreve a ordem de criação dos arquivos para garantir um fluxo de trabalho lógico e consistente com os princípios da Clean Architecture.

Passo 0: Configuração Inicial do Projeto
Criar o Projeto: Use o Spring Initializr com as seguintes configurações:

Project: Maven

Language: Java

Spring Boot: 3.2.x ou superior

Project Metadata: Defina seu Group (ex: com.tiagoreiz) e Artifact (ex: projeto).

Packaging: Jar

Java: 17 ou 21

Dependencies:

Spring Web

Spring Data JPA

Spring Security

PostgreSQL Driver

Lombok (para reduzir boilerplate)

Validation

Adicionar Dependências Manuais: Abra o arquivo pom.xml e adicione as dependências para JWT, Springdoc (Swagger) e MapStruct.

<!-- Para JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Para Documentação (Swagger) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>

<!-- Para Mappers -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<!-- Não se esqueça do plugin do mapstruct na seção <build> do pom.xml -->

Criar Estrutura de Pacotes: Dentro de src/main/java/com/tiagoreiz/projeto/, crie os pacotes principais: Adapters, Application, Core, Infra.

Configurar application.properties: Adicione as configurações iniciais para o banco de dados e JWT.

# Configuração do Banco de Dados PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmaster_db
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuração JWT
jwt.secret=SuaChaveSecretaSuperSeguraParaGerarTokensJWTValidos
jwt.expiration.ms=86400000 # 24 horas

Passo 1: A Camada Core (O Coração)
Objetivo: Definir as regras de negócio puras, sem depender de nenhum framework.

Core/Entities/TaskStatus.java (Enum: PENDING, COMPLETED)

Core/Entities/TaskPriority.java (Enum: LOW, MEDIUM, HIGH)

Core/Entities/User.java (Classe POJO com id, name, email, password)

Core/Entities/Task.java (Classe POJO com id, title, description, status, priority, createdAt, e referência ao User)

Core/Repositories/UserRepository.java (Interface com métodos como save, findByEmail, findById)

Core/Repositories/TaskRepository.java (Interface com métodos como save, findById, findAllByUserId)

Passo 2: A Camada Infra (Implementações Concretas)
Objetivo: Implementar as interfaces do Core com tecnologias e frameworks específicos.

Persistência (Infra/Persistence):

Infra/Persistence/Repositories/UserJpaRepositoryImpl.java (Classe que implementa UserRepository usando Spring Data JPA).

Infra/Persistence/Repositories/TaskJpaRepositoryImpl.java (Classe que implementa TaskRepository).

Infra/Persistence/Entities/UserPersistence.java (Entidade JPA para o usuário).

Infra/Persistence/Entities/TaskPersistence.java (Entidade JPA para a tarefa).

Nota: Para uma separação mais pura, use entidades de persistência separadas e mappers para convertê-las para as entidades do Core.

Segurança (Infra/Security):

Infra/Security/UserDetailsServiceImpl.java (Classe que implementa UserDetailsService do Spring Security).

Infra/Security/JwtService.java (Classe para gerar, validar e extrair informações de tokens JWT).

Infra/Security/JwtAuthenticationFilter.java (Filtro que intercepta requisições, valida o token JWT e define o contexto de autenticação).

Infra/Security/SecurityConfig.java (Classe principal de configuração do Spring Security).

Passo 3: A Camada Application (Orquestração)
Objetivo: Implementar a lógica específica da aplicação que conecta o pedido à ação.

Casos de Uso (Application/UseCases):

Comece com o usuário: RegisterUserUseCase.java, LoginUserUseCase.java.

Em seguida, as tarefas: CreateTaskUseCase.java, GetUserTasksUseCase.java, UpdateTaskUseCase.java, DeleteTaskUseCase.java.

Nota: Estas são classes concretas que recebem as interfaces dos repositórios via injeção de dependência e executam a lógica.

Passo 4: A Camada Adapters (A Conexão com o Mundo)
Objetivo: Adaptar as requisições HTTP para chamadas de Casos de Uso e converter os resultados para respostas HTTP.

DTOs (Adapters/DTOs):

UserRegistrationRequest.java, LoginRequest.java, LoginResponse.java

TaskRequest.java, TaskResponse.java

Mappers (Adapters/Mappers):

UserMapper.java (Interface MapStruct para converter entre UserRegistrationRequest e User)

TaskMapper.java (Interface MapStruct para converter entre TaskRequest/Task e TaskResponse)

Controllers (Adapters/Controllers):

AuthController.java (Com endpoints /register e /login. Injeta os casos de uso de usuário).

TaskController.java (Com endpoints para CRUD de tarefas. Injeta os casos de uso de tarefa).

Parte 3: Próximos Passos
Com o backend estruturado, os próximos passos seriam:

Testes: Escrever testes unitários para os Casos de Uso e testes de integração para os Controllers.

Frontend: Iniciar o desenvolvimento da aplicação em Angular, criando os componentes e serviços para consumir a API.

Containerização: Criar um Dockerfile para a aplicação Spring Boot e um docker-compose.yml para rodar a aplicação e o banco de dados PostgreSQL localmente.

Deploy: Configurar um pipeline de CI/CD (ex: com GitHub Actions) para fazer o deploy automático da aplicação no Azure App Service ou Azure Kubernetes Service (AKS).
