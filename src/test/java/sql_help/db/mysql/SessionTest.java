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
import sql_help.entity.vo.SchoolVo;
import sql_help.entity.vo.StudentVo;

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
		testEntity.setId(4);
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
		openSession.setAutoCommit(false);
		openSession.delete(ExampleImpl.newInstance().equals("name", "aaaa123").or().condition("length({name}) > ? or length({dd}) < ?", 4, 2).orderByAsc("aa").orderByDesc("dd"), SessionTestEntity.class);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}

	@Test
	public void findAll() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		List<SessionTestEntity> findAll = openSession.findAll(ExampleImpl.newInstance().equals("name", "bbbb2").or().condition("length({name}) > ? or length({dd}) < ?", 4, 2).orderByAsc("aa").orderByDesc("dd"), SessionTestEntity.class);
		System.out.println(findAll);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}

	@Test
	public void findOne() throws Exception {
		Session openSession = getSession();
		SessionTestEntity findOneById = openSession.findOneById(SessionTestEntity.class, 5);
		System.out.println(findOneById);
	}
	
	@Test
	public void findOneVo() throws Exception {
		Session openSession = getSession();
		SchoolVo findOneById = openSession.findOneById(SchoolVo.class, 1);
		System.out.println(findOneById);
	}

	@Test
	public void findCount() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		int findAll = openSession.findCount(ExampleImpl.newInstance().equals("name", "bbbb2").and().condition("length({name}) > ? and length({dd}) < ?", 5, 2).orderByAsc("aa").orderByDesc("dd"), SessionTestEntity.class);
		System.out.println(findAll);
		openSession.rollback();
		openSession.setAutoCommit(true);
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
