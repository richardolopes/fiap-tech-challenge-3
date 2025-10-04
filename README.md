# Sistema de Gerenciamento Hospitalar

Sistema hospitalar desenvolvido com Clean Architecture, Spring Boot, GraphQL e Kafka para comunicação assíncrona.

## Arquitetura

O projeto está organizado seguindo os princípios da Clean Architecture:

### Módulos

- **shared-domain**: Domínio compartilhado entre os serviços
- **scheduling-service**: Serviço de agendamento de consultas (PRODUCER de eventos)
- **notification-service**: Serviço de notificações (CONSUMER de eventos)

### Camadas

#### Domain (Domínio)
- **Entities**: Entidades de negócio (User, Consultation)
- **Enums**: Tipos de usuário e status de consulta
- **Events**: Eventos de domínio para comunicação assíncrona
- **Repositories**: Interfaces de repositório

#### Application (Aplicação)
- **DTOs**: Objetos de transferência de dados
- **Use Cases**: Casos de uso da aplicação

#### Infrastructure (Infraestrutura)
- **Config**: Configurações (Security, JWT, Kafka)
- **Persistence**: Implementações de repositório
- **Security**: Autenticação e autorização

#### Presentation (Apresentação)
- **Controllers**: Controllers REST
- **GraphQL**: Resolvers GraphQL

## Tecnologias

- **Java 21**: Linguagem de programação
- **Spring Boot 3.2**: Framework principal
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **GraphQL**: API flexível para consultas
- **Apache Kafka**: Comunicação assíncrona
- **JWT**: Tokens de autenticação
- **H2 Database**: Banco de dados em memória (desenvolvimento)
- **Maven**: Gerenciamento de dependências

## Funcionalidades

### Tipos de Usuário
- **Médicos**: Podem visualizar e editar histórico de consultas
- **Enfermeiros**: Podem registrar consultas e acessar histórico  
- **Pacientes**: Podem visualizar apenas suas próprias consultas

### Segurança
- Autenticação via JWT
- Controle de acesso baseado em roles
- Autorização por tipo de usuário

### Consultas
- Agendamento de consultas
- Edição e cancelamento
- Histórico de consultas
- Consultas via GraphQL

### Comunicação Assíncrona
- Eventos de domínio
- Publicação via Kafka
- Notificações automáticas

## Como Executar

### Pré-requisitos
- Java 21
- Maven 3.9+
- Apache Kafka (opcional, para funcionalidades completas)

### Executando o Projeto

1. **Compilar o projeto:**
   ```bash
   mvn clean install
   ```

2. **Executar o serviço de agendamento:**
   ```bash
   cd scheduling-service
   mvn spring-boot:run
   ```

3. **Executar o serviço de notificação (em terminal separado):**
   ```bash
   cd notification-service
   docker-compose up
   mvn spring-boot:run
   ```

4. **Acessar as aplicações:**
   - Scheduling API REST: http://localhost:8080
   - Notification Service: http://localhost:8081
   - GraphQL: http://localhost:8080/graphql
   - H2 Console: http://localhost:8080/h2-console

### Configuração do Kafka

Para testar as funcionalidades de notificação e eventos assíncronos, configure o Kafka:

#### 1. Instalação do Kafka

**Opção A - Download Manual:**
```bash
# Baixar Kafka
wget https://downloads.apache.org/kafka/2.13-3.6.0/kafka_2.13-3.6.0.tgz
tar -xzf kafka_2.13-3.6.0.tgz
cd kafka_2.13-3.6.0

# Terminal 1 - Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Terminal 2 - Kafka
bin/kafka-server-start.sh config/server.properties
```

**Opção B - Docker (Recomendado):**
```bash
# Criar docker-compose.yml
cat > docker-compose.yml << 'EOF'
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
EOF

# Iniciar serviços
docker-compose up -d
```

#### 2. Criar Tópicos (Opcional - Criação Automática Habilitada)

```bash
# Criar tópico manualmente (se necessário)
bin/kafka-topics.sh --create \
  --topic consultation-events \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Listar tópicos
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Verificar detalhes do tópico
bin/kafka-topics.sh --describe \
  --topic consultation-events \
  --bootstrap-server localhost:9092
```

#### 3. Testando o Kafka

**Verificar Status:**
```bash
curl http://localhost:8080/api/kafka/status
```

