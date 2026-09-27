package br.com.mercado.view.components;

import br.com.mercado.model.ChartItem;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PieChartPanel extends JPanel {
    private List<ChartItem> data = new ArrayList<>();
    private static final Color[] COLORS = {
            new Color(37, 110, 88), new Color(76, 140, 119),
            new Color(122, 171, 155), new Color(169, 204, 191),
            new Color(211, 229, 222), new Color(83, 102, 96)
    };

    public PieChartPanel() {
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

        if (data.isEmpty()) {
            g2.setColor(UiUtils.MUTED);
            g2.drawString("Sem dados", 20, 30);
            g2.dispose();
            return;
        }

        double total = data.stream().mapToDouble(ChartItem::value).sum();
        int diameter = Math.min(getHeight() - 40, Math.min(190, getWidth() / 2));
        int x = 18;
        int y = (getHeight() - diameter) / 2;
        int start = 0;

        for (int i = 0; i < data.size(); i++) {
            int angle = (i == data.size() - 1)
                    ? 360 - start
                    : (int) Math.round(data.get(i).value() / total * 360);
            g2.setColor(COLORS[i % COLORS.length]);
            g2.fillArc(x, y, diameter, diameter, start, angle);
            start += angle;
        }

        int legendX = x + diameter + 24;
        int legendY = 35;
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < data.size(); i++) {
            ChartItem item = data.get(i);
            double pct = total == 0 ? 0 : item.value() * 100.0 / total;
            g2.setColor(COLORS[i % COLORS.length]);
            g2.fillRoundRect(legendX, legendY - 10, 12, 12, 3, 3);
            g2.setColor(UiUtils.TEXT);
            String label = item.label() + " (" + String.format("%.1f%%", pct) + ")";
            if (fm.stringWidth(label) > getWidth() - legendX - 12) {
                label = item.label();
            }
            g2.drawString(label, legendX + 19, legendY);
            legendY += 27;
        }
        g2.dispose();
    }
}
