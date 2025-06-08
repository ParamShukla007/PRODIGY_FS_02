package com.example.employee_management.employee_management.doa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.employee_management.employee_management.Entities.User;
import java.util.List;
import org.springframework.data.domain.Pageable;


public interface UserRepository extends JpaRepository<User, Integer>{
    @Query("select u from User u where u.email = :email")
    public User getUserByUserEmail(@Param("email") String email);

    @Query("SELECT SUM(CAST(u.salary AS double)) FROM User u")
    Double calculateTotalSalaryExpense();

    
    @Query("SELECT AVG(CAST(u.salary AS double)) FROM User u")
    Double calculateAverageSalary();
    
    @Query("SELECT u.designation, SUM(CAST(u.salary AS double)) FROM User u GROUP BY u.designation")
    List<Object[]> getSalaryByDesignation();

    @Query("SELECT u.name,u.salary FROM User u ORDER BY u.salary DESC")
    List<Object[]> getTopPaidEmployees(Pageable pageable);

    @Query("SELECT COUNT(u.user_id) FROM User u")
    Long getTotalEmployees();
}