**Enviar Evento de Teste:**
```bash
curl -X POST http://localhost:8080/api/kafka/test-event
```

**Monitorar Mensagens (Consumer Console):**
```bash
# Terminal separado - Monitorar eventos
bin/kafka-console-consumer.sh \
  --topic consultation-events \
  --bootstrap-server localhost:9092 \
  --from-beginning \
  --formatter kafka.tools.DefaultMessageFormatter \
  --property print.key=true \
  --property print.value=true
```

#### 4. Configurações do Kafka no Sistema

O sistema está configurado para:
- **Tópico:** `consultation-events`
- **Produtores:** Publicam eventos de consulta (criação, reagendamento, cancelamento)
- **Consumidores:** Processam eventos para notificações
- **Grupo de Consumo:** `scheduling-service`

#### 5. Eventos Disponíveis

1. **ConsultationCreatedEvent** - Consulta criada
2. **ConsultationRescheduledEvent** - Consulta reagendada  
3. **ConsultationCancelledEvent** - Consulta cancelada

#### 6. Fluxo de Eventos

```bash
# 1. Registrar usuários
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Dr. Joao", "email": "joao@hospital.com", "password": "123456", "userType": "MEDICO", "crm": "CRM12345"}'

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Carlos", "email": "carlos@email.com", "password": "123456", "userType": "PACIENTE", "cpf": "12345678901"}'

# 2. Fazer login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "joao@hospital.com", "password": "123456"}' | jq -r '.token')

# 3. Criar consulta (dispara evento)
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 2, "doctorId": 1, "scheduledDateTime": "2025-09-02T14:30:00"}'

# 4. Verificar logs do consumidor
# Os eventos aparecerão nos logs da aplicação
```

#### 7. Monitoramento e Logs

Verifique os logs da aplicação para ver os eventos sendo processados:
```bash
# Logs mostrarão:
# 📧 NOTIFICAÇÃO PARA PACIENTE: carlos@email.com
# 📋 Assunto: Consulta agendada
# 💬 Mensagem: Sua consulta com Dr. Joao foi agendada para 2025-09-02T14:30:00
```

#### 8. Configuração Avançada

Para produção, considere estas configurações no `application.properties`:

```properties
# Kafka Configurações de Produção
spring.kafka.producer.acks=all
spring.kafka.producer.retries=2147483647
spring.kafka.producer.max-in-flight-requests-per-connection=5
spring.kafka.producer.enable-idempotence=true
spring.kafka.producer.compression-type=snappy

spring.kafka.consumer.enable-auto-commit=false
spring.kafka.listener.ack-mode=manual_immediate
spring.kafka.consumer.max-poll-records=500
spring.kafka.consumer.session-timeout-ms=30000
```

#### 9. Troubleshooting Kafka

**Problema: Kafka não conecta**
```bash
# Verificar se Kafka está rodando
docker ps
# ou
jps | grep Kafka

# Verificar portas
netstat -an | grep 9092
```

**Problema: Tópico não existe**
```bash
# Criar tópico manualmente
bin/kafka-topics.sh --create --topic consultation-events --bootstrap-server localhost:9092
```

**Problema: Eventos não são consumidos**
```bash
# Verificar grupo de consumidores
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group scheduling-service
```

## Endpoints da API
# API Endpoints

## Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/auth/login` | Fazer login no sistema |
| `POST` | `/api/auth/register` | Registrar novo usuário |

## Usuários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/users` | Listar todos os usuários ativos |
| `GET` | `/api/users/{id}` | Buscar usuário por ID |

## Consultas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/consultations` | Criar nova consulta |
| `GET` | `/api/consultations/{id}` | Buscar consulta por ID |
| `GET` | `/api/consultations` | Listar todas as consultas |
| `PUT` | `/api/consultations/{id}` | Atualizar consulta |
| `DELETE` | `/api/consultations/{id}` | Cancelar consulta |
| `GET` | `/api/consultations/patient/{patientId}` | Listar consultas de um paciente |
| `GET` | `/api/consultations/doctor/{doctorId}` | Listar consultas de um médico |


## Exemplos de Uso com curl

### 🔐 Autenticação

#### 1. Registrar um Médico
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Joao Silva",
    "email": "joao@hospital.com",
    "password": "123456",
    "userType": "MEDICO",
    "crm": "CRM12345"
  }'
