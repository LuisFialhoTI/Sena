package gui;

import com.sun.net.httpserver.HttpServer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import database.DatabaseConnection;
import controller.*;

public class ServerConsoleFrame extends JFrame {
    private HttpServer server;
    private Thread serverThread;
    private boolean isServerRunning = false;

    private JButton btnStart;
    private JButton btnStop;
    private JButton btnTestDb;
    private JLabel lblStatus;
    private JLabel lblDbStatus;
    private JTextArea txtLog;

    public ServerConsoleFrame() {
        setTitle("SENA - Painel do Servidor Backend");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        redirectSystemStreams();
        log("[Console] Painel administrativo inicializado.");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Panel (Server Control)
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlHeader.setBorder(BorderFactory.createTitledBorder("Controle do Servidor Web"));

        btnStart = new JButton("Iniciar Servidor");
        btnStop = new JButton("Parar Servidor");
        btnStop.setEnabled(false);

        lblStatus = new JLabel("STATUS: OFFLINE");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatus.setForeground(Color.RED);

        pnlHeader.add(btnStart);
        pnlHeader.add(btnStop);
        pnlHeader.add(lblStatus);

        // Center Panel (Logs Console)
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Console de Logs (Tempo Real)"));

        // Footer Panel (Database Status)
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlFooter.setBorder(BorderFactory.createEtchedBorder());

        btnTestDb = new JButton("Testar Conexão Supabase");
        lblDbStatus = new JLabel("Banco de Dados: Não testado");
        lblDbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        pnlFooter.add(lblDbStatus);
        pnlFooter.add(btnTestDb);

        add(pnlHeader, BorderLayout.NORTH);
        add(scrollLog, BorderLayout.CENTER);
        add(pnlFooter, BorderLayout.SOUTH);

        // Action Listeners
        btnStart.addActionListener(e -> startServer());
        btnStop.addActionListener(e -> stopServer());
        btnTestDb.addActionListener(e -> testDatabaseConnection());
    }

    private void startServer() {
        if (isServerRunning) return;

        btnStart.setEnabled(false);
        lblStatus.setText("STATUS: INICIANDO...");
        lblStatus.setForeground(Color.ORANGE);

        serverThread = new Thread(() -> {
            try {
                server = HttpServer.create(new InetSocketAddress(8080), 0);
                
                // Configure Controllers
                server.createContext("/api/login", new LoginHandler());
                server.createContext("/api/cadastro", new CadastroHandler());
                server.createContext("/api/cadastro-imovel", new CadastroImovelHandler());
                server.createContext("/api/chat", new ChatHandler());
                server.createContext("/api/avaliar", new AvaliarHandler());
                
                // Fetch property list endpoint
                server.createContext("/api/imoveis", ex -> {
                    try {
                        List<model.Imovel> list = new database.ImovelDAO().getAll();
                        List<String> jsons = new java.util.ArrayList<>();
                        for (model.Imovel im : list) jsons.add(im.toJson());
                        String res = "[" + String.join(",", jsons) + "]";
                        
                        byte[] bytes = res.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        ex.sendResponseHeaders(200, bytes.length);
                        try (OutputStream os = ex.getResponseBody()) {
                            os.write(bytes);
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                        byte[] bytes = "{\"msg\":\"Erro ao buscar imoveis.\"}".getBytes();
                        ex.sendResponseHeaders(500, bytes.length);
                        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
                    }
                });

                // Static file routing (Fallback)
                server.createContext("/", new StaticFileHandler());

                server.setExecutor(null);
                server.start();

                isServerRunning = true;
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("STATUS: ONLINE (Porta 8080)");
                    lblStatus.setForeground(new Color(12, 177, 119)); // Emerald Green
                    btnStop.setEnabled(true);
                    System.out.println("[Servidor] Servidor HTTP iniciado com sucesso na porta 8080.");
                    System.out.println("[Servidor] Acesse: http://localhost:8080/login.html");
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("STATUS: ERRO AO INICIAR");
                    lblStatus.setForeground(Color.RED);
                    btnStart.setEnabled(true);
                    System.out.println("[Erro] Falha ao iniciar o servidor: " + ex.getMessage());
                });
            }
        });
        serverThread.start();
    }

    private void stopServer() {
        if (!isServerRunning || server == null) return;

        btnStop.setEnabled(false);
        lblStatus.setText("STATUS: PARANDO...");
        lblStatus.setForeground(Color.ORANGE);

        try {
            server.stop(0);
            server = null;
            isServerRunning = false;
            btnStart.setEnabled(true);
            lblStatus.setText("STATUS: OFFLINE");
            lblStatus.setForeground(Color.RED);
            System.out.println("[Servidor] Servidor HTTP interrompido.");
        } catch (Exception ex) {
            System.out.println("[Erro] Erro ao parar o servidor: " + ex.getMessage());
            btnStop.setEnabled(true);
        }
    }

    private void testDatabaseConnection() {
        lblDbStatus.setText("Testando conexão...");
        lblDbStatus.setForeground(Color.BLACK);
        
        new Thread(() -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                if (conn != null && !conn.isClosed()) {
                    SwingUtilities.invokeLater(() -> {
                        lblDbStatus.setText("Supabase: CONECTADO COM SUCESSO");
                        lblDbStatus.setForeground(new Color(12, 177, 119));
                        System.out.println("[Banco] Conexão JDBC testada com sucesso!");
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        lblDbStatus.setText("Supabase: FALHA DE CONEXÃO");
                        lblDbStatus.setForeground(Color.RED);
                        System.out.println("[Banco] Falha ao estabelecer conexão.");
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    lblDbStatus.setText("Supabase: ERRO (" + ex.getClass().getSimpleName() + ")");
                    lblDbStatus.setForeground(Color.RED);
                    System.out.println("[Banco] Erro no teste de conexão: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                log(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                log(new String(b, off, len));
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}
