package com.smartCity.complaintSystem.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.smartCity.complaintSystem.model.Complaint;
import com.smartCity.complaintSystem.model.Status;
import com.smartCity.complaintSystem.model.Worker;
import com.smartCity.complaintSystem.repository.ComplaintRepository;
import com.smartCity.complaintSystem.repository.WorkerRepository;

import java.util.List;

@Service
public class WorkerService {


    @Autowired
    private WorkerRepository workerRepo;    
    @Autowired
    private ComplaintRepository complaintRepo;
    @Autowired
    private EmailService emailService;
  
    
    
    public List<Complaint> getWorkerComplaints(Long workerId) {
        return complaintRepo.findByWorkerId(workerId);
    }
    
 
    public List<Complaint> getAssignComplaints(Long workerId) {

        List<Status> activeStatuses = List.of(
                Status.ASSIGNED,
                Status.IN_PROGRESS
        );

        return complaintRepo.findByWorkerIdAndStatusIn(workerId, activeStatuses);
    }
    

	public List<Complaint> getResolvedComplaints(Long workerId) {
		
		List<Status> ResolvedStatuses = List.of(
                Status.RESOLVED,
                Status.IN_PROGRESS
        );

        return complaintRepo.findByWorkerIdAndStatusIn(workerId, ResolvedStatuses);
	}
    
    public Complaint startWork(Long complaintId)
    {

        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getStatus() != Status.ASSIGNED) {
            throw new RuntimeException("Only ASSIGNED complaints can be started");
        }

        complaint.setStatus(Status.IN_PROGRESS);
        emailService.sendComplaintStatusEmail(
                complaint.getUser().getEmail(),
                complaint.getUser().getName(),
                complaint.getTitle(),
                complaint.getStatus().name(),
                complaint.getProofImageUrl(),
                false
        );
        return complaintRepo.save(complaint);
        
    }
    
    public Complaint resolveComplaint(Long complaintId, String notes, String proofName) {

        Complaint complaint = complaintRepo.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        if (complaint.getStatus() != Status.IN_PROGRESS) {
            throw new RuntimeException("Complaint must be IN_PROGRESS");
        }

        complaint.setStatus(Status.RESOLVED);
        complaint.setResolutionNotes(notes);
        complaint.setProofImageUrl(proofName);
        
        Worker worker = complaint.getWorker();
        worker.setActiveTasks(Math.max(0, worker.getActiveTasks() - 1));
        workerRepo.save(worker);
       
        emailService.sendComplaintStatusEmail(
                complaint.getUser().getEmail(),
                complaint.getUser().getName(),
                complaint.getTitle(),
                complaint.getStatus().name(),
                complaint.getProofImageUrl(),
                false
        );
        return complaintRepo.save(complaint);
    }

    public Worker updateLocation(Long workerId, Double lat, Double lon) {

        Worker worker = workerRepo.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

        worker.setLatitude(lat);
        worker.setLongitude(lon);

        return workerRepo.save(worker);
    }
    public Complaint getComplaintById(Long complaintId)
    {
        return complaintRepo.findById(complaintId)
                .orElseThrow(() ->
                    new RuntimeException("Complaint not found"));
    }
}