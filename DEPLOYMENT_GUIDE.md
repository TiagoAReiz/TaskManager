# 🚀 Guia de Deploy TaskMaster no Azure

Este guia fornece instruções detalhadas para configurar e fazer deploy da aplicação TaskMaster no Microsoft Azure usando GitHub Actions.

## 📋 Pré-requisitos

- Conta no Azure com permissões de administrador
- Repositório GitHub com o código da aplicação
- Azure CLI instalado localmente (opcional, mas recomendado)

## 🔧 1. Configuração do Azure

### 1.1 Criação do Azure Container Registry (ACR)

O Azure Container Registry irá armazenar as imagens Docker da aplicação.

```bash
# Login no Azure
az login

# Definir variáveis
RESOURCE_GROUP="taskmaster-rg"
LOCATION="East US"
ACR_NAME="taskmasteracr"  # Deve ser globalmente único

# Criar Resource Group
az group create --name $RESOURCE_GROUP --location "$LOCATION"

# Criar Azure Container Registry
az acr create \
  --resource-group $RESOURCE_GROUP \
  --name $ACR_NAME \
  --sku Basic \
  --admin-enabled true

# Obter credenciais do ACR
az acr credential show --name $ACR_NAME
```

**Guarde as seguintes informações:**
- **ACR_LOGIN_SERVER**: `{ACR_NAME}.azurecr.io`
- **ACR_USERNAME**: username retornado pelo comando acima
- **ACR_PASSWORD**: password retornado pelo comando acima

### 1.2 Criação do Azure Database for PostgreSQL

```bash
# Definir variáveis para o banco
DB_SERVER_NAME="taskmaster-db-server"  # Deve ser globalmente único
DB_NAME="taskmaster"
DB_ADMIN_USER="taskmaster_admin"
DB_ADMIN_PASSWORD="StrongPassword123!"  # Use uma senha forte

# Criar servidor PostgreSQL
az postgres flexible-server create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --location "$LOCATION" \
  --admin-user $DB_ADMIN_USER \
  --admin-password $DB_ADMIN_PASSWORD \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --storage-size 32 \
  --version 15

# Criar banco de dados
az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP \
  --server-name $DB_SERVER_NAME \
  --database-name $DB_NAME

# Configurar firewall para permitir serviços do Azure
az postgres flexible-server firewall-rule create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --rule-name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0
```

**Guarde as seguintes informações:**
- **DATABASE_URL**: `jdbc:postgresql://{DB_SERVER_NAME}.postgres.database.azure.com:5432/{DB_NAME}?sslmode=require`
- **DATABASE_USERNAME**: `{DB_ADMIN_USER}`
- **DATABASE_PASSWORD**: `{DB_ADMIN_PASSWORD}`

### 1.3 Criação do Azure App Service

```bash
# Definir variáveis
APP_SERVICE_PLAN="taskmaster-plan"
WEBAPP_NAME="taskmaster-app"  # Deve ser globalmente único

# Criar App Service Plan (Linux)
az appservice plan create \
  --name $APP_SERVICE_PLAN \
  --resource-group $RESOURCE_GROUP \
  --is-linux \
  --sku B1

# Criar Web App configurado para containers
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan $APP_SERVICE_PLAN \
  --name $WEBAPP_NAME \
  --deployment-container-image-name nginx:latest
```

**Guarde a seguinte informação:**
- **WEBAPP_NAME**: `{WEBAPP_NAME}`

### 1.4 Criação do Service Principal para GitHub Actions

```bash
# Criar Service Principal
SUBSCRIPTION_ID=$(az account show --query id --output tsv)

az ad sp create-for-rbac \
  --name "taskmaster-github-actions" \
  --role contributor \
  --scopes /subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP \
  --sdk-auth
```

**Guarde toda a saída JSON** - ela será usada como `AZURE_CREDENTIALS`.

## 🔐 2. Configuração de Segredos no GitHub

Acesse seu repositório no GitHub e vá para **Settings > Secrets and variables > Actions**.

Crie os seguintes **Repository Secrets**:

### 2.1 Credenciais do Azure

- **Nome**: `AZURE_CREDENTIALS`
- **Valor**: O JSON completo retornado pelo comando `az ad sp create-for-rbac`

### 2.2 Credenciais do Azure Container Registry

- **Nome**: `ACR_LOGIN_SERVER`
- **Valor**: `{ACR_NAME}.azurecr.io`

- **Nome**: `ACR_USERNAME`
- **Valor**: Username do ACR obtido anteriormente

- **Nome**: `ACR_PASSWORD`
- **Valor**: Password do ACR obtido anteriormente

### 2.3 Configurações do Banco de Dados

- **Nome**: `DATABASE_URL`
- **Valor**: `jdbc:postgresql://{DB_SERVER_NAME}.postgres.database.azure.com:5432/{DB_NAME}?sslmode=require`

