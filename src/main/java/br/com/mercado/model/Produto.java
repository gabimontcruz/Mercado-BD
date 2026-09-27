package br.com.mercado.model;

import java.math.BigDecimal;

public class Produto {
    private int codigo;
    private String marca;
    private String nome;
    private BigDecimal preco;
    private String unidadeMedida;
    private int codigoCategoria;
    private String nomeCategoria;

    public Produto() {
    }

    public Produto(int codigo, String marca, String nome, BigDecimal preco,
                   String unidadeMedida, int codigoCategoria) {
        this.codigo = codigo;
        this.marca = marca;
        this.nome = nome;
        this.preco = preco;
        this.unidadeMedida = unidadeMedida;
        this.codigoCategoria = codigoCategoria;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public int getCodigoCategoria() { return codigoCategoria; }
    public void setCodigoCategoria(int codigoCategoria) { this.codigoCategoria = codigoCategoria; }
    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
}
