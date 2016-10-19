package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.tippers.TrimmerTestsUtils.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import il.org.spartan.spartanizer.research.patterns.*;

/** @author Ori Marcovitch
 * @since 2016 */
@SuppressWarnings("static-method") public class DefaultsToTest {
  @Test public void basic() {
    trimmingOf("return ¢ != null ? ¢ : \"\";").withTipper(ConditionalExpression.class, new DefaultsTo()).gives("return defaultsTo(¢, \"\");");
  }

  @Test public void methodOnX() {
    trimmingOf("return ¢ == null ? UNKNOWN : x.apply(¢) + \"\";").withTipper(ConditionalExpression.class, new DefaultsTo())
        .gives("return defaultsTo(¢, UNKNOWN, x.apply(¢) + \"\");");
  }
}
