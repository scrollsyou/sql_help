package sql_help.entity;

import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;

import lombok.Data;

@Data
@Entity
public class School {
	
	@Id
	private Integer id;
	private String name;

}
