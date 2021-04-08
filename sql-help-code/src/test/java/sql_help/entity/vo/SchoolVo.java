package sql_help.entity.vo;

import com.gugusong.sqlmapper.annotation.vo.FunctionMapping;
import com.gugusong.sqlmapper.annotation.vo.GroupBy;
import com.gugusong.sqlmapper.annotation.vo.Join;
import com.gugusong.sqlmapper.annotation.vo.ManyToOne;
import com.gugusong.sqlmapper.annotation.vo.PropertyMapping;
import com.gugusong.sqlmapper.annotation.vo.VOBean;

import lombok.Data;
import sql_help.entity.Clbum;
import sql_help.entity.School;
import sql_help.entity.Student;

@Data
@VOBean(mainPo = School.class, entityAlias = "school")
@Join(entityAlias = "clbum", po = Clbum.class, joinConditions = "{id} = {clbum.schoolId}")
@Join(entityAlias = "student", po = Student.class, joinConditions = "{student.clbumId} = {clbum.id}")
@GroupBy(properties = {"id", "school.name"})
public class SchoolVo {

	private Integer id;
	private String name;

//	@OneToMany(targetClass = StudentVo.class)
//	private List<StudentVo> students;
//	@OneToMany(targetClass = ClbumTestVo.class)
//	private Set<ClbumTestVo> clbums;
	@FunctionMapping(function = "count({student.id})")
	private Long studentCount;

	@Data
	public static class StudentVo{
		@PropertyMapping(originalName = "student.id")
		private Integer id;
		@PropertyMapping(originalName = "student.name")
		private String name;
		@ManyToOne(targetClass = SchoolTestVo.class)
		private SchoolTestVo school;
	}
	@Data
	public static class SchoolTestVo{
		@PropertyMapping(originalName = "school.name")
		private String name;
	}
	@Data
	public static class ClbumTestVo{
		@PropertyMapping(originalName = "clbum.name")
		private String name;
	}
}
