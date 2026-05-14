package com.smartCity.complaintSystem.service;


import com.smartCity.complaintSystem.dto.*;

import com.smartCity.complaintSystem.model.*;
import com.smartCity.complaintSystem.repository.AdminRepository;
import com.smartCity.complaintSystem.repository.UserRepository;
import com.smartCity.complaintSystem.repository.WorkerRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private WorkerRepository workerRepo;
    @Autowired
    private JwtService jwtService;
    
    public String register(RegisterRequest request) 
    {
        if (userRepository.findByEmail(request.getEmail()).isPresent() ||
        	adminRepo.findByEmail(request.getEmail()).isPresent() ||
        	workerRepo.findByEmail(request.getEmail()).isPresent()) 
        {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER); 

        userRepository.save(user);
        return "User Registered Successfully";
    }
    
    public String registerAdmin(RegisterRequest request) 
    {
    	if (userRepository.findByEmail(request.getEmail()).isPresent() ||
            	adminRepo.findByEmail(request.getEmail()).isPresent() ||
            	workerRepo.findByEmail(request.getEmail()).isPresent()) 
            {
                throw new RuntimeException("Email already registered");
            }
        
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(request.getPassword());
        admin.setRole(Role.ADMIN);
        
        adminRepo.save(admin);
    	return "Admin Registered Successfully";
	}
    
    public String registerWorker(WorkerRegisterReq request) 
    {
    	
    	if (userRepository.findByEmail(request.getEmail()).isPresent() ||
            adminRepo.findByEmail(request.getEmail()).isPresent() ||
            workerRepo.findByEmail(request.getEmail()).isPresent()) 
            {
                throw new RuntimeException("Email already registered");
            }
    	
        Worker worker = new Worker();
        
        worker.setName(request.getName());
        worker.setEmail(request.getEmail());
        worker.setPassword(request.getPassword());
        worker.setDepartment(request.getDepartment().toUpperCase());
        worker.setPhone(request.getPhone());
        worker.setRole(Role.WORKER);
        worker.setActiveTasks(0);
        worker.setArea(request.getArea());
        worker.setLatitude(request.getLatitude());
        worker.setLongitude(request.getLongitude());
        
        workerRepo.save(worker);
		return "Worker Registered Successfully";
	}
    

    public LoginResponse login(LoginRequest request) {

    	Optional<User> user = userRepository.findByEmail(request.getEmail());
    	if (user.isPresent()) 
    	{
    	    if (!user.get().getPassword().equals(request.getPassword())) 
    	    {
    	        throw new RuntimeException("Invalid password");
    	    }
    	    String token = jwtService.generateToken(request.getEmail(), "USER");
    	    return new LoginResponse(user.get().getId(), user.get().getName(), user.get().getEmail(), "USER",token);
    	}
    	
    	Optional<Admin> admin = adminRepo.findByEmail(request.getEmail());
    	if (admin.isPresent()) {
    	    if (!admin.get().getPassword().equals(request.getPassword())) {
    	        throw new RuntimeException("Invalid password");
    	    }
    	    String token = jwtService.generateToken(request.getEmail(), "ADMIN");
    	    return new LoginResponse(admin.get().getId(), admin.get().getName(), admin.get().getEmail(), "ADMIN",token);
    	}

   
    	Optional<Worker> worker = workerRepo.findByEmail(request.getEmail());
    	if (worker.isPresent()) {
    	    if (!worker.get().getPassword().equals(request.getPassword())) {
    	        throw new RuntimeException("Invalid password");
    	    }
    	    String token = jwtService.generateToken(request.getEmail(), "WORKER");
    	    return new LoginResponse(worker.get().getId(), worker.get().getName(), worker.get().getEmail(), "WORKER",token);
    	}
    	throw new RuntimeException("User not found");
    }

	
}
