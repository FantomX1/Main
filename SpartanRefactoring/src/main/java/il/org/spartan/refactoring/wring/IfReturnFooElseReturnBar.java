package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Funcs.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.preferences.PluginPreferencesResources.*;
import il.org.spartan.refactoring.utils.*;

/** A {@link Wring} to convert <pre>if (x) return b; else return c;</pre> into
 * <pre>return x? b : c</pre>
 * @author Yossi Gil
 * @since 2015-07-29 */
public final class IfReturnFooElseReturnBar extends Wring.ReplaceCurrentNode<IfStatement> {
  @Override Statement replacement(final IfStatement s) {
    final Expression condition = s.getExpression();
    final Expression then = extract.returnExpression(then(s));
    final Expression elze = extract.returnExpression(elze(s));
    return then == null || elze == null ? null : Subject.operand(Subject.pair(then, elze).toCondition(condition)).toReturn();
  }
  @Override boolean scopeIncludes(final IfStatement s) {
    return s != null && extract.returnExpression(then(s)) != null && extract.returnExpression(elze(s)) != null;
  }
  @Override String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Replace if with a return of a conditional statement";
  }
  @Override WringGroup wringGroup() {
    return WringGroup.IF_TO_TERNARY;
  }
}