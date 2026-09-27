package br.com.mercado.view.components;

import br.com.mercado.model.ChartItem;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BarChartPanel extends JPanel {
    private List<ChartItem> data = new ArrayList<>();

    public BarChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(420, 240));
    }

    public void setData(List<ChartItem> data) {
        this.data = data == null ? new ArrayList<>() : data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int left = 48, right = 18, top = 18, bottom = 58;
        int chartW = Math.max(1, w - left - right);
        int chartH = Math.max(1, h - top - bottom);

        if (data.isEmpty()) {
            g2.setColor(UiUtils.MUTED);
            g2.drawString("Sem dados", left, top + 20);
            g2.dispose();
            return;
        }

        double max = data.stream().mapToDouble(ChartItem::value).max().orElse(1);
        int n = data.size();
        double slot = (double) chartW / n;
        int barW = Math.max(12, (int) (slot * 0.62));

        g2.setColor(new Color(220, 228, 224));
        g2.drawLine(left, top + chartH, left + chartW, top + chartH);

        for (int i = 0; i < n; i++) {
            ChartItem item = data.get(i);
            int bh = (int) Math.round((item.value() / max) * (chartH - 18));
            int x = left + (int) (i * slot + (slot - barW) / 2);
            int y = top + chartH - bh;

            g2.setColor(UiUtils.PRIMARY_LIGHT);
            g2.fillRoundRect(x, y, barW, bh, 8, 8);

            g2.setColor(UiUtils.TEXT);
            String value = item.value() >= 1000 ? String.format("%.0f", item.value()) : String.format("%.1f", item.value());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(value, x + (barW - fm.stringWidth(value)) / 2, Math.max(top + 10, y - 5));

            String label = shorten(item.label(), 12);
            int labelX = x + (barW - fm.stringWidth(label)) / 2;
            g2.drawString(label, labelX, top + chartH + 20 + (i % 2) * 14);
        }
        g2.dispose();
    }

    private String shorten(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }
}
