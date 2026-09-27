# Mercado BD 

Primeira versão funcional da aplicação do projeto de Banco de Dados, preparada para os requisitos atualizados da Etapa 03.

## Tecnologias

- Java 17
- Java Swing
- JDBC (`Connection`, `PreparedStatement`, `ResultSet` e SQL explícito)
- MySQL
- Maven
- MySQL Connector/J
- **Sem ORM**

## Requisitos atendidos

- CRUD completo de **CLIENTE**: inserir, visualizar, alterar e excluir.
- CRUD completo de **PRODUTO**: inserir, visualizar, alterar e excluir.
- CRUD completo de **FORNECEDOR**: inserir, visualizar, alterar e excluir.
- Todos os comandos de CRUD são disparados pela interface e enviados ao MySQL como SQL explícito.
- Visualização de dados por `JTable`.
- Dashboard com 4 indicadores.
- 4 visualizações gráficas: barras, setores, série temporal e histograma/distribuição por faixa de valor.
- **8 consultas SQL executáveis pela interface**.
- **As 8 consultas usam JOIN e subconsulta**, superando o mínimo de 6.
- Consultas documentadas em `sql/03_consultas.sql` e `docs/CONSULTAS_EXPLICADAS.md`.
- Conceitos nas consultas: `INNER JOIN`, `LEFT JOIN`, subconsulta simples, subconsulta derivada, subconsulta correlacionada, `NOT EXISTS`, `GROUP BY`, `HAVING`, `SUM`, `AVG`, `COUNT`, `COUNT DISTINCT`, `COALESCE` e `GROUP_CONCAT`.
- Não há Hibernate, JPA, Spring Data, MyBatis ou biblioteca de mapeamento objeto-relacional.

## 1. Preparar o banco

No MySQL Workbench, execute nesta ordem:

1. `sql/01_criacao_tabelas_mercado.sql`
2. `sql/02_insercao_dados_mercado.sql`

O banco criado é `mercado_db`.

O arquivo `sql/03_consultas.sql` é a documentação das oito consultas e pode ser executado depois para conferência.

## 2. Configurar usuário e senha

Abra:

`src/main/resources/application.properties`

Exemplo:

```properties
db.url=jdbc:mysql://localhost:3306/mercado_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Recife&useUnicode=true&characterEncoding=UTF-8
db.user=root
db.password=SUA_SENHA
```

Se o usuário `root` não tiver senha, deixe `db.password=` vazio.

## 3. Executar no IntelliJ IDEA

1. `File > Open` e selecione a pasta que contém `pom.xml`.
2. Configure o **Project SDK como JDK 17**.
3. Aguarde o Maven importar o projeto e baixar o MySQL Connector/J.
4. Se `application.properties` não for encontrado, clique com o botão direito em `src/main/resources` e use `Mark Directory as > Resources Root`.
5. Confira `application.properties`.
6. Execute `src/main/java/br/com/mercado/Main.java`.

## 4. Executar pelo terminal

Com Java 17 e Maven instalados:

```bash
mvn clean package
java -jar target/mercado-bd-1.0.0.jar
```

### Observação importante sobre exclusão de produtos

Produtos que já aparecem em `item_venda` não podem ser excluídos, pois a FK foi criada com `ON DELETE RESTRICT`. Isso protege o histórico das vendas. Para demonstrar `DELETE`, cadastre um produto novo pela interface e depois exclua esse produto.

## Arquivos importantes da entrega

- `sql/01_criacao_tabelas_mercado.sql` — criação do banco.
- `sql/02_insercao_dados_mercado.sql` — carga de dados.
- `sql/03_consultas.sql` — oito consultas exigidas na documentação.
- `docs/CONSULTAS_EXPLICADAS.md` — explicação das consultas e conceitos.
- `docs/MATRIZ_REQUISITOS.md` — relação requisito x implementação.
- `ENTREGA_CHECKLIST.txt` — checklist rápido antes de enviar.