```

#### 2. Registrar um Enfermeiro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Enfermeira Maria Santos",
    "email": "maria@hospital.com",
    "password": "123456",
    "userType": "ENFERMEIRO",
    "coren": "COREN67890"
  }'
```

#### 3. Registrar um Paciente
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Carlos Oliveira",
    "email": "carlos@email.com",
    "password": "123456",
    "userType": "PACIENTE",
    "cpf": "12345678901"
  }'
```

#### 4. Fazer Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@hospital.com",
    "password": "123456"
  }'
```

**Resposta do Login:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "name": "Dr. Joao Silva",
    "email": "joao@hospital.com",
    "userType": "MEDICO",
    "crm": "CRM12345",
    "active": true
  }
}
```

### 👥 Gerenciamento de Usuários

#### 5. Listar Usuários (requer autenticação)
```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  http://localhost:8080/api/users
```

#### 6. Buscar Usuário por ID
```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  http://localhost:8080/api/users/1
```

### 📅 Gerenciamento de Consultas

#### 7. Criar Nova Consulta
```bash
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 3,
    "doctorId": 1,
    "scheduledDateTime": "2025-09-02T14:30:00"
  }'
```

#### 8. Listar Todas as Consultas
```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  http://localhost:8080/api/consultations
```

#### 9. Buscar Consulta por ID
```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  http://localhost:8080/api/consultations/1
```

#### 10. Atualizar Consulta (apenas médicos)
```bash
curl -X PUT http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 3,
    "doctorId": 1,
    "scheduledDateTime": "2025-09-02T15:00:00"
  }'
```

#### 11. Cancelar Consulta
```bash
curl -X DELETE http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

#### 12. Listar Consultas por Paciente
```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  http://localhost:8080/api/consultations/patient/3
```

#### 13. Listar Consultas por Médico
```bash
curl -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  http://localhost:8080/api/consultations/doctor/1
```

### 🗄️ Banco de Dados H2

Para visualizar os dados no banco:

1. Acesse: http://localhost:8080/h2-console
2. Configure:
   - **JDBC URL:** `jdbc:h2:mem:schedulingdb`
   - **User Name:** `sa`
   - **Password:** `password`
3. Clique em "Connect"

### 📝 Fluxo Completo de Teste

```bash
# 1. Registrar um médico
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Dr. Joao", "email": "joao@hospital.com", "password": "123456", "userType": "MEDICO", "crm": "CRM12345"}'

# 2. Registrar um paciente  
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Carlos", "email": "carlos@email.com", "password": "123456", "userType": "PACIENTE", "cpf": "12345678901"}'

# 3. Fazer login como médico
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "joao@hospital.com", "password": "123456"}' | jq -r '.token')

# 4. Listar usuários
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/users

# 5. Criar consulta (substitua os IDs pelos retornados no passo 4)
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 2, "doctorId": 1, "scheduledDateTime": "2025-09-02T14:30:00"}'

# 6. Listar todas as consultas
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/consultations

# 7. Consultar agendamentos do médico específico
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/consultations/doctor/1

# 8. Atualizar horário da consulta
curl -X PUT http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 2, "doctorId": 1, "scheduledDateTime": "2025-09-02T15:00:00"}'

# 9. Cancelar consulta
curl -X DELETE http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 🔄 Fluxo de Teste Alternativo (Windows PowerShell)

Para usuários do Windows PowerShell, use esta sintaxe:

```powershell
# 1. Registrar médico
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"name": "Dr. Joao", "email": "joao@hospital.com", "password": "123456", "userType": "MEDICO", "crm": "CRM12345"}'

# 2. Fazer login
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"email": "joao@hospital.com", "password": "123456"}'

# 3. Extrair token
$token = $loginResponse.token

# 4. Listar usuários
$headers = @{ "Authorization" = "Bearer $token" }
$users = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Headers $headers

