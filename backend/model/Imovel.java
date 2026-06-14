package model;

import java.util.ArrayList;
import java.util.List;

public class Imovel {
    private int id;
    private String titulo;
    private String localizacao;
    private String preco;
    private String imagem;
    private String detalhes;
    private String rating;
    private String badges;
    private String estrelas;
    
    // Campos adicionais para a visualização de detalhes
    private String endereco;
    private String descricao;
    private String comodidades; 
    private String scores;      
    private String proprietario;
    
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    public Imovel(int id, String titulo, String localizacao, String preco, String imagem, 
                  String detalhes, String rating, String badges, String estrelas,
                  String endereco, String descricao, String comodidades, String scores, String proprietario) {
        this.id = id;
        this.titulo = titulo;
        this.localizacao = localizacao;
        this.preco = preco;
        this.imagem = imagem;
        this.detalhes = detalhes;
        this.rating = rating;
        this.badges = badges;
        this.estrelas = estrelas;
        this.endereco = endereco;
        this.descricao = descricao;
        this.comodidades = comodidades;
        this.scores = scores;
        this.proprietario = proprietario;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getPreco() { return preco; }
    public void setPreco(String preco) { this.preco = preco; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getBadges() { return badges; }
    public void setBadges(String badges) { this.badges = badges; }

    public String getEstrelas() { return estrelas; }
    public void setEstrelas(String estrelas) { this.estrelas = estrelas; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getComodidades() { return comodidades; }
    public void setComodidades(String comodidades) { this.comodidades = comodidades; }

    public String getScores() { return scores; }
    public void setScores(String scores) { this.scores = scores; }

    public String getProprietario() { return proprietario; }
    public void setProprietario(String proprietario) { this.proprietario = proprietario; }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Avaliacao> avaliacoes) { this.avaliacoes = avaliacoes; }
    
    public void adicionarAvaliacao(Avaliacao av) {
        this.avaliacoes.add(av);
        recalcularScoreMedio();
    }
    
    private void recalcularScoreMedio() {
        if (avaliacoes.isEmpty()) return;
        double somaGeral = 0;
        double somaSeg = 0;
        for (Avaliacao a : avaliacoes) {
            somaGeral += a.getAvaliacaoGeral();
            somaSeg += a.getSegurancaBairro();
        }
        double mediaGeral = somaGeral / avaliacoes.size();
        double mediaSeg = somaSeg / avaliacoes.size();
        
        this.estrelas = String.format(java.util.Locale.US, "%.1f", mediaGeral);
        this.rating = String.format(java.util.Locale.US, "%.1f", mediaSeg);
        
        // Também atualiza a string de scores: "7.5,9.2,8.8,8.7" onde o primeiro é a segurança
        String[] scs = (this.scores != null ? this.scores.split(",") : new String[]{"7.5", "9.2", "8.8", "8.7"});
        if (scs.length >= 1) {
            scs[0] = String.format(java.util.Locale.US, "%.1f", mediaSeg);
        }
        this.scores = String.join(",", scs);
    }

    public String toJson() {
        List<String> listAvJson = new ArrayList<>();
        for (Avaliacao av : avaliacoes) {
            listAvJson.add(av.toJson());
        }
        String reviewsJson = "[" + String.join(",", listAvJson) + "]";
        
        // Escapa aspas na descrição para evitar erros de parser json
        String escapedDesc = (descricao != null) ? descricao.replace("\"", "\\\"").replace("\n", "\\n") : "";

        return String.format(java.util.Locale.US,
            "{\"id\":%d, \"titulo\":\"%s\", \"localizacao\":\"%s\", \"preco\":\"%s\", \"imagem\":\"%s\", " +
            "\"detalhes\":\"%s\", \"rating\":\"%s\", \"badges\":\"%s\", \"estrelas\":\"%s\", \"endereco\":\"%s\", " +
            "\"descricao\":\"%s\", \"comodidades\":\"%s\", \"scores\":\"%s\", \"proprietario\":\"%s\", \"reviews\":%s}", 
            id, titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas, 
            (endereco != null ? endereco : ""), escapedDesc, (comodidades != null ? comodidades : ""), 
            (scores != null ? scores : ""), (proprietario != null ? proprietario : ""), reviewsJson);
    }
}
