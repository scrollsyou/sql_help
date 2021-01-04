package lean.guava;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.gugusong.sqlmapper.annotation.Column;
import com.gugusong.sqlmapper.annotation.Entity;
import com.gugusong.sqlmapper.annotation.Id;

public class Test {

	@org.junit.Test
	public void test() {
		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "aaaBbb"));
		System.out.println(CharMatcher.javaIsoControl().removeFrom("Test Control.~"));
		System.out.println(Joiner.on(",").skipNulls().join(new String[] {"12","","32",null}));
		
		EventBus bus = new EventBus();
		bus.register(new AA());
		bus.post(new TestEvent());
		
		List<String> names = Lists.newArrayList();
		TypeToken<List<String>> nameTokens = new TypeToken<List<String>>() {
		};
		TypeToken<?> resolveType = nameTokens.resolveType(List.class.getTypeParameters()[0]);
		System.out.println(names.getClass());
		System.out.println(nameTokens.getType());
		System.out.println(TestEvent.class.getTypeParameters().length);
		
		Invokable<List<String>, ?> invokable = new TypeToken<List<String>>() {}.method(List.class.getMethods()[0]);
		System.out.println(invokable.getReturnType()); // String.class
		Annotation[] annotations = TestEvent.class.getAnnotations();
		for (Annotation annotation : annotations) {
			System.out.println(annotation instanceof Id);
		}
		System.out.println("调试完成");
		
		List<Long> aaa = new ArrayList<Long>();
		aaa.add(123L);
		aaa.add(34L);
		aaa.add(290L);
		aaa.sort(new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				return (int)(o1 - o2);
			}
		});
		System.out.println(aaa);
	}

	@Entity
	public static class TestEvent<E, T, A>{
		
	}
	public static class AA{
		@Subscribe
		public void testSubEvent(TestEvent e) {
			System.out.println("事件已触发!");
		}
	} 
	
	
}
