package sql_help.entity.vo;

import com.gugusong.sqlmapper.annotation.vo.PropertyMapping;

import lombok.Data;

@Data
public class SchoolVo {

	@PropertyMapping(originalName = "school.id")
	private Integer id;
	@PropertyMapping(originalName = "school.name")
	private String name;
}
