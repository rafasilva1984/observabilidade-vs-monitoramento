package com.observabilidade.lab.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
public class HealthController {
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    private static final Random RAND = new Random();

    @GetMapping("/ok")
    public String ok() {
        log.info("Endpoint /ok accessed");
        return "OK";
    }

    @GetMapping("/checkout")
    public String checkout() throws InterruptedException {
        int chance = RAND.nextInt(100);
        // 30% chance de erro 500, 40% de lentidão
        if (chance < 30) {
            log.error("Erro de processamento no /checkout (simulado)");
            throw new RuntimeException("Falha no processamento do pedido (simulado)");
        } else if (chance < 70) {
            long sleep = 1500 + RAND.nextInt(2500);
            log.warn("Checkout lento: {}ms", sleep);
            Thread.sleep(sleep);
        } else {
            log.info("Checkout rápido");
        }
        return "Checkout processado";
    }
}
