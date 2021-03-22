package com.gugusong.sqlmapper.config;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.common.util.SnowFlake;
import com.gugusong.sqlmapper.db.ColumnTypeMapping;
import com.gugusong.sqlmapper.db.mysql.ColumnTypeMappingImpl;
import com.gugusong.sqlmapper.strategy.ImplicitNamingStrategy;
import com.gugusong.sqlmapper.strategy.impl.DefaultJDBCImplicitNamingStrategyImpl;

import lombok.Getter;
import lombok.Setter;

/**
 * 获取会话工厂配置类
 * 需配置数据库连接池
 * @author yousongshu
 *
 */
public class GlogalConfig {

	public GlogalConfig() {
		this(null, new SnowFlake(1, 1), new DefaultJDBCImplicitNamingStrategyImpl(), 
				new ColumnTypeMappingImpl());
	}
	public GlogalConfig(DataSource dataSource) {
		this(dataSource, new SnowFlake(1, 1), new DefaultJDBCImplicitNamingStrategyImpl(), 
				new ColumnTypeMappingImpl());
	}
	
	public GlogalConfig(DataSource dataSource, SnowFlake snowFlake, ImplicitNamingStrategy implicitNamingStrategy,
			ColumnTypeMapping columnTypeMapping) {
		super();
		this.dataSource = dataSource;
		this.snowFlake = snowFlake;
		this.implicitNamingStrategy = implicitNamingStrategy;
		this.columnTypeMapping = columnTypeMapping;
	}

	/**
	 * 需配置数据库连接池
	 * 需进行数据库操作时不可这空
	 */
	@Getter
	@Setter
	private DataSource dataSource;
	// TODO 数据中心编码及机器编码需读取配置文件
	// TODO id生成器采用统一接口定义
	/**
	 * 雪花随机生成器实例
	 */
	@Getter
	@Setter
	private SnowFlake snowFlake = new SnowFlake(1, 1);
	
	/**
	 * bean映射到数据库中字段命名规则
	 */
	@Getter
	@Setter
	private ImplicitNamingStrategy implicitNamingStrategy = new DefaultJDBCImplicitNamingStrategyImpl();
	
	/**
	 * 字段类型映射
	 */
	@Getter
	@Setter
	private ColumnTypeMapping columnTypeMapping = new ColumnTypeMappingImpl();
	
}
