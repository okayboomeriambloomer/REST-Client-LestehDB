package com.mainserver.mainserver.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/core")
public class DataController {

    @GetMapping()
    @ResponseBody
    public String findAll() {
        RestTemplate restTemplate = new RestTemplate();
        String answer = restTemplate.getForObject("http://localhost:8080/core", String.class);
        return answer;
    }

    @PostMapping("/jsonapp.php")
    public ResponseEntity<String> insertOne(@RequestBody LinkedHashMap<String, LinkedHashMap<String, String>> allData) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:8080/core/jsonapp.php", allData, Map.class);
        restTemplate.postForObject("http://dbrobo.mgul.ac.ru/core/jsonapp.php", allData, Map.class);
        // TODO: сюды надо добавить ещё постФорОбжект для Родионовской базы
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(HttpClientErrorException.Forbidden e) {
        // в HTTP ответе будет тело (String) и статут в заголовке
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @GetMapping("/deb.php")
    public String getDataBetween(@RequestParam("fdate") String fdate,
                                 @RequestParam("sdate") String sdate,
                                 @RequestParam("jsonOrCsv") Boolean jsonOrCsv,
                                 @RequestParam("serverName") String serverName,
                                 Model model) {
        model.addAttribute("serverName", serverName);
        model.addAttribute("fdate", fdate);
        model.addAttribute("sdate", sdate);
        if (jsonOrCsv) {
            return "apifilebackJSON";
        } else {
            RestTemplate restTemplate = new RestTemplate();
            Map<?, ?> allDevice = restTemplate.getForObject("http://localhost:8080/core/devices", Map.class); // запрос к монговской базе за листом приборов
            model.addAttribute("devices", allDevice);
            return "apifilebackCSV";
        }
    }
    @GetMapping(value = "/deb.php/log.csv")
    public ResponseEntity<String> loadDataBetweenCSV(@RequestParam("fdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime fdate,
                                                     @RequestParam("sdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                        LocalDateTime sdate,
                                                     @RequestParam("deviceId") Long deviceId,
                                                     @RequestParam("serverName") String serverName) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(
                "http://" + serverName + "/core/deb.php?fdate={fdate}&sdate={sdate}&manualmode=1&unitid={deviceId}",
                String.class,
                fdate,
                sdate,
                deviceId);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=log.csv")
                .contentLength(result.length())
                .header("Content-Type", "text/csv; charset=utf-8")
                .body(result);
    }

    @GetMapping(value = "/deb.php/text") //
    public ModelAndView loadDataBetweenTextJSON
            (@RequestParam("fdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime fdate,
             @RequestParam("sdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime sdate,
             @RequestParam("serverName") String serverName,
             ModelMap model) {
        RestTemplate restTemplate = new RestTemplate();
//        String result = restTemplate.getForObject("http://" + serverName + "/core/deb.php?fdate={fdate}" +
//                        "&sdate={sdate}&fileback=1",
//                String.class,
//                fdate,
//                sdate);
        model.addAttribute("fdate", fdate);
        model.addAttribute("sdate", sdate);
        return new ModelAndView("redirect:" + "http://" + serverName + "/core/deb.php?fdate={fdate}" +
                        "&sdate={sdate}&fileback=1", model);
    }
}
