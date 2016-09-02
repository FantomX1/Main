package il.org.spartan.refactoring.wring;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.PostfixExpression.*;

import il.org.spartan.refactoring.assemble.*;
import il.org.spartan.refactoring.ast.*;

/** converts, whenever possible, postfix increment/decrement to prefix
 * increment/decrement
 * @author Yossi Gil
 * @since 2015-7-17 */
public final class PostfixToPrefix extends Wring.ReplaceCurrentNode<PostfixExpression> implements Kind.Canonicalization {
  private static String description(final Operator o) {
    return o == PostfixExpression.Operator.DECREMENT ? "decrement" : "increment";
  }

  private static PrefixExpression.Operator pre2post(final PostfixExpression.Operator o) {
    return o == PostfixExpression.Operator.DECREMENT ? PrefixExpression.Operator.DECREMENT : PrefixExpression.Operator.INCREMENT;
  }

  @Override String description(final PostfixExpression e) {
    return "Convert post-" + description(e.getOperator()) + " of " + step.operand(e) + " to pre-" + description(e.getOperator());
  }

  @Override protected boolean eligible(final PostfixExpression e) {
    return !(e.getParent() instanceof Expression) //
        && AncestorSearch.forType(ASTNode.VARIABLE_DECLARATION_STATEMENT).from(e) == null //
        && AncestorSearch.forType(ASTNode.SINGLE_VARIABLE_DECLARATION).from(e) == null //
        && AncestorSearch.forType(ASTNode.VARIABLE_DECLARATION_EXPRESSION).from(e) == null;
  }

  @Override PrefixExpression replacement(final PostfixExpression e) {
    return subject.operand(step.operand(e)).to(pre2post(e.getOperator()));
  }

  @Override boolean scopeIncludes(@SuppressWarnings("unused") final PostfixExpression __) {
    return true;
  }
}
