package kz.bonita.services.controller;

import org.springframework.web.bind.annotation.*;


@RestController
public class SampleResource {

    @RequestMapping("/")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping("/hello")
    public String hello2() {
        return "Hello World2!";
    }
}
