package model;

public abstract class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private TipoConta tipo;

    public Usuario(int id, String nome, String email, String senha, TipoConta tipo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public TipoConta getTipo() { return tipo; }
    public void setTipo(TipoConta tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", nome, tipo.getDescricao(), email);
    }
}
