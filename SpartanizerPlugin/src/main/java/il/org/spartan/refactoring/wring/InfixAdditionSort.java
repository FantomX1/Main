package il.org.spartan.refactoring.wring;

import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;

import il.org.spartan.refactoring.utils.*;

/** sorts the arguments of a {@link Operator#PLUS} expression. Extra care is
 * taken to leave intact the use of {@link Operator#PLUS} for the concatenation
 * of {@link String}s.
 * @author Yossi Gil
 * @since 2015-07-17 */
public final class InfixAdditionSort extends Wring.InfixSorting implements Kind.Canonicalization {
  @Override boolean eligible(final InfixExpression e) {
    return Is.notString(e) && super.eligible(e);
  }

  @Override Expression replacement(final InfixExpression e) {
    final List<Expression> operands = extract.allOperands(e);
    return !Is.notString(e) || !sort(operands) ? null : subject.operands(operands).to(e.getOperator());
  }

  @Override boolean scopeIncludes(final InfixExpression e) {
    return e.getOperator() == PLUS;
  }

  @Override boolean sort(final List<Expression> es) {
    return ExpressionComparator.ADDITION.sort(es);
  }
}
