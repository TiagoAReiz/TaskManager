# TaskMaster - Frontend Angular

Uma aplicação completa de gerenciamento de tarefas desenvolvida com Angular 17+ e Angular Material.

## 🚀 Recursos

- **Autenticação**: Sistema completo de login e cadastro
- **Gerenciamento de Tarefas**: Criar, editar, excluir e marcar tarefas como concluídas
- **Filtros**: Visualizar todas as tarefas, apenas pendentes ou concluídas
- **Interface Responsiva**: Design moderno e adaptável usando Angular Material
- **Estado Reativo**: Gerenciamento de estado com Angular Signals
- **Segurança**: Interceptor JWT para autenticação automática

## 🛠️ Tecnologias Utilizadas

- Angular 17+
- Angular Material
- TypeScript
- Angular Signals
- RxJS
- SCSS

## 📂 Estrutura do Projeto

```
src/app/
├── auth/
│   ├── components/
│   │   ├── login/
│   │   └── register/
│   └── services/
├── core/
│   ├── guards/
│   ├── interceptors/
│   ├── models/
│   └── services/
├── shared/
│   └── components/
│       ├── layout/
│       └── navbar/
└── tasks/
    ├── components/
    │   ├── task-dialog/
    │   ├── task-item/
    │   └── task-list/
    └── services/
```

## 🏃‍♂️ Como Executar

### Pré-requisitos
- Node.js (versão 18 ou superior)
- npm ou yarn
- Angular CLI

### Instalação

1. **Instalar dependências**:
   ```bash
   cd frontend
   npm install
   ```

2. **Executar a aplicação**:
   ```bash
   npm start
   ```

3. **Acessar a aplicação**:
   - URL: http://localhost:4200
   - A aplicação redirecionará automaticamente para `/login` se você não estiver autenticado

### Build para Produção

```bash
npm run build
```

## 🔧 Configuração da API

A aplicação está configurada para consumir a API REST em `http://localhost:8080/api`. 

Se sua API estiver rodando em uma URL diferente, você pode alterar nos seguintes arquivos:
- `src/app/core/services/auth.service.ts`
- `src/app/tasks/services/task.service.ts`

## 📱 Funcionalidades Principais

### Autenticação
- **Login**: Email e senha
- **Cadastro**: Nome, email e senha
- **Logout**: Limpar sessão e redirecionar para login
- **Proteção de rotas**: Guard automático para rotas protegidas

### Gerenciamento de Tarefas
- **Criar tarefa**: Título e descrição
- **Editar tarefa**: Título, descrição e prioridade
- **Marcar como concluída**: Checkbox para alternar status
- **Excluir tarefa**: Com confirmação
- **Filtros**: Todas, Pendentes, Concluídas

### Interface do Usuário
- **Layout responsivo**: Funciona em desktop e mobile
- **Material Design**: Componentes padronizados e acessíveis
- **Feedback visual**: Snackbars para notificações
- **Loading states**: Indicadores de carregamento
- **Empty states**: Mensagens para listas vazias

## 🔒 Segurança

- **JWT Token**: Armazenado no localStorage
- **Auth Interceptor**: Anexa automaticamente o token às requisições
- **Auth Guard**: Protege rotas que requerem autenticação
- **Validação de formulários**: Validação client-side com feedback

## 🎨 Temas e Estilos

- **Paleta de cores**: Tema Indigo/Pink personalizado
- **Tipografia**: Roboto (padrão Material Design)
- **Componentes**: Totalmente estilizados com Angular Material
- **Responsividade**: Breakpoints para diferentes tamanhos de tela

## 📊 Estado da Aplicação

A aplicação usa Angular Signals para gerenciamento de estado reativo:

- **AuthService**: Estado de autenticação (usuário atual, status de login)
- **TaskService**: Operações CRUD das tarefas
- **Componentes**: Estado local com signals para listas e filtros

## 🔄 Fluxo da Aplicação

1. **Carregamento inicial**: Verifica se há token válido no localStorage
2. **Não autenticado**: Redireciona para `/login`
3. **Login/Cadastro**: Autentica e salva token
4. **Dashboard**: Lista tarefas com filtros e ações
5. **Operações**: CRUD completo de tarefas
6. **Logout**: Limpa sessão e retorna ao login

## 🐛 Debug e Desenvolvimento

### Logs importantes
- Erros de autenticação no console
- Respostas da API no Network tab
- Estado dos signals no Angular DevTools

### Comandos úteis
```bash
# Desenvolvimento com live reload
npm start

# Build de desenvolvimento
npm run build

# Testes (se configurados)
npm test

# Análise do bundle
npm run build -- --stats-json
```

## 📝 Notas de Desenvolvimento

- **Standalone Components**: Todos os componentes são standalone (Angular 17+)
- **Lazy Loading**: Componentes carregados sob demanda
- **Type Safety**: TypeScript rigoroso habilitado
- **Performance**: OnPush change detection onde apropriado
- **Acessibilidade**: Componentes Material Design acessíveis por padrão

## 🤝 Integração com Backend

A aplicação espera os seguintes endpoints da API:

### Autenticação
- `POST /api/auth/login` - Login do usuário
- `POST /api/auth/register` - Cadastro de usuário

### Tarefas
- `GET /api/tasks` - Listar tarefas
- `GET /api/tasks?status=PENDING` - Filtrar por status
- `POST /api/tasks` - Criar tarefa
- `PUT /api/tasks/{id}` - Atualizar tarefa
- `PATCH /api/tasks/{id}/status` - Atualizar status
- `DELETE /api/tasks/{id}` - Excluir tarefa

### Headers esperados
- `Authorization: Bearer {token}` - Para rotas protegidas
- `Content-Type: application/json` - Para requisições POST/PUT
