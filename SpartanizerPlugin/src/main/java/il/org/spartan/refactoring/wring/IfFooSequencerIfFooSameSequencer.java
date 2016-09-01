package il.org.spartan.refactoring.wring;

import static il.org.spartan.Utils.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.ast.*;
import il.org.spartan.refactoring.builder.*;

/** convert
 *
 * <pre>
 * if (X)
 *   return A;
 * if (Y)
 *   return A;
 * </pre>
 *
 * into
 *
 * <pre>
 * if (X || Y)
 *   return A;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-07-29 */
public final class IfFooSequencerIfFooSameSequencer extends Wring.ReplaceToNextStatement<IfStatement> implements Kind.Ternarization {
  @Override String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Consolidate two 'if' statements with identical body";
  }

  @Override ASTRewrite go(final ASTRewrite r, final IfStatement s, final Statement nextStatement, final TextEditGroup g) {
    if (!iz.vacuousElse(s))
      return null;
    final IfStatement s2 = az.ifStatement(nextStatement);
    if (s2 == null || !iz.vacuousElse(s2))
      return null;
    final Statement then = step.then(s);
    final List<Statement> ss1 = extract.statements(then);
    return !wizard.same(ss1, extract.statements(step.then(s2))) || !iz.sequencer(last(ss1)) ? null
        : Wrings.replaceTwoStatements(r, s,
            makeIfWithoutElse(BlockSimplify.reorganizeNestedStatement(then), subject.pair(s.getExpression(), s2.getExpression()).to(CONDITIONAL_OR)),
            g);
  }
}
