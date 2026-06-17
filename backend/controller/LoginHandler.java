package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.UsuarioDAO;
import model.Usuario;

public class LoginHandler extends AbstractHttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void handle(HttpExchange conexao) throws IOException {
        if ("POST".equals(conexao.getRequestMethod())) {
            try {
                String corpo = lerCorpoRequisicao(conexao);
                String[] partes = corpo.split(":");
                if (partes.length < 2) {
                    responderErro(conexao, "Corpo da requisicao invalido.", 400);
                    return;
                }
                String email = partes[0].trim();
                String senha = partes[1].trim();

                Usuario usuario = usuarioDAO.getByEmail(email);
                if (usuario != null && usuario.getSenha().equals(senha)) {
                    String json = String.format("{\"msg\": \"Login com sucesso!\", \"tipo\": \"%s\"}", usuario.getTipo().getDescricao());
                    responderJSON(conexao, json, 200);
                } else {
                    responderErro(conexao, "Credenciais invalidas.", 401);
                }
            } catch (Exception erro) {
                erro.printStackTrace();
                responderErro(conexao, "Erro interno do servidor: " + erro.getMessage(), 500);
            }
        } else {
            responderErro(conexao, "Metodo nao suportado.", 405);
        }
    }
}
