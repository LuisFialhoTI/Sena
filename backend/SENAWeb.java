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

class Usuario {
    String nome, email, senha, tipo;
    public Usuario(String nome, String email, String senha, String tipo) {
        this.nome = nome; this.email = email; this.senha = senha; this.tipo = tipo;
    }
}

class Imovel {
    String titulo, localizacao, preco, imagem;
    public Imovel(String titulo, String localizacao, String preco, String imagem) {
        this.titulo = titulo; this.localizacao = localizacao; this.preco = preco; this.imagem = imagem;
    }
    public String toJson() {
        return String.format("{\"titulo\":\"%s\", \"localizacao\":\"%s\", \"preco\":\"%s\", \"imagem\":\"%s\"}", 
                              titulo, localizacao, preco, imagem);
    }
}

public class SENAWeb {
    private static List<Usuario> usuarios = new ArrayList<>();
    private static List<Imovel> imoveis = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        usuarios.add(new Usuario("Admin", "admin@sena.com", "123456", "Administrador"));
        usuarios.add(new Usuario("Luís Felipe", "luis@email.com", "Sena123", "Hóspede"));
        
        imoveis.add(new Imovel("Apartamento Moderno - Ipanema", "2 quartos • WiFi", "2.500", "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80"));
        imoveis.add(new Imovel("Studio Aconchegante - Vila Madalena", "1 quarto • Pet-friendly", "1.800", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80"));
        imoveis.add(new Imovel("Casa Familiar - Leblon", "3 quartos • Garagem", "4.200", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&q=80"));

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", ex -> {
            String rota = ex.getRequestURI().getPath();
            if (rota.equals("/")) rota = "/login.html"; 
            rotearArquivo(ex, "frontend" + rota);
        });

        server.createContext("/api/imoveis", ex -> responderJSON(ex, gerarJsonImoveis(), 200));
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/cadastro", new CadastroHandler());
        server.createContext("/api/cadastro-imovel", new CadastroImovelHandler());
        server.createContext("/api/chat", new ChatHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("======================================================");
        System.out.println("            SERVIDOR SENA ONLINE INICIADO             ");
        System.out.println("       Acesse: http://localhost:8080/login.html       ");
        System.out.println("======================================================");
    }

    private static void rotearArquivo(HttpExchange exchange, String caminhoArquivo) throws IOException {
        Path path = Paths.get(caminhoArquivo);
        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            String contentType = "text/html";
            if (caminhoArquivo.endsWith(".css")) contentType = "text/css";
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private static void responderJSON(HttpExchange exchange, String msg, int status) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private static String gerarJsonImoveis() {
        List<String> jsons = new ArrayList<>();
        for (Imovel i : imoveis) jsons.add(i.toJson());
        return "[" + String.join(",", jsons) + "]";
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String[] p = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).split(":");
                Usuario userLogado = usuarios.stream().filter(u -> u.email.equalsIgnoreCase(p[0]) && u.senha.equals(p[1])).findFirst().orElse(null);
                if(userLogado != null) {
                    String json = String.format("{\"msg\": \"Login com sucesso!\", \"tipo\": \"%s\"}", userLogado.tipo);
                    responderJSON(exchange, json, 200);
                } else {
                    responderJSON(exchange, "{\"msg\": \"Credenciais inválidas.\"}", 401);
                }
            }
        }
    }

    static class CadastroHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String[] p = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).split(":");
                usuarios.add(new Usuario(p[0], p[1], p[2], p[3]));
                responderJSON(exchange, "{\"msg\": \"Cadastro realizado com sucesso!\"}", 200);
            }
        }
    }

    static class CadastroImovelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String[] p = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).split(":");
                imoveis.add(new Imovel(p[0], p[1], p[2], p[3]));
                responderJSON(exchange, "{\"msg\": \"Imóvel cadastrado com sucesso!\"}", 200);
            }
        }
    }

    static class ChatHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String msg = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).toLowerCase();
                String resposta = "Desculpe, não entendi. Pergunte sobre segurança, cadastro ou senha.";
                if (msg.contains("segurança") || msg.contains("bairro")) resposta = "No SENA, priorizamos a segurança! Veja as notas de iluminação na aba Bairros.";
                else if (msg.contains("cadastro") || msg.contains("conta")) resposta = "Clique em 'Cadastrar' no menu superior.";
                else if (msg.contains("ola") || msg.contains("olá") || msg.contains("oi")) resposta = "Olá! Como posso ajudar na sua busca por um imóvel hoje?";
                responderJSON(exchange, resposta, 200);
            }
        }
    }
}