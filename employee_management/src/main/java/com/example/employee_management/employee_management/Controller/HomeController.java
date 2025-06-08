package com.example.employee_management.employee_management.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.employee_management.employee_management.Entities.User;
import com.example.employee_management.employee_management.Helper.Message;
import com.example.employee_management.employee_management.doa.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String Home1(Model model)
    {
        model.addAttribute("title", "Home");
        return "home";
    }

    @GetMapping("/home")
    public String Home(Model model)
    {
        model.addAttribute("title", "Home");
        return "home";
    }


    @GetMapping("/login")
    public String login(Model model)
    {
        model.addAttribute("title", "Login");
        return "login";
    }

  
}