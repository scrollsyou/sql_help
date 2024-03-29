package com.gugusong.sqlmapper.springboot;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.config.GlobalConfig;
import com.gugusong.sqlmapper.db.ConnectionHolper;
import com.gugusong.sqlmapper.db.ISqlHelp;
import com.gugusong.sqlmapper.db.SessionImpl;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import lombok.NonNull;

/**
 * 对spring jdbc进行封装适配
 * @author yousongshu
 *
 */
public class SessionFactoryImpl implements SessionFactory {

	private DataSource dataSource;
	private ISqlHelp sqlHelp;
	private GlobalConfig config;

	public SessionFactoryImpl(@NonNull GlobalConfig config) {
		this.dataSource = config.getDataSource();
		this.sqlHelp = new MysqlSqlHelp();
		this.config = config;
	}

	@Override
	public Session openSession() throws SQLException {
		return new SessionImpl(new ConnectionHolper() {
			private Connection targetConn;
			@Override
			public Connection getTargetConnection() {
				targetConn = DataSourceUtils.getConnection(dataSource);
				return targetConn;
			}

			@Override
			public void releaseConnection() {
				DataSourceUtils.releaseConnection(targetConn, dataSource);
			}
		}, sqlHelp, config);
	}

}
