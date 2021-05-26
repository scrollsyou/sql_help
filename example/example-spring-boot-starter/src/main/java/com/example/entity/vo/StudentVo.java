package com.example.entity.vo;

import java.util.List;
import java.util.Set;

import com.example.entity.School;
import com.example.entity.Student;
import com.gugusong.sqlmapper.annotation.vo.Join;
import com.gugusong.sqlmapper.annotation.vo.ManyToOne;
import com.gugusong.sqlmapper.annotation.vo.OneToMany;
import com.gugusong.sqlmapper.annotation.vo.PropertyMapping;
import com.gugusong.sqlmapper.annotation.vo.VOBean;

import lombok.Data;

@Data
@VOBean(mainPo = Student.class, entityAlias = "student")
@Join(po = School.class, entityAlias = "school", joinConditions = "{student.schoolId} = {school.id}")
public class StudentVo {

	private Integer id;
	private String name;

	@PropertyMapping(originalName = "school.name")
	private String schoolName;

	@ManyToOne(targetClass = SchoolVo.class)
	private SchoolVo school;

	@OneToMany(targetClass = SchoolVo.class)
	private List<SchoolVo> schools;

	@OneToMany(targetClass = SchoolVo.class)
	private Set<SchoolVo> schoolsSet;

	@Data
	public static class SchoolVo {

		@PropertyMapping(originalName = "school.id")
		private Integer id;
		@PropertyMapping(originalName = "school.name")
		private String name;
	}
}


