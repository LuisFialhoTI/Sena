package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractHttpHandler implements HttpHandler {

    protected void responderJSON(HttpExchange exchange, String msg, int status) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void responderErro(HttpExchange exchange, String msg, int status) throws IOException {
        String json = String.format("{\"msg\": \"%s\"}", msg.replace("\"", "\\\""));
        responderJSON(exchange, json, status);
    }

    protected void rotearArquivo(HttpExchange exchange, String caminhoArquivo) throws IOException {
        Path path = Paths.get(caminhoArquivo);
        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            String contentType = "text/html";
            if (caminhoArquivo.endsWith(".css")) {
                contentType = "text/css";
            } else if (caminhoArquivo.endsWith(".js")) {
                contentType = "application/javascript";
            } else if (caminhoArquivo.endsWith(".png")) {
                contentType = "image/png";
            } else if (caminhoArquivo.endsWith(".jpg") || caminhoArquivo.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (caminhoArquivo.endsWith(".svg")) {
                contentType = "image/svg+xml";
            }
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    protected String lerCorpoRequisicao(HttpExchange conexao) throws IOException {
        return new String(conexao.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
