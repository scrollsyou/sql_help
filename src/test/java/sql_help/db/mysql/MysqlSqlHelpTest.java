package sql_help.db.mysql;

import org.junit.Test;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.config.GlogalConfig;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import sql_help.entity.TestEntityA;

public class MysqlSqlHelpTest {

	public String getSqlToCreateTable(BeanWrapper wrapper, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void getSqlToSelect() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToSelect(BeanWrapper.instrance(TestEntityA.class, new GlogalConfig()), true));
	}

	@Test
	public void getSqlToUpdate() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToUpdate(BeanWrapper.instrance(TestEntityA.class, new GlogalConfig()), true));
	}

	@Test
	public void getSqlToInsert() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToInsert(BeanWrapper.instrance(TestEntityA.class, new GlogalConfig()), true));
	}

	public String getSqlToDelete(BeanWrapper wrapper, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

}
