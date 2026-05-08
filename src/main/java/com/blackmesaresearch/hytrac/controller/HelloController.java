package com.blackmesaresearch.hytrac.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/ouch")
    public String ouch() {
        return "ow that hurts";
    }
}
