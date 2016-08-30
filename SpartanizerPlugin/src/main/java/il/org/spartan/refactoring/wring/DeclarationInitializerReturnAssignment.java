package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.navigate.*;
import static il.org.spartan.refactoring.wring.Wrings.*;
import static org.eclipse.jdt.core.dom.Assignment.Operator.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Assignment.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.LocalInliner.*;

/** convert
 *
 * <pre>
 * int a = 3;
 * return a;
 * </pre>
 *
 * into
 *
 * <pre>
 * return a;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-08-07 */
public final class DeclarationInitializerReturnAssignment extends Wring.VariableDeclarationFragementAndStatement implements Kind.Inlining {
  @Override String description(final VariableDeclarationFragment f) {
    return "Eliminate temporary " + f.getName() + " and inline its value into the expression of the subsequent return statement";
  }

  @Override ASTRewrite go(final ASTRewrite r, final VariableDeclarationFragment f, final SimpleName n, final Expression initializer,
      final Statement nextStatement, final TextEditGroup g) {
    if (initializer == null || hasAnnotation(f))
      return null;
    final ReturnStatement s = az.returnStatement(nextStatement);
    if (s == null)
      return null;
    final Assignment a = az.assignment(extract.expression(s));
    if (a == null || !wizard.same(n, left(a)))
      return null;
    final Operator o = a.getOperator();
    if (o != ASSIGN)
      return null;
    final Expression newReturnValue = wizard.duplicate(right(a));
    final LocalInlineWithValue i = new LocalInliner(n, r, g).byValue(initializer);
    if (!i.canInlineInto(newReturnValue) || i.replacedSize(newReturnValue) - eliminationSaving(f) - size(newReturnValue) > 0)
      return null;
    r.replace(a, newReturnValue, g);
    i.inlineInto(newReturnValue);
    eliminate(f, r, g);
    return r;
  }
}
