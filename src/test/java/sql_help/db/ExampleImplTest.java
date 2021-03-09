package sql_help.db;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.db.ExampleImpl;

import sql_help.entity.TestEntityA;

/**
 * 测试example条件拼接
 * @author yousongshu
 *
 */
public class ExampleImplTest {


	@Test
	public void example() {
		Example example = ExampleImpl.newInstance();
		System.out.println(example.equals("name", "小明")
				.and().gtEquals("age", 18)
				.and().subCondition()
					.equals("class", "12班")
				.upCondition()
				.and().equals("sex", "女")
				.and().condition("length({property}) - length({property}) > ?", 12)
				.and().in("schoolId", new ArrayList<Object>() {}).toString());
	}
}
