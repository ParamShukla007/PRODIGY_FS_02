package com.example.employee_management.employee_management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.employee_management.employee_management.doa.UserRepository;

@Service
public class DashboardService {
    
    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // Get total employees
            Long totalEmployees = userRepository.getTotalEmployees();
            data.put("totalEmployees", totalEmployees != null ? totalEmployees : 0L);

            // Get total salary expense
            Double totalSalary = userRepository.calculateTotalSalaryExpense();
            data.put("totalSalaryExpense", totalSalary != null ? totalSalary : 0.0);
            
            // Get average salary
            Double avgSalary = userRepository.calculateAverageSalary();
            data.put("averageSalary", avgSalary != null ? avgSalary : 0.0);
            
            // Get salary by designation
            List<Object[]> salaryByDesignation = userRepository.getSalaryByDesignation();
            Map<String, Double> designationMap = new HashMap<>();
            for (Object[] row : salaryByDesignation) {
                if (row[0] != null && row[1] != null) {
                    String designation = (String) row[0];
                    Double salary = ((Number) row[1]).doubleValue();
                    designationMap.put(designation, salary);
                }
            }
            data.put("salaryByDesignation", designationMap);
            
            // Get top paid employees
            List<Object[]> topPaidEmployees = userRepository.getTopPaidEmployees(PageRequest.of(0, 5));
            data.put("topPaidEmployees", topPaidEmployees);
            
        } catch (Exception e) {
            e.printStackTrace();
            data.put("error", "Failed to fetch dashboard data");
        }
        
        return data;
    }
}