package br.com.mercado.view;

import br.com.mercado.model.Fornecedor;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;

public class FornecedorDialog extends JDialog {
    private final JTextField cnpj = new JTextField(22);
    private final JTextField nomeFantasia = new JTextField(22);
    private final JTextField razaoSocial = new JTextField(22);
    private final JTextField rua = new JTextField(22);
    private final JTextField numero = new JTextField(22);
    private final JTextField cep = new JTextField(22);
    private final JTextField bairro = new JTextField(22);
    private final JTextField cidade = new JTextField("Recife", 22);
    private final JTextField telefone = new JTextField(22);
    private Fornecedor resultado;

    public FornecedorDialog(Window owner, Fornecedor fornecedor) {
        super(owner, fornecedor == null ? "Novo fornecedor" : "Editar fornecedor", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        addRow(form, c, 0, "CNPJ (14 dígitos)", cnpj);
        addRow(form, c, 1, "Nome fantasia", nomeFantasia);
        addRow(form, c, 2, "Razão social", razaoSocial);
        addRow(form, c, 3, "Rua", rua);
        addRow(form, c, 4, "Número", numero);
        addRow(form, c, 5, "CEP (8 dígitos)", cep);
        addRow(form, c, 6, "Bairro", bairro);
        addRow(form, c, 7, "Cidade", cidade);
        addRow(form, c, 8, "Telefone", telefone);

        if (fornecedor != null) {
            cnpj.setText(fornecedor.getCnpj());
            cnpj.setEnabled(false);
            nomeFantasia.setText(fornecedor.getNomeFantasia());
            razaoSocial.setText(fornecedor.getRazaoSocial());
            rua.setText(fornecedor.getRua());
            numero.setText(fornecedor.getNumero());
            cep.setText(fornecedor.getCep());
            bairro.setText(fornecedor.getBairro());
            cidade.setText(fornecedor.getCidade());
            telefone.setText(fornecedor.getTelefone());
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
        String cnpjValue = cnpj.getText().replaceAll("\\D", "");
        String cepValue = cep.getText().replaceAll("\\D", "");
        if (cnpjValue.length() != 14) {
            warn("O CNPJ deve conter exatamente 14 dígitos.");
            return;
        }
        if (cepValue.length() != 8) {
            warn("O CEP deve conter exatamente 8 dígitos.");
            return;
        }
        if (nomeFantasia.getText().isBlank() || razaoSocial.getText().isBlank() || rua.getText().isBlank()
                || numero.getText().isBlank() || bairro.getText().isBlank() || cidade.getText().isBlank()) {
            warn("Preencha todos os campos obrigatórios.");
            return;
        }
        Fornecedor f = new Fornecedor();
        f.setCnpj(cnpjValue);
        f.setNomeFantasia(nomeFantasia.getText().trim());
        f.setRazaoSocial(razaoSocial.getText().trim());
        f.setRua(rua.getText().trim());
        f.setNumero(numero.getText().trim());
        f.setCep(cepValue);
        f.setBairro(bairro.getText().trim());
        f.setCidade(cidade.getText().trim());
        f.setTelefone(telefone.getText().trim());
        resultado = f;
        dispose();
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validação", JOptionPane.WARNING_MESSAGE);
    }

    public Fornecedor getResultado() { return resultado; }
}
