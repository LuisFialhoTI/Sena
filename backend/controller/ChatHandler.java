package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class ChatHandler extends AbstractHttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                String msg = lerBody(exchange).toLowerCase();
                String resposta = "Desculpe, nao entendi. Pergunte sobre seguranca, cadastro ou login.";
                if (msg.contains("segurança") || msg.contains("seguranca") || msg.contains("bairro")) {
                    resposta = "No SENA, priorizamos a seguranca! Veja as notas de iluminacao na aba Bairros.";
                } else if (msg.contains("cadastro") || msg.contains("conta")) {
                    resposta = "Clique em 'Cadastrar' no menu superior ou crie uma conta de Hóspede/Anfitrião.";
                } else if (msg.contains("ola") || msg.contains("olá") || msg.contains("oi")) {
                    resposta = "Olá! Como posso ajudar na sua busca por um imóvel seguro hoje?";
                } else if (msg.contains("reserva") || msg.contains("problema")) {
                    resposta = "Entendido! Vou verificar os detalhes da sua reserva agora mesmo. Pode me informar o código da reserva ou o endereço do imóvel?";
                }
                responderJSON(exchange, resposta, 200);
            } catch (Exception e) {
                responderErro(exchange, "Erro ao processar mensagem do chat.", 500);
            }
        } else {
            responderErro(exchange, "Metodo nao suportado.", 405);
        }
    }
}
