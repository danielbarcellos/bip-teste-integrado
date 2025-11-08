# 🎯 Sistema de Gerenciamento de Benefícios

Solução completa full-stack para gerenciamento de benefícios corporativos com transferências seguras entre contas.

## ✨ Funcionalidades

- ✅ **CRUD Completo** - Criar, listar, editar e deletar benefícios
- 🔒 **Transferências Seguras** - Locking otimista e validações
- 🎨 **Interface Moderna** - Angular com Bootstrap
- 🧪 **Testes Automatizados** - Cobertura completa
- 📊 **API Documentada** - Swagger/OpenAPI
- 🐋 **Containerizada** - Docker para banco de dados

## 🛠️ Tecnologias

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- JUnit 5 + Mockito
```bash
 cd backend
./mvnw spring-boot:run
```

API: http://localhost:8080/api
http://localhost:8000/api/swagger-ui/index.html

### Frontend
- Angular 17
- TypeScript
- Bootstrap 5
- Font Awesome

```bash
cd frontend
npm install
ng serve
```
App: http://localhost:4200

### Infraestrutura
- Docker
- Docker Compose
- Maven

## 🚀 Como Executar

### 1. Banco de Dados
```bash
docker-compose up -d
```
