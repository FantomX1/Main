package il.org.spartan.refactoring.utils;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.utils.Into.*;
import static il.org.spartan.refactoring.utils.extract.*;
import static org.eclipse.jdt.core.dom.ASTNode.*;

import org.junit.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.java.*;

/** Test class for class {@link Is}
 * @author Yossi Gil
 * @since 2015-07-17 */
@SuppressWarnings({ "javadoc", "static-method" }) //
public class IsTest {
  @Test public void booleanLiteralFalseOnNull() {
    azzert.that(Is.booleanLiteral(e("null")), is(false));
  }

  @Test public void booleanLiteralFalseOnNumeric() {
    azzert.that(Is.booleanLiteral(e("12")), is(false));
  }

  @Test public void booleanLiteralFalseOnThis() {
    azzert.that(Is.booleanLiteral(e("this")), is(false));
  }

  @Test public void booleanLiteralTrueOnFalse() {
    azzert.that(Is.booleanLiteral(e("false")), is(true));
  }

  @Test public void booleanLiteralTrueOnTrue() {
    azzert.that(Is.booleanLiteral(e("true")), is(true));
  }

  @Test public void callIsSpecificTrue() {
    azzert.that(Is.constant(e("this")), is(true));
  }

  @Test public void canMakeExpression() {
    e("2+2");
  }

  @Test public void deterministicArray1() {
    azzert.that(sideEffects.deterministic(e("new a[3]")), is(false));
  }

  @Test public void deterministicArray2() {
    azzert.that(sideEffects.deterministic(e("new int[] {12,13}")), is(false));
  }

  @Test public void deterministicArray3() {
    azzert.that(sideEffects.deterministic(e("new int[] {12,13, i++}")), is(false));
  }

  @Test public void deterministicArray4() {
    azzert.that(sideEffects.deterministic(e("new int[f()]")), is(false));
  }

  @Test public void isConstantFalse() {
    azzert.that(Is.constant(e("a")), is(false));
  }

  @Test public void isNullFalse1() {
    azzert.that(Is.nullLiteral(e("this")), is(false));
  }

  @Test public void isNullFalse2() {
    azzert.that(Is.thisLiteral(e("this.a")), is(false));
  }

  @Test public void isNullTrue() {
    azzert.that(Is.nullLiteral(e("null")), is(true));
  }

  @Test public void isOneOf() {
    azzert.that(Is.oneOf(e("this"), CHARACTER_LITERAL, NUMBER_LITERAL, NULL_LITERAL, THIS_EXPRESSION), is(true));
  }

  @Test public void isThisFalse1() {
    azzert.that(Is.thisLiteral(e("null")), is(false));
  }

  @Test public void isThisFalse2() {
    azzert.that(Is.thisLiteral(e("this.a")), is(false));
  }

  @Test public void isThisTrue() {
    azzert.that(Is.thisLiteral(e("this")), is(true));
  }

  @Test public void negative0() {
    azzert.that(Is.negative(e("0")), is(false));
  }

  @Test public void negative1() {
    azzert.that(Is.negative(e("0")), is(false));
  }

  @Test public void negativeMinus1() {
    azzert.that(Is.negative(e("- 1")), is(true));
  }

  @Test public void negativeMinus2() {
    azzert.that(Is.negative(e("- 2")), is(true));
  }

  @Test public void negativeMinusA() {
    azzert.that(Is.negative(e("- a")), is(true));
  }

  @Test public void negativeNull() {
    azzert.that(Is.negative(e("null")), is(false));
  }

  @Test public void nonnAssociative() {
    azzert.that(Precedence.nonAssociative(e("1")), is(false));
    azzert.that(Precedence.nonAssociative(e("-1")), is(false));
    azzert.that(Precedence.nonAssociative(e("-1+2")), is(false));
    azzert.that(Precedence.nonAssociative(e("1+2")), is(false));
    azzert.that(Precedence.nonAssociative(e("2-1")), is(true));
    azzert.that(Precedence.nonAssociative(e("2/1")), is(true));
    azzert.that(Precedence.nonAssociative(e("2%1")), is(true));
    azzert.that(Precedence.nonAssociative(e("2*1")), is(false));
  }

  @Test public void numericLiteralFalse1() {
    azzert.that(Is.numericLiteral(e("2*3")), is(false));
  }

  @Test public void numericLiteralFalse2() {
    azzert.that(Is.numericLiteral(e("2*3")), is(false));
  }

  @Test public void numericLiteralTrue() {
    azzert.that(Is.numericLiteral(e("1")), is(true));
  }

  @Test public void seriesA_3() {
    azzert.nay(Is.infixPlus(e("(i+j)")));
    azzert.aye(Is.infixPlus(core(e("(i+j)"))));
    azzert.nay(Is.infixMinus(e("(i-j)")));
    azzert.aye(Is.infixMinus(core(e("(i-j)"))));
  }
}
