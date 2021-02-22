package sql_help.common.beans;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.annotation.Transient;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.config.GlogalConfig;

import lombok.Data;

public class BeanWrapperTest {

	/**
	 * 测试bean类包装
	 * @throws IntrospectionException 
	 */
	@Test
	public void instrance() throws IntrospectionException {
//		BeanWrapper instrance = BeanWrapper.instrance(TestName1.class, new GlogalConfig());
//		System.out.println(Joiner.on(",").join(instrance.getColumns().stream().map(c -> c.getName()).toArray()));
//		System.out.println(instrance.getTableName());
		
//		
//		Supplier<String> getId = instrance::getTableName;
//		getId.get();
//		Function<BeanWrapper, ?> aa = BeanWrapper::getColumns;
//		TestName1.class.getInterfaces();
//		BeanInfo beanInfo = Introspector.getBeanInfo(SubTestName1.class, Object.class);
//		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
//			System.out.println(propertyDescriptor.getName());
//			System.out.println(propertyDescriptor.getReadMethod().getName());
//			System.out.println(propertyDescriptor.getWriteMethod().getName());
//		}
		
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