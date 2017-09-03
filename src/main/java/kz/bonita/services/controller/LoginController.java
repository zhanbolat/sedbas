package kz.bonita.services.controller;

import kz.bonita.services.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import kz.bonita.services.service.BonitaServices;

import java.util.List;

@RestController
@RequestMapping("api")
public class LoginController {

    private final BonitaServices bonitaServices;

    @Autowired
    public LoginController(BonitaServices bonitaServices) {
        this.bonitaServices = bonitaServices;
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("login email: " + user.getEmail());
        if (StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword())) {
            return new ResponseEntity<>(new User(), HttpStatus.OK);
        }

        try {
            bonitaServices.loginAs(user.getEmail(), user.getPassword());
            String userId = bonitaServices.getUserIdFromSession();
            List<String> cookies = bonitaServices.getCookies();


            User user1 = new User();
            user1.setEmail(user.getEmail());
            user1.setFirstname(user.getFirstname());
            user1.setLastname("Ss");
            user1.setId(user.getId());

            HttpHeaders headers = new HttpHeaders();
            for (String cookie : cookies) {
                System.out.println("Cookie to be added: " + cookie);
                headers.add("Set-Cookie", cookie);
            }
            headers.add("Access-Control-Allow-Origin", "*");
            ResponseEntity responseEntity = new ResponseEntity<>(user1, headers, HttpStatus.OK);
            System.out.println("Response headers: " + responseEntity.getHeaders());

            return responseEntity;
        } catch (Exception e) {
            System.err.println("Error occurred on method login. Username: " + user.getEmail()
                    + ". Exception: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new User(), HttpStatus.OK);
        }
    }
}
