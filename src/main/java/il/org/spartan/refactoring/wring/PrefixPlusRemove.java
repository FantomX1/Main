package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Plant.*;
import static il.org.spartan.refactoring.utils.extract.*;
import static org.eclipse.jdt.core.dom.ASTNode.*;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.Wring.*;

/** Replace <code>int i = +0</code> with <code>int i = 0</code>,
 * <code>int i = +1</code> with <code>int i = 1</code> <code>int i = +a</code>
 * with <code>int i = a</code>, etc.
 * @author Matteo Orru'
 * @since 2016 */
public class PrefixPlusRemove extends ReplaceCurrentNode<PrefixExpression> implements Kind.NoImpact {
  @Override ASTNode replacement(final PrefixExpression e) {
    return e.getOperator() != PLUS ? null : plant(wizard.duplicate(heart(e.getOperand()))).into(e.getParent());
  }

  private Expression heart(final Expression e) {
    if (iz.is(e, PARENTHESIZED_EXPRESSION))
      return heart(navigate.expression(e));
    final PrefixExpression p = az.prefixExpression(e);
    return p == null || p.getOperator() != PLUS ? e : heart(p.getOperand());
  }

  @Override String description(final PrefixExpression e) {
    return "Remove unary + in " + e;
  }
}
