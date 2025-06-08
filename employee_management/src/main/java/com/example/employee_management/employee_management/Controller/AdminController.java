package com.example.employee_management.employee_management.Controller;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.employee_management.employee_management.Entities.User;
import com.example.employee_management.employee_management.Helper.Message;
import com.example.employee_management.employee_management.doa.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import java.util.List;

import java.util.Map;


@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private DashboardService dashboardService;
  
    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.getUserByUserEmail(username);
            model.addAttribute("user", user);
        }
    }

    @GetMapping("/dashboard1")
    public String dashboard(Model model, Principal principal) {
        String username = principal.getName();
        User admin = userRepository.getUserByUserEmail(username);
        model.addAttribute("admin", admin);
        Map<String, Object> dashboardData = dashboardService.getDashboardData();
        model.addAllAttributes(dashboardData);  
        return "admin_dashboard";
    }
    
    @GetMapping("/profile1")
    public String profile(Model m, Principal p)
    {
        String userName = p.getName();
        User user = userRepository.getUserByUserEmail(userName);
        m.addAttribute("user", user);
        m.addAttribute("title", "Profile");
        return "admin_profile";
    }
  
    @GetMapping("/admin_register")
    public String signup(Model model) {
        model.addAttribute("title", "Register");
        model.addAttribute("user", new User());  // Changed to lowercase
        return "admin_register";
    }
    
    //handler for registration
    @PostMapping("/do_register")
    public String registerUser(
            @ModelAttribute("user") User user,
            @RequestParam(value = "imageFile", required = false) MultipartFile file,
            HttpSession session) {
        try {
            // Validate user object
            if (user == null) {
                throw new IllegalArgumentException("User object cannot be null");
            }

            // Debug logging
            System.out.println("Received user data: " + user.toString());

            // Initialize imageUrl before handling file upload
            user.setImageUrl("default.png");

            // Handle profile image upload
            if (file != null && !file.isEmpty()) {
                try {
                    // Generate unique filename
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    
                    // Create directories if they don't exist
                    String uploadDir = "src/main/resources/static/img/profile/";
                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Save file
                    Path path = Paths.get(uploadDir + fileName);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    user.setImageUrl(fileName);
                } catch (Exception e) {
                    System.err.println("Error saving file: " + e.getMessage());
                    // Continue with default image if file upload fails
                }
            }

            // Set default role if not present
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            // Validate role
            if (!user.getRole().equals("USER") && !user.getRole().equals("ADMIN")) {
                throw new IllegalArgumentException("Invalid role selected");
            }

            // Encrypt password
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            // Save user
            userRepository.save(user);
            
            session.setAttribute("message", new Message("User registered successfully!", "success"));
            return "redirect:/admin/admin_register";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Registration failed: " + e.getMessage(), "danger"));
            return "redirect:/admin/admin_register";
        }
    }
  
   @GetMapping("/users")
    public String showUsers(Model model) {
        try {
            List<User> users = userRepository.findAll();
            model.addAttribute("users", users);  // Add this line to pass users to the view
            System.out.println("Number of users found: " + users.size());  // Debug log
            return "admin_users";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/delete-user/{user_id}")
    public String deleteContact(@PathVariable("user_id") Integer user_id, Model model, HttpSession session) {
        try {
            Optional<User> userOptional = userRepository.findById(user_id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Delete user image if it exists
                if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                    File userImageFile = new ClassPathResource("static/img/profile").getFile();
                    File userImage = new File(userImageFile, user.getImageUrl());
                    if (userImage.exists()) {
                        userImage.delete();
                    }
                }

                // Delete user from database
                this.userRepository.delete(user);
                session.setAttribute("message", new Message("Contact deleted successfully!", "success"));
            } else {
                session.setAttribute("message", new Message("User not found!", "danger"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong! " + e.getMessage(), "danger"));
        }
        return "redirect:/admin/users/0"; // Updated redirect path
    }

    @PostMapping("/update-user/{user_id}")
    public String updateFrom(@PathVariable("user_id") Integer user_id, Model m)
    {
        m.addAttribute("title", "Update Contact");
        User user = this.userRepository.findById(user_id).get();
        m.addAttribute("user", user);
        return "update_user";
    }

    @PostMapping("/process-update")
    public String processUpdate(
            Principal principal, 
            @ModelAttribute User user, 
            @RequestParam(value = "imageFile", required = false) MultipartFile file, // Make file optional
            Model m, 
            HttpSession session) {
        try {
            Optional<User> optionalOldUser = this.userRepository.findById(user.getUser_id());
            if (optionalOldUser.isPresent()) {
                User oldUser = optionalOldUser.get();

                // Process and upload file only if a new file is provided
                if (file != null && !file.isEmpty()) {
                    // Delete old image
                    if (oldUser.getImageUrl() != null && !oldUser.getImageUrl().isEmpty()) {
                        File deleteFile = new ClassPathResource("static/img/profile").getFile();
                        File oldUserImageFile = new File(deleteFile, oldUser.getImageUrl());
                        if (oldUserImageFile.exists()) {
                            oldUserImageFile.delete();
                        }
                    }

                    // Save new image
                    File saveFile = new ClassPathResource("static/img/profile").getFile();
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // Add timestamp to prevent duplicates
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    user.setImageUrl(fileName);
                } else {
                    // If no new file is uploaded, keep the existing image
                    user.setImageUrl(oldUser.getImageUrl());
                }

                // Copy unchangeable properties from old user
                user.setPassword(oldUser.getPassword()); // Preserve password
                user.setRole(oldUser.getRole()); // Preserve role

                // Save updated user
                this.userRepository.save(user);
                session.setAttribute("message", new Message("Profile updated successfully!", "success"));
            } else {
                session.setAttribute("message", new Message("User not found!", "danger"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong! " + e.getMessage(), "danger"));
        }
        return "redirect:/admin/profile1"; // Redirect back to profile page
    }

    // Show the update password form
    @PostMapping("/update-password")
    public String showUpdatePasswordForm(Model model) {
        model.addAttribute("title", "Update User Password");
        return "update_password"; // Ensure this matches the HTML file name
    }

    // Process the password update
    @PostMapping("/process-update-password")
    public String processUpdatePassword(
            @RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session) {
        try {
            // Validate that the passwords match
            if (!newPassword.equals(confirmPassword)) {
                session.setAttribute("message", new Message("Passwords do not match!", "danger"));
                return "redirect:/admin/update-password";
            }

            // Find the user by email
            User user = userRepository.getUserByUserEmail(email);
            if (user == null) {
                session.setAttribute("message", new Message("User not found with the provided email!", "danger"));
                return "redirect:/admin/update-password";
            }

            // Update the password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Success message
            session.setAttribute("message", new Message("Password updated successfully for user: " + email, "success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong! " + e.getMessage(), "danger"));
        }
        return "redirect:/admin/dashboard1"; // Redirect to the admin dashboard after updating the password
    }
}

