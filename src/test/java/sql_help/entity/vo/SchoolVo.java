package sql_help.entity.vo;

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
@VOBean(mainPo = School.class, entityAlias = "school")
@LeftJoin(entityAlias = "student", po = Student.class, joinConditions = "{student.schoolId} = {id}")
public class SchoolVo {

	private Integer id;
	private String name;
	
	@OneToMany(tagerClass = StudentVo.class)
	private Set<StudentVo> students;
	
	@Data
	public static class StudentVo{
		@PropertyMapping(originalName = "student.id")
		private Integer id;
		@PropertyMapping(originalName = "student.name")
		private String name;
		@ManyToOne(tagerClass = SchoolTestVo.class)
		private SchoolTestVo school;
	}
	@Data
	public static class SchoolTestVo{
		@PropertyMapping(originalName = "school.name")
		private String name;
	}
}
