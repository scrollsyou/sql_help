package com.gugusong.sqlmapper.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import lombok.NonNull;

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
		Connection connection = this.dataSource.getConnection();
		return new SessionImpl(connection, sqlHelp, config);
	}

}
