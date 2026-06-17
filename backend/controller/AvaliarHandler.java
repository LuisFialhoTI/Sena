package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.ImovelDAO;
import model.Avaliacao;
import model.Imovel;

/**
 * [PADRÃO MVC - CONTROLLER]
 * Esta classe é o controlador responsável pela ação de avaliar um imóvel.
 * Ela estende 'AbstractHttpHandler' (Herança) e sobrescreve o método 'handle' (Polimorfismo).
 */
public class AvaliarHandler extends AbstractHttpHandler {
    
    // Objeto de Acesso a Dados (Padrão DAO) para interagir com a persistência de Imóveis
    private final ImovelDAO imovelDAO = new ImovelDAO();

    @Override
    public void handle(HttpExchange conexao) throws IOException {
        // [REQUISIÇÃO HTTP] Apenas processa se for um envio de dados via método POST
        if ("POST".equals(conexao.getRequestMethod())) {
            try {
                // [JAVA IO] Lê o corpo de texto (payload) enviado pelo front-end
                String corpo = lerCorpoRequisicao(conexao);
                String[] partes = corpo.split(":");
                
                // Valida se o formato do corpo contém os campos necessários
                if (partes.length < 8) {
                    responderErro(conexao, "Corpo da requisicao de avaliacao invalido.", 400);
                    return;
                }
                
                // [CONVERSÃO DE DADOS] Converte as strings recebidas em números inteiros
                int imovelId = Integer.parseInt(partes[0].trim());
                int avaliacaoGeral = Integer.parseInt(partes[1].trim());
                int segurancaBairro = Integer.parseInt(partes[2].trim());
                int segurancaRua = Integer.parseInt(partes[3].trim());
                int comodidade = Integer.parseInt(partes[4].trim());
                int localizacao = Integer.parseInt(partes[5].trim());
                String tags = partes[6].trim();
                
                // [STRING BUILDER] Reconstrói o texto de comentário caso o próprio comentário possua o caractere de dois-pontos
                StringBuilder construtorComentario = new StringBuilder();
                for (int i = 7; i < partes.length; i++) {
                    if (i > 7) construtorComentario.append(":");
                    construtorComentario.append(partes[i]);
                }
                String comentario = construtorComentario.toString().trim();

                // [DAO / BUSCA] Recupera o imóvel que está sendo avaliado do banco de dados
                Imovel imovel = imovelDAO.getById(imovelId);
                if (imovel == null) {
                    responderErro(conexao, "Imovel nao encontrado.", 404);
                    return;
                }

                // [CLASSES E OBJETOS / CONSTRUTOR] Instancia um novo objeto da classe 'Avaliacao'
                Avaliacao avaliacao = new Avaliacao(0, imovelId, avaliacaoGeral, segurancaBairro, segurancaRua, comodidade, localizacao, tags, comentario, "");
                imovelDAO.saveAvaliacao(avaliacao);

                // [POLIMORFISMO DE SOBRESCRITA / REGRAS DE NEGÓCIO]
                // Adiciona a avaliação ao modelo 'Imovel' para recalcular as médias de notas e segurança
                imovel.adicionarAvaliacao(avaliacao);
                imovelDAO.update(imovel);

                // Retorna sucesso para o navegador do cliente em formato JSON
                responderJSON(conexao, "{\"msg\": \"Avaliacao enviada com sucesso!\"}", 200);
            } catch (NumberFormatException erro) {
                // Captura especificamente erros se a nota enviada não for um número válido
                responderErro(conexao, "Formato numerico invalido nos campos de avaliacao.", 400);
            } catch (Exception erro) {
                // Captura qualquer outro erro geral e exibe a pilha de chamadas no console do servidor
                erro.printStackTrace();
                responderErro(conexao, "Erro ao processar avaliacao: " + erro.getMessage(), 500);
            }
        } else {
            responderErro(conexao, "Metodo nao suportado.", 405);
        }
    }
}
