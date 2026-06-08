package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.UsuarioDAO;
import model.Hospede;
import model.Anfitriao;
import model.Administrador;
import model.TipoConta;
import model.Usuario;

public class CadastroHandler extends AbstractHttpHandler {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                String body = lerBody(exchange);
                String[] p = body.split(":");
                if (p.length < 4) {
                    responderErro(exchange, "Corpo da requisicao invalido para cadastro.", 400);
                    return;
                }
                String nome = p[0].trim();
                String email = p[1].trim();
                String senha = p[2].trim();
                String tipoStr = p[3].trim();

                if (usuarioDAO.getByEmail(email) != null) {
                    responderErro(exchange, "Ja existe um usuario cadastrado com este e-mail.", 400);
                    return;
                }

                TipoConta tipo = TipoConta.obterPorDescricao(tipoStr);
                Usuario user;
                switch (tipo) {
                    case ADMINISTRADOR:
                        user = new Administrador(0, nome, email, senha);
                        break;
                    case ANFITRIAO:
                        user = new Anfitriao(0, nome, email, senha);
                        break;
                    case HOSPEDE:
                    default:
                        user = new Hospede(0, nome, email, senha);
                        break;
                }

                usuarioDAO.save(user);
                responderJSON(exchange, "{\"msg\": \"Cadastro realizado com sucesso!\"}", 200);
            } catch (Exception e) {
                e.printStackTrace();
                responderErro(exchange, "Erro ao realizar o cadastro: " + e.getMessage(), 500);
            }
        } else {
            responderErro(exchange, "Metodo nao suportado.", 405);
        }
    }
}
