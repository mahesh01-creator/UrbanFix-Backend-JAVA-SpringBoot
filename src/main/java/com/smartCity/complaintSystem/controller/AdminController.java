package com.smartCity.complaintSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.model.Worker;
import com.smartCity.complaintSystem.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService Service;

    
    @GetMapping("/{admin-id}/all-complaints")
    public List<Complaint> getAllComplaints() 
    {
        return Service.getAllComplaints();
    }

    
    @PutMapping("/{adminId}/verify-complaint/{complaintId}")
    public String verifyComplaint(
            @PathVariable Long adminId,
            @PathVariable Long complaintId,
            @RequestParam boolean isFake,
            @RequestParam String reason)
    {
        return Service.verifyComplaint(complaintId, isFake, reason);
    }
    
    
    @GetMapping("/{admin-id}/getAllWorkers")
    public List<Worker> findAllWorker() 
    {
     	return Service.getAllWorkers();
    }
    
    @PutMapping("/{adminId}/assign-smart/{complaintId}")
    public Complaint assignWorker(@PathVariable Long adminId,@PathVariable Long complaintId) 
    {
        return Service.verifyAndAssign(complaintId);
    }

    
    @PutMapping("/{adminId}/assign-worker/{complaintId}/{workerId}")
    public Complaint assignWorkerManual(
            @PathVariable Long adminId,
            @PathVariable Long complaintId,
            @PathVariable Long workerId) 
    {
        return Service.assignWorkerManual(complaintId, workerId);
    }

    @GetMapping("/{admin-id}/Monitor-Complaint/{complaintId}")
    public Complaint getComplaint(@PathVariable Long complaintId) 
    {
        return Service.getComplaintById(complaintId);
    }
    
    @GetMapping("/{admin-id}/Filter-Complaints/filterBy")
    public List<Complaint> filterComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) 
    {
        return Service.filterComplaints(status, category);
    }
}
