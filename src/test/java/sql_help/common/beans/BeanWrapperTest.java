package sql_help.common.beans;


import org.junit.Test;

import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.config.GlogalConfig;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sql_help.entity.vo.SchoolVo;
import sql_help.entity.vo.StudentVo;

@Slf4j
public class BeanWrapperTest {

	/**
	 * 测试bean类包装
	 * @throws Exception 
	 */
	@Test
	public void instrance() throws Exception {
		BeanWrapper studentVo = BeanWrapper.instrance(SchoolVo.class, new GlogalConfig());
		log.debug("切割数级:{}", studentVo);
		
	}
	
	@Entity
	@Data
	static class TestName1{
		@Id
		private Long id;
		@Column(sort = 9)
		private String name;
		@Column(sort = 2)
		private String userName;
		@Column(sort = 2)
		private String realName;
		@Transient
		private String remake;
	}
	
	static class SubTestName1 extends TestName1{
		private String aaa;
	}
}
