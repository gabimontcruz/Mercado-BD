package br.com.mercado.view;

import br.com.mercado.config.ConnectionFactory;
import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, JComponent> screens = new LinkedHashMap<>();
    private final JLabel status = new JLabel(" ");

    public MainFrame() {
        super("Mercado - Projeto de Banco de Dados");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setSize(1280, 820);
        setLocationRelativeTo(null);

        content.setBackground(UiUtils.BACKGROUND);

        screens.put("Dashboard", new DashboardPanel());
        screens.put("Clientes", new ClientePanel());
        screens.put("Produtos", new ProdutoPanel());
        screens.put("Fornecedores", new FornecedorPanel());
        screens.put("Consultas", new ConsultasPanel());
        screens.put("Sobre", new SobrePanel());
        screens.forEach(content::add);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        mostrar("Dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setBackground(UiUtils.PRIMARY);
        sidebar.setLayout(new BorderLayout());

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(22, 16, 16, 16));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("MERCADO BD");
        logo.setForeground(Color.WHITE);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 20f));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(logo);
        nav.add(Box.createVerticalStrut(4));

        JLabel stage = new JLabel("Etapa 03 • JDBC + MySQL");
        stage.setForeground(new Color(199, 224, 215));
        stage.setFont(stage.getFont().deriveFont(12f));
        stage.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(stage);
        nav.add(Box.createVerticalStrut(28));

        for (String screen : screens.keySet()) {
            JButton b = menuButton(screen);
            b.addActionListener(e -> mostrar(screen));
            nav.add(b);
            nav.add(Box.createVerticalStrut(6));
        }

        JButton sair = menuButton("Sair");
        sair.addActionListener(e -> dispose());
        nav.add(Box.createVerticalStrut(18));
        nav.add(sair);

        sidebar.add(nav, BorderLayout.NORTH);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 16, 20, 16));
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        status.setForeground(Color.WHITE);
        status.setFont(status.getFont().deriveFont(11f));
        JLabel db = new JLabel("Banco: mercado_db");
        db.setForeground(new Color(199, 224, 215));
        db.setFont(db.getFont().deriveFont(11f));
        footer.add(status);
        footer.add(Box.createVerticalStrut(4));
        footer.add(db);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton menuButton(String text) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(Color.WHITE);
        b.setBackground(UiUtils.PRIMARY);
        b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void mostrar(String nome) {
        cardLayout.show(content, nome);
        JComponent comp = screens.get(nome);
        if (comp instanceof Refreshable r) r.refreshData();
        atualizarStatus();
    }

    private void atualizarStatus() {
        boolean ok = ConnectionFactory.testConnection();
        status.setText(ok ? "● MySQL conectado" : "● MySQL desconectado");
    }
}
