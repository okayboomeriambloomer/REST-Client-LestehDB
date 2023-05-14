package com.mainserver.mainserver.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdministrationController {
    @GetMapping("/edit")
    public String getAdminEditPage(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        List<?> listOfAdm = restTemplate.getForObject("http://localhost:8080/admin/edit", List.class);
        model.addAttribute("admins",listOfAdm);
        return "administration/edit";
    }

}
