package com.example.employee_management.employee_management.Authentication;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.employee_management.employee_management.Entities.User;
import com.example.employee_management.employee_management.doa.UserRepository;

public class UserDetailsServiceImp implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.getUserByUserEmail(username);
        if(user == null)
        {
            throw new UsernameNotFoundException("Could not find user!!");
        }
        
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
    
}
