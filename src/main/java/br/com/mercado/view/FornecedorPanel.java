package br.com.mercado.view;

import br.com.mercado.dao.FornecedorDAO;
import br.com.mercado.model.Fornecedor;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecedorPanel extends JPanel implements Refreshable {
    private final FornecedorDAO dao = new FornecedorDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField search = new JTextField(20);
    private List<Fornecedor> dados = new ArrayList<>();

    public FornecedorPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UiUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JLabel title = new JLabel("Fornecedores");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UiUtils.TEXT);

        JButton buscar = UiUtils.secondaryButton("Buscar");
        buscar.addActionListener(e -> refreshData());
        search.addActionListener(e -> refreshData());
        JButton novo = UiUtils.primaryButton("Novo fornecedor");
        novo.addActionListener(e -> novo());
        JButton editar = UiUtils.secondaryButton("Editar");
        editar.addActionListener(e -> editar());
        JButton excluir = UiUtils.secondaryButton("Excluir");
        excluir.addActionListener(e -> excluir());
        JButton atualizar = UiUtils.secondaryButton("Atualizar");
        atualizar.addActionListener(e -> refreshData());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("CNPJ ou nome:"));
        actions.add(search);
        actions.add(buscar);
        actions.add(novo);
        actions.add(editar);
        actions.add(excluir);
        actions.add(atualizar);
        top.add(actions, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"CNPJ", "Nome fantasia", "Razão social", "Rua", "Nº", "CEP", "Bairro", "Cidade", "Telefone"}, 0) {
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
            for (Fornecedor f : dados) {
                model.addRow(new Object[]{f.getCnpj(), f.getNomeFantasia(), f.getRazaoSocial(), f.getRua(),
                        f.getNumero(), f.getCep(), f.getBairro(), f.getCidade(), f.getTelefone()});
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private Fornecedor selecionado() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int row = table.convertRowIndexToModel(viewRow);
        String cnpj = model.getValueAt(row, 0).toString();
        return dados.stream().filter(f -> f.getCnpj().equals(cnpj)).findFirst().orElse(null);
    }

    private void novo() {
        FornecedorDialog dialog = new FornecedorDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        Fornecedor f = dialog.getResultado();
        if (f == null) return;
        try {
            dao.inserir(f);
            refreshData();
            JOptionPane.showMessageDialog(this, "Fornecedor inserido com sucesso.");
        } catch (SQLException e) { erro(e); }
    }

    private void editar() {
        Fornecedor f = selecionado();
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela.");
            return;
        }
        FornecedorDialog dialog = new FornecedorDialog(SwingUtilities.getWindowAncestor(this), f);
        dialog.setVisible(true);
        Fornecedor atualizado = dialog.getResultado();
        if (atualizado == null) return;
        try {
            dao.atualizar(atualizado);
            refreshData();
            JOptionPane.showMessageDialog(this, "Fornecedor alterado com sucesso.");
        } catch (SQLException e) { erro(e); }
    }

    private void excluir() {
        Fornecedor f = selecionado();
        if (f == null) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela.");
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Excluir o fornecedor " + f.getNomeFantasia() + "?\nOs vínculos da tabela FORNECE serão removidos por ON DELETE CASCADE.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(f.getCnpj());
            refreshData();
            JOptionPane.showMessageDialog(this, "Fornecedor excluído com sucesso.");
        } catch (SQLException e) { erro(e); }
    }

    private void erro(SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro ao acessar fornecedores:\n" + e.getMessage(),
                "Banco de dados", JOptionPane.ERROR_MESSAGE);
    }
}
