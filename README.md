# ♻️ ESG Coleta de Resíduos — Sistema de Gestão com Testes Automatizados BDD

> Projeto Java Spring Boot com foco nos pilares **ESG** (Environmental, Social & Governance),  
> implementando automação de testes com **Cucumber/Gherkin (BDD)** e **REST Assured**.

---

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Tema ESG](#tema-esg)
- [Arquitetura da API](#arquitetura-da-api)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Testes Automatizados](#testes-automatizados)
- [Cenários BDD (Gherkin)](#cenários-bdd-gherkin)
- [JSON Schemas de Contrato](#json-schemas-de-contrato)
- [Pipeline CI/CD](#pipeline-cicd)
- [Estrutura do Projeto](#estrutura-do-projeto)

---

## 📌 Sobre o Projeto

Sistema de **Gestão de Coleta Inteligente de Resíduos**, alinhado ao pilar **E (Ambiental)** do ESG.  
Permite cadastrar pontos de coleta seletiva, registrar coletas realizadas e gerar relatórios de impacto ambiental (CO₂ evitado).

---

## 🌱 Tema ESG

| Pilar | Implementação |
|-------|--------------|
| **E - Ambiental** | Rastreamento de resíduos coletados e cálculo de CO₂ evitado |
| **S - Social** | Sistema acessível para diferentes perfis de operadores |
| **G - Governança** | Relatórios auditáveis, rastreabilidade de coletas e validação de dados |

---

## 🗂 Arquitetura da API

### Endpoints Disponíveis

#### Pontos de Coleta
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/pontos-coleta` | Cadastrar novo ponto de coleta |
| `GET` | `/api/pontos-coleta` | Listar todos (com filtro `?apenasAtivos=true`) |
| `GET` | `/api/pontos-coleta/{id}` | Buscar por ID |
| `PUT` | `/api/pontos-coleta/{id}` | Atualizar ponto |
| `PATCH` | `/api/pontos-coleta/{id}/desativar` | Desativar ponto |
| `DELETE` | `/api/pontos-coleta/{id}` | Deletar ponto |

#### Coletas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/coletas` | Registrar nova coleta |
| `GET` | `/api/coletas` | Listar todas (com filtro `?pontoColetaId=N`) |
| `GET` | `/api/coletas/{id}` | Buscar por ID |
| `PATCH` | `/api/coletas/{id}/status` | Atualizar status |
| `DELETE` | `/api/coletas/{id}` | Deletar coleta |

#### Relatório ESG
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/api/relatorio/esg` | Relatório consolidado de impacto ESG |

### Tipos de Resíduos
`PLASTICO`, `VIDRO`, `METAL`, `PAPEL`, `ORGANICO`, `ELETRONICO`

### Status de Coleta
`AGENDADA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`

---

## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework web |
| H2 Database | — | Banco em memória (dev/test) |
| JPA / Hibernate | — | ORM |
| Lombok | — | Redução de boilerplate |
| **Cucumber** | 7.15.0 | BDD / Gherkin |
| **REST Assured** | 5.4.0 | Testes de API |
| **JSON Schema Validator** | 5.4.0 | Testes de contrato |
| JUnit 5 | — | Test runner |
| Maven | 3.9+ | Build tool |
| Docker | — | Containerização |
| GitHub Actions | — | CI/CD |

---

## ✅ Pré-requisitos

- **Java 17+** instalado  
- **Maven 3.9+** instalado  
- **Docker** (opcional, para containerização)
- **Git**

Verificar instalações:
```bash
java -version
mvn -version
docker --version
```

---

## 🚀 Como Executar

### 1. Clonar o Repositório
```bash
git clone https://github.com/Felibread/esg-coleta-residuos.git
cd esg-coleta-residuos
```

### 2. Executar a Aplicação (local)
```bash
mvn spring-boot:run
```
A aplicação sobe em: `http://localhost:8080`  
Console H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:esgdb`)

### 3. Executar com Docker
```bash
# Build da imagem
docker build -t esg-coleta-residuos .

# Subir container
docker run -p 8080:8080 esg-coleta-residuos

# Ou via docker-compose
docker-compose up
```

---

## 🧪 Testes Automatizados

### Executar TODOS os testes
```bash
mvn test
```

### Executar apenas testes BDD (Cucumber)
```bash
mvn test -Dtest=CucumberRunner
```

### Executar apenas testes de API (REST Assured)
```bash
mvn test -Dtest="PontoColetaApiTest,ColetaApiTest,RelatorioApiTest"
```

### Executar teste específico
```bash
mvn test -Dtest=PontoColetaApiTest#deveCriarPontoDeColetaComSucesso
```

### Relatórios gerados após execução
```
target/
├── surefire-reports/          # Relatórios XML JUnit
└── cucumber-reports/
    ├── report.html            # Relatório HTML Cucumber (abrir no browser)
    └── report.json            # Relatório JSON Cucumber
```

Para abrir o relatório HTML Cucumber:
```bash
open target/cucumber-reports/report.html
# ou no Linux:
xdg-open target/cucumber-reports/report.html
```

---

## 🥒 Cenários BDD (Gherkin)

### Feature: Pontos de Coleta (`ponto_coleta.feature`)

| # | Cenário | Tipo |
|---|---------|------|
| 1 | Cadastrar um novo ponto de coleta com sucesso | ✅ Positivo |
| 2 | Consultar um ponto de coleta existente por ID | ✅ Positivo |
| 3 | Tentar cadastrar ponto com dados inválidos | ❌ Negativo |
| 4 | Consultar ponto de coleta inexistente | ❌ Negativo |
| 5 | Listar todos os pontos de coleta ativos | ✅ Positivo |

### Feature: Coletas (`coleta.feature`)

| # | Cenário | Tipo |
|---|---------|------|
| 1 | Registrar uma coleta com sucesso | ✅ Positivo |
| 2 | Tentar registrar coleta com peso negativo | ❌ Negativo |
| 3 | Atualizar o status de uma coleta para CONCLUIDA | ✅ Positivo |
| 4 | Tentar registrar coleta em ponto inexistente | ❌ Negativo |

### Feature: Relatório ESG (`relatorio_esg.feature`)

| # | Cenário | Tipo |
|---|---------|------|
| 1 | Gerar relatório ESG com dados de coletas concluídas | ✅ Positivo |
| 2 | Gerar relatório ESG sem coletas concluídas | ✅ Positivo |

### Exemplo de Cenário Gherkin
```gherkin
Cenário: Cadastrar um novo ponto de coleta com sucesso
  Dado que tenho os dados de um novo ponto de coleta:
    | nome         | Ecoponto Jardim América |
    | tipoResiduo  | PLASTICO                |
    | endereco     | Rua das Flores, 100     |
    | capacidadeKg | 500.0                   |
  Quando envio uma requisição POST para "/api/pontos-coleta"
  Então o status da resposta deve ser 201
  E o corpo da resposta deve conter o campo "id" não nulo
  E o corpo da resposta deve conter "nome" com valor "Ecoponto Jardim América"
```

---

## 📐 JSON Schemas de Contrato

Os schemas estão em `src/test/resources/schemas/`:

| Schema | Valida |
|--------|--------|
| `ponto-coleta-schema.json` | Resposta de `/api/pontos-coleta` |
| `coleta-schema.json` | Resposta de `/api/coletas` |
| `relatorio-esg-schema.json` | Resposta de `/api/relatorio/esg` |

Os testes de contrato garantem que a API nunca quebre o formato esperado pelos consumidores.

---

## ⚙️ Pipeline CI/CD (GitHub Actions)

O arquivo `.github/workflows/ci.yml` executa automaticamente:

1. **Compilação** do projeto
2. **Todos os testes** (BDD + API)
3. **Publicação do relatório** JUnit no GitHub
4. **Upload dos relatórios** Cucumber como artefato
5. **Build da imagem Docker**

Para configurar no seu repositório GitHub:
1. Faça push para o branch `main` ou `develop`
2. Acesse **Actions** no repositório
3. Acompanhe a execução do pipeline

---

## 📁 Estrutura do Projeto

```
esg-coleta-residuos/
├── .github/workflows/
│   └── ci.yml                          # Pipeline GitHub Actions
├── src/
│   ├── main/java/com/esg/coleta/
│   │   ├── EsgColetaApplication.java
│   │   ├── controller/
│   │   │   ├── PontoColetaController.java
│   │   │   ├── ColetaController.java
│   │   │   └── RelatorioController.java
│   │   ├── service/
│   │   │   ├── PontoColetaService.java
│   │   │   ├── ColetaService.java
│   │   │   └── RelatorioService.java
│   │   ├── repository/
│   │   │   ├── PontoColetaRepository.java
│   │   │   └── ColetaRepository.java
│   │   ├── model/
│   │   │   ├── PontoColeta.java
│   │   │   ├── Coleta.java
│   │   │   ├── TipoResiduo.java (enum)
│   │   │   └── StatusColeta.java (enum)
│   │   ├── dto/
│   │   │   ├── PontoColetaRequestDTO.java
│   │   │   ├── PontoColetaResponseDTO.java
│   │   │   ├── ColetaRequestDTO.java
│   │   │   ├── ColetaResponseDTO.java
│   │   │   ├── RelatorioESGDTO.java
│   │   │   └── ErrorResponseDTO.java
│   │   └── exception/
│   │       ├── RecursoNaoEncontradoException.java
│   │       └── GlobalExceptionHandler.java
│   └── test/
│       ├── java/com/esg/coleta/
│       │   ├── bdd/
│       │   │   ├── CucumberRunner.java      # Runner BDD
│       │   │   ├── CucumberSpringConfiguration.java
│       │   │   └── steps/
│       │   │       ├── PontoColetaSteps.java
│       │   │       ├── ColetaSteps.java
│       │   │       └── RelatorioSteps.java
│       │   └── api/
│       │       ├── PontoColetaApiTest.java  # 13 testes
│       │       ├── ColetaApiTest.java       # 12 testes
│       │       └── RelatorioApiTest.java    # 3 testes
│       └── resources/
│           ├── features/
│           │   ├── ponto_coleta.feature     # 5 cenários
│           │   ├── coleta.feature           # 4 cenários
│           │   └── relatorio_esg.feature    # 2 cenários
│           ├── schemas/
│           │   ├── ponto-coleta-schema.json
│           │   ├── coleta-schema.json
│           │   └── relatorio-esg-schema.json
│           ├── application-test.properties
│           └── cucumber.properties
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## 🌍 Contribuição ESG

Este projeto demonstra como a automação de testes contribui diretamente para os pilares ESG:

- **Ambiental**: Testes garantem a rastreabilidade e integridade dos dados de coleta de resíduos  
- **Social**: Validações asseguram acessibilidade e consistência para todos os usuários do sistema  
- **Governança**: Testes de contrato (JSON Schema) garantem conformidade regulatória e estabilidade das integrações

---

*Desenvolvido como projeto acadêmico — Fase de Testes Automatizados — ESG & Sustentabilidade*
#   e s g - c o l e t a - r e s i d u o s 
 
 