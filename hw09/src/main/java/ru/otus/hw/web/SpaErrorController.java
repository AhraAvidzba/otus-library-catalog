package ru.otus.hw.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String uri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        if (status != null && status == 404 &&
                uri != null &&
                !uri.startsWith("/api/") &&
                !uri.startsWith("/assets/") &&
                !uri.contains(".")) {
            return "forward:/index.html";
        }
        return "error";
    }
}