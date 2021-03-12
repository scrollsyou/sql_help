package sql_help.entity.vo;

import com.gugusong.sqlmapper.annotation.vo.LeftJoin;
import com.gugusong.sqlmapper.annotation.vo.ManyToOne;
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
	
	@ManyToOne(entityAlias = "school", tagerClass = SchoolVo.class)
	private SchoolVo school;
}
