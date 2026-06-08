import javax.swing.SwingUtilities;
import gui.ServerConsoleFrame;

public class SENAWeb {
    public static void main(String[] args) {
        // Check for --headless argument to run without GUI
        boolean headless = false;
        for (String arg : args) {
            if ("--headless".equalsIgnoreCase(arg)) {
                headless = true;
                break;
            }
        }

        if (headless) {
            System.out.println("[SENAWeb] Iniciando em modo CLI/Headless...");
            try {
                com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
                    new java.net.InetSocketAddress(8080), 0);
                
                server.createContext("/api/login", new controller.LoginHandler());
                server.createContext("/api/cadastro", new controller.CadastroHandler());
                server.createContext("/api/cadastro-imovel", new controller.CadastroImovelHandler());
                server.createContext("/api/chat", new controller.ChatHandler());
                server.createContext("/api/avaliar", new controller.AvaliarHandler());
                
                server.createContext("/api/imoveis", ex -> {
                    try {
                        java.util.List<model.Imovel> list = new database.ImovelDAO().getAll();
                        java.util.List<String> jsons = new java.util.ArrayList<>();
                        for (model.Imovel im : list) jsons.add(im.toJson());
                        String res = "[" + String.join(",", jsons) + "]";
                        
                        byte[] bytes = res.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        ex.sendResponseHeaders(200, bytes.length);
                        try (java.io.OutputStream os = ex.getResponseBody()) {
                            os.write(bytes);
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                        byte[] bytes = "{\"msg\":\"Erro ao buscar imoveis.\"}".getBytes();
                        ex.sendResponseHeaders(500, bytes.length);
                        try (java.io.OutputStream os = ex.getResponseBody()) { os.write(bytes); }
                    }
                });

                server.createContext("/", new controller.StaticFileHandler());
                server.setExecutor(null);
                server.start();

                System.out.println("======================================================");
                System.out.println("            SERVIDOR SENA ONLINE INICIADO (CLI)        ");
                System.out.println("       Acesse: http://localhost:8080/login.html       ");
                System.out.println("======================================================");
            } catch (Exception e) {
                System.err.println("Erro ao iniciar servidor headless: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[SENAWeb] Iniciando em modo GUI (Swing)...");
            SwingUtilities.invokeLater(() -> {
                try {
                    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Ignore
                }
                ServerConsoleFrame frame = new ServerConsoleFrame();
                frame.setVisible(true);
            });
        }
    }
}