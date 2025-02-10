package com.clevercloud.oauth_consumer_server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login-page"; // This will return login.html from the templates folder
    }
}