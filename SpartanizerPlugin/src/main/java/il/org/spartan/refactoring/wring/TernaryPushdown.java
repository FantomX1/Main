package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Plant.*;
import static il.org.spartan.refactoring.utils.Restructure.*;
import static il.org.spartan.refactoring.utils.expose.*;
import static il.org.spartan.refactoring.utils.extract.*;
import static org.eclipse.jdt.core.dom.ASTNode.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.java.*;
import il.org.spartan.refactoring.utils.*;

public final class TernaryPushdown extends Wring.ReplaceCurrentNode<ConditionalExpression> implements Kind.DistributiveRefactoring {
  private static int findSingleDifference(final List<Expression> es1, final List<Expression> es2) {
    int $ = -1;
    for (int i = 0; i < es1.size(); ++i)
      if (!wizard.same(es1.get(i), es2.get(i))) {
        if ($ >= 0)
          return -1;
        $ = i;
      }
    return $;
  }

  @SuppressWarnings("unchecked") private static <T extends Expression> T p(final ASTNode n, final T $) {
    return !Precedence.Is.legal(Precedence.of(n)) || Precedence.of(n) >= Precedence.of($) ? $ : (T) parenthesize($);
  }

  static Expression pushdown(final ConditionalExpression e) {
    if (e == null)
      return null;
    final Expression then = core(e.getThenExpression());
    final Expression elze = core(e.getElseExpression());
    return wizard.same(then, elze) ? null : pushdown(e, then, elze);
  }

  static Expression pushdown(final ConditionalExpression e, final Assignment a1, final Assignment a2) {
    return a1.getOperator() != a2.getOperator() || !wizard.same(expose.left(a1), expose.left(a2)) ? null
        : plant(subject.pair(expose.left(a1), subject.pair(right(a1), right(a2)).toCondition(e.getExpression())).to(a1.getOperator()))
            .into(e.getParent());
  }

  private static Expression pushdown(final ConditionalExpression e, final ClassInstanceCreation e1, final ClassInstanceCreation e2) {
    if (!wizard.same(e1.getType(), e2.getType()) || !wizard.same(e1.getExpression(), e2.getExpression()))
      return null;
    final List<Expression> es1 = arguments(e1);
    final List<Expression> es2 = arguments(e2);
    if (es1.size() != es2.size())
      return null;
    final int i = findSingleDifference(es1, es2);
    if (i < 0)
      return null;
    final ClassInstanceCreation $ = wizard.duplicate(e1);
    arguments($).remove(i);
    arguments($).add(i, subject.pair(es1.get(i), es2.get(i)).toCondition(e.getExpression()));
    return $;
  }

  private static Expression pushdown(final ConditionalExpression e, final Expression e1, final Expression e2) {
    if (e1.getNodeType() != e2.getNodeType())
      return null;
    switch (e1.getNodeType()) {
      case SUPER_METHOD_INVOCATION:
        return pushdown(e, (SuperMethodInvocation) e1, (SuperMethodInvocation) e2);
      case METHOD_INVOCATION:
        return pushdown(e, (MethodInvocation) e1, (MethodInvocation) e2);
      case INFIX_EXPRESSION:
        return pushdown(e, (InfixExpression) e1, (InfixExpression) e2);
      case ASSIGNMENT:
        return pushdown(e, (Assignment) e1, (Assignment) e2);
      case FIELD_ACCESS:
        return pushdown(e, (FieldAccess) e1, (FieldAccess) e2);
      case CLASS_INSTANCE_CREATION:
        return pushdown(e, (ClassInstanceCreation) e1, (ClassInstanceCreation) e2);
      default:
        return null;
    }
  }

  private static Expression pushdown(final ConditionalExpression e, final FieldAccess e1, final FieldAccess e2) {
    if (!wizard.same(e1.getName(), e2.getName()))
      return null;
    final FieldAccess $ = wizard.duplicate(e1);
    $.setExpression(parenthesize(subject.pair(e1.getExpression(), e2.getExpression()).toCondition(e.getExpression())));
    return $;
  }

  private static Expression pushdown(final ConditionalExpression e, final InfixExpression e1, final InfixExpression e2) {
    if (e1.getOperator() != e2.getOperator())
      return null;
    final List<Expression> es1 = extract.operands(e1);
    final List<Expression> es2 = extract.operands(e2);
    if (es1.size() != es2.size())
      return null;
    final int i = findSingleDifference(es1, es2);
    if (i < 0)
      return null;
    final InfixExpression $ = wizard.duplicate(e1);
    final List<Expression> operands = extract.operands($);
    operands.remove(i);
    operands.add(i, p($, subject.pair(es1.get(i), es2.get(i)).toCondition(e.getExpression())));
    return p(e, subject.operands(operands).to($.getOperator()));
  }

  private static Expression pushdown(final ConditionalExpression e, final MethodInvocation e1, final MethodInvocation e2) {
    if (!wizard.same(e1.getName(), e2.getName()))
      return null;
    final List<Expression> es1 = arguments(e1);
    final List<Expression> es2 = arguments(e2);
    final Expression receiver1 = e1.getExpression();
    final Expression receiver2 = e2.getExpression();
    if (!wizard.same(receiver1, receiver2)) {
      if (receiver1 == null || !wizard.same(es1, es2))
        return null;
      final MethodInvocation $ = wizard.duplicate(e1);
      $.setExpression(parenthesize(subject.pair(receiver1, receiver2).toCondition(e.getExpression())));
      return $;
    }
    if (es1.size() != es2.size())
      return null;
    final int i = findSingleDifference(es1, es2);
    if (i < 0)
      return null;
    final MethodInvocation $ = wizard.duplicate(e1);
    arguments($).remove(i);
    arguments($).add(i, subject.pair(es1.get(i), es2.get(i)).toCondition(e.getExpression()));
    return $;
  }

  private static Expression pushdown(final ConditionalExpression e, final SuperMethodInvocation e1, final SuperMethodInvocation e2) {
    if (!wizard.same(e1.getName(), e2.getName()))
      return null;
    final List<Expression> es1 = arguments(e1);
    final List<Expression> es2 = arguments(e2);
    if (es1.size() != es2.size())
      return null;
    final int i = findSingleDifference(es1, es2);
    if (i < 0)
      return null;
    final SuperMethodInvocation $ = wizard.duplicate(e1);
    arguments($).remove(i);
    arguments($).add(i, subject.pair(es1.get(i), es2.get(i)).toCondition(e.getExpression()));
    return $;
  }

  public static Expression right(final Assignment a1) {
    return a1.getRightHandSide();
  }

  @Override String description(@SuppressWarnings("unused") final ConditionalExpression __) {
    return "Pushdown ?: into expression";
  }

  @Override Expression replacement(final ConditionalExpression e) {
    return pushdown(e);
  }

  @Override boolean scopeIncludes(final ConditionalExpression e) {
    return pushdown(e) != null;
  }
}
