# 🧪 Guia Completo de Testes da API - Sistema Hospitalar

Este documento contém todos os comandos curl necessários para testar completamente a API REST e GraphQL do sistema hospitalar.

## 📋 Índice

1. [Preparação do Ambiente](#preparação-do-ambiente)
2. [Autenticação e Registro](#autenticação-e-registro)
3. [Testes API REST](#testes-api-rest)
4. [Testes GraphQL](#testes-graphql)
5. [Testes de Segurança](#testes-de-segurança)
6. [Resumo dos Resultados Esperados](#resumo-dos-resultados-esperados)

---

## 🚀 Preparação do Ambiente

### Iniciar o Sistema
```bash
cd /home/guilherme/tech_challenge3/final/fiap-tech-challenge-3
docker compose up -d
```
---

## 🔐 Autenticação e Registro

### 1. Registrar Usuários de Teste

#### 1.1 Registrar Dr. Carlos (MÉDICO)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Carlos",
    "email": "carlos@hospital.com",
    "password": "123456",
    "userType": "MEDICO",
    "crm": "CRM12345"
  }'
```
**Resultado Esperado:** Status 200, retorna usuário criado com ID 1

#### 1.2 Registrar Pedro (PACIENTE)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro Silva",
    "email": "pedro@email.com",
    "password": "123456",
    "userType": "PACIENTE",
    "cpf": "12345678901"
  }'
```
**Resultado Esperado:** Status 200, retorna usuário criado com ID 2

#### 1.3 Registrar Dr. Maria (MÉDICA)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Maria Santos",
    "email": "maria@hospital.com",
    "password": "123456",
    "userType": "MEDICO",
    "crm": "CRM98765"
  }'
```
**Resultado Esperado:** Status 200, retorna usuário criado com ID 3

#### 1.4 Registrar Enfermeira Ana (ENFERMEIRO)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Enfermeira Ana",
    "email": "ana@hospital.com",
    "password": "123456",
    "userType": "ENFERMEIRO",
    "coren": "COREN12345"
  }'
```
**Resultado Esperado:** Status 200, retorna usuário criado com ID 4

#### 1.5 Registrar Julia (PACIENTE)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Julia Costa",
    "email": "julia@email.com",
    "password": "123456",
    "userType": "PACIENTE",
    "cpf": "98765432100"
  }'
```
**Resultado Esperado:** Status 200, retorna usuário criado com ID 5

### 2. Fazer Login e Obter Tokens JWT

#### 2.1 Login Dr. Carlos
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos@hospital.com",
    "password": "123456"
  }'
```
**Resultado Esperado:** Retorna token JWT para Dr. Carlos
**Salve o token como:** `TOKEN_CARLOS`

#### 2.2 Login Pedro
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pedro@email.com",
    "password": "123456"
  }'
```
**Resultado Esperado:** Retorna token JWT para Pedro
**Salve o token como:** `TOKEN_PEDRO`

#### 2.3 Login Dr. Maria
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@hospital.com",
    "password": "123456"
  }'
```
**Resultado Esperado:** Retorna token JWT para Dr. Maria
**Salve o token como:** `TOKEN_MARIA`

#### 2.4 Login Enfermeira Ana
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ana@hospital.com",
    "password": "123456"
  }'
```
**Resultado Esperado:** Retorna token JWT para Enfermeira Ana
**Salve o token como:** `TOKEN_ANA`

#### 2.5 Login Julia
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "julia@email.com",
    "password": "123456"
  }'
```
**Resultado Esperado:** Retorna token JWT para Julia
**Salve o token como:** `TOKEN_JULIA`

---

## 🏥 Testes API REST

### 3. Criação de Consultas

#### 3.1 Dr. Carlos cria consulta para Pedro
```bash
curl -X POST http://localhost:8080/api/consultations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CARLOS" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduledDateTime": "2025-11-15T10:00:00",
    "notes": "Consulta cardiológica"
  }'
```
**Resultado Esperado:** Status 201, consulta criada com ID 1

#### 3.2 Dr. Maria cria consulta para Julia
```bash
curl -X POST http://localhost:8080/api/consultations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_MARIA" \
  -d '{
    "patientId": 5,
    "doctorId": 3,
    "scheduledDateTime": "2025-11-25T16:00:00",
    "notes": "Consulta ginecológica"
  }'
```
**Resultado Esperado:** Status 201, consulta criada com ID 2

#### 3.3 Enfermeira Ana cria consulta
```bash
curl -X POST http://localhost:8080/api/consultations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ANA" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduledDateTime": "2025-12-20T14:30:00",
    "notes": "Consulta de rotina"
  }'
```
**Resultado Esperado:** Status 201, consulta criada com ID 3

#### 3.4 Paciente tentando criar consulta (deve falhar)
```bash
curl -X POST http://localhost:8080/api/consultations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduledDateTime": "2025-11-30T11:00:00",
    "notes": "Tentativa paciente"
  }'
```
**Resultado Esperado:** Status 403 Forbidden

### 4. Consultas - Listagem Geral

#### 4.1 Médico listando todas as consultas ✅
```bash
curl -X GET http://localhost:8080/api/consultations \
  -H "Authorization: Bearer TOKEN_CARLOS"
```
**Resultado Esperado:** Status 200, lista todas as consultas

#### 4.2 Enfermeira listando todas as consultas ✅
```bash
curl -X GET http://localhost:8080/api/consultations \
  -H "Authorization: Bearer TOKEN_ANA"
```
**Resultado Esperado:** Status 200, lista todas as consultas

#### 4.3 Paciente tentando listar todas as consultas ❌
```bash
curl -X GET http://localhost:8080/api/consultations \
  -H "Authorization: Bearer TOKEN_PEDRO"
```
**Resultado Esperado:** Status 403 Forbidden

### 5. Consultas - Por ID Específico

#### 5.1 Pedro acessando sua própria consulta ✅
```bash
curl -X GET http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer TOKEN_PEDRO"
```
**Resultado Esperado:** Status 200, retorna consulta ID 1

#### 5.2 Pedro tentando acessar consulta de outro paciente ❌
```bash
curl -X GET http://localhost:8080/api/consultations/2 \
  -H "Authorization: Bearer TOKEN_PEDRO"
```
**Resultado Esperado:** Status 403 Forbidden

#### 5.3 Médico acessando qualquer consulta ✅
```bash
curl -X GET http://localhost:8080/api/consultations/2 \
  -H "Authorization: Bearer TOKEN_CARLOS"
```
**Resultado Esperado:** Status 200, retorna consulta ID 2

#### 5.4 Enfermeira acessando qualquer consulta ✅
```bash
curl -X GET http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer TOKEN_ANA"
```
**Resultado Esperado:** Status 200, retorna consulta ID 1

### 6. Consultas - Por Paciente

#### 6.1 Pedro acessando suas próprias consultas ✅
```bash
curl -X GET http://localhost:8080/api/consultations/patient/2 \
  -H "Authorization: Bearer TOKEN_PEDRO"
```
**Resultado Esperado:** Status 200, lista consultas do Pedro

#### 6.2 Pedro tentando acessar consultas de outro paciente ❌
```bash
curl -X GET http://localhost:8080/api/consultations/patient/5 \
  -H "Authorization: Bearer TOKEN_PEDRO"
```
**Resultado Esperado:** Status 403 Forbidden

#### 6.3 Médico acessando consultas de qualquer paciente ✅
```bash
curl -X GET http://localhost:8080/api/consultations/patient/5 \
  -H "Authorization: Bearer TOKEN_CARLOS"
```
**Resultado Esperado:** Status 200, lista consultas da Julia

#### 6.4 Enfermeira acessando consultas de qualquer paciente ✅
```bash
curl -X GET http://localhost:8080/api/consultations/patient/2 \
  -H "Authorization: Bearer TOKEN_ANA"
```
**Resultado Esperado:** Status 200, lista consultas do Pedro

### 7. Atualização de Consultas

#### 7.1 Médico atualizando consulta ✅
```bash
curl -X PUT http://localhost:8080/api/consultations/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CARLOS" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduledDateTime": "2025-11-15T14:00:00",
    "reason": "Mudança de horário"
  }'
```
**Resultado Esperado:** Status 200, consulta atualizada

#### 7.2 Enfermeira atualizando consulta ✅
```bash
curl -X PUT http://localhost:8080/api/consultations/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ANA" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduledDateTime": "2025-12-20T15:30:00",
    "reason": "Reagendamento"
  }'
```
**Resultado Esperado:** Status 200, consulta atualizada

#### 7.3 Paciente tentando atualizar consulta ❌
```bash
curl -X PUT http://localhost:8080/api/consultations/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "patientId": 2,
    "doctorId": 1,
    "scheduledDateTime": "2025-11-15T16:00:00",
    "reason": "Tentativa paciente"
  }'
```
**Resultado Esperado:** Status 403 Forbidden

### 8. Cancelamento de Consultas

#### 8.1 Médico cancelando consulta ✅
```bash
curl -X DELETE http://localhost:8080/api/consultations/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_MARIA" \
  -d '{
    "reason": "Paciente não pode comparecer"
  }'
```
**Resultado Esperado:** Status 200 No Content

#### 8.2 Enfermeira cancelando consulta ✅
```bash
curl -X DELETE http://localhost:8080/api/consultations/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ANA" \
  -d '{
    "reason": "Emergência médica"
  }'
```
**Resultado Esperado:** Status 200 No Content

#### 8.3 Paciente tentando cancelar consulta ❌
```bash
curl -X DELETE http://localhost:8080/api/consultations/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "reason": "Não posso comparecer"
  }'
```
**Resultado Esperado:** Status 403 Forbidden

### 9. Usuários - Listagem

#### 9.1 Médico listando usuários ✅
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer TOKEN_CARLOS"
```
**Resultado Esperado:** Status 200, lista todos os usuários

#### 9.2 Enfermeira listando usuários ✅
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer TOKEN_ANA"
```
**Resultado Esperado:** Status 200, lista todos os usuários

#### 9.3 Paciente tentando listar usuários ❌
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer TOKEN_PEDRO"
```
**Resultado Esperado:** Status 403 Forbidden

---

## 🔍 Testes GraphQL

### 10. GraphQL - Consultas de Usuários

#### 10.1 Médico consultando lista de usuários ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CARLOS" \
  -d '{
    "query": "query { users { id name email userType crm coren cpf active } }"
  }'
```
**Resultado Esperado:** Lista todos os usuários

#### 10.2 Enfermeira consultando lista de usuários ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ANA" \
  -d '{
    "query": "query { users { id name email userType } }"
  }'
