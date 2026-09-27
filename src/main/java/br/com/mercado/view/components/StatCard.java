package br.com.mercado.view.components;

import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel valueLabel = new JLabel("-");

    public StatCard(String title) {
        setLayout(new BorderLayout(0, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 229)),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(UiUtils.MUTED);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        valueLabel.setForeground(UiUtils.TEXT);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 24f));

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
