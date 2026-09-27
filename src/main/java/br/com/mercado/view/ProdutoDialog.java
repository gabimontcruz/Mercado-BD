package br.com.mercado.view;

import br.com.mercado.model.Categoria;
import br.com.mercado.model.Produto;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoDialog extends JDialog {
    private final JTextField codigo = new JTextField(20);
    private final JTextField nome = new JTextField(20);
    private final JTextField marca = new JTextField(20);
    private final JTextField preco = new JTextField(20);
    private final JTextField unidade = new JTextField("un", 20);
    private final JComboBox<Categoria> categoria = new JComboBox<>();
    private Produto resultado;

    public ProdutoDialog(Window owner, Produto produto, List<Categoria> categorias) {
        super(owner, produto == null ? "Novo produto" : "Editar produto", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        categorias.forEach(categoria::addItem);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        addRow(form, c, 0, "Código", codigo);
        addRow(form, c, 1, "Nome", nome);
        addRow(form, c, 2, "Marca", marca);
        addRow(form, c, 3, "Preço", preco);
        addRow(form, c, 4, "Unidade", unidade);
        addRow(form, c, 5, "Categoria", categoria);

        if (produto != null) {
            codigo.setText(String.valueOf(produto.getCodigo()));
            codigo.setEnabled(false);
            nome.setText(produto.getNome());
            marca.setText(produto.getMarca());
            preco.setText(produto.getPreco().toPlainString());
            unidade.setText(produto.getUnidadeMedida());
            for (int i = 0; i < categoria.getItemCount(); i++) {
                if (categoria.getItemAt(i).getCodigo() == produto.getCodigoCategoria()) {
                    categoria.setSelectedIndex(i);
                    break;
                }
            }
        }

        JButton cancelar = UiUtils.secondaryButton("Cancelar");
        cancelar.addActionListener(e -> dispose());
        JButton salvar = UiUtils.primaryButton("Salvar");
        salvar.addActionListener(e -> salvar());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelar);
        buttons.add(salvar);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridy = row;
        c.gridx = 0;
        c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(field, c);
    }

    private void salvar() {
        try {
            int cod = Integer.parseInt(codigo.getText().trim());
            BigDecimal valor = new BigDecimal(preco.getText().trim().replace(',', '.'));
            Categoria cat = (Categoria) categoria.getSelectedItem();
            if (cod <= 0 || valor.signum() < 0 || cat == null || nome.getText().isBlank()
                    || marca.getText().isBlank() || unidade.getText().isBlank()) {
                throw new IllegalArgumentException();
            }
            resultado = new Produto(cod, marca.getText().trim(), nome.getText().trim(), valor,
                    unidade.getText().trim(), cat.getCodigo());
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Verifique os dados. Código deve ser inteiro positivo e preço deve ser numérico não negativo.",
                    "Validação", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Produto getResultado() { return resultado; }
}
