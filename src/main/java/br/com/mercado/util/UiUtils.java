package br.com.mercado.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class UiUtils {
    public static final Color PRIMARY = new Color(24, 75, 61);
    public static final Color PRIMARY_LIGHT = new Color(37, 110, 88);
    public static final Color BACKGROUND = new Color(244, 247, 246);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(34, 45, 42);
    public static final Color MUTED = new Color(100, 116, 111);

    private UiUtils() {}

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 216)),
                BorderFactory.createEmptyBorder(8, 13, 8, 13)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(232, 236, 234));
        table.setSelectionBackground(new Color(218, 236, 229));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 32));
    }

    public static DefaultTableCellRenderer currencyRenderer() {
        return new DefaultTableCellRenderer() {
            private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number n) setText(nf.format(n.doubleValue()));
                else if (value instanceof BigDecimal b) setText(nf.format(b));
                else super.setValue(value);
            }
        };
    }

    public static String money(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(value == null ? BigDecimal.ZERO : value);
    }

    public static JPanel titledPanel(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 229)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        label.setForeground(TEXT);
        p.add(label, BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }
}
