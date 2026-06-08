package controller;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class StaticFileHandler extends AbstractHttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String rota = exchange.getRequestURI().getPath();
        if (rota.equals("/")) {
            rota = "/login.html";
        }
        rotearArquivo(exchange, "frontend" + rota);
    }
}
