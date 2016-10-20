package il.org.spartan.spartanizer.research;

import static il.org.spartan.spartanizer.tippers.TrimmerTestsUtils.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import il.org.spartan.spartanizer.research.patterns.*;

/** @author Ori Marcovitch
 * @since 2016 */
@SuppressWarnings("static-method") public class DefaultsToTest {
  @Test public void basic() {
    trimmingOf("return ¢ != null ? ¢ : \"\";").withTipper(ConditionalExpression.class, new DefaultsTo()).gives("return defolt(¢).to(\"\");");
  }

  // TODO: Marco decide what to do with this pattern
  @Ignore @Test public void methodOnX() {
    trimmingOf("return ¢ == null ? null : x.apply(¢) + \"\";").withTipper(ConditionalExpression.class, new DefaultsTo())
        .gives("return defolt(¢).to(x.apply(¢) + \"\");");
  }
}
