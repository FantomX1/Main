package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.wring.Wrings.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Assignment.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.LocalInliner.*;

/** convert
 *
 * <pre>
 * int a = 2;
 * if (b)
 *   a = 3;
 * </pre>
 *
 * into
 *
 * <pre>
 * int a = b ? 3 : 2;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-08-07 */
public final class DeclarationInitializerIfUpdateAssignment extends Wring.VariableDeclarationFragementAndStatement implements Kind.Canonicalization {
  @Override public String description(final VariableDeclarationFragment f) {
    return "Consolidate initialization of " + f.getName() + " with the subsequent conditional assignment to it";
  }

  @Override ASTRewrite go(final ASTRewrite r, final VariableDeclarationFragment f, final SimpleName n, final Expression initializer,
      final Statement nextStatement, final TextEditGroup g) {
    if (initializer == null)
      return null;
    final IfStatement s = az.ifStatement(nextStatement);
    if (s == null || !iz.vacuousElse(s))
      return null;
    s.setElseStatement(null);
    final Expression condition = s.getExpression();
    final Assignment a = extract.assignment(expose.then(s));
    if (a == null || !wizard.same(expose.left(a), n) || doesUseForbiddenSiblings(f, condition, expose.right(a)))
      return null;
    final Operator o = a.getOperator();
    if (o == Assignment.Operator.ASSIGN)
      return null;
    final ConditionalExpression newInitializer = subject.pair(assignmentAsExpression(a), initializer).toCondition(condition);
    final LocalInlineWithValue i = new LocalInliner(n, r, g).byValue(initializer);
    if (!i.canInlineInto(newInitializer) || i.replacedSize(newInitializer) - size(nextStatement, initializer) > 0)
      return null;
    r.replace(initializer, newInitializer, g);
    i.inlineInto(expose.then(newInitializer), newInitializer.getExpression());
    r.remove(nextStatement, g);
    return r;
  }
}