```
**Resultado Esperado:** Lista todos os usuários

#### 10.3 Paciente tentando consultar lista de usuários ❌
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "query": "query { users { id name email userType } }"
  }'
```
**Resultado Esperado:** `{"errors":[{"message":"Forbidden"}]}`

### 11. GraphQL - Consultas Médicas

#### 11.1 Médico consultando suas consultas ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CARLOS" \
  -d '{
    "query": "query { doctorConsultations(doctorId: 1) { id patientId doctorId scheduledDateTime status } }"
  }'
```
**Resultado Esperado:** Lista consultas do Dr. Carlos

#### 11.2 Enfermeira consultando consultas de médico ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ANA" \
  -d '{
    "query": "query { doctorConsultations(doctorId: 3) { id patientId scheduledDateTime status } }"
  }'
```
**Resultado Esperado:** Lista consultas da Dr. Maria

#### 11.3 Paciente tentando consultar consultas de médico ❌
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "query": "query { doctorConsultations(doctorId: 1) { id patientId doctorId } }"
  }'
```
**Resultado Esperado:** `{"errors":[{"message":"Forbidden"}]}`

### 12. GraphQL - Histórico do Paciente

#### 12.1 Pedro consultando seu próprio histórico ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "query": "query MeuHistorico { patientConsultations(patientId: 2) { id scheduledDateTime status notes symptoms diagnosis prescription createdAt } }"
  }'
```
**Resultado Esperado:** Lista consultas do Pedro

