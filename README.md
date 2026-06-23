<h1 align="center">🍲 Raízes do Nordeste — Backend</h1>

<p align="center">
  API REST para gestão de pedidos, pagamentos e fidelidade de uma rede de comida nordestina.<br>
  <i>Projeto Multidisciplinar — UNINTER 2026</i>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black">
</p>

---

## 📋 Sobre o projeto

API REST que simula o backend completo de uma rede de restaurantes — desde o cadastro do cliente até a entrega do pedido, com programa de fidelidade. O sistema cobre o ciclo de vida de um pedido de ponta a ponta:

```
Cadastro → Login (JWT) → Pedido → Pagamento → Preparo → Entrega → Pontos de fidelidade
```

## ✨ Funcionalidades

- 🔐 **Autenticação JWT** com controle de acesso por papéis (`ADMIN` / `CLIENTE`)
- 🛡️ **Spring Security** protegendo as rotas
- ✅ **Consentimento LGPD** registrado no cadastro do usuário
- 🛒 **Gestão de pedidos** com múltiplos itens e ciclo de status
- 💳 **Pagamentos** (PIX) com simulação de falha para testes
- 📦 **Controle de estoque** por unidade (entrada e saída)
- ⭐ **Programa de fidelidade** — acúmulo e resgate de pontos
- 🏪 **Múltiplas unidades** (lojas)
- 📚 **Documentação interativa** via Swagger / OpenAPI

## 🏗️ Arquitetura

Organizado em **camadas** com separação clara de responsabilidades:

```
src/main/java/com/raizesdonordeste/backend/
├── api/              → Controllers REST (entrada HTTP)
├── application/      → Services (regras de negócio)
├── domain/           → Entidades do domínio (Pedido, Produto, Usuario…)
└── infrastructure/   → Repositórios JPA e segurança (JWT)
```

## 🛠️ Tecnologias

| Categoria | Stack |
|-----------|-------|
| Linguagem | Java 21 |
| Framework | Spring Boot (Web, Data JPA, Validation, Security) |
| Banco de dados | PostgreSQL |
| Autenticação | JWT (jjwt) |
| Documentação | springdoc-openapi (Swagger UI) |
| Build | Maven |

## 🔌 Principais endpoints

| Método | Rota | Descrição |
|:------:|------|-----------|
| `POST` | `/auth/cadastro` | Cadastra um novo usuário |
| `POST` | `/auth/login` | Autentica e retorna o token JWT |
| `POST` | `/pedidos` | Cria um pedido |
| `GET`  | `/pedidos/meus` | Lista os pedidos do usuário logado |
| `PATCH`| `/pedidos/{id}/status` | Atualiza o status do pedido |
| `POST` | `/pagamentos` | Processa o pagamento de um pedido |
| `POST` | `/produtos` | Cadastra um produto |
| `POST` | `/estoque/unidades/{u}/produtos/{p}/entrada` | Entrada de estoque |
| `GET`  | `/fidelidade/{usuarioId}` | Consulta saldo de pontos |
| `POST` | `/fidelidade/{usuarioId}/resgatar` | Resgata pontos |

> 🔎 A lista completa fica em **`/swagger-ui/index.html`** com a aplicação rodando.

### Ciclo de status do pedido
```
AGUARDANDO_PAGAMENTO → PAGO → EM_PREPARO → PRONTO → ENTREGUE
```

## 🚀 Como rodar

### Pré-requisitos
- Java 21 · Maven · PostgreSQL

### 1. Crie o banco
```sql
CREATE DATABASE raizes_db;
```

### 2. Configure as credenciais
Em `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/raizes_db
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```
> As tabelas são criadas automaticamente na primeira execução.

### 3. Execute
```bash
mvn spring-boot:run
```
A API sobe em **http://localhost:8080** e o Swagger em **http://localhost:8080/swagger-ui/index.html**.

## 🧪 Testando rapidamente

<details>
<summary><b>Fluxo completo passo a passo (clique para expandir)</b></summary>

<br>

**1. Cadastrar usuário** — `POST /auth/cadastro`
```json
{
  "nome": "Victor",
  "email": "admin@raizesdonordeste.com",
  "senha": "Admin@123",
  "role": "ADMIN",
  "consentimentoLgpd": true
}
```

**2. Login** — `POST /auth/login` → copie o `accessToken` retornado e use no header:
```
Authorization: Bearer SEU_TOKEN
```

**3. Criar pedido** — `POST /pedidos`
```json
{
  "usuarioId": 1,
  "unidadeId": 1,
  "canalPedido": "APP",
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 3, "quantidade": 1 }
  ]
}
```

**4. Pagar** — `POST /pagamentos` (`"simularFalha": true` testa recusa)
```json
{ "pedidoId": 1, "formaPagamento": "PIX", "simularFalha": false }
```

**5. Avançar status** — `PATCH /pedidos/1/status`
```json
{ "status": "EM_PREPARO" }
```

**6. Pontos** — `GET /fidelidade/1` (1 ponto por real gasto) · resgate com `POST /fidelidade/1/resgatar`

</details>

> 📮 A coleção **`raizes-do-nordeste.postman_collection.json`** (na raiz) tem todos os cenários prontos — é só importar no Postman.

---

<p align="center">
  Desenvolvido por <a href="https://github.com/victor1308code">Victor Oliveira</a>
</p>