- **Nome**: `DATABASE_USERNAME`
- **Valor**: `{DB_ADMIN_USER}`

- **Nome**: `DATABASE_PASSWORD`
- **Valor**: `{DB_ADMIN_PASSWORD}`

### 2.4 Configurações da Aplicação

- **Nome**: `JWT_SECRET`
- **Valor**: Uma string segura de pelo menos 256 bits (ex: `myVerySecureJwtSecretKey123456789012345678901234567890`)

### 2.5 Informações do Azure

- **Nome**: `RESOURCE_GROUP`
- **Valor**: `taskmaster-rg`

- **Nome**: `WEBAPP_NAME`
- **Valor**: `{WEBAPP_NAME}`

## 🚦 3. Configuração do Pipeline

### 3.1 Estrutura de Arquivos Necessária

Certifique-se de que seu repositório tenha a seguinte estrutura:

```
projeto/
├── .github/
│   └── workflows/
│       └── deploy-to-azure.yml
├── taskManager/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
└── docker-compose.yml
```

### 3.2 Configuração do Profile Azure no Spring Boot

Crie um arquivo `application-azure.yml` em `taskManager/src/main/resources/`:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: ${JWT_EXPIRATION:86400000}

server:
  port: ${PORT:8080}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.tiagoreiz: INFO
    org.springframework.security: WARN
```

## 🔄 4. Processo de Deploy

### 4.1 Deploy Automático

O deploy acontece automaticamente quando você faz push para a branch `main`:

```bash
git add .
git commit -m "feat: add containerization and CI/CD"
git push origin main
```

### 4.2 Monitoramento do Deploy

1. Acesse **Actions** no seu repositório GitHub
2. Clique no workflow em execução
3. Monitore os logs de cada job

### 4.3 Verificação do Deploy

Após o deploy bem-sucedido:

1. **Health Check**: https://{WEBAPP_NAME}.azurewebsites.net/actuator/health
2. **API Documentation**: https://{WEBAPP_NAME}.azurewebsites.net/swagger-ui.html
3. **Application**: https://{WEBAPP_NAME}.azurewebsites.net

## 🛠️ 5. Comandos Úteis para Desenvolvimento Local

### 5.1 Build e teste do projeto

```bash
# Build do backend
cd taskManager
mvn clean package -DskipTests

# Build do frontend
cd frontend
npm install
ng build

# Executar com Docker Compose
docker-compose up --build

# Executar apenas os serviços principais
docker-compose up postgres-db backend frontend

# Executar com Adminer para debug
docker-compose --profile dev up
```

### 5.2 Comandos de gerenciamento

```bash
# Ver logs dos containers
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres-db

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (dados serão perdidos)
docker-compose down -v

# Rebuild forçado
docker-compose build --no-cache
docker-compose up --force-recreate
```

## 🔍 6. Troubleshooting

### 6.1 Problemas Comuns

**Backend não conecta ao banco:**
```bash
# Verificar se o banco está rodando
docker-compose ps postgres-db

# Verificar logs do banco
docker-compose logs postgres-db

# Testar conexão manual
docker-compose exec postgres-db psql -U taskmaster_user -d taskmaster -c "SELECT 1;"
```

**Frontend não consegue acessar o backend:**
```bash
# Verificar configuração do Nginx
docker-compose exec frontend cat /etc/nginx/nginx.conf

# Testar conectividade
docker-compose exec frontend curl http://backend:8080/actuator/health
```

**Pipeline falhando no Azure:**
1. Verificar se todos os secrets estão configurados
2. Verificar se os recursos do Azure existem
3. Verificar logs detalhados no GitHub Actions
4. Verificar se o Service Principal tem as permissões necessárias

### 6.2 Logs e Monitoramento

```bash
# Logs do Azure App Service
az webapp log tail --resource-group taskmaster-rg --name {WEBAPP_NAME}

# Logs do Container Registry
az acr repository show-tags --name {ACR_NAME} --repository taskmaster-backend

# Status dos recursos
az resource list --resource-group taskmaster-rg --output table
```

## 🔒 7. Considerações de Segurança

### 7.1 Produção

- Use senhas fortes e únicas
- Configure SSL/TLS nos endpoints
- Use Azure Key Vault para secrets sensíveis
- Configure Network Security Groups
- Ative monitoramento e alertas
- Configure backup automático do banco

### 7.2 Desenvolvimento

- Não commite secrets no código
- Use `.env` local para desenvolvimento
- Configure CORS adequadamente
- Use perfis Spring diferentes para cada ambiente

## 📞 8. Suporte

Em caso de problemas:

1. Verificar logs do GitHub Actions
2. Verificar logs do Azure App Service
3. Testar localmente com Docker Compose
4. Verificar configuração dos secrets
5. Consultar documentação oficial do Azure

---

**🎉 Parabéns! Sua aplicação TaskMaster agora está configurada para deploy contínuo no Azure!**
