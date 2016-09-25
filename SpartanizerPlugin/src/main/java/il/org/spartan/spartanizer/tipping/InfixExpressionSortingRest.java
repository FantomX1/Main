package il.org.spartan.spartanizer.tipping;

import static il.org.spartan.lisp.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.dispatch.*;

public abstract class InfixExpressionSortingRest extends InfixExpressionSorting {
  @Override public final boolean prerequisite(final InfixExpression x) {
    if (!suitable(x))
      return false;
    final List<Expression> es = extract.allOperands(x);
    return es.size() > 2 && !Wrings.mixedLiteralKind(es) && sort(chop(es));
  }

  @Override public final Expression replacement(final InfixExpression x) {
    final List<Expression> operands = extract.allOperands(x);
    final Expression first = operands.remove(0);
    if (!sort(operands))
      return null;
    operands.add(0, first);
    return subject.operands(operands).to(x.getOperator());
  }
}