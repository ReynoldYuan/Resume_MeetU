package com.meetu.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Component
@Entity
@Table(name = "managers")
@Getter
@Setter
public class Managers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer managerId;
	
	private String email;
    
    private String password;
    
    private String name;
    
    private Character gender;
    
    private String birth;
    
    private String department;
    
    private String position;
    
    private String picture;
    
    private byte deleteState=0;
}
