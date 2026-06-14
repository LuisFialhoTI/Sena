package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.ImovelDAO;
import model.Avaliacao;
import model.Imovel;

public class AvaliarHandler extends AbstractHttpHandler {
    private final ImovelDAO imovelDAO = new ImovelDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                String body = lerBody(exchange);
                String[] p = body.split(":");
                if (p.length < 8) {
                    responderErro(exchange, "Corpo da requisicao de avaliacao invalido.", 400);
                    return;
                }
                int imovelId = Integer.parseInt(p[0].trim());
                int avaliacaoGeral = Integer.parseInt(p[1].trim());
                int segurancaBairro = Integer.parseInt(p[2].trim());
                int segurancaRua = Integer.parseInt(p[3].trim());
                int comodidade = Integer.parseInt(p[4].trim());
                int localizacao = Integer.parseInt(p[5].trim());
                String tags = p[6].trim();
                
                // Junta novamente o comentário se ele contiver dois-pontos
                StringBuilder commentBuilder = new StringBuilder();
                for (int i = 7; i < p.length; i++) {
                    if (i > 7) commentBuilder.append(":");
                    commentBuilder.append(p[i]);
                }
                String comentario = commentBuilder.toString().trim();

                Imovel im = imovelDAO.getById(imovelId);
                if (im == null) {
                    responderErro(exchange, "Imovel nao encontrado.", 404);
                    return;
                }

                Avaliacao av = new Avaliacao(0, imovelId, avaliacaoGeral, segurancaBairro, segurancaRua, comodidade, localizacao, tags, comentario, "");
                imovelDAO.saveAvaliacao(av);

                // Dispara o recálculo da média no modelo
                im.adicionarAvaliacao(av);
                imovelDAO.update(im);

                responderJSON(exchange, "{\"msg\": \"Avaliacao enviada com sucesso!\"}", 200);
            } catch (NumberFormatException e) {
                responderErro(exchange, "Formato numerico invalido nos campos de avaliacao.", 400);
            } catch (Exception e) {
                e.printStackTrace();
                responderErro(exchange, "Erro ao processar avaliacao: " + e.getMessage(), 500);
            }
        } else {
            responderErro(exchange, "Metodo nao suportado.", 405);
        }
    }
}
