package model;

public class Avaliacao {
    private int id;
    private int imovelId;
    private int avaliacaoGeral;
    private int segurancaBairro;
    private int segurancaRua;
    private int comodidade;
    private int localizacao;
    private String tags;        // Separado por vírgulas (ex: "Rua bem iluminada,Regiao segura")
    private String comentario;
    private String dataCriacao; // Formato de texto para serialização JSON simples

    // Construtor Padrão
    public Avaliacao(int id, int imovelId, int avaliacaoGeral, int segurancaBairro, int segurancaRua, 
                     int comodidade, int localizacao, String tags, String comentario, String dataCriacao) {
        this.id = id;
        this.imovelId = imovelId;
        this.avaliacaoGeral = avaliacaoGeral;
        this.segurancaBairro = segurancaBairro;
        this.segurancaRua = segurancaRua;
        this.comodidade = comodidade;
        this.localizacao = localizacao;
        this.tags = tags;
        this.comentario = comentario;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getImovelId() { return imovelId; }
    public void setImovelId(int imovelId) { this.imovelId = imovelId; }

    public int getAvaliacaoGeral() { return avaliacaoGeral; }
    public void setAvaliacaoGeral(int avaliacaoGeral) { this.avaliacaoGeral = avaliacaoGeral; }

    public int getSegurancaBairro() { return segurancaBairro; }
    public void setSegurancaBairro(int segurancaBairro) { this.segurancaBairro = segurancaBairro; }

    public int getSegurancaRua() { return segurancaRua; }
    public void setSegurancaRua(int segurancaRua) { this.segurancaRua = segurancaRua; }

    public int getComodidade() { return comodidade; }
    public void setComodidade(int comodidade) { this.comodidade = comodidade; }

    public int getLocalizacao() { return localizacao; }
    public void setLocalizacao(int localizacao) { this.localizacao = localizacao; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public String toJson() {
        String escComentario = (comentario != null) ? comentario.replace("\"", "\\\"").replace("\n", "\\n") : "";
        return String.format(java.util.Locale.US,
            "{\"id\":%d, \"imovelId\":%d, \"avaliacaoGeral\":%d, \"segurancaBairro\":%d, \"segurancaRua\":%d, " +
            "\"comodidade\":%d, \"localizacao\":%d, \"tags\":\"%s\", \"comentario\":\"%s\", \"dataCriacao\":\"%s\"}",
            id, imovelId, avaliacaoGeral, segurancaBairro, segurancaRua, comodidade, localizacao,
            (tags != null ? tags : ""), escComentario, (dataCriacao != null ? dataCriacao : ""));
    }
}
