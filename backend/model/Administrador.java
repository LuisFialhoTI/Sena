package model;

public class Administrador extends Usuario {
    public Administrador(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, TipoConta.ADMINISTRADOR);
    }
}
