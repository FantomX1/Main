package il.org.spartan.spartanizer.patterns;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.navigate.*;

/** Specializes {@link AbstractPattern} for return statement with value.
 * @author Yossi Gil
 * @since 2017-04-22 */
public abstract class ReturnValuePattern extends AbstractPattern<ReturnStatement> {
  private static final long serialVersionUID = 1L;
  protected Expression value;
  protected MethodDeclaration methodDeclaration;

  public ReturnValuePattern() {
    super.andAlso("Extract returned value", //
        () -> value = current.getExpression() //
    ).andAlso("Return is from a method", //
        () -> methodDeclaration = containing.methodDeclaration(current));
  }
}