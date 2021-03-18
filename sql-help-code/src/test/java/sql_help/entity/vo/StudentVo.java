package sql_help.entity.vo;

import java.util.List;
import java.util.Set;

import com.gugusong.sqlmapper.annotation.vo.LeftJoin;
import com.gugusong.sqlmapper.annotation.vo.ManyToOne;
import com.gugusong.sqlmapper.annotation.vo.OneToMany;
import com.gugusong.sqlmapper.annotation.vo.PropertyMapping;
import com.gugusong.sqlmapper.annotation.vo.VOBean;

import lombok.Data;
import sql_help.entity.School;
import sql_help.entity.Student;

@Data
@VOBean(mainPo = Student.class, entityAlias = "student")
@LeftJoin(po = School.class, entityAlias = "school", joinConditions = "{student.schoolId} = {school.id}")
public class StudentVo {

	private Integer id;
	private String name;
	
	@PropertyMapping(originalName = "school.name")
	private String schoolName;
	
	@ManyToOne(tagerClass = SchoolVo.class)
	private SchoolVo school;
	
	@OneToMany(tagerClass = SchoolVo.class)
	private List<SchoolVo> schools;
	
	@OneToMany(tagerClass = SchoolVo.class)
	private Set<SchoolVo> schoolsSet;
	
	@Data
	public static class SchoolVo {

		@PropertyMapping(originalName = "school.id")
		private Integer id;
		@PropertyMapping(originalName = "school.name")
		private String name;
	}
}


