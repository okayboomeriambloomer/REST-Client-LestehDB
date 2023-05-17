package com.mainserver.mainserver.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping
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

    @GetMapping("/chooseServer")
    public String getChooseServerPage(Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("localhost:8080", "MongoDB");
        map.put("localhost:8079", "MariaDB");
        map.put("dbrobo.mgul.ac.ru", "MySQL");
        model.addAttribute("servers", map);
        return "chooseServer";
    }

    @GetMapping("/mainexport")
    public String getExportPage(@RequestParam("serverName") String serverName, Model model){
        model.addAttribute("serverName", serverName);
        return "mainexport";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "/administration/admin";
    }
}
