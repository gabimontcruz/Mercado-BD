package br.com.mercado.view.components;

import br.com.mercado.model.ChartItem;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class LineChartPanel extends JPanel {
    private List<ChartItem> data = new ArrayList<>();

    public LineChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(880, 230));
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

        int left = 52, right = 20, top = 20, bottom = 45;
        int chartW = getWidth() - left - right;
        int chartH = getHeight() - top - bottom;
        if (data.size() < 2 || chartW <= 0 || chartH <= 0) {
            g2.setColor(UiUtils.MUTED);
            g2.drawString(data.isEmpty() ? "Sem dados" : "Dados insuficientes", 20, 30);
            g2.dispose();
            return;
        }

        double max = data.stream().mapToDouble(ChartItem::value).max().orElse(1);
        double min = data.stream().mapToDouble(ChartItem::value).min().orElse(0);
        if (max == min) min = 0;

        g2.setColor(new Color(226, 232, 229));
        for (int i = 0; i <= 4; i++) {
            int y = top + i * chartH / 4;
            g2.drawLine(left, y, left + chartW, y);
        }

        int prevX = -1, prevY = -1;
        for (int i = 0; i < data.size(); i++) {
            double ratioX = data.size() == 1 ? 0 : (double) i / (data.size() - 1);
            int x = left + (int) (ratioX * chartW);
            double normalized = (data.get(i).value() - min) / (max - min);
            int y = top + chartH - (int) (normalized * (chartH - 10));

            if (prevX >= 0) {
                g2.setColor(UiUtils.PRIMARY_LIGHT);
                g2.setStroke(new BasicStroke(2.4f));
                g2.drawLine(prevX, prevY, x, y);
            }
            g2.setColor(UiUtils.PRIMARY);
            g2.fill(new Ellipse2D.Double(x - 3.5, y - 3.5, 7, 7));

            if (i % Math.max(1, data.size() / 10) == 0 || i == data.size() - 1) {
                g2.setColor(UiUtils.MUTED);
                String label = data.get(i).label();
                g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, top + chartH + 22);
            }
            prevX = x;
            prevY = y;
        }
        g2.dispose();
    }
}
