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
    public void handle(HttpExchange conexao) throws IOException {
        if ("POST".equals(conexao.getRequestMethod())) {
            try {
                String corpo = lerCorpoRequisicao(conexao);
                String[] partes = corpo.split(":");
                if (partes.length < 4) {
                    responderErro(conexao, "Corpo da requisicao invalido para cadastro.", 400);
                    return;
                }
                String nome = partes[0].trim();
                String email = partes[1].trim();
                String senha = partes[2].trim();
                String tipoStr = partes[3].trim();

                if (usuarioDAO.getByEmail(email) != null) {
                    responderErro(conexao, "Ja existe um usuario cadastrado com este e-mail.", 400);
                    return;
                }

                TipoConta tipo = TipoConta.obterPorDescricao(tipoStr);
                Usuario usuario;
                switch (tipo) {
                    case ADMINISTRADOR:
                        usuario = new Administrador(0, nome, email, senha);
                        break;
                    case ANFITRIAO:
                        usuario = new Anfitriao(0, nome, email, senha);
                        break;
                    case HOSPEDE:
                    default:
                        usuario = new Hospede(0, nome, email, senha);
                        break;
                }

                usuarioDAO.save(usuario);
                responderJSON(conexao, "{\"msg\": \"Cadastro realizado com sucesso!\"}", 200);
            } catch (Exception erro) {
                erro.printStackTrace();
                responderErro(conexao, "Erro ao realizar o cadastro: " + erro.getMessage(), 500);
            }
        } else {
            responderErro(conexao, "Metodo nao suportado.", 405);
        }
    }
}
