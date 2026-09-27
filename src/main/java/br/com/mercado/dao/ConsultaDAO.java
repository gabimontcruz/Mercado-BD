package br.com.mercado.dao;

import br.com.mercado.config.ConnectionFactory;
import br.com.mercado.model.TableData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    public enum Consulta {
        PRODUTOS_ACIMA_MEDIA(
                "1 - Produtos acima da média de vendas",
                "JOINs entre produto, categoria e item_venda + subconsulta para comparar cada produto com a média de quantidade vendida por produto.",
                """
                SELECT
                    p.codigo_produto,
                    p.nome AS produto,
                    p.marca,
                    c.nome AS categoria,
                    SUM(iv.quantidade) AS quantidade_vendida,
                    SUM(iv.subtotal) AS valor_vendido
                FROM produto p
                INNER JOIN categoria c ON c.codigo_categoria = p.codigo_categoria
                INNER JOIN item_venda iv ON iv.codigo_produto = p.codigo_produto
                GROUP BY p.codigo_produto, p.nome, p.marca, c.nome
                HAVING SUM(iv.quantidade) > (
                    SELECT AVG(media_produto.quantidade_total)
                    FROM (
                        SELECT SUM(iv2.quantidade) AS quantidade_total
                        FROM item_venda iv2
                        INNER JOIN produto p2 ON p2.codigo_produto = iv2.codigo_produto
                        GROUP BY p2.codigo_produto
                    ) AS media_produto
                )
                ORDER BY quantidade_vendida DESC, valor_vendido DESC
                """),

        CLIENTES_ACIMA_MEDIA(
                "2 - Clientes com gasto acima da média",
                "JOIN entre cliente e venda + subconsulta derivada para localizar clientes cujo gasto total supera a média de gasto dos clientes compradores.",
                """
                SELECT
                    c.cpf,
                    c.nome AS cliente,
                    COUNT(v.numero_venda) AS quantidade_compras,
                    SUM(v.valor_total) AS total_gasto,
                    AVG(v.valor_total) AS ticket_medio
                FROM cliente c
                INNER JOIN venda v ON v.cpf_cliente = c.cpf
                GROUP BY c.cpf, c.nome
                HAVING SUM(v.valor_total) > (
                    SELECT AVG(t.total_cliente)
                    FROM (
                        SELECT v2.cpf_cliente, SUM(v2.valor_total) AS total_cliente
                        FROM venda v2
                        INNER JOIN cliente c2 ON c2.cpf = v2.cpf_cliente
                        WHERE v2.cpf_cliente IS NOT NULL
                        GROUP BY v2.cpf_cliente
                    ) AS t
                )
                ORDER BY total_gasto DESC
                """),

        FUNCIONARIOS_ACIMA_MEDIA(
                "3 - Funcionários acima da média de faturamento",
                "JOIN entre funcionário e venda + subconsulta para comparar o faturamento de cada funcionário com a média dos funcionários que registraram vendas.",
                """
                SELECT
                    f.matricula,
                    f.nome AS funcionario,
                    COUNT(v.numero_venda) AS quantidade_vendas,
                    SUM(v.valor_total) AS total_vendido,
                    AVG(v.valor_total) AS ticket_medio
                FROM funcionario f
                INNER JOIN venda v ON v.matricula_funcionario = f.matricula
                GROUP BY f.matricula, f.nome
                HAVING SUM(v.valor_total) > (
                    SELECT AVG(t.total_funcionario)
                    FROM (
                        SELECT v2.matricula_funcionario, SUM(v2.valor_total) AS total_funcionario
                        FROM venda v2
                        INNER JOIN funcionario f2 ON f2.matricula = v2.matricula_funcionario
                        GROUP BY v2.matricula_funcionario
                    ) AS t
                )
                ORDER BY total_vendido DESC
                """),

        CATEGORIAS_ACIMA_MEDIA(
                "4 - Categorias com faturamento acima da média",
                "Múltiplos JOINs entre categoria, produto, item_venda e venda + subconsulta para comparar o faturamento de cada categoria com a média das categorias vendidas.",
                """
                SELECT
                    c.codigo_categoria,
                    c.nome AS categoria,
                    COUNT(DISTINCT v.numero_venda) AS vendas_com_produtos_da_categoria,
                    SUM(iv.quantidade) AS quantidade_itens,
                    SUM(iv.subtotal) AS faturamento_categoria
                FROM categoria c
                INNER JOIN produto p ON p.codigo_categoria = c.codigo_categoria
                INNER JOIN item_venda iv ON iv.codigo_produto = p.codigo_produto
                INNER JOIN venda v ON v.numero_venda = iv.numero_venda
                GROUP BY c.codigo_categoria, c.nome
                HAVING SUM(iv.subtotal) > (
                    SELECT AVG(t.total_categoria)
                    FROM (
                        SELECT p2.codigo_categoria, SUM(iv2.subtotal) AS total_categoria
                        FROM produto p2
                        INNER JOIN item_venda iv2 ON iv2.codigo_produto = p2.codigo_produto
                        GROUP BY p2.codigo_categoria
                    ) AS t
                )
                ORDER BY faturamento_categoria DESC
                """),

        FORNECEDORES_PRODUTOS_ACIMA_MEDIA_CATEGORIA(
                "5 - Fornecedores com produtos acima do preço médio",
                "JOINs entre fornecedor, fornece, produto e categoria + subconsulta que compara o preço dos produtos com a média geral de preços do cadastro.",
                """
                SELECT
                    f.cnpj,
                    f.nome_fantasia AS fornecedor,
                    COUNT(DISTINCT p.codigo_produto) AS produtos_acima_media,
                    ROUND(AVG(p.preco), 2) AS preco_medio_desses_produtos,
                    GROUP_CONCAT(DISTINCT c.nome ORDER BY c.nome SEPARATOR ', ') AS categorias
                FROM fornecedor f
                INNER JOIN fornece fr ON fr.cnpj_fornecedor = f.cnpj
                INNER JOIN produto p ON p.codigo_produto = fr.codigo_produto
                INNER JOIN categoria c ON c.codigo_categoria = p.codigo_categoria
                WHERE p.preco > (
                    SELECT AVG(p2.preco)
                    FROM produto p2
                    INNER JOIN categoria c2 ON c2.codigo_categoria = p2.codigo_categoria
                )
                GROUP BY f.cnpj, f.nome_fantasia
                ORDER BY produtos_acima_media DESC, fornecedor
                """),

        VENDAS_ACIMA_TICKET_MEDIO(
                "6 - Vendas acima do ticket médio",
                "JOINs entre venda, cliente e funcionário + subconsulta para selecionar apenas vendas acima do ticket médio geral e calcular a diferença para a média.",
                """
                SELECT
                    v.numero_venda,
                    v.data_hora,
                    COALESCE(c.nome, 'Consumidor não identificado') AS cliente,
                    f.nome AS funcionario,
                    v.valor_total,
                    ROUND(v.valor_total - (SELECT AVG(v2.valor_total) FROM venda v2), 2) AS acima_da_media
                FROM venda v
                LEFT JOIN cliente c ON c.cpf = v.cpf_cliente
                INNER JOIN funcionario f ON f.matricula = v.matricula_funcionario
                WHERE v.valor_total > (SELECT AVG(v3.valor_total) FROM venda v3)
                ORDER BY v.valor_total DESC
                """),

        FORMAS_PAGAMENTO_RELEVANTES(
                "7 - Formas de pagamento com participação acima da média",
                "JOIN entre pagamento e venda + subconsultas para percentual do total e comparação com a média de faturamento entre formas de pagamento.",
                """
                SELECT
                    pg.forma_pagamento,
                    COUNT(*) AS quantidade_pagamentos,
                    COUNT(DISTINCT pg.numero_venda) AS vendas_atendidas,
                    SUM(pg.valor) AS valor_movimentado,
                    ROUND(AVG(v.valor_total), 2) AS ticket_medio_das_vendas,
                    ROUND(
                        SUM(pg.valor) * 100 / (SELECT SUM(pg2.valor) FROM pagamento pg2),
                        2
                    ) AS participacao_percentual
                FROM pagamento pg
                INNER JOIN venda v ON v.numero_venda = pg.numero_venda
                GROUP BY pg.forma_pagamento
                HAVING SUM(pg.valor) >= (
                    SELECT AVG(t.total_forma)
                    FROM (
                        SELECT SUM(pg3.valor) AS total_forma
                        FROM pagamento pg3
                        GROUP BY pg3.forma_pagamento
                    ) AS t
                )
                ORDER BY valor_movimentado DESC
                """),

        CLIENTES_SEM_COMPRAS(
                "8 - Clientes cadastrados sem compras",
                "LEFT JOIN entre cliente e telefone + subconsulta correlacionada NOT EXISTS para identificar clientes que nunca aparecem em venda.",
                """
                SELECT
                    c.cpf,
                    c.nome AS cliente,
                    c.bairro,
                    c.cidade,
                    COUNT(t.telefone_pk) AS quantidade_telefones
                FROM cliente c
                LEFT JOIN telefone t ON t.cpf_cliente = c.cpf
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM venda v
                    WHERE v.cpf_cliente = c.cpf
                )
                GROUP BY c.cpf, c.nome, c.bairro, c.cidade
                ORDER BY c.nome
                """);

        private final String titulo;
        private final String descricao;
        private final String sql;

        Consulta(String titulo, String descricao, String sql) {
            this.titulo = titulo;
            this.descricao = descricao;
            this.sql = sql;
        }

        public String getTitulo() { return titulo; }
        public String getDescricao() { return descricao; }
        public String getSql() { return sql; }

        @Override
        public String toString() { return titulo; }
    }

    public TableData executar(Consulta consulta) throws SQLException {
        List<String> colunas = new ArrayList<>();
        List<Object[]> linhas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(consulta.getSql());
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int totalColunas = meta.getColumnCount();
            for (int i = 1; i <= totalColunas; i++) {
                colunas.add(meta.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] linha = new Object[totalColunas];
                for (int i = 1; i <= totalColunas; i++) {
                    linha[i - 1] = rs.getObject(i);
                }
                linhas.add(linha);
            }
        }
        return new TableData(colunas, linhas);
    }
}
