package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.extract.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.utils.*;

/** convert
 *
 * <pre>
 * a = 3;
 * return a;
 * </pre>
 *
 * to
 *
 * <pre>
 * return a = 3;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-08-28 */
public final class AssignmentAndReturn extends Wring.ReplaceToNextStatement<Assignment> implements Kind.Canonicalization {
  @Override String description(final Assignment a) {
    return "Inline assignment to " + navigate.left(a) + " with its subsequent 'return'";
  }

  @Override ASTRewrite go(final ASTRewrite r, final Assignment a, final Statement nextStatement, final TextEditGroup g) {
    final Statement parent = az.asStatement(a.getParent());
    if (parent == null || parent instanceof ForStatement)
      return null;
    final ReturnStatement s = az.returnStatement(nextStatement);
    if (s == null || !wizard.same(navigate.left(a), core(s.getExpression())))
      return null;
    r.remove(parent, g);
    r.replace(s, subject.operand(a).toReturn(), g);
    return r;
  }
}
