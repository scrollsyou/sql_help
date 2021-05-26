package com.example.entity;

import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;

import lombok.Data;

@Data
@Entity
public class Student {
	
	@Id
	private Integer id;
	private String name;
	private Integer schoolId;
	private Integer clbumId;

}
