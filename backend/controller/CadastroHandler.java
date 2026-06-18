package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import database.UsuarioDAO;
import model.Hospede;
import model.Anfitriao;
import model.Administrador;
import model.TipoConta;
import model.Usuario;

/**
 * [PADRÃO MVC - CONTROLLER]
 * Esta classe é o controlador responsável pelo cadastro de novos usuários.
 * 
 * [CONCEITO DE HERANÇA]
 * Ao usar "extends AbstractHttpHandler", herdamos métodos prontos para ler requisições e enviar respostas.
 */
public class CadastroHandler extends AbstractHttpHandler {
    
    // Objeto de Acesso a Dados (Padrão DAO) para interagir com a persistência de Usuários
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // [POLIMORFISMO DE SOBRESCRITA] Sobrescrevemos o método 'handle' herdado do servidor HTTP do Java
    @Override
    public void handle(HttpExchange conexao) throws IOException {
        // [FILTRO DE PROTOCOLO] Apenas processamos requisições se forem do tipo POST (envio de dados)
        if ("POST".equals(conexao.getRequestMethod())) {
            
            // [TRATAMENTO DE EXCEÇÕES] Bloco try-catch que monitora e trata erros em tempo de execução
            try {
                // [JAVA IO] Obtemos o corpo de texto bruto enviado pelo navegador do usuário
                String corpo = lerCorpoRequisicao(conexao);
                
                // Dividimos a string em partes usando o delimitador ":" (ex: nome:email:senha:tipo)
                String[] partes = corpo.split(":");
                if (partes.length < 4) {
                    responderErro(conexao, "Corpo da requisicao invalido para cadastro.", 400);
                    return;
                }
                
                // Limpamos espaços extras nos dados obtidos
                String nome = partes[0].trim();
                String email = partes[1].trim();
                String senha = partes[2].trim();
                String tipoStr = partes[3].trim();

                // [REGRA DE NEGÓCIO] Verificamos no banco se o e-mail informado já existe
                if (usuarioDAO.getByEmail(email) != null) {
                    responderErro(conexao, "Ja existe um usuario cadastrado com este e-mail.", 400);
                    return;
                }

                // [ENUM EM JAVA] TipoConta é um Enum. Convertemos o texto recebido na respectiva constante do Enum.
                TipoConta tipo = TipoConta.obterPorDescricao(tipoStr);
                
                // [CLASSES ABSTRATAS]
                // 'Usuario' é uma classe abstrata e não pode ser instanciada com 'new' diretamente.
                // Mas declaramos uma variável do tipo 'Usuario' que pode apontar para qualquer subclasse.
                Usuario usuario;
                
                // [POLIMORFISMO DE SUBTIPAGEM (Polimorfismo de Herança)]
                // Dependendo do tipo de conta selecionado no Enum, instanciamos a subclasse correta.
                // Atribuímos uma classe especializada (como Anfitriao) para a variável genérica (Usuario).
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

                // [ENCAPSULAMENTO] Salvamos o usuário recém-criado usando o DAO
                usuarioDAO.save(usuario);

                // [CONCEITO DE THREADS]
                // Disparamos uma Thread paralela assíncrona para simular o envio de um e-mail de confirmação.
                // Isso roda em segundo plano sem travar a resposta da tela para o usuário (processamento em background).
                new Thread(() -> {
                    try {
                        Thread.sleep(3000); // Simula o atraso de envio do e-mail por 3 segundos
                        System.out.println("[E-mail] Mensagem de boas-vindas enviada com sucesso para: " + email);
                    } catch (InterruptedException erroThread) {
                        erroThread.printStackTrace();
                    }
                }).start();

                // Retorna a resposta de sucesso em formato JSON
                responderJSON(conexao, "{\"msg\": \"Cadastro realizado com sucesso!\"}", 200);
            } catch (Exception erro) {
                // Captura erros genéricos e imprime a pilha de erro no terminal do console
                erro.printStackTrace();
                responderErro(conexao, "Erro ao realizar o cadastro: " + erro.getMessage(), 500);
            }
        } else {
            responderErro(conexao, "Metodo nao suportado.", 405);
        }
    }
}
