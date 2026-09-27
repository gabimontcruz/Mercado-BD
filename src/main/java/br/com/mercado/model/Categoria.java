package br.com.mercado.model;

public class Categoria {
    private final int codigo;
    private final String nome;

    public Categoria(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
