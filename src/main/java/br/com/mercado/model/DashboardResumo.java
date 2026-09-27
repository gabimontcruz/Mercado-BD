package br.com.mercado.model;

import java.math.BigDecimal;

public record DashboardResumo(long vendas, BigDecimal faturamento, BigDecimal ticketMedio, long clientes) {
}
