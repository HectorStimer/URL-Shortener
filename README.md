# 🔗 Encurtador de URL com Analytics

API REST para encurtamento de URLs com sistema de analytics, cache com Redis, autenticação JWT e documentação automática com Swagger.

---

## Sobre o projeto

O sistema permite encurtar URLs longas gerando códigos curtos únicos em Base62. Cada acesso ao link curto é registrado de forma assíncrona, permitindo visualizar estatísticas de cliques por URL. O projeto foi construído com foco em boas práticas de engenharia, separação de camadas, cache, segurança e testes.

---

## Decisões técnicas

**Por que Redis para cache?**
O redirecionamento precisa ser sub-100ms. Buscar no PostgreSQL a cada acesso adicionaria latência desnecessária. Com `@Cacheable` no Spring, a primeira chamada vai ao banco e as seguintes vêm do Redis. O dado raramente muda depois de criado, então o cache é válido por horas.

**Por que `@Async` no registro de cliques?**
O redirecionamento é o caminho crítico da aplicação — o usuário não pode esperar. Salvar o clique no banco é uma operação secundária que não precisa bloquear a resposta. Com `@Async`, o redirect acontece imediatamente e o clique é salvo em background por outra thread.

**Por que Flyway e não `ddl-auto`?**
O `ddl-auto=update` do Hibernate é imprevisível em produção — nunca remove colunas e pode causar inconsistências entre ambientes. Com Flyway, cada mudança no banco é um arquivo SQL versionado, commitado no Git, que roda na ordem certa em qualquer ambiente automaticamente.

**Por que Base62 e não UUID?**
UUID gera strings de 36 caracteres. Base62 com 6 caracteres já oferece mais de 56 bilhões de combinações, mais que suficiente, e gera URLs curtas de verdade, que é o propósito do projeto.

---

## Stack

- **Java 17** + **Spring Boot 4.0.3**
- **Spring Security** + **JWT** (jjwt 0.12.6)
- **Spring Data JPA** + **PostgreSQL**
- **Spring Cache** + **Redis**
- **Flyway** — migrations versionadas
- **Springdoc OpenAPI 3.0** — documentação automática
- **Bucket4j** — rate limiting por IP
- **Testcontainers** — testes de integração com infraestrutura real
- **Docker Compose** — ambiente local

---

## Endpoints

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/v1/auth/register` | Cria uma conta | Não |
| POST | `/api/v1/auth/login` | Autentica e retorna JWT | Não |
| POST | `/api/v1/url` | Encurta uma URL | Sim |
| GET | `/{shortCode}` | Redireciona para a URL original | Não |
| DELETE | `/api/v1/url/{id}` | Remove uma URL | Sim |
| GET | `/api/v1/stats/{shortCode}` | Retorna analytics da URL | Sim |

---

## Como rodar localmente

### Pré-requisitos

- Docker e Docker Compose

### Passo a passo

**1. Clone o repositório**
```bash
git clone https://github.com/seu-usuario/encurtadorLink.git
cd encurtadorLink
```

### Rodar tudo no Docker

Na raiz do projeto (com Docker rodando):

```bash
docker compose up --build
```

Isso sobe PostgreSQL, Redis e a API Spring Boot. A API usa o perfil `docker` (`application-docker.yml`), apontando para os serviços `postgres` e `redis` da rede do Compose.

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

Só infra (Postgres + Redis), sem a app:

```bash
docker compose up postgres redis
```

Para rebuild após mudanças no código:

```bash
docker compose build --no-cache app
docker compose up
```

A aplicação sobe na porta `8080`. O Flyway cria as tabelas automaticamente na primeira inicialização.

---

## Documentação da API

Com a aplicação rodando, acesse o Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

Todos os endpoints estão documentados com exemplos de request e response.

### Autenticação no Swagger

1. Faça `POST /api/v1/auth/register` ou `POST /api/v1/auth/login`
2. Copie o token retornado
3. Clique em **Authorize** no topo do Swagger UI
4. Cole o token no formato: `Bearer seu-token-aqui`
5. Agora os endpoints autenticados estão liberados

---

## Exemplo de uso

**Registrar e autenticar**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "hector@email.com", "name": "Hector", "password": "senha123"}'
```

**Encurtar uma URL**
```bash
curl -X POST http://localhost:8080/api/v1/url \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer seu-token-aqui" \
  -d '{"originalUrl": "https://www.google.com.br", "expiresAt": "2026-12-31T23:59:59"}'
```

**Resposta:**
```json
{
  "originalUrl": "https://www.google.com.br",
  "shortCode": "UjFP4H",
  "shortUrl": "http://localhost:8080/UjFP4H",
  "createdAt": "2026-03-25T08:38:49",
  "expiresAt": "2026-12-31T23:59:59"
}
```

**Acessar o link curto**
```bash
curl -L http://localhost:8080/UjFP4H
# Redireciona para https://www.google.com.br
```

**Ver analytics**
```bash
curl http://localhost:8080/api/v1/stats/UjFP4H \
  -H "Authorization: Bearer seu-token-aqui"
```

---

## Estrutura do projeto

```
src/main/java/com/hector/encurtadorlink/
├── controller/       # Endpoints REST
├── service/          # Lógica de negócio
├── repository/       # Acesso ao banco
├── model/            # Entidades JPA
├── dto/
│   ├── request/      # Objetos de entrada
│   └── response/     # Objetos de saída
├── exception/        # Exceções customizadas e GlobalExceptionHandler
└── config/           # SecurityConfig, RedisConfig, AsyncConfig

src/main/resources/
├── application.yml
└── db/migration/     # Migrations Flyway (V1, V2, V3...)
```

---

## Variáveis de ambiente em produção

| Variável | Descrição |
|----------|-----------|
| `SPRING_DATASOURCE_URL` | URL de conexão do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `SPRING_REDIS_HOST` | Host do Redis |
| `SPRING_REDIS_PORT` | Porta do Redis |
| `JWT_SECRET` | Chave secreta para assinar os tokens |
| `JWT_EXPIRATION` | Tempo de expiração do token em ms |

---

## Autor

Feito por **Hector** — projeto desenvolvido para portfólio com foco em boas práticas Spring Boot.
