package br.com.mercado.view;

import br.com.mercado.dao.ProdutoDAO;
import br.com.mercado.model.Categoria;
import br.com.mercado.model.Produto;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoPanel extends JPanel implements Refreshable {
    private final ProdutoDAO dao = new ProdutoDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField search = new JTextField(22);
    private List<Produto> dados = new ArrayList<>();

    public ProdutoPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UiUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JLabel title = new JLabel("Produtos");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UiUtils.TEXT);

        JButton buscar = UiUtils.secondaryButton("Buscar");
        buscar.addActionListener(e -> refreshData());
        search.addActionListener(e -> refreshData());
        JButton novo = UiUtils.primaryButton("Novo produto");
        novo.addActionListener(e -> novoProduto());
        JButton editar = UiUtils.secondaryButton("Editar");
        editar.addActionListener(e -> editarProduto());
        JButton excluir = UiUtils.secondaryButton("Excluir");
        excluir.addActionListener(e -> excluirProduto());
        JButton atualizar = UiUtils.secondaryButton("Atualizar");
        atualizar.addActionListener(e -> refreshData());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("Código, nome ou marca:"));
        actions.add(search);
        actions.add(buscar);
        actions.add(novo);
        actions.add(editar);
        actions.add(excluir);
        actions.add(atualizar);
        top.add(actions, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Código", "Produto", "Marca", "Preço", "Unidade", "Categoria"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        UiUtils.styleTable(table);
        table.getColumnModel().getColumn(3).setCellRenderer(UiUtils.currencyRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        try {
            dados = dao.listar(search.getText());
            model.setRowCount(0);
            for (Produto p : dados) {
                model.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getMarca(), p.getPreco(),
                        p.getUnidadeMedida(), p.getNomeCategoria()});
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private Produto selecionado() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int row = table.convertRowIndexToModel(viewRow);
        int codigo = Integer.parseInt(model.getValueAt(row, 0).toString());
        return dados.stream().filter(p -> p.getCodigo() == codigo).findFirst().orElse(null);
    }

    private List<Categoria> categorias() throws SQLException {
        return dao.listarCategorias();
    }

    private void novoProduto() {
        try {
            ProdutoDialog dialog = new ProdutoDialog(SwingUtilities.getWindowAncestor(this), null, categorias());
            dialog.setVisible(true);
            Produto p = dialog.getResultado();
            if (p == null) return;
            dao.inserir(p);
            refreshData();
            JOptionPane.showMessageDialog(this, "Produto inserido com sucesso.");
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void editarProduto() {
        Produto p = selecionado();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        try {
            ProdutoDialog dialog = new ProdutoDialog(SwingUtilities.getWindowAncestor(this), p, categorias());
            dialog.setVisible(true);
            Produto atualizado = dialog.getResultado();
            if (atualizado == null) return;
            dao.atualizar(atualizado);
            refreshData();
            JOptionPane.showMessageDialog(this, "Produto alterado com sucesso.");
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void excluirProduto() {
        Produto p = selecionado();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Excluir o produto " + p.getNome() + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(p.getCodigo());
            refreshData();
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this,
                        "Este produto já aparece em vendas e não pode ser apagado por integridade referencial.\n" +
                                "Para demonstrar a deleção, cadastre um novo produto e depois o exclua.",
                        "Produto relacionado a vendas", JOptionPane.WARNING_MESSAGE);
            } else {
                erro(e);
            }
        }
    }

    private void erro(SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro ao acessar produtos:\n" + e.getMessage(),
                "Banco de dados", JOptionPane.ERROR_MESSAGE);
    }
}
