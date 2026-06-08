package model;

public class Anfitriao extends Usuario {
    public Anfitriao(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, TipoConta.ANFITRIAO);
    }
}
