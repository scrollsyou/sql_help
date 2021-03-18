package sql_help.entity;

import com.gugusong.sqlmapper.annotation.Id;

public class TestEntityB {

	@Id
	private String id;
	
	private String two;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}
	
}
