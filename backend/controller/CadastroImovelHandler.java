package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.ImovelDAO;
import model.Imovel;

public class CadastroImovelHandler extends AbstractHttpHandler {
    private final ImovelDAO imovelDAO = new ImovelDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                String body = lerBody(exchange);
                String[] p = body.split(":");
                if (p.length < 4) {
                    responderErro(exchange, "Corpo da requisicao invalido para cadastro de imovel.", 400);
                    return;
                }
                String titulo = p[0].trim();
                String localizacao = p[1].trim();
                String preco = p[2].trim();
                String imagem = p[3].trim();

                // Gera valores padrão completos para a nova visualização de detalhes do imóvel
                String detalhes = "2 quartos • 1 banheiro • WiFi";
                String rating = "8.0";
                String badges = "Boa iluminação,Rua boa";
                String estrelas = "4.0";
                String endereco = "Rua Principal, 123 - Centro, São Paulo - SP";
                String descricao = "Excelente moradia recem-cadastrada no portal Sena, localizada em regiao de otimo acesso e boa iluminacao.";
                String comodidades = "Wi-fi,Estacionamento,Pet Friendly,Câmeras";
                String scores = "8.0,8.5,8.0,8.2";
                String proprietario = "Anfitriao Sena";

                Imovel im = new Imovel(0, titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas,
                                       endereco, descricao, comodidades, scores, proprietario);
                imovelDAO.save(im);
                
                responderJSON(exchange, "{\"msg\": \"Imovel cadastrado com sucesso!\"}", 200);
            } catch (Exception e) {
                e.printStackTrace();
                responderErro(exchange, "Erro ao cadastrar imovel: " + e.getMessage(), 500);
            }
        } else {
            responderErro(exchange, "Metodo nao suportado.", 405);
        }
    }
}
