# 🐳 TaskMaster - Containerização com Docker

Este documento fornece instruções para executar o TaskMaster usando Docker e Docker Compose.

## 📋 Pré-requisitos

- Docker 20.10 ou superior
- Docker Compose 2.0 ou superior
- Node.js 18+ (para build do frontend)

## 🚀 Início Rápido

### Opção 1: Script Automatizado (Recomendado)

```bash
# Dar permissão ao script
chmod +x scripts/start-dev.sh

# Iniciar aplicação completa
./scripts/start-dev.sh start

# Iniciar com Adminer para debug do banco
./scripts/start-dev.sh start-dev

# Ver todas as opções
./scripts/start-dev.sh help
```

### Opção 2: Comandos Manuais

```bash
# 1. Build do frontend Angular
cd frontend
npm install
npm run build
cd ..

# 2. Iniciar todos os serviços
docker-compose up --build

# Ou em background
docker-compose up --build -d
```

## 🏗️ Arquitetura dos Containers

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   PostgreSQL    │
│   (Nginx)       │◄──►│  (Spring Boot)  │◄──►│     Database    │
│   Port: 80      │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │    Adminer      │
                    │  (DB Manager)   │
                    │   Port: 8081    │
                    └─────────────────┘
```

## 📂 Estrutura de Arquivos

```
projeto/
├── docker-compose.yml           # Orquestração dos containers
├── env.example                  # Exemplo de variáveis de ambiente
├── scripts/
│   └── start-dev.sh            # Script de desenvolvimento
├── taskManager/
│   ├── Dockerfile              # Imagem do backend
│   └── src/main/resources/
│       ├── application-docker.yml
│       └── application-azure.yml
└── frontend/
    ├── nginx.conf              # Configuração do Nginx
    └── dist/                   # Build do Angular
```

## 🔧 Configuração

### Variáveis de Ambiente

Copie `env.example` para `.env` e ajuste conforme necessário:

```bash
cp env.example .env
```

### Configurações do Docker Compose

O `docker-compose.yml` define três serviços principais:

1. **postgres-db**: Banco de dados PostgreSQL 15
2. **backend**: API Spring Boot (Java 17)
3. **frontend**: SPA Angular com Nginx
4. **adminer**: Interface web para o banco (perfil dev)

## 🌐 URLs Disponíveis

Após inicializar a aplicação:

| Serviço | URL | Descrição |
|---------|-----|-----------|
| Frontend | http://localhost | Interface principal da aplicação |
| Backend API | http://localhost:8080/api | Endpoints da API REST |
| Documentação API | http://localhost:8080/api/swagger-ui.html | Swagger UI |
| Health Check | http://localhost:8080/api/actuator/health | Status da aplicação |
| Adminer | http://localhost:8081 | Gerenciador do banco (modo dev) |

### Credenciais do Banco (Adminer)

- **Sistema**: PostgreSQL
- **Servidor**: postgres-db
- **Usuário**: taskmaster_user
- **Senha**: taskmaster_pass
- **Base de dados**: taskmaster

## 🛠️ Comandos Úteis

### Gerenciamento de Containers

```bash
# Ver status dos containers
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f

# Ver logs de um serviço específico
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

### Desenvolvimento

```bash
# Executar comando no container do backend
docker-compose exec backend bash

# Executar comando no container do banco
docker-compose exec postgres-db psql -U taskmaster_user -d taskmaster

# Acessar logs da aplicação
docker-compose exec backend tail -f /app/logs/taskmaster.log

# Reiniciar apenas um serviço
docker-compose restart backend
```

### Limpeza

```bash
# Remover containers parados
docker container prune

# Remover imagens não utilizadas
docker image prune

# Remover volumes não utilizados
docker volume prune

# Limpeza completa (CUIDADO!)
docker system prune -a
```

## 🔍 Troubleshooting

### Problemas Comuns

#### 1. Backend não conecta ao banco

```bash
# Verificar se o banco está rodando
docker-compose ps postgres-db

# Verificar logs do banco
docker-compose logs postgres-db

# Testar conexão manualmente
docker-compose exec postgres-db pg_isready -U taskmaster_user -d taskmaster
```

#### 2. Frontend retorna 502 Bad Gateway

```bash
# Verificar se o backend está rodando
docker-compose ps backend

# Verificar health do backend
curl http://localhost:8080/api/actuator/health

# Verificar configuração do Nginx
docker-compose exec frontend cat /etc/nginx/nginx.conf
```

#### 3. Porta já está em uso

```bash
# Verificar processos usando as portas
sudo netstat -tulpn | grep :80
sudo netstat -tulpn | grep :8080
sudo netstat -tulpn | grep :5432

# Parar o processo ou usar portas diferentes no docker-compose.yml
```

#### 4. Permissões de arquivo

```bash
# Ajustar permissões se necessário
sudo chown -R $USER:$USER .
chmod +x scripts/start-dev.sh
```

### Logs Detalhados

Para debug mais detalhado, você pode ajustar os níveis de log:

```yaml
# No docker-compose.yml, seção backend > environment
LOGGING_LEVEL_COM_TIAGOREIZ: DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY: DEBUG
SPRING_JPA_SHOW_SQL: "true"
```

### Verificação de Saúde

```bash
# Script automático de verificação
./scripts/start-dev.sh health

# Verificação manual
curl -f http://localhost/health                           # Frontend
curl -f http://localhost:8080/api/actuator/health        # Backend
docker-compose exec postgres-db pg_isready -U taskmaster_user -d taskmaster  # Database
```

## 🔄 Profiles do Docker Compose

### Profile Padrão
```bash
docker-compose up
# Executa: postgres-db, backend, frontend
```

### Profile de Desenvolvimento
```bash
docker-compose --profile dev up
# Executa: postgres-db, backend, frontend, adminer
```

## 📊 Monitoramento

### Métricas da Aplicação

- **Health Check**: `/api/actuator/health`
- **Métricas**: `/api/actuator/metrics`
- **Info**: `/api/actuator/info`

### Logs

Logs são salvos em volumes Docker e podem ser acessados:

```bash
# Logs da aplicação Spring Boot
docker-compose exec backend tail -f /app/logs/taskmaster.log

# Logs do Nginx
docker-compose exec frontend tail -f /var/log/nginx/access.log
docker-compose exec frontend tail -f /var/log/nginx/error.log
```

## 🚀 Deploy

Para deploy em produção, consulte o [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) que contém instruções completas para Azure.

---

**💡 Dica**: Use sempre o script `./scripts/start-dev.sh` para uma experiência mais fluida de desenvolvimento!
