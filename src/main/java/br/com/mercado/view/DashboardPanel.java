package br.com.mercado.view;

import br.com.mercado.dao.DashboardDAO;
import br.com.mercado.model.DashboardResumo;
import br.com.mercado.util.UiUtils;
import br.com.mercado.view.components.BarChartPanel;
import br.com.mercado.view.components.LineChartPanel;
import br.com.mercado.view.components.PieChartPanel;
import br.com.mercado.view.components.StatCard;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DashboardPanel extends JPanel implements Refreshable {
    private final DashboardDAO dao = new DashboardDAO();
    private final StatCard vendas = new StatCard("Vendas realizadas");
    private final StatCard faturamento = new StatCard("Faturamento");
    private final StatCard ticket = new StatCard("Ticket médio");
    private final StatCard clientes = new StatCard("Clientes cadastrados");
    private final BarChartPanel produtosChart = new BarChartPanel();
    private final PieChartPanel pagamentosChart = new PieChartPanel();
    private final LineChartPanel faturamentoChart = new LineChartPanel();
    private final BarChartPanel distribuicaoChart = new BarChartPanel();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UiUtils.BACKGROUND);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UiUtils.BACKGROUND);
        body.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JLabel title = new JLabel("Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UiUtils.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title);
        body.add(Box.createVerticalStrut(4));

        JLabel subtitle = new JLabel("Visão geral, indicadores e gráficos estatísticos do mercado");
        subtitle.setForeground(UiUtils.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(subtitle);
        body.add(Box.createVerticalStrut(18));

        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.setBackground(UiUtils.BACKGROUND);
        cards.add(vendas);
        cards.add(faturamento);
        cards.add(ticket);
        cards.add(clientes);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(cards);
        body.add(Box.createVerticalStrut(14));

        JPanel charts1 = new JPanel(new GridLayout(1, 2, 14, 0));
        charts1.setBackground(UiUtils.BACKGROUND);
        charts1.add(UiUtils.titledPanel("Produtos mais vendidos (barras)", produtosChart));
        charts1.add(UiUtils.titledPanel("Participação por forma de pagamento (setores)", pagamentosChart));
        charts1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        charts1.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(charts1);
        body.add(Box.createVerticalStrut(14));

        JPanel charts2 = new JPanel(new GridLayout(1, 2, 14, 0));
        charts2.setBackground(UiUtils.BACKGROUND);
        charts2.add(UiUtils.titledPanel("Faturamento diário (série temporal)", faturamentoChart));
        charts2.add(UiUtils.titledPanel("Distribuição das vendas por faixa de valor (histograma)", distribuicaoChart));
        charts2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        charts2.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(charts2);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        try {
            DashboardResumo resumo = dao.carregarResumo();
            vendas.setValue(String.valueOf(resumo.vendas()));
            faturamento.setValue(UiUtils.money(resumo.faturamento()));
            ticket.setValue(UiUtils.money(resumo.ticketMedio()));
            clientes.setValue(String.valueOf(resumo.clientes()));
            produtosChart.setData(dao.produtosMaisVendidos());
            pagamentosChart.setData(dao.formasPagamento());
            faturamentoChart.setData(dao.faturamentoPorDia());
            distribuicaoChart.setData(dao.distribuicaoVendasPorFaixa());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível carregar o dashboard.\n\n" +
                            "Verifique se o MySQL está ligado, se o banco mercado_db foi criado e se\n" +
                            "usuário/senha estão corretos em src/main/resources/application.properties.\n\n" +
                            "Detalhe: " + e.getMessage(),
                    "Erro de conexão", JOptionPane.ERROR_MESSAGE);
        }
    }
}
