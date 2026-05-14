package com.smartCity.complaintSystem.dto;

import lombok.Data;

@Data
public class WorkerRegisterReq 
{
	 	private String name;
	    private String email;
	    private String password;
	    private String department;
	    private String phone; 
	    private int activeTasks;    
	    private String area; 
	    private Double latitude;
	    private Double longitude;
}
