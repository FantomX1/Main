package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.navigate.*;
import static il.org.spartan.refactoring.wring.Wrings.*;
import static org.eclipse.jdt.core.dom.Assignment.Operator.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.LocalInliner.*;

/** convert
 *
 * <pre>
 * int a;
 * a = 3;
 * </pre>
 *
 * into
 *
 * <pre>
 * int a = 3;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-08-07 */
public final class DeclarationInitialiazerAssignment extends Wring.VariableDeclarationFragementAndStatement implements Kind.Canonicalization {
  @Override String description(final VariableDeclarationFragment f) {
    return "Consolidate declaration of " + f.getName() + " with its subsequent initialization";
  }

  @Override ASTRewrite go(final ASTRewrite r, final VariableDeclarationFragment f, final SimpleName n, final Expression initializer,
      final Statement nextStatement, final TextEditGroup g) {
    if (initializer == null)
      return null;
    final Assignment a = extract.assignment(nextStatement);
    if (a == null || !wizard.same(n, left(a)) || a.getOperator() != ASSIGN)
      return null;
    final Expression newInitializer = wizard.duplicate(right(a));
    if (doesUseForbiddenSiblings(f, newInitializer))
      return null;
    final LocalInlineWithValue i = new LocalInliner(n, r, g).byValue(initializer);
    if (!i.canInlineInto(newInitializer) || i.replacedSize(newInitializer) - size(nextStatement, initializer) > 0)
      return null;
    r.replace(initializer, newInitializer, g);
    i.inlineInto(newInitializer);
    r.remove(nextStatement, g);
    return r;
  }
}
