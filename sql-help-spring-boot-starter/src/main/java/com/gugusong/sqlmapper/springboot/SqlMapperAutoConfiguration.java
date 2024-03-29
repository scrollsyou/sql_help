package com.gugusong.sqlmapper.springboot;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.common.util.SnowFlake;
import com.gugusong.sqlmapper.config.GlobalConfig;
import com.gugusong.sqlmapper.db.ColumnTypeMapping;
import com.gugusong.sqlmapper.db.mysql.ColumnTypeMappingImpl;
import com.gugusong.sqlmapper.strategy.ImplicitNamingStrategy;
import com.gugusong.sqlmapper.strategy.impl.DefaultJDBCImplicitNamingStrategyImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * 自动配置sqlMapper
 * @author yousongshu
 *
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SqlMapperProperties.class)
//@ConditionalOnBean(DataSource.class)
public class SqlMapperAutoConfiguration implements DisposableBean {

	@Resource
	private SqlMapperProperties properties;

	/**
	 * 雪花随机数生成器
	 * 可自定义编写
	 * @return
	 */
	@ConditionalOnMissingBean(SnowFlake.class)
	@Bean
	public SnowFlake sqlHelpSnowFlake() {
		return new SnowFlake(properties.getDatacenterId(), properties.getMachineId());
	}

	/**
	 * 命名转换策略
	 * @return
	 */
	@ConditionalOnMissingBean(ImplicitNamingStrategy.class)
	@Bean
	public ImplicitNamingStrategy sqlHelpImplicitNamingStrategy() {
		return new DefaultJDBCImplicitNamingStrategyImpl();
	}

	/**
	 * java类中属性类型与数据库字段类型映射
	 * @return
	 */
	@ConditionalOnMissingBean(ColumnTypeMapping.class)
	@Bean
	public ColumnTypeMapping sqlHelpColumnTypeMapping() {
		return new ColumnTypeMappingImpl();
	}


	@ConditionalOnMissingBean(GlobalConfig.class)
	@Bean
	public GlobalConfig sqlHelpGlogalConfig(DataSource dataSource, SnowFlake snowFlake, ImplicitNamingStrategy implicitNamingStrategy,
                                            ColumnTypeMapping columnTypeMapping) {
		return new GlobalConfig(dataSource, snowFlake, implicitNamingStrategy, columnTypeMapping);
	}

	@ConditionalOnMissingBean(SessionFactory.class)
	@Bean
	public SessionFactory sqlHelpSessionFactory(GlobalConfig config) {
		log.info("sql-help init ... ");
		return new SessionFactoryImpl(config);
	}

	@Bean
	public SqlHelpBaseDao sqlHelpSession(SessionFactory factory) throws SQLException {
		return new SqlHelpBaseDao(factory.openSession());
	}

	@Override
	public void destroy() throws Exception {
		log.info("sql-help destroy...");

	}

}
