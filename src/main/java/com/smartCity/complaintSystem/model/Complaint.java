package com.smartCity.complaintSystem.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;
    private String location;
    private double latitude;
    private double longitude;
    

    private String imageUrl; // user uploaded image
    private String proofImageUrl;   // worker proof image

    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean isFake;
    private String rejectReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    private String resolutionNotes; // worker notes
    

    private LocalDateTime createdAt;
    private String feedback;
    
    @PrePersist
    public void prePersist() {this.createdAt = LocalDateTime.now();}
    
        
    
}