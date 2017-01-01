package il.org.spartan.spartanizer.research.patterns;

import static il.org.spartan.spartanizer.tippers.TrimmerTestsUtils.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

/** @author Ori Marcovitch
 * @since 2016 */
@SuppressWarnings("static-method")
public class CreateFromTest {
  @Test public void a() {
    trimmingOf("StatsAccumulator $=new StatsAccumulator();  $.addAll(values);").withTipper(Block.class, new CreateFrom())
        .gives("StatsAccumulator $=Create.from(values);");
  }
}
