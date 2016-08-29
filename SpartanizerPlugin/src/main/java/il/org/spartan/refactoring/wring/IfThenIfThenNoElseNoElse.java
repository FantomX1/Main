package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Funcs.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.utils.*;

/** convert
 *
 * <pre>
 * if (x)
 *   if (a)
 *     f();
 * </pre>
 *
 * into
 *
 * <pre>
 * if (x &amp;&amp; a)
 *   f();
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-09-01 */
public final class IfThenIfThenNoElseNoElse extends Wring<IfStatement> implements Kind.DistributiveRefactoring {
  static void collapse(final IfStatement s, final ASTRewrite r, final TextEditGroup g) {
    final IfStatement then = az.ifStatement(extract.singleThen(s));
    final InfixExpression e = subject.pair(s.getExpression(), then.getExpression()).to(CONDITIONAL_AND);
    r.replace(s.getExpression(), e, g);
    r.replace(then, duplicate(then(then)), g);
  }

  @Override String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Merge conditionals of nested if staement";
  }

  @Override Rewrite make(final IfStatement s) {
    return make(s, null);
  }

  @Override Rewrite make(final IfStatement s, final ExclusionManager exclude) {
    if (!iz.vacuousElse(s))
      return null;
    final IfStatement then = az.ifStatement(extract.singleThen(s));
    if (then == null || !iz.vacuousElse(then))
      return null;
    if (exclude != null)
      exclude.exclude(then);
    return new Rewrite(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        collapse(Wrings.blockIfNeeded(s, r, g), r, g);
      }
    };
  }
}
