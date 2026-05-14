package com.smartCity.complaintSystem.repository;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.model.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByWorkerId(Long workerId);
    List<Complaint> findByStatus(Status status);
    List<Complaint> findByCategory(String category);
    List<Complaint> findByStatusAndCategory(Status status, String category);
    List<Complaint> findByWorkerIdAndStatusIn(Long workerId, List<Status> statuses);
}
