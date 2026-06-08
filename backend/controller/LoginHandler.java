package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.UsuarioDAO;
import model.Usuario;

public class LoginHandler extends AbstractHttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                String body = lerBody(exchange);
                String[] p = body.split(":");
                if (p.length < 2) {
                    responderErro(exchange, "Corpo da requisicao invalido.", 400);
                    return;
                }
                String email = p[0].trim();
                String senha = p[1].trim();

                Usuario user = usuarioDAO.getByEmail(email);
                if (user != null && user.getSenha().equals(senha)) {
                    String json = String.format("{\"msg\": \"Login com sucesso!\", \"tipo\": \"%s\"}", user.getTipo().getDescricao());
                    responderJSON(exchange, json, 200);
                } else {
                    responderErro(exchange, "Credenciais invalidas.", 401);
                }
            } catch (Exception e) {
                e.printStackTrace();
                responderErro(exchange, "Erro interno do servidor: " + e.getMessage(), 500);
            }
        } else {
            responderErro(exchange, "Metodo nao suportado.", 405);
        }
    }
}
