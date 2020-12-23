package sql_help.entity;

import java.math.BigDecimal;

import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;

//@Entity(tableName="entity_aBB")
//@Entity(tableName="entity_a")
@Entity
public class TestEntityA {

	@Id
	@Column(name = "")
	private String id;
	
	@Column(name = "column_two")
	private String two;

	@Column(name = "column_threeAAA")
	private boolean three;
	
	private int four;
	
	private double five;
	
	private BigDecimal six;
	
	private String sevenStr;

}
