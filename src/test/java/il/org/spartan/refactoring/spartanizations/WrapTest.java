package il.org.spartan.refactoring.spartanizations;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.spartanizations.Wrap.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.*;
import org.junit.*;

import il.org.spartan.*;

@SuppressWarnings({ "static-method", "javadoc" }) public class WrapTest {
  @Test public void dealWithBothKindsOfComment() {
    similar(
        ""//
            + "if (b) {\n"//
            + " /* empty */"//
            + "; \n"//
            + "} { // no else \n"//
            + " throw new Exception();\n"//
            + "}", //
        "if (b) {;} { throw new Exception(); }");
  }

  @Test public void dealWithComment() {
    azzert.that(Wrap.find(""//
        + "if (b) {\n"//
        + " /* empty */"//
        + "} else {\n"//
        + " throw new Exception();\n"//
        + "}"), is(Wrap.STATEMENT_OR_SOMETHING_THAT_MAY_APPEAR_IN_A_METHOD));
  }

  @Test public void essenceTest() {
    azzert.that("if(b){;}throw new Exception();", is(essence("if (b) {\n /* empty */; \n} // no else \n throw new Exception();\n")));
  }

  @Test public void expression() {
    azzert.that(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT.off(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT.on("a+b")), is("a+b"));
  }

  @Test public void findAddition() {
    azzert.that(Wrap.find("a+b"), is(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT));
  }

  @Test public void findDivision() {
    azzert.that(Wrap.find("a/b"), is(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT));
  }

  @Test public void findDivisionOfExpressions() {
    azzert.that(Wrap.find("(a+b)/++b"), is(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT));
  }

  @Test public void findEmptyBlock() {
    azzert.that(Wrap.find("{}"), is(Wrap.STATEMENT_OR_SOMETHING_THAT_MAY_APPEAR_IN_A_METHOD));
  }

  @Test(expected = AssertionError.class) public void findError() {
    azzert.that(Wrap.find("}} f() { a();} b();}"), is(nullValue()));
  }

  @Test public void findExpression() {
    azzert.that(Wrap.find("i++"), is(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT));
  }

  @Test public void findMethod() {
    azzert.that(Wrap.find("f() { a(); b();}"), is(Wrap.A_CLASS_MEMBER_OF_SOME_SORT));
  }

  @Test public void findStatement() {
    azzert.that(Wrap.find("for(;;);"), is(Wrap.STATEMENT_OR_SOMETHING_THAT_MAY_APPEAR_IN_A_METHOD));
  }

  @Test public void findTwoStatements() {
    azzert.that(Wrap.find("a(); b();"), is(Wrap.STATEMENT_OR_SOMETHING_THAT_MAY_APPEAR_IN_A_METHOD));
  }

  @Test public void intMethod() {
    azzert.that(Wrap.find("int f() { int s = 0; for (int i = 0; i < 10; ++i) s += i; return s;}"), is(Wrap.A_CLASS_MEMBER_OF_SOME_SORT));
  }

  @Test public void intoCompilationUnit() {
    final Wrap w = Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT;
    final String codeFragment = "a + b * c";
    final CompilationUnit u = w.intoCompilationUnit(codeFragment);
    azzert.notNull(u);
    azzert.that(w.off(u.toString()), containsString(codeFragment));
  }

  @Test public void intoDocument() {
    final Wrap w = Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT;
    final String codeFragment = "a + b * c";
    final Document d = w.intoDocument(codeFragment);
    azzert.notNull(d);
    azzert.that(w.off(d.get()), containsString(codeFragment));
  }

  @Test public void method() {
    azzert.that(Wrap.A_CLASS_MEMBER_OF_SOME_SORT.off(Wrap.A_CLASS_MEMBER_OF_SOME_SORT.on("int f() { return a; }")), is("int f() { return a; }"));
  }

  @Test public void offDivision() {
    azzert.that("a/b", is(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT.off(Wrap.EXPRESSION_IE_SOMETHING_THAT_MAY_SERVE_AS_ARGUMENT.on("a/b"))));
  }

  @Test public void removeComments() {
    similar(Wrap.removeComments("" + "if (b) {\n" + " /* empty */" + "} else {\n" + " throw new Exception();\n" + "}"),
        "if (b) {} else { throw new Exception(); }");
  }

  private void similar(final String s1, final String s2) {
    azzert.that(essence(s2), is(essence(s1)));
  }

  @Test public void statement() {
    azzert.that(Wrap.STATEMENT_OR_SOMETHING_THAT_MAY_APPEAR_IN_A_METHOD.off(Wrap.STATEMENT_OR_SOMETHING_THAT_MAY_APPEAR_IN_A_METHOD.on("int a;")), is("int a;"));
  }
}
