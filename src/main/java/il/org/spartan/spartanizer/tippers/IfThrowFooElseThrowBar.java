package il.org.spartan.spartanizer.tippers;

import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.step.*;

import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.wringing.*;

/** convert
 *
 * <pre>
 * if (x)
 *   throw b;
 * else
 *   throw c;
 * </pre>
 *
 * into
 *
 * <pre>
 * throw x? b : c
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-07-29 */
public final class IfThrowFooElseThrowBar extends ReplaceCurrentNode<IfStatement> implements Kind.Ternarization {
  @Override public String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Consolidate 'if' into a 'throw' statement of a conditional expression";
  }

  @Override public boolean prerequisite(final IfStatement ¢) {
    return ¢ != null && extract.throwExpression(then(¢)) != null && extract.throwExpression(elze(¢)) != null;
  }

  @Override public Statement replacement(final IfStatement s) {
    final Expression condition = s.getExpression();
    final Expression then = extract.throwExpression(then(s));
    final Expression elze = extract.throwExpression(elze(s));
    return then == null || elze == null ? null : make.throwOf(subject.pair(then, elze).toCondition(condition));
  }
}
