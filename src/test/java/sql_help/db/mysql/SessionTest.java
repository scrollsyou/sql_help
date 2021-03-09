package sql_help.db.mysql;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.Session;
import com.gugusong.sqlmapper.SessionFactory;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.ExampleImpl;
import com.gugusong.sqlmapper.db.SessionFactoryImpl;
import com.gugusong.sqlmapper.strategy.GenerationType;

import lombok.Data;
import lombok.ToString;
import sql_help.db.mysql.datasource.DataSourceFactory;

public class SessionTest {

	private Session getSession() throws SQLException {
		GlogalConfig glogalConfig = new GlogalConfig();
		glogalConfig.setDataSource(DataSourceFactory.getDataSource());
		SessionFactory factory = new SessionFactoryImpl(glogalConfig);
		Session openSession = factory.openSession();
		return openSession;
	}
	@Test
	public void save() throws Exception {
		Session openSession = getSession();
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setName("aaaa");
		openSession.save(testEntity);
		System.out.println(testEntity.getId() + ":" + testEntity.getName());
	}

	@Test
	public void update() throws Exception {
		Session openSession = getSession();
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setId(5);
		testEntity.setName("bbbb2");
		testEntity.setAa(new Date());
		openSession.update(testEntity);
	}

	@Test
	public void delete() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setId(5);
		openSession.delete(testEntity);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}
	
	@Test
	public void deleteByExample() throws Exception {
		Session openSession = getSession();
//		openSession.setAutoCommit(false);
		openSession.delete(ExampleImpl.newInstance().equals("name", "aaaa123").or().condition("length({name}) > ? or length({dd}) < ?", 4, 2), SessionTestEntity.class);
//		openSession.rollback();
//		openSession.setAutoCommit(true);
	}

	public <E> List<E> findAll(Example example, Class<E> E) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void findOne() throws Exception {
		Session openSession = getSession();
		SessionTestEntity findOneById = openSession.findOneById(SessionTestEntity.class, 5);
		System.out.println(findOneById);
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
	@ToString
	@Entity(tableName = "test")
	public static class SessionTestEntity{
		@Id(stragegy = GenerationType.IDENTITY)
		private int id;
		
		private String name;
		@Column(sort = 12)
		private Date aa;
		
		private String dd;
	}
}
