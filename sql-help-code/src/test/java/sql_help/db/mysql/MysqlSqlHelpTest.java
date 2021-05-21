package sql_help.db.mysql;

import org.junit.Test;

import com.gugusong.sqlmapper.common.beans.BeanWrapper;
import com.gugusong.sqlmapper.config.GlobalConfig;
import com.gugusong.sqlmapper.db.mysql.MysqlSqlHelp;

import sql_help.entity.TestEntityA;
import sql_help.entity.vo.StudentVo;

public class MysqlSqlHelpTest {

	public String getSqlToCreateTable(BeanWrapper wrapper, boolean hasFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void getSqlToSelect() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToSelect(BeanWrapper.instrance(TestEntityA.class, new GlobalConfig()), true));
	}
	@Test
	public void getSqlToSelectCount() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToSelectCount(BeanWrapper.instrance(StudentVo.class, new GlobalConfig()), true));
	}

	@Test
	public void getSqlToSelectByVo() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToSelect(BeanWrapper.instrance(StudentVo.class, new GlobalConfig()), true));
	}

	@Test
	public void getSqlToUpdate() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToUpdate(BeanWrapper.instrance(TestEntityA.class, new GlobalConfig()), true));
	}

	@Test
	public void getSqlToInsert() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToInsert(BeanWrapper.instrance(TestEntityA.class, new GlobalConfig()), true));
	}

	@Test
	public void getSqlToDelete() throws Exception {
		MysqlSqlHelp help = new MysqlSqlHelp();
		System.out.println(help.getSqlToDelete(BeanWrapper.instrance(TestEntityA.class, new GlobalConfig()), true));
	}

}
