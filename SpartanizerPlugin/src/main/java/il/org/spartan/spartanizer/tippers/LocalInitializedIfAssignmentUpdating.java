package il.org.spartan.spartanizer.tippers;

import static org.eclipse.jdt.core.dom.Assignment.Operator.*;

import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.java.*;
import il.org.spartan.utils.*;

/** TODO Yossi Gil: document class
 * @author Yossi Gil
 * @since 2017-04-23 */
public class LocalInitializedIfAssignmentUpdating extends LocalInitializedIfAssignmentPattern {
  public LocalInitializedIfAssignmentUpdating() {
    andAlso("Assignment must be updating", () -> operator != ASSIGN);
    andAlso("Initializer has no side effects", () -> sideEffects.free(initializer));
    // andAlso("Initializer is deterministic ", () ->
    // sideEffects.deterministic(initializer));
    // andAlso("Condition does not use initializer", () ->
    // compute.usedNames(condition).contains(name + ""));
    // andAlso("From does not use initializer", () ->
    // compute.usedNames(from).contains(name + ""));
  }

  private static final long serialVersionUID = -0x3B3BD65F8057A88DL;

  @Override public Examples examples() {
    return convert("int a = 2;if (b) a += 3;").to("int a = y ? 2 + 3 : 2");
  }

  @Override protected ASTRewrite go(final ASTRewrite $, final TextEditGroup g) {
    remove.statement(nextIf, $, g);
    $.replace(initializer,
        subject.pair(//
            subject.pair(initializer, from).to(op.assign2infix(operator)), //
            initializer //
        ).toCondition(condition), g);
    return $;
  }
}