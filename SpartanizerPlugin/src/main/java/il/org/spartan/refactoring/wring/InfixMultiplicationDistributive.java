package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Funcs.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;

import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.Wring.*;

public final class InfixMultiplicationDistributive extends ReplaceCurrentNode<InfixExpression> implements Kind.DistributiveRefactoring {
  @Override String description(final InfixExpression e) {
    return "Apply the distributive rule to " + e;
  }

  @Override public String description() {
    return "a*b + a*c => a * (b + c)";
  }

  @Override ASTNode replacement(final InfixExpression e) {
    return e.getOperator() != PLUS ? null : replacement(extract.allOperands(e));
  }

  private ASTNode replacement(final List<Expression> es) {
    if (es.size() == 1)
      return asInfixExpression(es.get(0)).getOperator() != TIMES ? null : es.get(0);
    if (es.size() == 2)
      return replacement(asInfixExpression(es.get(0)), asInfixExpression(es.get(1)));
    final List<Expression> common = new ArrayList<>();
    final List<Expression> different = new ArrayList<>();
    List<Expression> temp = new ArrayList<>(es);
    for (int i = 0; i < es.size(); ++i) {
      System.out.println(" === " + es.get(i));
      temp = removeFirstEl(temp);
      for (final Expression op : extract.allOperands(asInfixExpression(es.get(i)))) { // b
        for (final Expression ops : temp)
          if (isIn(op, extract.allOperands(asInfixExpression(ops))))
            addCommon(op, common);
          else
            addDifferent(op, different);
        if (temp.size() == 1)
          for (final Expression $ : extract.allOperands(asInfixExpression(temp.get(0))))
            if (!isIn($, common))
              addDifferent($, different);
        removeElFromList(different, common);
      }
    }
    Expression addition = null;
    for (int i = 0; i < different.size() - 1; ++i)
      addition = subject.pair(addition != null ? addition : different.get(i), different.get(i + 1)).to(Operator.PLUS);
    Expression multiplication = null;
    if (common.size() == 0)
      return addition;
    if (common.size() == 1)
      return subject.pair(common.get(0), addition).to(Operator.TIMES);
    if (common.size() <= 1)
      return null;
    for (int i = 0; i < common.size() - 1; ++i)
      multiplication = (multiplication == null ? subject.pair(common.get(i), common.get(i + 1)) : subject.pair(multiplication, different.get(i + 1)))
          .to(Operator.TIMES);
    return subject.pair(multiplication, addition).to(Operator.TIMES);
  }

  @SuppressWarnings("static-method") private void removeElFromList(final List<Expression> items, final List<Expression> from) {
    for (final Expression item : items)
      from.remove(item);
  }

  private void addCommon(final Expression op, final List<Expression> common) {
    addNewInList(op, common);
  }

  private void addNewInList(final Expression item, final List<Expression> es) {
    if (!isIn(item, es))
      es.add(item);
  }

  private void addDifferent(final Expression op, final List<Expression> different) {
    addNewInList(op, different);
  }

  @SuppressWarnings("static-method") private List<Expression> removeFirstEl(final List<Expression> es) {
    final List<Expression> $ = new ArrayList<>(es);
    $.remove($.get(0));// remove first
    return $;
  }

  private ASTNode replacement(final InfixExpression e1, final InfixExpression e2) {
    assert e1 != null;
    assert e2 != null;
    final List<Expression> common = new ArrayList<>();
    final List<Expression> different = new ArrayList<>();
    List<Expression> es1 = extract.allOperands(e1);
    assert es1 != null;
    List<Expression> es2 = extract.allOperands(e2);
    assert es2 != null;
    for (final Expression e : es1) {
      assert e != null;
      (isIn(e, es2) ? common : different).add(e);
    }
    for (final Expression e : es2) { // [a c]
      assert e != null;
      if (!isIn(e, common))
        different.add(e);
    }
    assert common != null;
    if (!common.isEmpty())
      different.remove(common);
    assert first(common) != null;
    assert first(different) != null; 
    assert second(different) != null; 
    return subject.pair(first(common), //
        subject.pair(//
            first(different), second(different)//
        ).to(//
            Operator.PLUS)//
    ).to(//
        Operator.TIMES//
    );
  }

  @SuppressWarnings("static-method") private boolean isIn(final Expression op, final List<Expression> allOperands) {
    for (final Expression $ : allOperands)
      if (same(op, $))
        return true;
    return false;
  }

  @Override boolean scopeIncludes(final InfixExpression $) {
    return super.scopeIncludes($);
  }
}
