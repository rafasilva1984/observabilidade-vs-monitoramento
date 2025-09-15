package com.observabilidade.lab;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Random;

public class AppJavaApplication {

    private static final Random RAND = new Random();

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/ok", AppJavaApplication::okHandler);
        server.createContext("/checkout", AppJavaApplication::checkoutHandler);

        server.setExecutor(null);
        System.out.println(ts() + " App iniciado na porta " + port);
        server.start();
    }

    private static void okHandler(HttpExchange ex) throws IOException {
        String resp = "OK";
        log("INFO", "/ok accessed");
        writeResponse(ex, 200, resp);
    }

    private static void checkoutHandler(HttpExchange ex) throws IOException {
        int chance = RAND.nextInt(100);
        try {
            if (chance < 30) {
                // 30% erro 500
                log("ERROR", "Erro de processamento no /checkout (simulado)");
                writeResponse(ex, 500, "Falha no processamento do pedido (simulado)");
                return;
            } else if (chance < 70) {
                // 40% lentidão (1.5s a 4s)
                long sleep = 1500 + RAND.nextInt(2500);
                log("WARN", "Checkout lento: " + sleep + "ms");
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            } else {
                log("INFO", "Checkout rápido");
            }
            writeResponse(ex, 200, "Checkout processado");
        } catch (Exception e) {
            log("ERROR", "Exceção inesperada: " + e.getMessage());
            writeResponse(ex, 500, "Erro inesperado");
        }
    }

    private static void writeResponse(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void log(String level, String msg) {
        System.out.println(ts() + " [" + level + "] " + msg);
    }

    private static String ts() {
        return Instant.now().toString();
    }
}
