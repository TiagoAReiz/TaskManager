#!/bin/bash

# Script para inicializar o ambiente de desenvolvimento TaskMaster
# Uso: ./scripts/start-dev.sh [options]

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para logging
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

# Verificar se Docker e Docker Compose estão instalados
check_requirements() {
    log "Verificando requisitos..."
    
    if ! command -v docker &> /dev/null; then
        error "Docker não está instalado. Por favor, instale o Docker primeiro."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
        exit 1
    fi
    
    log "Requisitos verificados com sucesso!"
}

# Função para build do frontend
build_frontend() {
    log "Fazendo build do frontend Angular..."
    
    cd frontend
    
    if [ ! -d "node_modules" ]; then
        info "Instalando dependências do npm..."
        npm install
    fi
    
    info "Executando build do Angular..."
    npm run build
    
    cd ..
    log "Build do frontend concluído!"
}

# Função para limpeza
cleanup() {
    log "Limpando containers e volumes antigos..."
    docker-compose down -v --remove-orphans
    docker system prune -f
    log "Limpeza concluída!"
}

# Função para inicializar a aplicação
start_application() {
    local mode="$1"
    
    log "Iniciando aplicação TaskMaster..."
    
    case $mode in
        "dev")
            info "Modo desenvolvimento (com Adminer)"
            docker-compose --profile dev up --build -d
            ;;
        "prod")
            info "Modo produção"
            docker-compose up --build -d
            ;;
        "logs")
            info "Modo desenvolvimento com logs"
            docker-compose --profile dev up --build
            ;;
        *)
            info "Modo padrão"
            docker-compose up --build -d
            ;;
    esac
}

# Função para verificar saúde dos serviços
check_health() {
    log "Verificando saúde dos serviços..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        info "Tentativa $attempt/$max_attempts..."
        
        # Verificar PostgreSQL
        if docker-compose exec -T postgres-db pg_isready -U taskmaster_user -d taskmaster &> /dev/null; then
            log "✅ PostgreSQL está saudável"
            postgres_healthy=true
        else
            warn "❌ PostgreSQL não está respondendo"
            postgres_healthy=false
        fi
        
        # Verificar Backend
        if curl -f http://localhost:8080/api/actuator/health &> /dev/null; then
            log "✅ Backend está saudável"
            backend_healthy=true
        else
            warn "❌ Backend não está respondendo"
            backend_healthy=false
        fi
        
        # Verificar Frontend
        if curl -f http://localhost/health &> /dev/null; then
            log "✅ Frontend está saudável"
            frontend_healthy=true
        else
            warn "❌ Frontend não está respondendo"
            frontend_healthy=false
        fi
        
        if [ "$postgres_healthy" = true ] && [ "$backend_healthy" = true ] && [ "$frontend_healthy" = true ]; then
            log "🎉 Todos os serviços estão saudáveis!"
            break
        fi
        
        sleep 10
        ((attempt++))
    done
    
    if [ $attempt -gt $max_attempts ]; then
        error "❌ Alguns serviços não estão respondendo após $max_attempts tentativas"
        show_logs
        exit 1
    fi
}

# Função para mostrar logs
show_logs() {
    log "Mostrando logs dos serviços..."
    docker-compose logs --tail=50
}

# Função para mostrar URLs úteis
show_urls() {
    log "URLs disponíveis:"
    echo ""
    echo "🌐 Frontend (Angular):     http://localhost"
    echo "🔧 Backend API:           http://localhost:8080/api"
    echo "📊 API Documentation:     http://localhost:8080/api/swagger-ui.html"
    echo "❤️  Health Check:          http://localhost:8080/api/actuator/health"
    echo "📋 Database (Adminer):    http://localhost:8081"
    echo ""
    echo "📝 Credenciais do banco (Adminer):"
    echo "   Sistema: PostgreSQL"
    echo "   Servidor: postgres-db"
    echo "   Usuário: taskmaster_user"
    echo "   Senha: taskmaster_pass"
    echo "   Base de dados: taskmaster"
}

# Função para mostrar ajuda
show_help() {
    echo "TaskMaster Development Environment"
    echo ""
    echo "Uso: $0 [OPÇÃO]"
    echo ""
    echo "Opções:"
    echo "  start         Inicia a aplicação em modo padrão"
    echo "  start-dev     Inicia com Adminer para debug"
    echo "  start-logs    Inicia mostrando logs em tempo real"
    echo "  stop          Para todos os serviços"
    echo "  restart       Reinicia todos os serviços"
    echo "  clean         Limpa containers e volumes"
    echo "  logs          Mostra logs dos serviços"
    echo "  health        Verifica saúde dos serviços"
    echo "  urls          Mostra URLs disponíveis"
    echo "  build-fe      Faz apenas build do frontend"
    echo "  help          Mostra esta ajuda"
    echo ""
}

# Função principal
main() {
    case "${1:-start}" in
        "start")
            check_requirements
            build_frontend
            start_application "prod"
            check_health
            show_urls
            ;;
        "start-dev")
            check_requirements
            build_frontend
            start_application "dev"
            check_health
            show_urls
            ;;
        "start-logs")
            check_requirements
            build_frontend
            start_application "logs"
            ;;
        "stop")
            log "Parando todos os serviços..."
            docker-compose down
            log "Serviços parados!"
            ;;
        "restart")
            log "Reiniciando serviços..."
            docker-compose down
            build_frontend
            start_application "prod"
            check_health
            show_urls
            ;;
        "clean")
            cleanup
            ;;
        "logs")
            show_logs
            ;;
        "health")
            check_health
            ;;
        "urls")
            show_urls
            ;;
        "build-fe")
            build_frontend
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            error "Opção inválida: $1"
            show_help
            exit 1
            ;;
    esac
}

# Executar função principal
main "$@"
