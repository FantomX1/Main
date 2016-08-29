package il.org.spartan.refactoring.wring;


import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.utils.*;

/**
 * Evaluate the addition of numbers according to the following rules <br/> <br/>
 * <code>
 * int + int --> int <br/>
 * double + double --> double <br/>
 * long + long --> long <br/>
 * int + double --> double <br/>
 * int + long --> long <br/>
 * long + double --> double <br/>
 * </code>
 * @author Dor Ma'ayan 
 * @since 2016
 */
public class EvaluateAddition extends Wring.ReplaceCurrentNode<InfixExpression> implements Kind.NoImpact {

 
  
  @Override public String description() {
      return "Evaluate addition of int numbers";
  }

  @Override String description(@SuppressWarnings("unused") InfixExpression __) {
    return "Evaluate addition of int numbers";
  }
  
  @Override ASTNode replacement(InfixExpression e) {
    if( e.getOperator() != PLUS )
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
    
    int sum = 0;
    for (final Expression ¢ : es){
      if (!(¢ instanceof NumberLiteral) || !EvaluateAux.isInt(¢))
        return null;
      sum=sum + EvaluateAux.extractInt(¢);
    }  
    return e.getAST().newNumberLiteral(Integer.toString(sum));
  }
 
 private static ASTNode replacementDouble(final List<Expression> es, InfixExpression e) {
   double sum = 0;
   for (final Expression ¢ : es){
     if (!(¢ instanceof NumberLiteral) || !EvaluateAux.isNumber(¢))
       return null;
     sum=sum + EvaluateAux.extractDouble(¢);
   }  
   return e.getAST().newNumberLiteral(Double.toString(sum));
 }
 
 private static ASTNode replacementLong(final List<Expression> es, InfixExpression e) {
   long sum = 0;
   for (final Expression ¢ : es){
     if (!(¢ instanceof NumberLiteral) || !EvaluateAux.isNumber(¢))
       return null;
     sum=sum + EvaluateAux.extractLong(¢);
   }  
   return e.getAST().newNumberLiteral(Long.toString(sum)+"L");
 }
  
}
