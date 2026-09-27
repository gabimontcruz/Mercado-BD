package br.com.mercado.dao;

import br.com.mercado.config.ConnectionFactory;
import br.com.mercado.model.Categoria;
import br.com.mercado.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public List<Produto> listar(String filtro) throws SQLException {
        String sql = """
                SELECT p.codigo_produto, p.marca, p.nome, p.preco, p.unidade_medida,
                       p.codigo_categoria, c.nome AS categoria
                FROM produto p
                INNER JOIN categoria c ON c.codigo_categoria = p.codigo_categoria
                WHERE (? = '' OR CAST(p.codigo_produto AS CHAR) LIKE CONCAT('%', ?, '%')
                       OR p.nome LIKE CONCAT('%', ?, '%') OR p.marca LIKE CONCAT('%', ?, '%'))
                ORDER BY p.nome
                """;

        List<Produto> produtos = new ArrayList<>();
        String termo = filtro == null ? "" : filtro.trim();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) stmt.setString(i, termo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setCodigo(rs.getInt("codigo_produto"));
                    p.setMarca(rs.getString("marca"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getBigDecimal("preco"));
                    p.setUnidadeMedida(rs.getString("unidade_medida"));
                    p.setCodigoCategoria(rs.getInt("codigo_categoria"));
                    p.setNomeCategoria(rs.getString("categoria"));
                    produtos.add(p);
                }
            }
        }
        return produtos;
    }

    public List<Categoria> listarCategorias() throws SQLException {
        String sql = "SELECT codigo_categoria, nome FROM categoria ORDER BY nome";
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(new Categoria(rs.getInt("codigo_categoria"), rs.getString("nome")));
            }
        }
        return categorias;
    }

    public void inserir(Produto p) throws SQLException {
        String sql = """
                INSERT INTO produto
                    (codigo_produto, marca, nome, preco, unidade_medida, codigo_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getCodigo());
            stmt.setString(2, p.getMarca());
            stmt.setString(3, p.getNome());
            stmt.setBigDecimal(4, p.getPreco());
            stmt.setString(5, p.getUnidadeMedida());
            stmt.setInt(6, p.getCodigoCategoria());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Produto p) throws SQLException {
        String sql = """
                UPDATE produto
                SET marca = ?, nome = ?, preco = ?, unidade_medida = ?, codigo_categoria = ?
                WHERE codigo_produto = ?
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getMarca());
            stmt.setString(2, p.getNome());
            stmt.setBigDecimal(3, p.getPreco());
            stmt.setString(4, p.getUnidadeMedida());
            stmt.setInt(5, p.getCodigoCategoria());
            stmt.setInt(6, p.getCodigo());
            stmt.executeUpdate();
        }
    }

    public void excluir(int codigo) throws SQLException {
        String sql = "DELETE FROM produto WHERE codigo_produto = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }
}
