package sql_help.entity;

import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;

import lombok.Data;

/**
 * 班级表
 * @author yousongshu
 *
 */
@Data
@Entity
public class Clbum {
	
	@Id
	private Integer id;
	private String name;
	private Integer schoolId;

}
