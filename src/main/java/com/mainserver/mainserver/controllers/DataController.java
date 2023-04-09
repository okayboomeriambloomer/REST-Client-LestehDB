package com.mainserver.mainserver.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
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
                                 Model model) {
        model.addAttribute("fdate", fdate);
        model.addAttribute("sdate", sdate);
        if (jsonOrCsv) {
            return "apifilebackJSON";
        } else {
            model.addAttribute("fdate", fdate);
            model.addAttribute("sdate", sdate);
            RestTemplate restTemplate = new RestTemplate();
            List<?> allDevice = restTemplate.getForObject("http://localhost:8080/core/devices", List.class);
            model.addAttribute("devices", allDevice);
            return "apifilebackCSV";
        }
    }
    @GetMapping(value = "/deb.php/log.csv")
    public ResponseEntity<String> loadDataBetweenCSV(@RequestParam("fdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime fdate,
                                                     @RequestParam("sdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                        LocalDateTime sdate,
                                                     @RequestParam("deviceName") String deviceName) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(
                "http://localhost:8080/core/deb.php/log.csv?fdate={fdate}&sdate={sdate}&deviceName={deviceName}",
                String.class,
                fdate,
                sdate,
                deviceName);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=log.csv")
                .contentLength(result.length())
                .header("Content-Type", "text/csv; charset=utf-8")
                .body(result);
    }

    @GetMapping(value = "/deb.php/text") //
    @ResponseBody
    public String loadDataBetweenTextJSON
            (@RequestParam("fdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime fdate,
             @RequestParam("sdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime sdate) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:8080/core/deb.php/text?fdate={fdate}&sdate={sdate}",
                String.class,
                fdate,
                sdate);
        return result;
    }
}
