package sql_help.db.mysql;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.SessionFactoryImpl;
import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.Data;
import sql_help.db.mysql.datasource.DataSourceFactory;

public class SessionTest {

	@Test
	public void save() throws Exception {
		GlogalConfig glogalConfig = new GlogalConfig();
		glogalConfig.setDataSource(DataSourceFactory.getDataSource());
		SessionFactory factory = new SessionFactoryImpl(glogalConfig);
		Session openSession = factory.openSession();
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setName("aaaa");
		openSession.save(testEntity);
	}

	public <T> int update(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <T> int delete(T entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	public <E> List<E> findAll(Example example, Class<E> E) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public <E> E findOne(Example example, Class<E> E) {
		// TODO Auto-generated method stub
		return null;
	}

	public <E> int findCount(Example example, Class<E> E) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void commit() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Data
	@Entity(tableName = "test")
	public static class SessionTestEntity{
		@Id(stragegy = GenerationType.IDENTITY)
		private Long id;
		
		private String name;
	}
}
