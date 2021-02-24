package sql_help.db.mysql.datasource;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 获取datasource
 * @author yousongshu
 *
 */
public class DataSourceFactory {

	/**
	 * 获取测试用的dataSource
	 * @return
	 */
	public static DataSource getDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://192.168.1.184:3306/cloud-canteen?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
		dataSource.setUsername("root");
		dataSource.setPassword("you");
		return dataSource;
	}
}
