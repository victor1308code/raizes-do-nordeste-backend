Raizes do Nordeste - Backend
Projeto Multidisciplinar UNINTER 2026

==============================================
O QUE PRECISA TER INSTALADO
==============================================

- Java 17
- Maven
- PostgreSQL

==============================================
CONFIGURAR O BANCO DE DADOS
==============================================

1. Abra o pgAdmin e crie o banco:

   CREATE DATABASE raizes_db;

2. Abra o arquivo:
   src/main/resources/application.properties

   e coloque suas credenciais do postgres:

   spring.datasource.url=jdbc:postgresql://localhost:5432/raizes_db
   spring.datasource.username=postgres
   spring.datasource.password=SUA_SENHA

As tabelas sao criadas automaticamente quando a aplicacao inicia.

==============================================
COMO RODAR
==============================================

1. Baixe o projeto do GitHub

2. Abra o terminal na pasta do projeto e rode:

   mvn spring-boot:run

3. A API vai estar rodando em:
   http://localhost:8080

==============================================
SWAGGER (DOCUMENTACAO DA API)
==============================================

Com a aplicacao rodando, acesse no navegador:

   http://localhost:8080/swagger-ui/index.html

La voce ve todos os endpoints disponiveis.

==============================================
COMO TESTAR (PASSO A PASSO)
==============================================

Passo 1 - Cadastrar usuario
POST http://localhost:8080/auth/cadastro

{
    "nome": "Victor",
    "email": "admin@raizesdonordeste.com",
    "senha": "Admin@123",
    "role": "ADMIN",
    "consentimentoLgpd": true
}

---

Passo 2 - Fazer login
POST http://localhost:8080/auth/login

{
    "email": "admin@raizesdonordeste.com",
    "senha": "Admin@123"
}

Vai retornar um accessToken. Copie ele e use em todas
as proximas requisicoes no header:
Authorization: Bearer SEU_TOKEN

---

Passo 3 - Criar pedido
POST http://localhost:8080/pedidos

{
    "usuarioId": 1,
    "unidadeId": 1,
    "canalPedido": "APP",
    "itens": [
        { "produtoId": 1, "quantidade": 2 },
        { "produtoId": 3, "quantidade": 1 }
    ]
}

---

Passo 4 - Processar pagamento
POST http://localhost:8080/pagamentos

{
    "pedidoId": 1,
    "formaPagamento": "PIX",
    "simularFalha": false
}

Para simular pagamento recusado, mude simularFalha para true.

---

Passo 5 - Atualizar status do pedido
PATCH http://localhost:8080/pedidos/1/status

{
    "status": "EM_PREPARO"
}

Ordem dos status:
AGUARDANDO_PAGAMENTO -> PAGO -> EM_PREPARO -> PRONTO -> ENTREGUE

---

Passo 6 - Consultar pontos de fidelidade
GET http://localhost:8080/fidelidade/1

Retorna o saldo de pontos do usuario. Os pontos sao acumulados
automaticamente a cada pedido (1 ponto por real gasto).

---

Passo 7 - Resgatar pontos
POST http://localhost:8080/fidelidade/1/resgatar

Desconta 100 pontos do saldo. Retorna erro se nao houver
pontos suficientes.

==============================================
COLECAO DE TESTES
==============================================

O arquivo raizes-do-nordeste.postman_collection.json
esta na raiz do projeto com os cenarios de teste.

Importe ele no Postman para rodar todos os testes.
