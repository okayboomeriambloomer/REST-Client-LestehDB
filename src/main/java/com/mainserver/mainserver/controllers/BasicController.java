package com.mainserver.mainserver.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class BasicController {

    @GetMapping
    public String getHomePage() {
        return "homepage";
    }

    @GetMapping("/debug")
    public ResponseEntity<?> getDebugPage() {
        RestTemplate restTemplate = new RestTemplate();
        String answer = restTemplate.getForObject("http://localhost:8080/core", String.class);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @GetMapping("/mainexport")
    public String getExportPage(Model model){
        return "mainexport";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "/administration/admin";
    }
}
