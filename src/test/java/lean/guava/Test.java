package lean.guava;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Test {

	@org.junit.Test
	public void test() {
		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "aaaBbb"));
		System.out.println(CharMatcher.javaIsoControl().removeFrom("Test Control.~"));
		System.out.println(Joiner.on(",").skipNulls().join(new String[] {"12","","32",null}));
		
		EventBus bus = new EventBus();
		bus.register(new AA());
		bus.post(new TestEvent());
	}
	public static void main(String[] args) {
		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "aaaBbb"));
		System.out.println(CharMatcher.javaIsoControl().removeFrom("Test Control.~"));
		System.out.println(Joiner.on(",").skipNulls().join(new String[] {"12","","32",null}));
		
		EventBus bus = new EventBus();
		bus.register(new AA());
		bus.post(new TestEvent());
	}
	public static class TestEvent{
		
	}
	public static class AA{
		@Subscribe
		public void testSubEvent(TestEvent e) {
			System.out.println("事件已触发!");
		}
	} 
	
	
}
