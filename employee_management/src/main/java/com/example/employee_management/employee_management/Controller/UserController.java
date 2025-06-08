package com.example.employee_management.employee_management.Controller;


import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.employee_management.employee_management.Entities.User;
import com.example.employee_management.employee_management.doa.UserRepository;



import org.springframework.ui.Model;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Add this method to make user available in all templates
    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.getUserByUserEmail(username);
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal)
    {
        String username = principal.getName();
        //get the userd details using username(email)
        User user = userRepository.getUserByUserEmail(username);
        System.out.println("User: " + user);
        model.addAttribute("user", user);
        return "user_dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model m, Principal p)
    {
        String userName = p.getName();
        User user = userRepository.getUserByUserEmail(userName);
        m.addAttribute("user", user);
        m.addAttribute("title", "Profile");
        return "profile";
    }

}
