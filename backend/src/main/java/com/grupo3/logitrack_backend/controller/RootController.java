package com.grupo3.logitrack_backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  @GetMapping("/")
  public Map<String, String> home() {
    return Map.of(
        "app", "LogiTrack API",
        "status", "ok",
        "swagger", "/swagger-ui/index.html",
        "healthHint", "Use /api/ouch or /api/envios");
  }
}
