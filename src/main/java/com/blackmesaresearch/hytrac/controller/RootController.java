package com.blackmesaresearch.hytrac.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  @GetMapping("/api")
  public Map<String, String> home() {
    return Map.of(
        "app", "HYTRAC API",
        "status", "ok",
        "swagger", "/api/swagger-ui.html", // This will redirect to /api/swagger-ui/index.html
        "healthHint", "Use /api/ouch or /api/envios");
  }
}
