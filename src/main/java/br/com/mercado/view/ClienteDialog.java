package br.com.mercado.view;

import br.com.mercado.model.Cliente;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ClienteDialog extends JDialog {
    private final JTextField cpf = new JTextField(20);
    private final JTextField nome = new JTextField(20);
    private final JTextField data = new JTextField(20);
    private final JTextField rua = new JTextField(20);
    private final JTextField numero = new JTextField(20);
    private final JTextField cep = new JTextField(20);
    private final JTextField bairro = new JTextField(20);
    private final JTextField cidade = new JTextField("Recife", 20);
    private Cliente resultado;

    public ClienteDialog(Window owner, Cliente cliente) {
        super(owner, cliente == null ? "Novo cliente" : "Editar cliente", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        addRow(form, c, 0, "CPF (11 dígitos)", cpf);
        addRow(form, c, 1, "Nome", nome);
        addRow(form, c, 2, "Nascimento (AAAA-MM-DD)", data);
        addRow(form, c, 3, "Rua", rua);
        addRow(form, c, 4, "Número", numero);
        addRow(form, c, 5, "CEP (8 dígitos)", cep);
        addRow(form, c, 6, "Bairro", bairro);
        addRow(form, c, 7, "Cidade", cidade);

        if (cliente != null) {
            cpf.setText(cliente.getCpf());
            cpf.setEnabled(false);
            nome.setText(cliente.getNome());
            data.setText(cliente.getDataNascimento().toString());
            rua.setText(cliente.getRua());
            numero.setText(cliente.getNumero());
            cep.setText(cliente.getCep());
            bairro.setText(cliente.getBairro());
            cidade.setText(cliente.getCidade());
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
        String cpfValue = cpf.getText().replaceAll("\\D", "");
        String cepValue = cep.getText().replaceAll("\\D", "");
        if (cpfValue.length() != 11) {
            showError("O CPF deve conter exatamente 11 dígitos.");
            return;
        }
        if (cepValue.length() != 8) {
            showError("O CEP deve conter exatamente 8 dígitos.");
            return;
        }
        if (nome.getText().isBlank() || rua.getText().isBlank() || numero.getText().isBlank()
                || bairro.getText().isBlank() || cidade.getText().isBlank()) {
            showError("Preencha todos os campos obrigatórios.");
            return;
        }

        try {
            LocalDate nascimento = LocalDate.parse(data.getText().trim());
            resultado = new Cliente(cpfValue, nome.getText().trim(), nascimento,
                    rua.getText().trim(), numero.getText().trim(), cepValue,
                    bairro.getText().trim(), cidade.getText().trim());
            dispose();
        } catch (DateTimeParseException e) {
            showError("Data inválida. Use o formato AAAA-MM-DD, por exemplo 2000-05-21.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validação", JOptionPane.WARNING_MESSAGE);
    }

    public Cliente getResultado() {
        return resultado;
    }
}
