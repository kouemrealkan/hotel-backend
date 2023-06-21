package com.alkan.definitionservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/definitions")
public class DefinitionController {
    @GetMapping
    public String test() {
        return "test";
    }
}
