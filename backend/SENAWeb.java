import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class CredenciaisInvalidasException extends Exception {
    public CredenciaisInvalidasException(String msg) { super(msg); }
}

abstract class Usuario {
    private String nome, email, senha;
    public Usuario(String nome, String email, String senha, String tipoConta) {
        this.nome = nome; this.email = email; this.senha = senha;
    }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getNome() { return nome; }
}

class Administrador extends Usuario {
    public Administrador(String nome, String email, String senha) { super(nome, email, senha, "Administrador"); }
}

class Hospede extends Usuario {
    public Hospede(String nome, String email, String senha) { super(nome, email, senha, "Hóspede"); }
}

class SenaAutenticacaoEngine {
    private List<Usuario> tabelaUsuariosDB = new ArrayList<>();
    public SenaAutenticacaoEngine() {
        tabelaUsuariosDB.add(new Hospede("Luís Felipe", "luis@email.com", "Sena123"));
        tabelaUsuariosDB.add(new Administrador("Breno Admin", "admin@sena.com.br", "Admin123"));
    }
    public Usuario autenticar(String email, String senha) throws CredenciaisInvalidasException {
        for (Usuario u : tabelaUsuariosDB) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        throw new CredenciaisInvalidasException("MSG03: Credenciais inválidas. Verifique seus dados e tente novamente.");
    }
    public boolean verificarEmail(String email) {
        for (Usuario u : tabelaUsuariosDB) {
            if (u.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }
}

public class SENAWeb {
    private static SenaAutenticacaoEngine engine = new SenaAutenticacaoEngine();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Rotas apontando diretamente para a pasta frontend mapeada na raiz
        server.createContext("/login.html", ex -> rotearArquivo(ex, "frontend/login.html", "text/html"));
        server.createContext("/recsenha.html", ex -> rotearArquivo(ex, "frontend/recsenha.html", "text/html"));
        server.createContext("/registramento.css", ex -> rotearArquivo(ex, "frontend/registramento.css", "text/css"));

        // Endpoints da API
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/recuperar", new RecuperarHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("=======================================================");
        System.out.println("Servidor SENA rodando com estrutura de pastas!");
        System.out.println("Abra no navegador: http://localhost:8080/login.html");
        System.out.println("=======================================================");
    }

    private static void rotearArquivo(HttpExchange exchange, String caminhoArquivo, String contentType) throws IOException {
        Path path = Paths.get(caminhoArquivo);
        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            System.err.println("❌ Arquivo nao encontrado: " + caminhoArquivo);
            byte[] erroBytes = ("Erro 404: O arquivo '" + caminhoArquivo + "' nao foi encontrado.").getBytes();
            exchange.sendResponseHeaders(404, erroBytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(erroBytes); }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String dados = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String[] partes = dados.split(":");
                try {
                    engine.autenticar(partes[0], partes[1]);
                    responderAPI(exchange, 200, "MSG04: Login realizado com sucesso. Bem-vindo(a) ao SENA!");
                } catch (CredenciaisInvalidasException e) {
                    responderAPI(exchange, 401, e.getMessage());
                }
            }
        }
    }

static class RecuperarHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String email = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            
            if (engine.verificarEmail(email)) {
                // 1. Responde IMEDIATAMENTE para o Front-End (não trava a tela do usuário)
                responderAPI(exchange, 200, "MSG05: E-mail de recuperação de senha enviado.");
                
                // 2. Dispara a Thread em segundo plano para o envio real do e-mail
                new Thread(() -> {
                    try {
                        System.out.println("[Thread de E-mail] Conectando ao servidor SMTP...");
                        Thread.sleep(2500); // Simulando o tempo de processamento da rede
                        System.out.println("[Thread de E-mail] E-mail enviado silenciosamente para: " + email);
                    } catch (InterruptedException e) {
                        System.out.println("Erro na Thread de E-mail: " + e.getMessage());
                    }
                }).start();

            } else {
                responderAPI(exchange, 404, "MSG06: E-mail não encontrado no sistema.");
            }
        }
    }
}

    private static void responderAPI(HttpExchange exchange, int status, String msg) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}