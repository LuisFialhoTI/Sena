package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.ImovelDAO;
import model.Imovel;

public class CadastroImovelHandler extends AbstractHttpHandler {
    private final ImovelDAO imovelDAO = new ImovelDAO();

    @Override
    public void handle(HttpExchange conexao) throws IOException {
        if ("POST".equals(conexao.getRequestMethod())) {
            try {
                String corpo = lerCorpoRequisicao(conexao);
                String[] partes = corpo.split(":");
                if (partes.length < 4) {
                    responderErro(conexao, "Corpo da requisicao invalido para cadastro de imovel.", 400);
                    return;
                }
                String titulo = partes[0].trim();
                String localizacao = partes[1].trim();
                String preco = partes[2].trim();
                String imagem = partes[3].trim();

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

                Imovel imovel = new Imovel(0, titulo, localizacao, preco, imagem, detalhes, rating, badges, estrelas,
                                       endereco, descricao, comodidades, scores, proprietario);
                imovelDAO.save(imovel);
                
                responderJSON(conexao, "{\"msg\": \"Imovel cadastrado com sucesso!\"}", 200);
            } catch (Exception erro) {
                erro.printStackTrace();
                responderErro(conexao, "Erro ao cadastrar imovel: " + erro.getMessage(), 500);
            }
        } else {
            responderErro(conexao, "Metodo nao suportado.", 405);
        }
    }
}
