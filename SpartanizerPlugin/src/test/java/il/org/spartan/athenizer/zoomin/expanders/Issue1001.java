package il.org.spartan.athenizer.zoomin.expanders;

import static il.org.spartan.spartanizer.testing.TestUtilsBloating.*;

import org.junit.*;

import il.org.spartan.athenizer.bloaters.*;
import il.org.spartan.spartanizer.meta.*;

/** Unit test for {@link AssignmentOperatorBloater}.
 * @author Ori Roth {@code ori.rothh@gmail.com}
 * @since 2016-12-25 [[SuppressWarningsSpartan]] */
@SuppressWarnings("static-method")
//This Should be ignored because we removed this expander
@Ignore
public class Issue1001 {
  @Test public void basic() {
    bloatingOf(Aux.instance()).givesWithBinding("" //
        + "void f1() {\n" //
        + "  int a;\n" //
        + "  a = 0;\n" //
        + "  a = a + 1;\n" //
        + "}", "f1");
  }

  @Test public void inclusion() {
    bloatingOf(Aux.instance()).givesWithBinding("" //
        + "void f2() {\n" //
        + "  int a;\n" //
        + "  int b;\n" //
        + "  a = 0;\n" //
        + "  b = 0;\n" //
        + "  x(a = a + (b += 1));\n" //
        + "}", "f2");
  }

  @Test public void inclusion2() {
    bloatingOf(Aux.instance()).givesWithBinding("" //
        + "void f22() {\n" //
        + "  int a;\n" //
        + "  int b;\n" //
        + "  a = 0;\n" //
        + "  b = 0;\n" //
        + "  x(a = a + (b = b + 1));\n" //
        + "}", "f22");
  }

  @Test public void inclusion3() {
    bloatingOf(Aux.instance()).givesWithBinding("" //
        + "void f3() {\n" //
        + "  int a;\n" //
        + "  int b;\n" //
        + "  a = 0;\n" //
        + "  b = 0;\n" //
        + "  x(a = a + (b = 1));\n" //
        + "}", "f3");
  }

  @Test public void nonMatchingPrimitives() {
    bloatingOf(Aux.instance()).staysWithBinding();
  }

  @Test public void operators() {
    bloatingOf(Aux.instance()).givesWithBinding("" //
        + "void f4() {\n" //
        + "  int a;\n" //
        + "  int b;\n" //
        + "  a = 0;\n" //
        + "  b = 0;\n" //
        + "  x(a = a % (b |= 1));\n" //
        + "}", "f4");
  }

  @Test public void operators2() {
    bloatingOf(Aux.instance()).givesWithBinding("" //
        + "void f44() {\n" //
        + "  int a;\n" //
        + "  int b;\n" //
        + "  a = 0;\n" //
        + "  b = 0;\n" //
        + "  x(a = a % (b = b | 1));\n" //
        + "}", "f44");
  }

  /** [[SuppressWarningsSpartan]] */
  @SuppressWarnings({ "unused", "TooBroadScope" })
  public static class Aux extends MetaFixture {
    public static Aux instance() {
      return new Aux();
    }

    void f1() { /**/ }

    void f2() {
      int a;
      int b;
      a = 0;
      b = 0;
      x(a += b += 1);
    }

    void f22() {
      int a;
      int b;
      a = 0;
      b = 0;
      x(a = a + (b += 1));
    }

    void f3() {
      int a;
      a = 0;
      x(a += 1);
    }

    void f4() {
      int a;
      int b;
      a = 0;
      b = 0;
      x(a %= b |= 1);
    }

    void f44() {
      int a;
      int b;
      a = 0;
      b = 0;
      x(a = a % (b |= 1));
    }

    void x(final int y) {
      //
    }
  }
}
