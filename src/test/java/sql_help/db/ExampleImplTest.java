package sql_help.db;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import sql_help.entity.TestEntityA;

/**
 * 测试example条件拼接
 * @author yousongshu
 *
 */
public class ExampleImplTest {

	@FunctionalInterface
	public interface Supplier {
	    void get();
	}
	
	@Test
	public void andEquest() {
		TestEntityA aa = new TestEntityA();
//		(TestEntityA::getFive)
	}
}
