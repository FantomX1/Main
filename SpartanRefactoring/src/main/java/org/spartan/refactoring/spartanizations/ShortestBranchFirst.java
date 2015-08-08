package org.spartan.refactoring.spartanizations;

import static org.eclipse.jdt.core.dom.InfixExpression.Operator.CONDITIONAL_AND;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.CONDITIONAL_OR;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER_EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS_EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.NOT_EQUALS;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.NOT;
import static org.spartan.refactoring.utils.Funcs.asPrefixExpression;
import static org.spartan.refactoring.utils.Funcs.makeIfStatement;
import static org.spartan.refactoring.utils.Funcs.makeParenthesizedConditionalExp;
import static org.spartan.refactoring.utils.Funcs.makeParenthesizedExpression;
import static org.spartan.refactoring.utils.Funcs.makePrefixExpression;
import static org.spartan.refactoring.utils.Funcs.statementsCount;
import static org.spartan.refactoring.wring.ExpressionComparator.countNodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.spartan.refactoring.utils.Extract;
import org.spartan.refactoring.utils.Is;
import org.spartan.refactoring.utils.Subject;
import org.spartan.utils.Range;

/**
 * @author Artium Nihamkin (original)
 * @author Boris van Sosin <code><boris.van.sosin [at] gmail.com></code> (v2)
 * @author Tomer Zeltzer <code><tomerr90 [at] gmail.com></code> (v3)
 * @since 2013/01/01
 */
public class ShortestBranchFirst extends SpartanizationOfInfixExpression {
  /** Instantiates this class */
  public ShortestBranchFirst() {
    super("Shortest Branch First", "Negate the expression of a conditional, and change the order of branches so that shortest branch occurs first");
  }
  @Override protected final void fillRewrite(final ASTRewrite r, final AST t, final CompilationUnit cu, final IMarker m) {
    cu.accept(new ASTVisitor() {
      @Override public boolean visit(final IfStatement n) {
        if (!inRange(m, n) || !longerFirst(n))
          return true;
        final IfStatement newIfStmnt = transpose(n);
        if (newIfStmnt != null)
          r.replace(n, newIfStmnt, null);
        return true;
      }
      @Override public boolean visit(final ConditionalExpression n) {
        if (!inRange(m, n) || !longerFirst(n))
          return true;
        final ParenthesizedExpression newCondExp = transpose(n);
        if (newCondExp != null)
          r.replace(n, newCondExp, null);
        return true;
      }
      private IfStatement transpose(final IfStatement n) {
        final Expression negatedOp = negate(t, r, n.getExpression());
        if (negatedOp == null)
          return null;
        final Statement elseStmnt = n.getElseStatement();
        final Statement thenStmnt = n.getThenStatement();
        if (statementsCount(elseStmnt) == 1 && ASTNode.IF_STATEMENT == Extract.singleStatement(elseStmnt).getNodeType()) {
          final Block newElseBlock = t.newBlock();
          newElseBlock.statements().add(r.createCopyTarget(elseStmnt));
          return makeIfStatement(t, r, negatedOp, newElseBlock, thenStmnt);
        }
        return makeIfStatement(t, r, negatedOp, elseStmnt, thenStmnt);
      }
      private ParenthesizedExpression transpose(final ConditionalExpression n) {
        return n == null ? null : makeParenthesizedConditionalExp(r, negate(t, r, n.getExpression()), n.getElseExpression(), n.getThenExpression());
      }
    });
  }
  /**
   * @return a prefix expression that is the negation of the provided
   *         expression.
   */
  static Expression negate(final AST t, final ASTRewrite r, final Expression e) {
    return e instanceof InfixExpression ? tryNegateComparison(t, r, (InfixExpression) e) //
        : e instanceof PrefixExpression ? tryNegatePrefix(r, asPrefixExpression(e)) //
            : makePrefixExpression(t, makeParenthesizedExpression(e), NOT);
  }
  private static Expression tryNegateComparison(final AST t, final ASTRewrite r, final InfixExpression e) {
    final Operator op = negate(e.getOperator());
    return op == null ? null
        : !Is.deMorgan(op) ? new Subject.Pair(e.getLeftOperand(), e.getRightOperand()).to(op)
            : new Subject.Pair(negateExp(t, r, e.getLeftOperand()), negateExp(t, r, e.getRightOperand())).to(op);
  }
  private static Expression negateExp(final AST t, final ASTRewrite r, final Expression e) {
    return Is.infix(e) ? makePrefixExpression(t, makeParenthesizedExpression(e), NOT)
        : !Is.prefix(e) || !asPrefixExpression(e).getOperator().equals(NOT) ? makePrefixExpression(t, e, NOT) : (Expression) r.createCopyTarget(asPrefixExpression(e).getOperand());
  }
  public static Operator negate(final Operator o) {
    return !negate.containsKey(o) ? null : negate.get(o);
  }
  private static Map<Operator, Operator> makeNegation() {
    final Map<Operator, Operator> $ = new HashMap<>();
    $.put(EQUALS, NOT_EQUALS);
    $.put(NOT_EQUALS, EQUALS);
    $.put(LESS_EQUALS, GREATER);
    $.put(GREATER, LESS_EQUALS);
    $.put(LESS, GREATER_EQUALS);
    $.put(GREATER_EQUALS, LESS);
    $.put(CONDITIONAL_AND, CONDITIONAL_OR);
    $.put(CONDITIONAL_OR, CONDITIONAL_AND);
    return $;
  }
  private static Map<Operator, Operator> negate = makeNegation();
  private static Expression tryNegatePrefix(final ASTRewrite r, final PrefixExpression exp) {
    return !exp.getOperator().equals(NOT) ? null : (Expression) r.createCopyTarget(exp.getOperand());
  }
  private static final int threshold = 1;
  @Override protected ASTVisitor collectOpportunities(final List<Range> $) {
    return new ASTVisitor() {
      @Override public boolean visit(final IfStatement n) {
        if (longerFirst(n))
          $.add(new Range(n));
        return true;
      }
      @Override public boolean visit(final ConditionalExpression n) {
        if (longerFirst(n))
          $.add(new Range(n));
        return true;
      }
    };
  }
  static boolean longerFirst(final IfStatement n) {
    return n.getElseStatement() != null && countNodes(n.getThenStatement()) > threshold + countNodes(n.getElseStatement());
  }
  static boolean longerFirst(final ConditionalExpression n) {
    return n.getThenExpression().getLength() > threshold + n.getElseExpression().getLength();
  }
}
