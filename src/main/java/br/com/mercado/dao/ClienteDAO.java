package br.com.mercado.dao;

import br.com.mercado.config.ConnectionFactory;
import br.com.mercado.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> listar(String filtro) throws SQLException {
        String sql = """
                SELECT cpf, nome, data_nascimento, rua, numero, cep, bairro, cidade
                FROM cliente
                WHERE (? = '' OR cpf LIKE CONCAT('%', ?, '%') OR nome LIKE CONCAT('%', ?, '%'))
                ORDER BY nome
                """;

        List<Cliente> clientes = new ArrayList<>();
        String termo = filtro == null ? "" : filtro.trim();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, termo);
            stmt.setString(2, termo);
            stmt.setString(3, termo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setCpf(rs.getString("cpf"));
                    c.setNome(rs.getString("nome"));
                    c.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    c.setRua(rs.getString("rua"));
                    c.setNumero(rs.getString("numero"));
                    c.setCep(rs.getString("cep"));
                    c.setBairro(rs.getString("bairro"));
                    c.setCidade(rs.getString("cidade"));
                    clientes.add(c);
                }
            }
        }
        return clientes;
    }

    public void inserir(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO cliente
                    (cpf, nome, data_nascimento, rua, numero, cep, bairro, cidade)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, cliente, false);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = """
                UPDATE cliente
                SET nome = ?, data_nascimento = ?, rua = ?, numero = ?,
                    cep = ?, bairro = ?, cidade = ?
                WHERE cpf = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setDate(2, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(3, cliente.getRua());
            stmt.setString(4, cliente.getNumero());
            stmt.setString(5, cliente.getCep());
            stmt.setString(6, cliente.getBairro());
            stmt.setString(7, cliente.getCidade());
            stmt.setString(8, cliente.getCpf());
            stmt.executeUpdate();
        }
    }

    public void excluir(String cpf) throws SQLException {
        String sql = "DELETE FROM cliente WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.executeUpdate();
        }
    }

    private void preencher(PreparedStatement stmt, Cliente c, boolean ignorado) throws SQLException {
        stmt.setString(1, c.getCpf());
        stmt.setString(2, c.getNome());
        stmt.setDate(3, Date.valueOf(c.getDataNascimento()));
        stmt.setString(4, c.getRua());
        stmt.setString(5, c.getNumero());
        stmt.setString(6, c.getCep());
        stmt.setString(7, c.getBairro());
        stmt.setString(8, c.getCidade());
    }
}
