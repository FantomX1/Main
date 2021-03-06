package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Assignment.*;

import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;

/** An abstract tipper pattern for something like {@code
 * int a = 2;
 * if (b)
 *   a ?= 3;
 * }
 * @author Yossi Gil
 * @since 2017-04-23 */
public abstract class LocalInitializedIfAssignmentPattern extends LocalInitialized {
  private static final long serialVersionUID = 1;
  protected Assignment assignment;
  protected Expression condition;
  protected IfStatement nextIf;
  protected Operator operator;
  protected Expression from;

  public LocalInitializedIfAssignmentPattern() {
    needs("Next statement is an if", () -> nextIf = az.ifStatement(nextStatement));
    andAlso("Else is empty", () -> iz.vacuousElse(nextIf));
    needs("Then part is an assignment", () -> assignment = extract.assignment(then(nextIf)));
    andAlso("Assignment is to current variable", () -> wizard.eq(name, to(assignment)));
    property("Operator", () -> operator = assignment.getOperator());
    property("Condition", () -> condition = nextIf.getExpression());
    property("From", () -> from = from(assignment));
    // andAlso("Not used in older siblings", () -> !usedInOlderSiblings());
    // andAlso("Not used in condition ", () -> !usedInOlderSiblings());
  }
  @Override public String description() {
    return "Consolidate initialization of " + name + " with the subsequent conditional assignment to it";
  }
}
