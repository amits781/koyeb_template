package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

  @GetMapping("/hello")
  public ResponseEntity<String> sayHello() {
    return new ResponseEntity<String>("Hello World!", HttpStatus.OK);
  }
  @GetMapping(value = "/metrics", produces = "text/plain")
    public String metrics() {
        int value = fetchValueFromHttp(); // your HTTP call
        return "my_cost_http_metric " + value + "\n";
    }

    private int fetchValueFromHttp() {
        // call your API and return some number
        return 42;
    }
}
