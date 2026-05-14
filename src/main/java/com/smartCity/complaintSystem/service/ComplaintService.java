package com.smartCity.complaintSystem.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.model.Feedback;
import com.smartCity.complaintSystem.model.Status;
import com.smartCity.complaintSystem.model.User;
import com.smartCity.complaintSystem.repository.ComplaintRepository;
import com.smartCity.complaintSystem.repository.FeedbackRepository;
import com.smartCity.complaintSystem.repository.UserRepository;


@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private FeedbackRepository feedbackRepo;   
    @Autowired
    private EmailService emailService;
    
  
    public Complaint issueComplaint(Long userId,
            String title,
            String description,
            String category,
            String location,
            Double lat,
            Double lon,
            String imageName) 
    {

    	User user = userRepo.findById(userId)
    			.orElseThrow(() -> new RuntimeException("User not found"));
    	
    	if (lat == null || lon == null) {
            throw new RuntimeException("Location is required");
        }


    	Complaint c = new Complaint();
    	c.setTitle(title);
    	c.setDescription(description);
    	c.setCategory(category.toUpperCase());
    	c.setLocation(location);
    	c.setLatitude(lat);
        c.setLongitude(lon);
    	c.setImageUrl(imageName); 
    	c.setStatus(Status.PENDING);
    	c.setUser(user);

    	emailService.sendComplaintStatusEmail(
    	        c.getUser().getEmail(),
    	        c.getUser().getName(),
    	        c.getTitle(),
    	        "PENDING",
    	        c.getImageUrl(),
    	        true
    	);
    	return complaintRepo.save(c);
    }

    public List<Complaint> getMyComplaints(Long userId) {
        return complaintRepo.findByUserId(userId);
    }
    
    public Complaint trackComplaint(Long complaintId) 
    {
        return complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public Complaint giveFeedback(Long complaintId, String feedback,String Rating) 
    {

        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getStatus() != Status.RESOLVED) {
            throw new RuntimeException("Complaint not resolved yet");
        }
        
        Feedback fb = new Feedback();
        fb.setComplaintId(complaintId);
        fb.setMessage(feedback);
        fb.setRating(Rating); 
      
        complaint.setFeedback(feedback);
        complaintRepo.save(complaint);
        feedbackRepo.save(fb);
        
        return complaint;
    }
     
}
