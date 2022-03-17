package com.example.spring.controller

import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/vacina")
class AppController {


    @GetMapping
    fun all(): String {
        return "Funciona"
    }
}