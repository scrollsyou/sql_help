package com.gugusong.sqlmapper.config;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.PageHelp;
import com.gugusong.sqlmapper.common.util.SnowFlake;
import com.gugusong.sqlmapper.db.ColumnTypeMapping;
import com.gugusong.sqlmapper.db.PageHelpImpl;
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
	
	/**
	 * 默认分页逻辑
	 * 可重写分页逻辑
	 */
	@Getter
	@Setter
	private PageHelp pageHelp = new PageHelpImpl();
}
