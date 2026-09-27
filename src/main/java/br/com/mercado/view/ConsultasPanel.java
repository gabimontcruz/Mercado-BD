package br.com.mercado.view;

import br.com.mercado.dao.ConsultaDAO;
import br.com.mercado.dao.ConsultaDAO.Consulta;
import br.com.mercado.model.TableData;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ConsultasPanel extends JPanel implements Refreshable {
    private final ConsultaDAO dao = new ConsultaDAO();
    private final JComboBox<Consulta> combo = new JComboBox<>(Consulta.values());
    private final JTextArea descricao = new JTextArea(2, 80);
    private final JTextArea sqlArea = new JTextArea(11, 70);
    private final DefaultTableModel model = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel totalLinhas = new JLabel(" ");

    public ConsultasPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UiUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JLabel title = new JLabel("Consultas SQL — 8 consultas com JOIN + subconsulta");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UiUtils.TEXT);

        JButton executar = UiUtils.primaryButton("Executar consulta");
        executar.addActionListener(e -> executar());
        combo.addActionListener(e -> atualizarDetalhes());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        controls.setOpaque(false);
        controls.add(new JLabel("Consulta:"));
        controls.add(combo);
        controls.add(executar);

        descricao.setEditable(false);
        descricao.setOpaque(false);
        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setForeground(UiUtils.MUTED);
        descricao.setFont(descricao.getFont().deriveFont(13f));
        descricao.setBorder(null);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        controls.setAlignmentX(Component.LEFT_ALIGNMENT);
        descricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(12));
        header.add(controls);
        header.add(Box.createVerticalStrut(8));
        header.add(descricao);
        add(header, BorderLayout.NORTH);

        sqlArea.setEditable(false);
        sqlArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        sqlArea.setBackground(new Color(248, 250, 249));
        sqlArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        sqlArea.setLineWrap(false);

        UiUtils.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(UiUtils.titledPanel("SQL explícito enviado ao MySQL por JDBC", new JScrollPane(sqlArea)), BorderLayout.NORTH);
        center.add(UiUtils.titledPanel("Resultado da consulta", new JScrollPane(table)), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        totalLinhas.setForeground(UiUtils.MUTED);
        add(totalLinhas, BorderLayout.SOUTH);

        atualizarDetalhes();
    }

    private void atualizarDetalhes() {
        Consulta c = (Consulta) combo.getSelectedItem();
        if (c == null) return;
        descricao.setText(c.getDescricao());
        descricao.setCaretPosition(0);
        sqlArea.setText(c.getSql().strip());
        sqlArea.setCaretPosition(0);
    }

    private void executar() {
        Consulta c = (Consulta) combo.getSelectedItem();
        if (c == null) return;
        try {
            TableData data = dao.executar(c);
            model.setDataVector(data.rows().toArray(new Object[0][]), data.columns().toArray());
            ajustarLarguraColunas();
            totalLinhas.setText("Linhas retornadas: " + data.rows().size());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao executar consulta:\n" + e.getMessage(),
                    "Banco de dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajustarLarguraColunas() {
        for (int col = 0; col < table.getColumnCount(); col++) {
            int largura = 110;
            String titulo = table.getColumnName(col);
            largura = Math.max(largura, titulo.length() * 9 + 24);
            for (int row = 0; row < Math.min(table.getRowCount(), 30); row++) {
                Object valor = table.getValueAt(row, col);
                if (valor != null) largura = Math.max(largura, Math.min(320, valor.toString().length() * 8 + 24));
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(largura);
        }
    }

    @Override
    public void refreshData() {
        executar();
    }
}
