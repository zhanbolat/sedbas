package de.vorb.wildfly_springboot;

import model.User;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class SampleResource {

    @RequestMapping("/")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping("/zhanbo/hello")
    public String hello2() {
        return "Hello World2!";
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public String loginAs(@RequestBody User user) {
        System.out.println("loginAs method call. Username: " + user.getEmail());

        return "Login success!";
    }

}
