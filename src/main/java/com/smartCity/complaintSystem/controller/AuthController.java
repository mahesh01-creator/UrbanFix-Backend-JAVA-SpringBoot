package com.smartCity.complaintSystem.controller;

import com.smartCity.complaintSystem.dto.*;
import com.smartCity.complaintSystem.service.AuthService;
import com.smartCity.complaintSystem.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(
	    origins = {
	        "http://localhost:5173",
	        "http://localhost:5174",
	        "http://localhost:5175",
	        "http://localhost:5176"
	    },
	    allowedHeaders = "*",
	    methods = {RequestMethod.POST, RequestMethod.OPTIONS},
	    allowCredentials = "true"
	)
public class AuthController 
{

    @Autowired
    private AuthService authService;
    @Autowired
    private TokenBlacklistService blacklistService;

    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) 
    {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/register/worker")
    public ResponseEntity<?> registerWorker(@RequestBody WorkerRegisterReq request) 
    {
        return ResponseEntity.ok(authService.registerWorker(request));
    }
    
   
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) 
    {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) 
    {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) 
        {
            String token = header.substring(7);
            blacklistService.blacklistToken(token);
        }
        return "Logged out successfully";
    }
    
}