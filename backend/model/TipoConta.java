package model;

public enum TipoConta {
    HOSPEDE("Hóspede"),
    ANFITRIAO("Anfitrião"),
    ADMINISTRADOR("Administrador");

    private final String descricao;

    TipoConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoConta obterPorDescricao(String desc) {
        for (TipoConta t : values()) {
            if (t.descricao.equalsIgnoreCase(desc) || t.name().equalsIgnoreCase(desc)) {
                return t;
            }
        }
        return HOSPEDE; // Fallback padrão
    }
}
