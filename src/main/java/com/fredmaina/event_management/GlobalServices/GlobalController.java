package com.fredmaina.event_management.GlobalServices;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@Hidden
@RestController
@Controller
public class GlobalController {
    @RequestMapping("/")
    public void routeToDocs(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}
