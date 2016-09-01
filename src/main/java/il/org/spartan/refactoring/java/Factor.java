package il.org.spartan.refactoring.java;

import org.eclipse.jdt.core.dom.*;
//TOOD: Who wrote this class?
class Factor {
  private final boolean divider;
  public final Expression expression;

  Factor(final boolean divide, final Expression expression) {
    divider = divide;
    this.expression = expression;
  }

  static Factor times(final Expression e) {
    return new Factor(false, e);
  }

  static Factor divide(final Expression e) {
    return new Factor(true, e);
  }

  boolean divider() {
    return divider;
  }

  // doesn't work for division, need to figure out why
  Expression asExpression() {
    if (!divider)
      return expression;
    final InfixExpression $ = expression.getAST().newInfixExpression();
    $.setOperator(InfixExpression.Operator.DIVIDE);
    $.setLeftOperand(expression.getAST().newNumberLiteral("1"));
    $.setRightOperand(expression);
    return $;
  }

  public boolean multiplier() {
    return !divider;
  }
}