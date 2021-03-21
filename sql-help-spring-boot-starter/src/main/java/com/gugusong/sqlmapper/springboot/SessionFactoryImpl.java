package com.gugusong.sqlmapper.springboot;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.config.GlogalConfig;
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
				targerConn = DataSourceUtils.getConnection(dataSource);
				return targerConn;
			}

			@Override
			public void releaseConnection() {
				DataSourceUtils.releaseConnection(targerConn, dataSource);
			}
		}, sqlHelp, config);
	}

}