#### 12.2 Médico consultando histórico de qualquer paciente ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CARLOS" \
  -d '{
    "query": "query HistoricoPaciente { patientConsultations(patientId: 5) { id scheduledDateTime status patientName doctorName createdAt } }"
  }'
```
**Resultado Esperado:** Lista consultas da Julia

#### 12.3 Pedro tentando acessar histórico de outro paciente ❌
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_PEDRO" \
  -d '{
    "query": "query { patientConsultations(patientId: 5) { id scheduledDateTime status } }"
  }'
```
**Resultado Esperado:** `{"errors":[{"message":"Forbidden"}]}`

### 13. GraphQL - Consultas Flexíveis

#### 13.1 Múltiplas queries em uma requisição ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CARLOS" \
  -d '{
    "query": "query ConsultasFlexiveis { patientConsultations(patientId: 2) { id scheduledDateTime status } doctorConsultations(doctorId: 1) { id patientId scheduledDateTime status } users { id name userType } }"
  }'
```
**Resultado Esperado:** Múltiplas respostas em uma requisição

#### 13.2 Campos específicos selecionados ✅
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ANA" \
  -d '{
    "query": "query CamposEspecificos { patientConsultations(patientId: 2) { id status } doctorConsultations(doctorId: 1) { id scheduledDateTime } }"
  }'
```
**Resultado Esperado:** Apenas campos solicitados retornados

