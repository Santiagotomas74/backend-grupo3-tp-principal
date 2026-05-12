package com.blackmesaresearch.hytrac.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("\n=== HYTRAC INICIALIZADO CORRECTAMENTE ===\n");
    }
}