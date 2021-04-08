package com.gugusong.sqlmapper.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.config.GlobalConfig;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import lombok.NonNull;

/**
 * 简单的jdbc操作
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
				// TODO Auto-generated method stub
				try {
					targetConn = dataSource.getConnection();
					return targetConn;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void releaseConnection() {
				try {
					targetConn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}, sqlHelp, config);
	}

}
