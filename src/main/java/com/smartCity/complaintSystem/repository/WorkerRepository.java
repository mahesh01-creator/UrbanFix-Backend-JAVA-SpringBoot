package com.smartCity.complaintSystem.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smartCity.complaintSystem.model.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Long> 
{
    Optional<Worker> findByEmail(String email);
    List<Worker> findByDepartmentIgnoreCase(String department);
}
