package br.com.mercado.model;

import java.util.List;

public record TableData(List<String> columns, List<Object[]> rows) {
}
