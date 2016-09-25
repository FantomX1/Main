package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.azzert.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.tippers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) @SuppressWarnings({ "javadoc", "static-method" }) public final class IfEmptyThenTest {
  private static final Statement INPUT = into.s("{if (b) ; else ff();}");
  private static final IfStatement IF = findFirst.ifStatement(INPUT);
  private static final IfEmptyThen WRING = new IfEmptyThen();

  @Test public void eligible() {
    assert WRING.canTip(IF);
  }

  @Test public void emptyThen() {
    assert iz.vacuousThen(IF);
  }

  @Test public void extractFirstIf() {
    assert IF != null;
  }

  @Test public void inputType() {
    azzert.that(INPUT, instanceOf(Block.class));
  }

  @Test public void scopeIncludes() {
    assert WRING.canTip(IF);
  }
}
