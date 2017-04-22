package il.org.spartan.spartanizer.patterns;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.navigate.*;

/** Specializes {@link NodePattern} for return statement with value.
 * @author Yossi Gil
 * @since 2017-04-22 */
public abstract class ReturnValuePattern extends NodePattern<ReturnStatement> {
  private static final long serialVersionUID = 1;
  protected Expression value;
  protected MethodDeclaration methodDeclaration;

  public ReturnValuePattern() {
    super.notNil("Extract returned value", //
        () -> value = current.getExpression() //
    ).notNil("Return is from a method", //
        () -> methodDeclaration = containing.methodDeclaration(current));
  }
}
