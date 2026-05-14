package com.smartCity.complaintSystem.repository;

import com.smartCity.complaintSystem.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}