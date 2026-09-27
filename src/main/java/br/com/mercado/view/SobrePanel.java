package br.com.mercado.view;

import br.com.mercado.util.UiUtils;

import javax.swing.*;
import java.awt.*;

public class SobrePanel extends JPanel {
    public SobrePanel() {
        setLayout(new BorderLayout());
        setBackground(UiUtils.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(area.getFont().deriveFont(15f));
        area.setBackground(Color.WHITE);
        area.setForeground(UiUtils.TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        area.setText("""
                PROJETO DE BANCO DE DADOS — ETAPA 03

                Tema: Sistema de gerenciamento de mercado
                Tecnologias: Java 17, Swing, JDBC e MySQL
                Integração: SQL explícito com Connection, PreparedStatement e ResultSet (sem ORM)

                Requisitos implementados:
                • Inserção, alteração, exclusão e visualização na tabela CLIENTE.
                • Inserção, alteração, exclusão e visualização na tabela PRODUTO.
                • Inserção, alteração, exclusão e visualização na tabela FORNECEDOR.
                • Dashboard com indicadores e quatro visualizações gráficas.
                • Gráfico de barras de produtos mais vendidos.
                • Gráfico de setores por forma de pagamento.
                • Série temporal de faturamento diário.
                • Histograma/distribuição das vendas por faixa de valor.
                • Oito consultas SQL executáveis pela interface.
                • As oito consultas utilizam JOIN e subconsulta, ultrapassando o mínimo de seis.
                • Conceitos usados: INNER JOIN, LEFT JOIN, subconsulta simples, subconsulta correlacionada,
                  NOT EXISTS, GROUP BY, HAVING, SUM, AVG, COUNT, COUNT DISTINCT, COALESCE e GROUP_CONCAT.
                • SQL das consultas documentado em sql/03_consultas.sql e docs/CONSULTAS_EXPLICADAS.md.
                • Nenhum framework/biblioteca de mapeamento objeto-relacional é utilizado.

                Observação sobre PRODUTO:
                produtos que já constam em ITEM_VENDA não podem ser excluídos devido à chave estrangeira
                ON DELETE RESTRICT. Para demonstrar a exclusão, cadastre um novo produto e exclua-o.
                """);

        JLabel title = new JLabel("Sobre / Checklist da entrega");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(UiUtils.TEXT);
        add(title, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}
