# Encurtador de Link com Analytics

API REST para encurtamento de URLs com autenticação JWT, persistência em PostgreSQL e cache com Redis.

## Visão geral

Este projeto implementa um encurtador de links com:

- cadastro e login de usuários;
- criação e exclusão de URLs encurtadas;
- consulta de estatísticas de cliques por URL;
- segurança com Spring Security + JWT;
- versionamento de banco com Flyway.

## Stack utilizada

- Java 17
- Spring Boot 4
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Redis (cache)
- SpringDoc OpenAPI (Swagger)
- Maven

## Estrutura do projeto

```
src/main/java/com/hector/encurtadorlink
├── config          # Segurança, Redis e Async
├── controller      # Endpoints REST
├── dto             # Requests e Responses
├── exception       # Exceções de domínio e handler global
├── model           # Entidades JPA
├── repository      # Repositórios Spring Data
└── service         # Regras de negócio e JWT

src/main/resources
├── application.yml
└── db/migration    # Scripts Flyway (V1, V2, V3)
```

## Requisitos

- Java 17+
- Maven 3.9+ (ou uso do wrapper `mvnw`)
- Docker e Docker Compose (para PostgreSQL e Redis)

## Configuração de ambiente

As configurações atuais estão em `src/main/resources/application.yml`:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432` (db `encurtador`)
- Redis: `localhost:6379`
- JWT secret e expiração configurados em `jwt.secret` e `jwt.expiration`

> Recomendação: em produção, mova segredo JWT e credenciais para variáveis de ambiente.

## Subindo dependências com Docker

No diretório raiz:

```bash
docker compose up -d
```

Serviços criados:

- `encurtador-db` (PostgreSQL 16)
- `redis`

## Executando o projeto

### Com Maven Wrapper (recomendado)

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

### Build

```bash
./mvnw clean package
```

## Banco de dados e migrações

As migrações Flyway ficam em `src/main/resources/db/migration`:

- `V1__create_urls_table.sql`
- `V2__create_clicks_table.sql`
- `V3__create_users_table.sql`

Ao iniciar a aplicação, as migrações são aplicadas automaticamente.

## Documentação da API

Após subir a aplicação:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Autenticação

A API usa JWT no header:

```
Authorization: Bearer <token>
```

Rotas públicas:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- documentação Swagger (`/swagger-ui/**`, `/v3/api-docs/**`)

Demais rotas exigem token.

## Endpoints principais

### Auth

#### `POST /api/v1/auth/register`

Cria usuário e retorna token JWT.

Exemplo request:

```json
{
  "email": "usuario@email.com",
  "name": "Usuario",
  "password": "123456"
}
```

Exemplo response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### `POST /api/v1/auth/login`

Autentica usuário e retorna token JWT.

Exemplo request:

```json
{
  "email": "usuario@email.com",
  "password": "123456"
}
```

### Usuários

#### `POST /api/v1/users/`

Cria usuário (rota protegida no estado atual da configuração de segurança).

### URLs

#### `POST /api/v1/url/`

Cria URL encurtada.

Exemplo request:

```json
{
  "originalUrl": "https://www.exemplo.com/pagina",
  "expiresAt": "2026-12-31T23:59:59"
}
```

Exemplo response:

```json
{
  "originalUrl": "https://www.exemplo.com/pagina",
  "shortCode": "a1B2c3",
  "shortUrl": "https://dominio.com/a1B2c3",
  "createdAt": "2026-03-27T10:00:00",
  "expiresAt": "2026-12-31T23:59:59"
}
```

#### `GET /api/v1/url/{shortCode}`

Retorna redirecionamento HTTP 302 para a URL original (se existir e não estiver expirada).

#### `DELETE /api/v1/url/{id}`

Exclui uma URL pelo `id`.

### Estatísticas

#### `GET /api/v1/stats/{shortCode}/`

Retorna estatísticas da URL:

- dados da URL;
- total de cliques;
- agrupamento de cliques por dia.

## Códigos de status e erros

Tratados pelo `GlobalExceptionHandler`:

- `404 Not Found`: URL não encontrada (`UrlNotFoundException`)
- `410 Gone`: URL expirada (`UrlExpiredException`)

Outros erros podem retornar comportamento padrão do Spring Boot, dependendo do tipo de exceção.

## Testes

Executar:

```bash
./mvnw test
```

Atualmente existe teste básico de carregamento de contexto (`contextLoads`).

## Observações importantes do estado atual

- O campo `shortUrl` retornado é montado com domínio fixo (`https://dominio.com/`).
- O registro de clique existe no serviço (`ClickService`), mas ainda não está integrado ao fluxo de redirecionamento.
- O `application.yml` atual contém segredo JWT e credenciais fixas para ambiente local.

