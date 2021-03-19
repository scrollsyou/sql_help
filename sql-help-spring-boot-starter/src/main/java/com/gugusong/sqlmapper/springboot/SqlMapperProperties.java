package com.gugusong.sqlmapper.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 配置数据中心编号及机器编号
 * 默认单机部署无需配置，如未用到雪花随机数生成id
 * 无需配置
 * @author yousongshu
 *
 */
@Data
@ConfigurationProperties(prefix = "spring.sql-help")
public class SqlMapperProperties {

	/**
	 * 数据中心编号
	 */
	private Long datacenterId = 1L;
	/**
	 * 机器编号
	 */
	private Long machineId = 1L;
	
	
}