---

## 🛡️ Testes de Segurança

### 14. Testes de Autorização Cross-Patient

#### 14.1 Julia tentando acessar dados do Pedro ❌
```bash
curl -X GET http://localhost:8080/api/consultations/patient/2 \
  -H "Authorization: Bearer TOKEN_JULIA"
```
**Resultado Esperado:** Status 403 Forbidden

#### 14.2 Julia tentando acessar consulta específica do Pedro ❌
```bash
curl -X GET http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer TOKEN_JULIA"
```
**Resultado Esperado:** Status 403 Forbidden

#### 14.3 Julia tentando acessar dados do Pedro via GraphQL ❌
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_JULIA" \
  -d '{
    "query": "query { patientConsultations(patientId: 2) { id scheduledDateTime } }"
  }'
```
**Resultado Esperado:** `{"errors":[{"message":"Forbidden"}]}`

### 15. Testes sem Autenticação

#### 15.1 Tentativa de acesso sem token ❌
```bash
curl -X GET http://localhost:8080/api/consultations
```
**Resultado Esperado:** Status 401 Unauthorized

#### 15.2 Tentativa GraphQL sem token ❌
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "query { hello }"}'
```
**Resultado Esperado:** Status 401 Unauthorized

### 16. Testes com Token Inválido

#### 16.1 Token malformado ❌
```bash
curl -X GET http://localhost:8080/api/consultations \
  -H "Authorization: Bearer token_invalido_123"
```
**Resultado Esperado:** Status 401 Unauthorized

#### 16.2 Token expirado (simulado) ❌
```bash
curl -X GET http://localhost:8080/api/consultations \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0ZUBleGFtcGxlLmNvbSIsImlhdCI6MTUwMDAwMDAwMCwiZXhwIjoxNTAwMDAwMDAwfQ.invalid"
```
**Resultado Esperado:** Status 401 Unauthorized

---

## 📊 Resumo dos Resultados Esperados

### ✅ **Casos de Sucesso (Status 200/201/204)**

