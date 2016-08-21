package il.org.spartan.refactoring.wring;

import static il.org.spartan.utils.Utils.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;

import il.org.spartan.refactoring.utils.*;

/** A {@link Wring} that sorts the arguments of a {@link Operator#DIVIDE}
 * expression.
 * @author Yossi Gil
 * @since 2015-09-05 */
public final class InfixDivisonSort extends Wring.InfixSortingOfCDR implements Kind.Sorting {
  @Override boolean scopeIncludes(final InfixExpression e) {
    return in(e.getOperator(), DIVIDE);
  }

  @Override boolean sort(final List<Expression> es) {
    return ExpressionComparator.MULTIPLICATION.sort(es);
  }
}
