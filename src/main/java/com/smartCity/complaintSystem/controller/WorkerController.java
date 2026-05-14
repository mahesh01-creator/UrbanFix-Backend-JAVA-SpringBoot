package com.smartCity.complaintSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.model.Worker;
import com.smartCity.complaintSystem.service.FileService;
import com.smartCity.complaintSystem.service.WorkerService;

import java.util.List;

@RestController
@RequestMapping("/worker")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://localhost:5176"
    },
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)
public class WorkerController {

    @Autowired
    private WorkerService workerService;
    @Autowired
    private FileService fileService;


    @GetMapping("/{workerId}/myAll-complaints")
    public List<Complaint> getAllMyComplaints(@PathVariable Long workerId) 
    {
        return workerService.getWorkerComplaints(workerId);
    }


    @GetMapping("/{workerId}/myAssign-complaints")
    public List<Complaint> getAssignComplaints(@PathVariable Long workerId) 
    {
        return workerService.getAssignComplaints(workerId);
    }
    
    @GetMapping("/{workerId}/myResolve-complaints")
    public List<Complaint> getResolveComplaints(@PathVariable Long workerId) 
    {
        return workerService.getResolvedComplaints(workerId);
    }

    @PutMapping("/{workerId}/start-work/{complaintId}")
    public Complaint startWork(@PathVariable Long complaintId) 
    {
        return workerService.startWork(complaintId);
    }

    @PutMapping("/{workerId}/resolve-work/{complaintId}")
    public Complaint resolveComplaint(@PathVariable Long complaintId,
                                      @RequestParam String notes,
                                      @RequestParam(required = false) MultipartFile proofImage) 
    {
    	 String proofName = fileService.uploadFile(proofImage);
         return workerService.resolveComplaint(complaintId, notes, proofName);
    }
    
    
    @PutMapping("{workerId}/update-location")
    public Worker updateLocation(
            @PathVariable Long workerId,
            @RequestParam ("latitude") Double latitude,
            @RequestParam ("longitude") Double longitude) 
    {
        return workerService.updateLocation(workerId, latitude, longitude);
    }
    

    @GetMapping("/{workerId}/complaint/{complaintId}")
    public Complaint getComplaintById(
            @PathVariable Long complaintId) 
    {
        return workerService.getComplaintById(complaintId);
    }

    
}