package il.org.spartan.refactoring.utils;

import static il.org.spartan.azzert.*;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.*;

import org.junit.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.utils.LiteralParser.*;

@SuppressWarnings({ "static-method", "javadoc" }) public class LiteralParserTest {
  @Test public void doubleBinaryLiterals() {
    azzert.that(new LiteralParser("0x1.fffffffffffffP+1023").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("0x1.0p-1022").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("0x0.0000000000001P-1022").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void doubleLiteralsFromSpecification() {
    azzert.that(new LiteralParser("1e1").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("2.").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser(".3").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("0.0").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("3.14").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("1e-9d").type(), is(Types.DOUBLE.ordinal()));
    azzert.that(new LiteralParser("1e137").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void floatLiteralsFromSpecification() {
    azzert.that(new LiteralParser("1e1f").type(), is(Types.FLOAT.ordinal()));
    azzert.that(new LiteralParser("2.f").type(), is(Types.FLOAT.ordinal()));
    azzert.that(new LiteralParser(".3f").type(), is(Types.FLOAT.ordinal()));
    azzert.that(new LiteralParser("0f").type(), is(Types.FLOAT.ordinal()));
    azzert.that(new LiteralParser("3.14f").type(), is(Types.FLOAT.ordinal()));
    azzert.that(new LiteralParser("6.022137e+23f").type(), is(Types.FLOAT.ordinal()));
  }

  @Test public void hasConstructor() {
    azzert.notNull(new LiteralParser("a"));
  }

  @Test public void hasKind() {
    azzert.that(new LiteralParser("2F").type(), greaterThanOrEqualTo(0));
  }

  @Test public void hasVisibleValue() {
    azzert.that(new LiteralParser("2F").literal, equalToIgnoringWhiteSpace("2F"));
  }

  @Test public void kindCharacter() {
    azzert.that(new LiteralParser("'l'").type(), is(Types.CHARACTER.ordinal()));
  }

  @Test public void kindDoubleLower() {
    azzert.that(new LiteralParser("2d").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindDoubleNoDoublePart() {
    azzert.that(new LiteralParser("0.4").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindDoubleNoFraction() {
    azzert.that(new LiteralParser("0.").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindDoublePlain() {
    azzert.that(new LiteralParser("0.5").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindDoubleUpper() {
    azzert.that(new LiteralParser("2D").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindDoubleWithExponetLower() {
    azzert.that(new LiteralParser("0E1").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindDoubleWithExponetUpper() {
    azzert.that(new LiteralParser("0E1").type(), is(Types.DOUBLE.ordinal()));
  }

  @Test public void kindFloatLower() {
    azzert.that(new LiteralParser("2f").type(), is(Types.FLOAT.ordinal()));
  }

  @Test public void kindFloatUpper() {
    azzert.that(new LiteralParser("2F").type(), is(Types.FLOAT.ordinal()));
  }

  @Test public void kindInteger() {
    azzert.that(new LiteralParser("22").type(), is(Types.INTEGER.ordinal()));
  }

  @Test public void kindLongLower() {
    azzert.that(new LiteralParser("22l").type(), is(Types.LONG.ordinal()));
  }

  @Test public void kindLongUpper() {
    azzert.that(new LiteralParser("22L").type(), is(Types.LONG.ordinal()));
  }
}