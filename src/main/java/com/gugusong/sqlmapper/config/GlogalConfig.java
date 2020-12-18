package com.gugusong.sqlmapper.config;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.common.util.SnowFlake;

import lombok.Getter;

/**
 * 获取会话工厂配置类
 * 需配置数据库连接池
 * @author yousongshu
 *
 */
public class GlogalConfig {

	/**
	 * 需配置数据库连接池
	 */
	@Getter
	private DataSource dataSource;
	// TODO 数据中心编码及机器编码需读取配置文件
	/**
	 * 雪花随机生成器实例
	 */
	@Getter
	private SnowFlake snowFlake = new SnowFlake(1, 1);
}
