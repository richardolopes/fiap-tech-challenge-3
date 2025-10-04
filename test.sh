#!/bin/bash

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{ "name":"Guilherme de Oliveira Vicente","email": "guilherme@mail.com","password": "olamundo", "userType": "MEDICO", "crm": "123456789"}'

echo "guilherme criado\n"

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Julia Schlindwein","email": "julia@mail.com","password": "olamundo","cpf": "123456789","userType": "PACIENTE"}'

echo "julha criado\n"

TOKEN_MEDICO=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "guilherme@mail.com", "password": "olamundo"}' | jq -r '.token');

echo $TOKEN_MEDICO
echo "\nfim token\n\n"

TOKEN_PACIENTE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "julia@mail.com","password": "olamundo"}' | jq -r '.token');

echo $TOKEN_PACIENTE
echo "\nfim token\n\n"

curl -X POST http://localhost:8080/api/consultations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_MEDICO" \
  -d '{"patientId": 2,"doctorId": 1,"scheduledDateTime": "2039-12-25T16:00:00","notes": "Consulta ginecolOOgica"}';

echo "consulta"