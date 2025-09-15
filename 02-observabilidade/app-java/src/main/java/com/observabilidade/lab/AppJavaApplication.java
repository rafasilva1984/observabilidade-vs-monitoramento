package com.observabilidade.lab;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Random;

// API pública do Elastic APM
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import co.elastic.apm.api.Span;
import co.elastic.apm.api.Scope;

public class AppJavaApplication {

    private static final Random RAND = new Random();

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", AppJavaApplication::rootHandler);
        server.createContext("/ok", AppJavaApplication::okHandler);
        server.createContext("/checkout", AppJavaApplication::checkoutHandler);

        server.setExecutor(null);
        System.out.println(ts() + " App iniciado na porta " + port);
        server.start();
    }

    private static void rootHandler(HttpExchange ex) throws IOException {
        writeResponse(ex, 404, "Use /ok ou /checkout");
    }

    private static void okHandler(HttpExchange ex) throws IOException {
        Transaction txn = startTxn(ex, "/ok");
        try (Scope s = txn.activate()) {
            log("INFO", "/ok accessed");
            writeResponse(ex, 200, "OK");
            txn.setResult("HTTP 2xx");
        } catch (Exception e) {
            txn.captureException(e);
            txn.setResult("HTTP 5xx");
            throw e;
        } finally {
            txn.end();
        }
    }

    private static void checkoutHandler(HttpExchange ex) throws IOException {
        Transaction txn = startTxn(ex, "/checkout");
        try (Scope s = txn.activate()) {

            int chance = RAND.nextInt(100);

            if (chance < 30) {
                // 30% erro 500
                log("ERROR", "Erro de processamento no /checkout (simulado)");
                txn.setResult("HTTP 5xx");
                txn.captureException(new RuntimeException("Falha no processamento do pedido (simulado)"));
                writeResponse(ex, 500, "Falha no processamento do pedido (simulado)");
                return;
            }

            if (chance < 70) {
                // 40% lentidão (1.5s a 4s)
                long sleep = 1500 + RAND.nextInt(2500);
                Span span = txn.startSpan("work", "sleep", "simulated");
                span.setName("processamento demorado");
                log("WARN", "Checkout lento: " + sleep + "ms");
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ignored) {}
                span.end();
            } else {
                log("INFO", "Checkout rápido");
            }

            writeResponse(ex, 200, "Checkout processado");
            txn.setResult("HTTP 2xx");

        } catch (Exception e) {
            txn.captureException(e);
            txn.setResult("HTTP 5xx");
            writeResponse(ex, 500, "Erro inesperado");
        } finally {
            txn.end();
        }
    }

    private static Transaction startTxn(HttpExchange ex, String name) {
        Transaction txn = ElasticApm.startTransaction();
        txn.setName(name);
        txn.setType("request");
        txn.setLabel("path", ex.getRequestURI().getPath());
        txn.setLabel("method", ex.getRequestMethod());
        return txn;
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
