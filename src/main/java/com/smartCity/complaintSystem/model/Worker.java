package com.smartCity.complaintSystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    private String department;
    private String phone;
    
    private int activeTasks;
    
    private String area; 
    private Double latitude;
    private Double longitude;
    
    @Enumerated(EnumType.STRING)
    private Role role;
}