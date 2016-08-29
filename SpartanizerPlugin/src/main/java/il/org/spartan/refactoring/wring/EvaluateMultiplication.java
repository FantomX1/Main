package il.org.spartan.refactoring.wring;

import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.utils.*;


/**
 * Evaluate the multiplication of integer numbers according to the following rules : </br></br>
 * <code>
 * int * int --> int <br/>
 * double * double --> double <br/>
 * long * long --> long <br/>
 * int * double --> double <br/>
 * int * long --> long <br/>
 * long * double --> double <br/>
 * </code>
 * @author Dor Ma'ayan 
 * @since 2016
 */
public class EvaluateMultiplication extends Wring.ReplaceCurrentNode<InfixExpression> implements Kind.NoImpact {
  
  @Override public String description() {
      return "Evaluate multiplication of int numbers";
  }

  @Override String description(@SuppressWarnings("unused") InfixExpression __) {
    return "Evaluate multiplication of int numbers";
  }
  
  @Override ASTNode replacement(InfixExpression e) {
    if( e.getOperator() != TIMES )
      return null;
    switch(EvaluateAux.getEvaluatedType(e)){
      case INT :
        return replacementInt(extract.allOperands(e),e);
      case DOUBLE :
        return replacementDouble(extract.allOperands(e),e);
      case LONG :
        return replacementLong(extract.allOperands(e),e);
      default:
        return null;
    }
  }
  
  private static ASTNode replacementInt(final List<Expression> es, InfixExpression e) {
    int mul = 1;
    for (final Expression ¢ : es){
      if (!EvaluateAux.isCompitable(¢))
        return null;
        mul = mul * EvaluateAux.extractInt(¢);
    }  
    return e.getAST().newNumberLiteral(Integer.toString(mul));
  }
  
  private static ASTNode replacementDouble(final List<Expression> es, InfixExpression e) {
    double mul = 1;
    for (final Expression ¢ : es){
      if (!EvaluateAux.isCompitable(¢))
        return null;
        mul = mul *  EvaluateAux.extractDouble(¢);
    }  
    return e.getAST().newNumberLiteral(Double.toString(mul));
  }
  
  private static ASTNode replacementLong(final List<Expression> es, InfixExpression e) {
    long mul = 1;
    for (final Expression ¢ : es){
      if (!EvaluateAux.isCompitable(¢))
        return null;
        mul = mul *  EvaluateAux.extractLong(¢);
    }  
    return e.getAST().newNumberLiteral(Long.toString(mul)+"L");
  }
}