# 5. Criar consulta
$consultationData = @{
  patientId = 2
  doctorId = 1
  scheduledDateTime = "2025-09-02T14:30:00"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/consultations" `
  -Method POST `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $consultationData
```

### ⚠️ Observações Importantes

- **Substitua `SEU_TOKEN_AQUI`** pelo token JWT retornado no login
- **IDs dos usuários** são gerados automaticamente (1, 2, 3...)
- **Datas** devem estar no formato ISO: `2025-09-02T14:30:00`
- **Tipos de usuário:** `MEDICO`, `ENFERMEIRO`, `PACIENTE`
- **Campos obrigatórios:** CRM para médicos, COREN para enfermeiros, CPF para pacientes

### 🔐 Controle de Acesso por Tipo de Usuário

#### Endpoints acessíveis por MÉDICOS:
- ✅ Todas as operações de consulta (criar, listar, atualizar, cancelar)
- ✅ Ver consultas de qualquer paciente/médico
- ✅ Atualizar consultas (exclusivo)

#### Endpoints acessíveis por ENFERMEIROS:
- ✅ Criar e cancelar consultas
- ✅ Listar todas as consultas
- ✅ Ver consultas de qualquer paciente/médico
- ❌ Atualizar consultas (apenas médicos)

#### Endpoints acessíveis por PACIENTES:
- ✅ Ver suas próprias consultas
- ✅ Listar consultas (filtro futuro para suas próprias)
- ❌ Criar, atualizar ou cancelar consultas

### 🧪 Testando Diferentes Perfis

```bash
# Registrar usuários de diferentes tipos
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"name": "Dr. Ana", "email": "ana@hospital.com", "password": "123456", "userType": "MEDICO", "crm": "CRM67890"}'

curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"name": "Enfermeira Rosa", "email": "rosa@hospital.com", "password": "123456", "userType": "ENFERMEIRO", "coren": "COREN12345"}'

curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"name": "Paciente José", "email": "jose@email.com", "password": "123456", "userType": "PACIENTE", "cpf": "98765432100"}'

# Testar login com enfermeiro
NURSE_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "rosa@hospital.com", "password": "123456"}' | jq -r '.token')

# Enfermeiro pode criar consulta
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer $NURSE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 4, "doctorId": 1, "scheduledDateTime": "2025-09-03T10:00:00"}'

# Testar login com paciente
PATIENT_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "jose@email.com", "password": "123456"}' | jq -r '.token')

# Paciente pode ver suas consultas
curl -H "Authorization: Bearer $PATIENT_TOKEN" \
  http://localhost:8080/api/consultations/patient/4
```

## 🌐 Acessando pelo Navegador

### URLs Diretas (sem autenticação)
- **Página Inicial:** http://localhost:8080
- **Informações da API:** http://localhost:8080/api/info  
- **Console do Banco H2:** http://localhost:8080/h2-console

### Interface Web
A página inicial (http://localhost:8080) fornece:
- Status da aplicação
- Lista de endpoints disponíveis
- Instruções de uso
- Links úteis para desenvolvimento

### ⚠️ Evitando Erros de JWT
Se você tentar acessar endpoints protegidos diretamente pelo navegador, receberá erro 401 (Unauthorized). Use:
- **APIs públicas:** `/`, `/api/info`, `/h2-console`, `/api/auth/*`
- **Para APIs protegidas:** Use ferramentas como curl, Postman ou inclua o header `Authorization: Bearer TOKEN`

## 🔍 Testes de Validação e Erros

### Testando Validações de Entrada

```bash
# 1. Tentativa de criar consulta sem token (401 Unauthorized)
curl -X POST http://localhost:8080/api/consultations \
  -H "Content-Type: application/json" \
  -d '{"patientId": 1, "doctorId": 1, "scheduledDateTime": "2025-09-02T14:30:00"}'

# 2. Tentativa de criar consulta com data no passado (400 Bad Request)
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 1, "doctorId": 1, "scheduledDateTime": "2023-01-01T14:30:00"}'

# 3. Tentativa de criar consulta com IDs inválidos (400 Bad Request)
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 999, "doctorId": 999, "scheduledDateTime": "2025-09-02T14:30:00"}'

# 4. Paciente tentando criar consulta (403 Forbidden)
curl -X POST http://localhost:8080/api/consultations \
  -H "Authorization: Bearer TOKEN_DO_PACIENTE" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 1, "doctorId": 1, "scheduledDateTime": "2025-09-02T14:30:00"}'

# 5. Enfermeiro tentando atualizar consulta (403 Forbidden)
curl -X PUT http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer TOKEN_DO_ENFERMEIRO" \
  -H "Content-Type: application/json" \
  -d '{"patientId": 1, "doctorId": 1, "scheduledDateTime": "2025-09-02T15:00:00"}'
```

### Respostas de Erro Esperadas

#### 400 Bad Request
```json
{
  "timestamp": "2025-09-01T03:46:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/consultations"
}
```

#### 401 Unauthorized
```json
{
  "timestamp": "2025-09-01T03:46:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/consultations"
}
```

#### 403 Forbidden
```json
{
  "timestamp": "2025-09-01T03:46:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/consultations"
}
```

#### 404 Not Found
```json
{
  "timestamp": "2025-09-01T03:46:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/consultations/999"
}
```

## GraphQL

Acesse o playground GraphQL em: http://localhost:8080/graphql

### Exemplo de Query
```graphql
query GetPatientConsultations($patientId: ID!) {
  patientConsultations(patientId: $patientId) {
    id
    scheduledDateTime
    status
    doctor {
      name
    }
    notes
  }
}
```

## Estrutura do Banco

### Tabela users
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL, -- MEDICO, ENFERMEIRO, PACIENTE
    crm VARCHAR(20),     -- Apenas para médicos
    coren VARCHAR(20),   -- Apenas para enfermeiros  
    cpf VARCHAR(14),     -- Apenas para pacientes
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Tabela consultations  
```sql
CREATE TABLE consultations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    scheduled_date_time TIMESTAMP NOT NULL,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL, -- AGENDADA, EM_ANDAMENTO, CONCLUIDA, CANCELADA, FALTOU
    notes TEXT,
    symptoms TEXT,
    diagnosis TEXT,
    prescription TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Exemplos de Queries Úteis

```sql
-- Ver todas as consultas com nomes dos usuários
SELECT 
    c.id,
    p.name as patient_name,
    d.name as doctor_name,
    c.scheduled_date_time,
    c.status
FROM consultations c
JOIN users p ON c.patient_id = p.id
JOIN users d ON c.doctor_id = d.id
ORDER BY c.scheduled_date_time;

-- Consultas de um médico específico
SELECT * FROM consultations 
WHERE doctor_id = 1 
ORDER BY scheduled_date_time;

-- Consultas de um paciente específico
SELECT * FROM consultations 
WHERE patient_id = 2 
ORDER BY scheduled_date_time DESC;
```

## Próximos Passos

1. ✅ **Implementar o serviço de notificações**
2. ✅ **Adicionar endpoints GraphQL**  
3. ✅ **Configurar integração com Kafka**
4. 🔄 **Adicionar testes de integração**
5. 🔄 **Configurar banco de dados PostgreSQL para produção**
6. 🔄 **Implementar cache com Redis**
7. 🔄 **Adicionar monitoramento com Prometheus**
8. 🔄 **Configurar CI/CD**

## 🛠️ Troubleshooting

### Problemas Comuns

#### Erro 401 - Token Inválido
```bash
# Verificar se o token não expirou (válido por 5 horas)
# Fazer novo login para obter token atualizado
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "seu@email.com", "password": "suasenha"}'
```

#### Aplicação não inicia na porta 8080
```bash
# Verificar se a porta está ocupada
netstat -ano | findstr :8080

# Ou alterar a porta no application.properties
echo "server.port=8081" >> scheduling-service/src/main/resources/application.properties
```

#### Erro de conexão com banco H2
```bash
# Limpar e recompilar o projeto
mvn clean install
cd scheduling-service
mvn spring-boot:run
```

### Ferramentas Úteis

#### Postman Collection
Para importar no Postman, crie uma collection com estas variáveis:
- `baseUrl`: `http://localhost:8080`
- `token`: `Bearer SEU_TOKEN_AQUI`

#### VS Code Extensions Recomendadas
- **REST Client**: Para testar APIs direto no editor
- **Thunder Client**: Cliente REST integrado
- **Database Client**: Para visualizar dados do H2

#### Script de Teste Rápido (.http file)
```http
### Registrar médico
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Dr. Teste",
  "email": "teste@hospital.com", 
  "password": "123456",
  "userType": "MEDICO",
  "crm": "CRM999"
}

### Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "teste@hospital.com",
  "password": "123456"
}

### Listar usuários
GET http://localhost:8080/api/users
Authorization: Bearer {{token}}
```

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os logs da aplicação no terminal
2. Consulte a documentação no H2 Console
3. Teste os endpoints com curl ou Postman
4. Verifique as validações de entrada de dados

---

**Sistema Hospitalar - Clean Architecture com Spring Boot** 🏥  
*Desenvolvido com Java 21, Spring Security, GraphQL e Kafka*
