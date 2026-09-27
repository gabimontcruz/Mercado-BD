package br.com.mercado.dao;

import br.com.mercado.config.ConnectionFactory;
import br.com.mercado.model.ChartItem;
import br.com.mercado.model.DashboardResumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public DashboardResumo carregarResumo() throws SQLException {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM venda) AS vendas,
                    (SELECT COALESCE(SUM(valor_total), 0) FROM venda) AS faturamento,
                    (SELECT COALESCE(AVG(valor_total), 0) FROM venda) AS ticket_medio,
                    (SELECT COUNT(*) FROM cliente) AS clientes
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return new DashboardResumo(
                    rs.getLong("vendas"),
                    rs.getBigDecimal("faturamento"),
                    rs.getBigDecimal("ticket_medio"),
                    rs.getLong("clientes")
            );
        }
    }

    public List<ChartItem> produtosMaisVendidos() throws SQLException {
        String sql = """
                SELECT p.nome, SUM(iv.quantidade) AS quantidade
                FROM item_venda iv
                INNER JOIN produto p ON p.codigo_produto = iv.codigo_produto
                GROUP BY p.codigo_produto, p.nome
                ORDER BY quantidade DESC
                LIMIT 8
                """;
        return carregarItens(sql, "nome", "quantidade");
    }

    public List<ChartItem> formasPagamento() throws SQLException {
        String sql = """
                SELECT forma_pagamento, SUM(valor) AS total
                FROM pagamento
                GROUP BY forma_pagamento
                ORDER BY total DESC
                """;
        return carregarItens(sql, "forma_pagamento", "total");
    }

    public List<ChartItem> faturamentoPorDia() throws SQLException {
        String sql = """
                SELECT DATE_FORMAT(data_hora, '%d/%m') AS dia, SUM(valor_total) AS total
                FROM venda
                GROUP BY DATE(data_hora), DATE_FORMAT(data_hora, '%d/%m')
                ORDER BY DATE(data_hora)
                """;
        return carregarItens(sql, "dia", "total");
    }

    public List<ChartItem> distribuicaoVendasPorFaixa() throws SQLException {
        String sql = """
                SELECT faixa, COUNT(*) AS quantidade
                FROM (
                    SELECT CASE
                        WHEN valor_total < 50 THEN 'Até R$ 49,99'
                        WHEN valor_total < 100 THEN 'R$ 50 a 99,99'
                        WHEN valor_total < 150 THEN 'R$ 100 a 149,99'
                        ELSE 'R$ 150 ou mais'
                    END AS faixa,
                    CASE
                        WHEN valor_total < 50 THEN 1
                        WHEN valor_total < 100 THEN 2
                        WHEN valor_total < 150 THEN 3
                        ELSE 4
                    END AS ordem
                    FROM venda
                ) AS faixas
                GROUP BY faixa, ordem
                ORDER BY ordem
                """;
        return carregarItens(sql, "faixa", "quantidade");
    }

    private List<ChartItem> carregarItens(String sql, String colunaLabel, String colunaValor) throws SQLException {
        List<ChartItem> itens = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                itens.add(new ChartItem(rs.getString(colunaLabel), rs.getDouble(colunaValor)));
            }
        }
        return itens;
    }
}
