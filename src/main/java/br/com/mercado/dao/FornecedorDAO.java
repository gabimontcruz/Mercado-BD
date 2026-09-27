package br.com.mercado.dao;

import br.com.mercado.config.ConnectionFactory;
import br.com.mercado.model.Fornecedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {
    public List<Fornecedor> listar(String filtro) throws SQLException {
        String sql = """
                SELECT cnpj, nome_fantasia, razao_social, rua, cep, numero, bairro, cidade, telefone
                FROM fornecedor
                WHERE (? = '' OR cnpj LIKE CONCAT('%', ?, '%')
                       OR nome_fantasia LIKE CONCAT('%', ?, '%')
                       OR razao_social LIKE CONCAT('%', ?, '%'))
                ORDER BY nome_fantasia
                """;
        String termo = filtro == null ? "" : filtro.trim();
        List<Fornecedor> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) stmt.setString(i, termo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fornecedor f = new Fornecedor();
                    f.setCnpj(rs.getString("cnpj"));
                    f.setNomeFantasia(rs.getString("nome_fantasia"));
                    f.setRazaoSocial(rs.getString("razao_social"));
                    f.setRua(rs.getString("rua"));
                    f.setCep(rs.getString("cep"));
                    f.setNumero(rs.getString("numero"));
                    f.setBairro(rs.getString("bairro"));
                    f.setCidade(rs.getString("cidade"));
                    f.setTelefone(rs.getString("telefone"));
                    lista.add(f);
                }
            }
        }
        return lista;
    }

    public void inserir(Fornecedor f) throws SQLException {
        String sql = """
                INSERT INTO fornecedor
                    (cnpj, nome_fantasia, razao_social, rua, cep, numero, bairro, cidade, telefone)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, f, true);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Fornecedor f) throws SQLException {
        String sql = """
                UPDATE fornecedor
                SET nome_fantasia = ?, razao_social = ?, rua = ?, cep = ?, numero = ?,
                    bairro = ?, cidade = ?, telefone = ?
                WHERE cnpj = ?
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, f.getNomeFantasia());
            stmt.setString(2, f.getRazaoSocial());
            stmt.setString(3, f.getRua());
            stmt.setString(4, f.getCep());
            stmt.setString(5, f.getNumero());
            stmt.setString(6, f.getBairro());
            stmt.setString(7, f.getCidade());
            stmt.setString(8, f.getTelefone());
            stmt.setString(9, f.getCnpj());
            stmt.executeUpdate();
        }
    }

    public void excluir(String cnpj) throws SQLException {
        String sql = "DELETE FROM fornecedor WHERE cnpj = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnpj);
            stmt.executeUpdate();
        }
    }

    private void preencher(PreparedStatement stmt, Fornecedor f, boolean incluirCnpj) throws SQLException {
        stmt.setString(1, f.getCnpj());
        stmt.setString(2, f.getNomeFantasia());
        stmt.setString(3, f.getRazaoSocial());
        stmt.setString(4, f.getRua());
        stmt.setString(5, f.getCep());
        stmt.setString(6, f.getNumero());
        stmt.setString(7, f.getBairro());
        stmt.setString(8, f.getCidade());
        stmt.setString(9, f.getTelefone());
    }
}
