package com.gugusong.sqlmapper.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.config.GlogalConfig;
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
	private GlogalConfig config;
	
	public SessionFactoryImpl(@NonNull GlogalConfig config) {
		this.dataSource = config.getDataSource();
		this.sqlHelp = new MysqlSqlHelp();
		this.config = config;
	}
	
	@Override
	public Session openSession() throws SQLException {
		return new SessionImpl(new ConnectionHolper() {
			private Connection targerConn;
			@Override
			public Connection getTagerConnection() {
				// TODO Auto-generated method stub
				try {
					targerConn = dataSource.getConnection();
					return targerConn;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void releaseConnection() {
				try {
					targerConn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}, sqlHelp, config);
	}

}
