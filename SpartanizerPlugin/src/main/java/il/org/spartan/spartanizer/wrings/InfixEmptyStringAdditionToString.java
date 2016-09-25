package il.org.spartan.spartanizer.wrings;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.wizard.*;

import static il.org.spartan.spartanizer.ast.step.*;

import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.engine.type.Primitive.*;
import il.org.spartan.spartanizer.wringing.*;

/** Converts <code>""+"foo"</code> to <code>"foo"</code> when x is of type
 * String
 * @author Stav Namir
 * @author Niv Shalmon
 * @since 2016-08-29 */
public final class InfixEmptyStringAdditionToString extends ReplaceCurrentNode<InfixExpression> implements Kind.InVain {
  @Override public String description() {
    return "[\"\"+foo]->foo";
  }

  @Override public String description(final InfixExpression ¢) {
    return "Eliminate concatentation of \"\" to" + (iz.emptyStringLiteral(right(¢)) ? left(¢) : right(¢));
  }

  @Override public Expression replacement(final InfixExpression x) {
    if (type.of(x) != Certain.STRING)
      return null;
    final List<Expression> es = hop.operands(x);
    assert es.size() > 1;
    final List<Expression> $ = new ArrayList<>();
    boolean isString = false;
    for (int i = 0; i < es.size(); ++i) {
      final Expression e = es.get(i);
      if (!iz.emptyStringLiteral(e)) {
        $.add(e);
        if (type.of(e) == Certain.STRING)
          isString = true;
      } else {
        if (i < es.size() - 1 && type.of(es.get(i + 1)) == Certain.STRING)
          continue;
        if (!isString) {
          $.add(e);
          isString = true;
        }
      }
    }
    return $.size() == es.size() ? null : $.size() == 1 ? $.get(0) : subject.operands($).to(PLUS2);
  }
}
