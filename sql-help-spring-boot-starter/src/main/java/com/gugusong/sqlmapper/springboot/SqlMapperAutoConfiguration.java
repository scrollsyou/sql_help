package com.gugusong.sqlmapper.springboot;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.gugusong.sqlmapper.PageHelp;
import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.common.util.SnowFlake;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.ColumnTypeMapping;
import com.gugusong.sqlmapper.db.PageHelpImpl;
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
	
	/**
	 * 分页处理器
	 * @return
	 */
	@ConditionalOnMissingBean(PageHelp.class)
	@Bean
	public PageHelp sqlHelpPageHelp() {
		return new PageHelpImpl();
	}
	
	@ConditionalOnMissingBean(GlogalConfig.class)
	@Bean
	public GlogalConfig sqlHelpGlogalConfig(DataSource dataSource, SnowFlake snowFlake, ImplicitNamingStrategy implicitNamingStrategy,
			ColumnTypeMapping columnTypeMapping, PageHelp pageHelp) {
		return new GlogalConfig(dataSource, snowFlake, implicitNamingStrategy, columnTypeMapping, pageHelp);
	}
	
	@ConditionalOnMissingBean(SessionFactory.class)
	@Bean
	public SessionFactory sqlHelpSessionFactory(GlogalConfig config) {
		return new SessionFactoryImpl(config);
	}
	
	@Bean
	public Session sqlHelpSession(SessionFactory factory) throws SQLException {
		return factory.openSession();
	}
	
	@Override
	public void destroy() throws Exception {
		log.info("sql-help destroy...");

	}

}
