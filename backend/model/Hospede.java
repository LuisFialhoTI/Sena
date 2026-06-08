package model;

public class Hospede extends Usuario {
    public Hospede(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, TipoConta.HOSPEDE);
    }
}
