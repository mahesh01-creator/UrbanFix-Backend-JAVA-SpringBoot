package com.smartCity.complaintSystem.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.service.ComplaintService;
import com.smartCity.complaintSystem.service.FileService;

@RestController
@RequestMapping("/user")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://localhost:5176"
    },
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true")
public class UserController 
{

    @Autowired
    private ComplaintService complaintService;
    @Autowired
    private FileService fileService;

    
    @PostMapping("{userId}/issue-complaint")
    public Complaint issueComplaint( @PathVariable Long userId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String location,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) MultipartFile image) 
    {
    	  String imageName = fileService.uploadFile(image);

    	    return complaintService.issueComplaint(
    	            userId, title, description, category, location,latitude, longitude, imageName);
    }

    @GetMapping("/{userId}/my-complaints")
    public List<Complaint> myComplaints(@PathVariable Long userId) {
        return complaintService.getMyComplaints(userId);
    }

    @GetMapping("/{userId}/track-complaint/{complaintId}")
    public Complaint trackComplaint(@PathVariable Long complaintId) {
        return complaintService.trackComplaint(complaintId);
    }

    @PutMapping("/{userId}/give-feedback/{complaintId}")
    public Complaint giveFeedback(
            @PathVariable Long complaintId,
            @RequestParam String feedback,
            @RequestParam String rating) {

        return complaintService.giveFeedback(complaintId, feedback, rating);
    }
}
