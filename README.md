# Credit Card Transaction Authorization

## Descrição

Este projeto é uma aplicação de autorização de transações de cartão de crédito. A aplicação gerencia contas e transações, garantindo que as transações sejam autorizadas com base em saldos de diferentes tipos (alimentação, refeição, dinheiro).

## Tecnologias Utilizadas

- **Java 11**: Linguagem de programação principal.
- **Spring Boot**: Framework utilizado para criação da aplicação.
    - **Spring Boot Starter Data JPA**: Para persistência de dados.
    - **Spring Boot Starter Web**: Para criação de APIs RESTful.
    - **Spring Boot Starter Log4j2**: Para logging.
- **H2 Database**: Banco de dados em memória utilizado para desenvolvimento e testes.
- **JUnit 5**: Framework de testes.
- **Mockito**: Framework de mocking para testes.
- **Gradle**: Ferramenta de automação de build.

## Estrutura do Projeto

- **src/main/java**: Código fonte da aplicação.
- **src/test/java**: Código fonte dos testes.
- **src/main/resources**: Arquivos de configuração e recursos estáticos.
- **src/test/resources**: Arquivos de configuração de teste.

## Funcionalidades Implementadas

- **Autorização de Transações**: Autoriza transações baseadas no saldo disponível da conta.
- **Gerenciamento de Contas**: Criação e consulta de contas.
- **Logging**: Implementação de logs detalhados nas camadas de serviço e controlador.

## Executando o Projeto Localmente

### Pré-requisitos

- Java 11 ou superior instalado.
- Gradle instalado.

### Passos para Executar

1. **Clone o Repositório**

   ```sh
   git clone [https://github.com/DanielSBaumann/DanielSBaumann-Credit-Card-Transaction-Authorization](https://github.com/DanielSBaumann/DanielSBaumann-Credit-Card-Transaction-Authorization.git)
   cd Credit-Card-Transaction-Authorization
   ```

2. **Build do Projeto**

   ```sh
   ./gradlew build
   ```

3. **Executar a Aplicação**

   ```sh
   ./gradlew bootRun
   ```

   A aplicação estará disponível em http://localhost:8080.

### Executando os Testes
Para rodar todos os testes, execute:

```
./gradlew test
```

## Estrutura de Logs

A aplicação está configurada para utilizar Log4j2. Os logs são gerados nas camadas de serviço e controlador para acompanhar o fluxo de autorização de transações.

### Exemplos de Logs

- **INFO**: Informações gerais sobre o fluxo da aplicação.
- **DEBUG**: Informações detalhadas para debugging.
- **WARN**: Avisos sobre possíveis problemas.
- **ERROR**: Erros que ocorrem durante a execução.

Os arquivos de configuração de log podem ser encontrados em `src/main/resources/logback-spring.xml`.

## L4. Questão aberta: Transações Simultâneas

Para gerenciar transações simultâneas, optamos por utilizar um bloqueio de escrita pessimista (PESSIMISTIC_WRITE). Este método garante que apenas uma transação por conta seja processada de cada vez, mantendo a simplicidade e a integridade dos dados.
Exemplo de Implementação:
```
package com.example.creditcardtransactionauthorization.repository;

import com.example.creditcardtransactionauthorization.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :accountId")
    Account findAccountForUpdate(@Param("accountId") String accountId);
}
```
## Vantagens do `PESSIMISTIC_WRITE`

- **Simplicidade**: Implementação direta e fácil de entender.
- **Consistência**: Garante que apenas uma transação por conta seja processada por vez.
- **Segurança**: Evita conflitos e inconsistências de dados sem necessidade de lógica complexa.

## Desvantagens do `PESSIMISTIC_WRITE`

- **Desempenho**: Pode reduzir a taxa de processamento de transações devido a bloqueios longos.
- **Escalabilidade**: Menos eficiente em cenários com alta concorrência de transações.
- **Contenção**: Pode resultar em contenção de recursos, com múltiplas transações esperando pelo mesmo recurso.

## Consideração de Mensageria

A mensageria com RabbitMQ foi considerada para gerenciar transações simultâneas, devido aos seus benefícios em termos de escalabilidade e desacoplamento de serviços. Utilizar uma fila de mensagens poderia melhorar a capacidade de processamento e a resiliência do sistema. No entanto, a mensageria implica em comunicação assíncrona, onde as transações são colocadas em uma fila para serem processadas posteriormente. Isso resultaria em uma resposta inicial de "criado" ou "em processamento", seguida de uma notificação após a conclusão do processamento, o que não atende à necessidade de transações síncronas, como proposto no desafio.

## Conclusão

Para atender aos requisitos de transações síncronas e garantir que apenas uma transação por conta seja processada por vez, utilizamos o bloqueio pessimista (`PESSIMISTIC_WRITE`). Esta abordagem é simples, eficaz e evita a complexidade adicional da comunicação assíncrona. No entanto, para cenários que exigem alta escalabilidade e resiliência, soluções baseadas em mensageria, como RabbitMQ, seriam mais adequadas, apesar de introduzirem comunicação assíncrona.


## Desenvolvido por

Este projeto foi desenvolvido por [Daniel Baumann](https://github.com/DanielSBaumann).

## Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE](https://opensource.org/license/mit) para mais detalhes.
