package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class ChatHandler extends AbstractHttpHandler {
    @Override
    public void handle(HttpExchange conexao) throws IOException {
        if ("POST".equals(conexao.getRequestMethod())) {
            try {
                String mensagem = lerCorpoRequisicao(conexao).toLowerCase();
                String resposta = "Desculpe, nao entendi. Pergunte sobre seguranca, cadastro ou login.";
                if (mensagem.contains("segurança") || mensagem.contains("seguranca") || mensagem.contains("bairro")) {
                    resposta = "No SENA, priorizamos a seguranca! Veja as notas de iluminacao na aba Bairros.";
                } else if (mensagem.contains("cadastro") || mensagem.contains("conta")) {
                    resposta = "Clique em 'Cadastrar' no menu superior ou crie uma conta de Hóspede/Anfitrião.";
                } else if (mensagem.contains("ola") || mensagem.contains("olá") || mensagem.contains("oi")) {
                    resposta = "Olá! Como posso ajudar na sua busca por um imóvel seguro hoje?";
                } else if (mensagem.contains("reserva") || mensagem.contains("problema")) {
                    resposta = "Entendido! Vou verificar os detalhes da sua reserva agora mesmo. Pode me informar o código da reserva ou o endereço do imóvel?";
                }
                responderJSON(conexao, resposta, 200);
            } catch (Exception erro) {
                responderErro(conexao, "Erro ao processar mensagem do chat.", 500);
            }
        } else {
            responderErro(conexao, "Metodo nao suportado.", 405);
        }
    }
}