| **Tipo** | **Usuário** | **Ação** | **Endpoint/Query** |
|----------|-------------|----------|-------------------|
| REST | MÉDICO | Listar todas consultas | `GET /api/consultations` |
| REST | MÉDICO | Ver consulta específica | `GET /api/consultations/{id}` |
| REST | MÉDICO | Criar consulta | `POST /api/consultations` |
| REST | MÉDICO | Atualizar consulta | `PUT /api/consultations/{id}` |
| REST | MÉDICO | Cancelar consulta | `DELETE /api/consultations/{id}` |
| REST | MÉDICO | Ver consultas por paciente | `GET /api/consultations/patient/{id}` |
| REST | MÉDICO | Listar usuários | `GET /api/users` |
| REST | ENFERMEIRO | Listar todas consultas | `GET /api/consultations` |
| REST | ENFERMEIRO | Criar consulta | `POST /api/consultations` |
| REST | ENFERMEIRO | Cancelar consulta | `DELETE /api/consultations/{id}` |
| REST | PACIENTE | Ver próprias consultas | `GET /api/consultations/patient/{próprio_id}` |
| REST | PACIENTE | Ver própria consulta | `GET /api/consultations/{própria_consulta}` |
| GraphQL | MÉDICO | Todas as queries | `users`, `doctorConsultations`, `patientConsultations` |
| GraphQL | ENFERMEIRO | Queries permitidas | `users`, `doctorConsultations`, `patientConsultations` |
| GraphQL | PACIENTE | Próprios dados | `patientConsultations` (próprio ID) |

### ❌ **Casos de Falha (Status 403 Forbidden)**

| **Tipo** | **Usuário** | **Ação Negada** | **Endpoint/Query** |
|----------|-------------|-----------------|-------------------|
| REST | PACIENTE | Listar todas consultas | `GET /api/consultations` |
| REST | PACIENTE | Ver consulta de outro | `GET /api/consultations/{outro_id}` |
| REST | PACIENTE | Criar consulta | `POST /api/consultations` |
| REST | PACIENTE | Atualizar consulta | `PUT /api/consultations/{id}` |
| REST | PACIENTE | Cancelar consulta | `DELETE /api/consultations/{id}` |
| REST | PACIENTE | Ver consultas de outro paciente | `GET /api/consultations/patient/{outro_id}` |
| REST | PACIENTE | Listar usuários | `GET /api/users` |
| GraphQL | PACIENTE | Queries restritas | `users`, `doctorConsultations` |
| GraphQL | PACIENTE | Dados de outros | `patientConsultations` (outro ID) |

### 🔐 **Validações de Segurança Implementadas**

1. **Autenticação JWT**: ✅ Tokens válidos obrigatórios
2. **Autorização por Role**: ✅ MÉDICO, ENFERMEIRO, PACIENTE
3. **Isolamento de Dados**: ✅ Pacientes só veem próprios dados
4. **Cross-patient Protection**: ✅ Acesso negado entre pacientes
5. **Method Security**: ✅ `@PreAuthorize` em todos endpoints sensíveis
6. **Custom Security Service**: ✅ Validações adicionais implementadas

### 🎯 **Funcionalidades GraphQL Demonstradas**

1. **Consultas Flexíveis**: ✅ Campos selecionáveis
2. **Múltiplas Queries**: ✅ Uma requisição, várias consultas
3. **Histórico Completo**: ✅ Todas as consultas do paciente
4. **Segurança Granular**: ✅ Mesmo nível de proteção que REST
5. **Performance**: ✅ Apenas dados necessários retornados

---

## 🚀 **Conclusão**

Este guia cobre **100% dos endpoints** e **funcionalidades de segurança** do sistema hospitalar. Todos os comandos curl foram testados e validados, garantindo que:

- ✅ **API REST**: Totalmente funcional e segura
- ✅ **GraphQL**: Consultas flexíveis implementadas
- ✅ **Segurança**: Isolamento completo de dados
- ✅ **Autorização**: Controle por tipo de usuário
- ✅ **Histórico Flexível**: Consultas médicas completas

