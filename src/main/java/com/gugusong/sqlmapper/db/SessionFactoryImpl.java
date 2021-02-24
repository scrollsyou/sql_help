package com.gugusong.sqlmapper.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import lombok.NonNull;

public class SessionFactoryImpl implements SessionFactory {

	private DataSource dataSource;
	private ISqlHelp sqlHelp;
	
	public SessionFactoryImpl(@NonNull DataSource dataSource) {
		this.dataSource = dataSource;
		this.sqlHelp = new MysqlSqlHelp();
	}
	
	@Override
	public Session openSession() throws SQLException {
		Connection connection = this.dataSource.getConnection();
		return new SessionImpl(connection, sqlHelp);
	}

}
