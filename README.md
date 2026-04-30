# Crediário — Sistema de Crédito Parcelado

Sistema de crediário desenvolvido em Java para estudo de desenvolvimento de software profissional. O projeto simula um sistema real de crédito parcelado com controle de clientes, compras, parcelas e pagamentos.

---

## Fases do projeto

| Fase | Descrição | Status |
|------|-----------|--------|
| Fase 1 | Java puro no terminal, dados em memória | ✅ Concluída |
| Fase 2 | Persistência com PostgreSQL via JDBC | ✅ Concluída |
| Fase 3 | API REST com Spring Boot e JPA | 🚧 Em desenvolvimento |

---

## Tecnologias utilizadas

- **Java 17**
- **PostgreSQL 17**
- **JDBC** — acesso ao banco de dados sem ORM
- **Maven** — gerenciamento de dependências
- **JUnit 5** — testes unitários
- **Mockito** — mocks para testes
- **Git / GitHub** — controle de versão

---

## Funcionalidades

### Perfis de usuário
- **Admin** — gerencia usuários, configura o sistema e acessa relatórios
- **Caixa** — cadastra clientes, registra compras e recebe pagamentos
- **Vendedor** — cadastra clientes e consulta saldo
- **Cliente** — visualiza suas parcelas, compras e saldo disponível

### Regras de negócio
- Limite de crédito padrão configurável pelo administrador
- Cliente com parcela atrasada não pode realizar nova compra
- Valor mínimo de compra configurável
- Máximo de 6 parcelas por compra
- Cálculo automático de multa (2%) e juros (0,1% ao dia) para parcelas em atraso
- Sistema de comissão: R$4,00 por cliente cadastrado e R$10,00 se o cliente fizer a primeira compra em até 24 horas

### Gestão financeira
- Geração automática de parcelas ao registrar uma compra
- Atualização automática de status de parcelas vencidas
- Registro de pagamentos com detalhamento de multa, juros e valor pago
- Extrato de pagamentos por cliente

---

## Arquitetura

O projeto segue uma arquitetura em camadas com separação clara de responsabilidades:

```
src/main/java/
├── config/         → AppContext (injeção de dependências) e DatabaseConnection
├── database/       → Conexão com o PostgreSQL
├── enums/          → InstallmentStatus, PaymentMethod, UserType
├── exception/      → BusinessException
├── model/          → Entidades do domínio
├── repository/     → Interfaces e implementações (em memória e JDBC)
│   ├── customer/
│   ├── installment/
│   ├── payment/
│   ├── purchase/
│   └── user/
├── service/        → Regras de negócio (interfaces e implementações)
├── ui/             → Menus do terminal
└── util/           → ValidationUtils
```

### Padrões utilizados
- **Repository Pattern** — separação entre lógica de negócio e acesso a dados
- **Singleton** — AppContext e SystemConfig
- **Dependency Injection** — manual via construtores
- **Interface + Implementation** — permite trocar a implementação sem alterar o restante do código

---

## Banco de dados

### Pré-requisitos
- PostgreSQL 17 instalado
- Banco de dados `crediario` criado

### Estrutura das tabelas

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    commission DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    phone VARCHAR(20),
    profession VARCHAR(100),
    birth_date DATE,
    credit_limit DECIMAL(10,2) NOT NULL,
    registered_by BIGINT REFERENCES users(id),
    registration_date TIMESTAMP NOT NULL
);

CREATE TABLE purchases (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    value DECIMAL(10,2) NOT NULL,
    date DATE NOT NULL,
    qty_installments INT NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE installments (
    id BIGSERIAL PRIMARY KEY,
    purchase_id BIGINT REFERENCES purchases(id),
    value DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    installment_id BIGINT REFERENCES installments(id),
    date TIMESTAMP NOT NULL,
    original_amount DECIMAL(10,2) NOT NULL,
    fine_amount DECIMAL(10,2) NOT NULL,
    interest_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL
);
```

---

## Como executar

### 1. Clone o repositório
```bash
git clone https://github.com/Wilsouuza/Crediario.git
cd Crediario
```

### 2. Configure o banco de dados
Edite o arquivo `src/main/java/database/DatabaseConnection.java` com suas credenciais:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/crediario";
private static final String USER = "postgres";
private static final String PASSWORD = "sua_senha";
```

### 3. Execute o script SQL
Execute o script de criação das tabelas no pgAdmin ou psql.

### 4. Execute o projeto
Rode a classe `Main.java`. O sistema cria automaticamente um usuário admin padrão:
- **Login:** `admin`
- **Senha:** `admin123`

---

## Testes

O projeto conta com testes unitários cobrindo as principais regras de negócio:

```bash
mvn test
```

### Cobertura de testes
- `CustomerServiceTest` — 14 testes: validações, criação, busca, limite e comissão
- `PaymentServiceTest` — cálculo de multa e juros, status da parcela
- `PurchaseServiceTest` — regras de negócio da compra, comissão de bônus
- `InstallmentServiceTest` — geração de parcelas, datas de vencimento, atualização de status

---

## Configurações do sistema

O administrador pode alterar as seguintes configurações pelo menu:

| Configuração | Valor padrão |
|---|---|
| Taxa de juros diária | 0,1% |
| Multa por atraso | 2% |
| Máximo de parcelas | 6 |
| Valor mínimo de compra | R$ 10,00 |
| Limite de crédito padrão | R$ 500,00 |

---

## Autor

Wilson — estudante de Análise e Desenvolvimento de Sistemas, 3º semestre.

Técnico em Informática pelo IFBA.

[GitHub](https://github.com/Wilsouuza)
