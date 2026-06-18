package model;

public class Anfitriao extends Usuario {
    
    public Anfitriao(int id, String nome, String email, String senha) {
        // A palavra 'super' chama o construtor da classe mãe (Usuario)
        super(id, nome, email, senha, TipoConta.ANFITRIAO);
    }
}
