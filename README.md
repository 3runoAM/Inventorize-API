# Inventorize API 📦

## Sobre o Projeto 🚀
O Inventorize API é um projeto desenvolvido como parte da disciplina de Projeto de Bloco: Desenvolvimento Back-end 
2025.1. Esta API oferece uma solução simples para gerenciamento de inventário, e busca demonstrar a aplicação prática de 
conceitos modernos de desenvolvimento de software para a WEB.

### Funcionalidades principais:
- Cadastro, consulta e monitoramento de produtos em estoque
- Identificação e notificação de itens com nível crítico de estoque
- Autenticação segura com JWT
- Gerenciamento de múltiplos inventários por usuário
- Controle granular de produtos e itens

## Tecnologias Utilizadas 🛠️
- **Spring Boot 3**: Framework principal
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Persistência de dados
- **Spring Web**: Criação de APIs REST
- **Spring Mail**: Envio de e-mails para notificações
- **Spring Validation**: Validação de dados
- **Spring Test**: Testes unitários
- **SpringDoc OpenAPI 3**: Documentação Interativa da API
- **H2 Database**: Banco de dados em memória para desenvolvimento.
- **PostgreSQL**: Banco de dados relacional para entrega final
- **Lombok**: Redução de boilerplate
- **Hateoas**: Hypermedia para APIs REST
- **JWT**: Autenticação stateless

## Como Executar o Projeto 🏃‍♂️

### Pré-requisitos 🔧
- Java 17 ou superior
- Maven 3.6 ou superior
- Banco de dados PostgreSQL (opcional)
- IDE de sua preferência (IntelliJ, Eclipse, etc.)

### Passos para Execução 📝
1. Clone o repositório:
   ```bash
   git clone https://github.com/3runoAM/Inventorize-API
   ```

2. Configure as variáveis de ambiente utilizadas:
 - JWT_SECRET: Chave secreta para assinatura do token JWT.
 - EMAIL_USERNAME: Endereço de e-mail do remetente para envio notificações.
 - EMAIL_PASSWORD: Senha do e-mail do remetente para envio notificações.
 - DB_URL: URL de conexão com o banco de dados PostgreSQL, apenas em produção.
 - USERNAME_DB: Nome de usuário do banco de dados, apenas em produção.
 - PASSWORD_DB: Senha do banco de dados, apenas em produção.

3. Navegue até o diretório do projeto:
   ```bash
   cd Inventorize-API
   ```
   
4. Execute o comando Maven para iniciar a aplicação:
   ```bash
    mvn spring-boot:run
    ```
   
## Endpoint de Documentação Interativa 📚
A documentação interativa está disponível via Swagger UI quando a aplicação está em execução e pode ser utilizada para explorar os endpoints da API e interagir com o banco de dados de forma mais simples.

1. Inicie a aplicação
2. Acesse o endpoint: [/inventorize/v1/swagger-ui/index.html]
3. Explore os endpoints disponíveis
4. Não esqueça de utilizar o token JWT para autenticação.

## Lista de Endpoints 📑
| VERBO    | Endpoint                               | Descrição                                             |
|----------|----------------------------------------|-------------------------------------------------------|
| POST     | /inventorize/v1/auth/register          | Registra um novo usuário                              |
| POST     | /inventorize/v1/auth/login             | Autentica um usuário, retornando um token             |
| -------  | -------------------------------------- | ----------------------------------------------------- |
| GET      | /inventorize/v1/products               | Lista todos os produtos                               |
| GET      | /inventorize/v1/products/{id}          | Consulta um produto específico                        |
| POST     | /inventorize/v1/products               | Cria um novo produto                                  |
| PUT      | /inventorize/v1/products/{id}          | Atualiza um produto existente                         |
| PATCH    | /inventorize/v1/products/{id}          | Atualiza parcialmente um produto                      |
| DELETE   | /inventorize/v1/products/{id}          | Remove um produto                                     |
| -------- | -------------------------------------  | ---------------------------------------------------   |
| GET      | /inventorize/v1/inventories            | Lista todos os estoques                               |
| GET      | /inventorize/v1/inventories/{id}       | Consulta um estoques específico                       |
| POST     | /inventorize/v1/inventories            | Cria um novo estoque                                  |
| PUT      | /inventorize/v1/inventories/{id}       | Atualiza um estoque existente                         |
| PATCH    | /inventorize/v1/inventories/{id}       | Atualiza parcialmente um estoque                      |
| DELETE   | /inventorize/v1/inventories/{id}       | Remove um estoque                                     |
| -------- | -------------------------------------- | ---------------------------------------------------   |
| GET      | /inventorize/v1/items                  | Lista todos os itens                                  |
| GET      | /inventorize/v1/items/{id}             | Consulta um item específico                           |
| GET      | /inventorize/v1/items/low-stock        | Consulta todos os itens abaixo do limite              |
| GET      | /inventorize/v1/items/inventory/{id}   | Consulta todos os itens de um estoque                 |
| POST     | /inventorize/v1/items                  | Cria um novo item                                     |
| PUT      | /inventorize/v1/items/{id}             | Atualiza um item existente                            |
| PATCH    | /inventorize/v1/items/{id}             | Atualiza parcialmente um item                         |
| PATCH    | /inventorize/v1/items/{id}/adjust      | Ajusta a quantidade atual de um item                  |
| DELETE   | /inventorize/v1/items/{id}             | Remove um item                                        |
| -------- | -----------------------------------    | ---------------------------------------------------   |

### Diagrama de Classes 📊

![Diagrama de Classes](src/main/resources/static/class-diagram.png)

---
Desenvolvido por Bruno Martins (@3runoAM) ☕