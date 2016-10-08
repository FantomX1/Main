package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.tippers.TrimmerTestsUtils.*;

import org.junit.*;

/**
 * A test class regarding a bug with {@link ForToForInitializers}. 
 * Desired behavior is not to allow removal of final modifier.
 * @author Dan Greenstein
 * @since 2016 */
@SuppressWarnings("static-method") public class Issue402 {
  @Test public void a(){
    trimmingOf("final List<Object> list = new ArrayList<>();"
        + "final int len = Array.getLength(defaultValue);"
        + "for (int ¢ = 0; ¢ < len; ++¢)"
        + "list.add(Array.get(defaultValue, ¢));"
        + "$.append(list);")
    .stays();   
  }
  
  @Test public void b(){
    trimmingOf("final List<Object> list = new ArrayList<>();"
        + "final int len = onoes();"
        + "for (int ¢ = 0; ¢ < len; ++¢)"
        + "list.add(Array.get(defaultValue, ¢));"
        + "$.append(list);")
    .stays();   
  }
  
  @Test public void c(){
    trimmingOf("final List<Object> list = new ArrayList<>();"
        + "volatile int len = onoes();"
        + "for (int ¢ = 0; ¢ < len; ++¢)"
        + "list.add(Array.get(defaultValue, ¢));"
        + "$.append(list);")
    .stays();   
  }
}
