package com.example.employee_management.employee_management.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class DefaultRedirectController {
    @GetMapping("/default-redirect")
    public String defaultRedirect(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard1";
        }
        return "redirect:/user/dashboard";
    }
}
