package br.com.mercado.view;

import br.com.mercado.dao.ClienteDAO;
import br.com.mercado.model.Cliente;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientePanel extends JPanel implements Refreshable {
    private final ClienteDAO dao = new ClienteDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField search = new JTextField(24);
    private List<Cliente> dados = new ArrayList<>();

    public ClientePanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UiUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JLabel title = new JLabel("Clientes");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UiUtils.TEXT);

        JButton buscar = UiUtils.secondaryButton("Buscar");
        buscar.addActionListener(e -> refreshData());
        search.addActionListener(e -> refreshData());

        JButton novo = UiUtils.primaryButton("Novo cliente");
        novo.addActionListener(e -> novoCliente());
        JButton editar = UiUtils.secondaryButton("Editar");
        editar.addActionListener(e -> editarCliente());
        JButton excluir = UiUtils.secondaryButton("Excluir");
        excluir.addActionListener(e -> excluirCliente());
        JButton atualizar = UiUtils.secondaryButton("Atualizar");
        atualizar.addActionListener(e -> refreshData());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("CPF ou nome:"));
        actions.add(search);
        actions.add(buscar);
        actions.add(novo);
        actions.add(editar);
        actions.add(excluir);
        actions.add(atualizar);
        top.add(actions, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"CPF", "Nome", "Nascimento", "Rua", "Nº", "CEP", "Bairro", "Cidade"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        UiUtils.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        try {
            dados = dao.listar(search.getText());
            model.setRowCount(0);
            for (Cliente c : dados) {
                model.addRow(new Object[]{c.getCpf(), c.getNome(), c.getDataNascimento(), c.getRua(),
                        c.getNumero(), c.getCep(), c.getBairro(), c.getCidade()});
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private Cliente selecionado() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int row = table.convertRowIndexToModel(viewRow);
        String cpf = model.getValueAt(row, 0).toString();
        return dados.stream().filter(c -> c.getCpf().equals(cpf)).findFirst().orElse(null);
    }

    private void novoCliente() {
        ClienteDialog dialog = new ClienteDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        Cliente c = dialog.getResultado();
        if (c == null) return;
        try {
            dao.inserir(c);
            refreshData();
            JOptionPane.showMessageDialog(this, "Cliente inserido com sucesso.");
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void editarCliente() {
        Cliente c = selecionado();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }
        ClienteDialog dialog = new ClienteDialog(SwingUtilities.getWindowAncestor(this), c);
        dialog.setVisible(true);
        Cliente atualizado = dialog.getResultado();
        if (atualizado == null) return;
        try {
            dao.atualizar(atualizado);
            refreshData();
            JOptionPane.showMessageDialog(this, "Cliente alterado com sucesso.");
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void excluirCliente() {
        Cliente c = selecionado();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Excluir o cliente " + c.getNome() + "?\nVendas e pagamentos antigos permanecerão, mas sem vínculo com o cliente.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(c.getCpf());
            refreshData();
            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void erro(SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro ao acessar clientes:\n" + e.getMessage(),
                "Banco de dados", JOptionPane.ERROR_MESSAGE);
    }
}
