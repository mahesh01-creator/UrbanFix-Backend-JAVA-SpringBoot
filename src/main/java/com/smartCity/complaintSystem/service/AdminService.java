package com.smartCity.complaintSystem.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.model.Status;
import com.smartCity.complaintSystem.model.Worker;
import com.smartCity.complaintSystem.repository.ComplaintRepository;
import com.smartCity.complaintSystem.repository.WorkerRepository;

@Service
public class AdminService {

    @Autowired
    private ComplaintRepository complaintRepo;
    @Autowired
    private WorkerRepository workerRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SMSService smsService;

   
    public List<Complaint> getAllComplaints()
    {
        return complaintRepo.findAll();
    }

  
    public String verifyComplaint(Long complaintId, boolean isFake, String reason) 
    {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (isFake) {
        	complaint.setFake(true); 
        	complaint.setStatus(Status.REJECTED);
            complaint.setRejectReason(reason);
            complaintRepo.save(complaint);
            return "Fake complaint Rejected successfully";
        }

        if (complaint.getStatus() != Status.PENDING) {
            throw new RuntimeException("Only PENDING complaints can be verified");
        }

        complaint.setStatus(Status.VERIFIED);
        complaintRepo.save(complaint);

  
        emailService.sendComplaintStatusEmail(
                complaint.getUser().getEmail(),
                complaint.getUser().getName(),
                complaint.getTitle(),
                complaint.getStatus().name(),
                complaint.getProofImageUrl(),
                false
        );
        
        return "Complaint verified successfully";
    }

  
    public List<Worker> getAllWorkers() {
        return workerRepo.findAll();
    }
    
    
    private double calculateDistance(double lat1, double lon1,
            double lat2, double lon2) 
    {

    	final int R = 6371;

    	double latDistance = Math.toRadians(lat2 - lat1);
    	double lonDistance = Math.toRadians(lon2 - lon1);

    	double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
    			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
    			* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    	return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    
  
    public Worker findBestWorker(Complaint complaint) 
    {
        List<Worker> workers =workerRepo.findByDepartmentIgnoreCase(complaint.getCategory());

        if (workers.isEmpty()) {
            throw new RuntimeException("No workers available for this category");
        }

        Worker bestWorker = null;
        double bestScore = Double.MAX_VALUE;

        for (Worker w : workers) {

            
            if (w.getLatitude() == null || w.getLongitude() == null) continue;

            
            double distance = calculateDistance(
                    complaint.getLatitude(),
                    complaint.getLongitude(),
                    w.getLatitude(),
                    w.getLongitude()
            );

        
            int load = w.getActiveTasks();
            double score = distance + (load * 3);

            if (score < bestScore) {
                bestScore = score;
                bestWorker = w;
            }
        }

        if (bestWorker == null) {
            throw new RuntimeException("No worker with valid location");
        }

        return bestWorker;
    }
    
    
    public Complaint verifyAndAssign(Long complaintId) 
    {

        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

       
        complaint.setStatus(Status.VERIFIED);
        Worker bestWorker = findBestWorker(complaint);
        complaint.setWorker(bestWorker);
        complaint.setStatus(Status.ASSIGNED);
        Complaint c = complaintRepo.save(complaint);
     
        
        bestWorker.setActiveTasks(bestWorker.getActiveTasks() + 1);
        workerRepo.save(bestWorker);
        System.out.println("Worker assigned");
        
   
        emailService.sendComplaintStatusEmail(
                complaint.getUser().getEmail(),
                complaint.getUser().getName(),
                complaint.getTitle(),
                complaint.getStatus().name(),
                complaint.getProofImageUrl(),
                false
        );
        
        
        String phone = bestWorker.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("Worker phone number is missing");
        }

        phone = phone.replaceAll("[^\\d]", "");

        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }

        if (!phone.startsWith("91")) {
            phone = "91" + phone;
        }


        phone = "+" + phone;

        if (!phone.matches("^\\+91\\d{10}$")) {
            throw new RuntimeException("Invalid phone number: " + phone);
        }

        smsService.sendSMS(phone,"Smart City: You have been assigned a new complaint - "+ complaint.getTitle());
        return c;
    }
    
    public Complaint getComplaintById(Long complaintId)
    {
        return complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    private Status parseStatus(String status) 
    {
        try 
        {
            return Status.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid status value: " + status);
        }
    }
    
    public List<Complaint> getByStatus(String status) 
    {
        return complaintRepo.findByStatus(parseStatus(status));
    }
    
    public List<Complaint> getByCategory(String category) 
    {
        return complaintRepo.findByCategory(category.toUpperCase());
    }
    
    public List<Complaint> filterComplaints(String status, String category) 
    {
        if (status != null && category != null) {
            return complaintRepo.findByStatusAndCategory(parseStatus(status),category.toUpperCase());
        }
        if (status != null) {
            return complaintRepo.findByStatus(parseStatus(status));
        }
        if (category != null) {
            return complaintRepo.findByCategory(category.toUpperCase());
        }
        return complaintRepo.findAll(); 
    }

    public Complaint assignWorkerManual(Long complaintId, Long workerId)
    {
        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found"));

        Worker worker = workerRepo.findById(workerId)
                .orElseThrow(() ->
                        new RuntimeException("Worker not found"));

        if (complaint.getWorker() != null) 
        {
            Worker oldWorker = complaint.getWorker();
            if (oldWorker.getActiveTasks() > 0) 
            {
                oldWorker.setActiveTasks(oldWorker.getActiveTasks() - 1);
            }
            workerRepo.save(oldWorker);
        }
        complaint.setWorker(worker);
        complaint.setStatus(Status.ASSIGNED);

        worker.setActiveTasks(worker.getActiveTasks() + 1);
        workerRepo.save(worker);
        return complaintRepo.save(complaint);
    }
}
