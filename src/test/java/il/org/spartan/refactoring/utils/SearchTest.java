package il.org.spartan.refactoring.utils;

import static il.org.spartan.azzert.*;
import static il.org.spartan.azzert.is;
import static il.org.spartan.refactoring.utils.Into.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import static il.org.spartan.refactoring.utils.Funcs.*;

import il.org.spartan.refactoring.utils.Collect.*;

@SuppressWarnings({ "javadoc", "static-method" }) public class SearchTest {
  private final SimpleName n = asSimpleName(e("n"));

  @Test public void awful() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "    if (n != n) return n;\n" + //
        "    final n n = new n();\n" + //
        "    if (n instanceof n)\n" + //
        "      new Object();\n" + //
        "    n();\n" + //
        "    return n;\n" + //
        "  }")).size(), is(3));
  }
  @Test public void awful1() {
    that(searcher().in(d("Object n() {\n" + //
        "    final int n = null; \n" + //
        "    if (n instanceof n)\n" + //
        "      new Object();\n" + //
        "    n();\n" + //
        "    return n;\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awful2() {
    that(searcher().in(d("Object n() {\n" + //
        "    if (n instanceof n)\n" + //
        "      new Object();\n" + //
        "    n();\n" + //
        "    return n;\n" + //
        "  }")).size(), is(2));
  }
  @Test public void awful3() {
    that(searcher().in(d("Object f() {\n" + //
        "    return n;\n" + //
        "  }")).size(), is(1));
  }
  @Test public void awfulA() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n(Object n) {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulB() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "    }\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulC() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "    }\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulClassDestroyingFurtherUses() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "    }\n" + //
        "    return n; // 1\n" + //
        "  }")).size(), is(1));
  }
  @Test public void awfulD() {
    that(searcher().in(d("Object a() {\n" + //
        "    class n {\n" + //
        "    }\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulE() {
    that(searcher().in(d("Object n() {\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulF() {
    that(searcher().in(d("Object a() {\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulNestedClass() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulNestedClassVariableAfter() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "      n n;;\n" + //
        "    }\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulOriginal() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "    final n n = new n();\n" + //
        "    if (n instanceof n)\n" + //
        "      new Object();\n" + //
        "    n();\n" + //
        "    return n;\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulShort() {
    that(searcher().in(d("Object n() {\n" + //
        "    if (n != n) return n;\n" + //
        "  }")).size(), is(3));
  }
  @Test public void awfulShortWithParameter() {
    that(searcher().in(d("Object n(int n) {\n" + //
        "    if (n != n) return n;\n" + //
        "  }")).size(), is(0));
  }
  @Test public void awfulVariantWithClass() {
    that(searcher().in(d("Object n() {\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "    if (n != n) return n;\n" + //
        "  }")).size(), is(3));
  }
  @Test public void awfulVariantWithoutClass() {
    that(searcher().in(d("Object n() {\n" + //
        "    if (n != n) return n;\n" + //
        "  }")).size(), is(3));
  }
  @Test public void awfulWithClassAfter() {
    that(searcher().in(d("Object n() {\n" + //
        "    if (n != n) return n;\n" + //
        "    final n n = new n();\n" + //
        "    if (n instanceof n)\n" + //
        "      new Object();\n" + //
        "    n();\n" + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "    return n;\n" + //
        "  }")).size(), is(3));
  }
  @Test public void awfulWithClassAfterNoRedefinition() {
    that(searcher().in(d("Object n() {\n" + //
        "    if (n != n) return n; // 3\n" + //
        "    if (n instanceof n) // 1\n" + //
        "      new Object();\n" + //
        "    n(); // 0\n " + //
        "    class n {\n" + //
        "      n n;;\n" + //
        "      Object n() {\n" + //
        "        return n;\n" + //
        "      }\n" + //
        "    }\n" + //
        "    return n; // 1\n" + //
        "  }")).size(), is(5));
  }
  @Test public void classInMethod() {
    that(searcher().in(d("void f() {class n {}}")).size(), is(0));
  }
  @Test public void constructorCall() {
    that(searcher().in(e("new n(this)\n")).size(), is(0));
  }
  @Test public void declarationVoidsUse() {
    that(nCount("final A n = n * 2; a = n;"), is(0));
  }
  @Test public void declarationVoidsUseA() {
    that(nCount("final A n = n * 2;"), is(0));
  }
  @Test public void declarationVoidsUseB() {
    that(nCount("final A n = 2; a = n;"), is(0));
  }
  @Test public void declarationVoidsUseC() {
    that(nCount("final A n = 2;"), is(0));
  }
  @Test public void definedUntilEndOfBlock() {
    that(nCount("a = n; { int n; a = n * n + n;} a = n;"), is(2));
  }
  @Test public void definedUntilEndOfBlockA() {
    that(nCount("a = n; { int n; a = n;}"), is(1));
  }
  @Test public void delarationAndDoLoopEmptyBody() {
    that(nCount("int n; do {  } while (b[i] != n);"), is(0));
  }
  @Test public void delarationAndDoLoopInMethod() {
    final String input = "void f() { int b = 3; do ; while(b != 0);  }";
    final MethodDeclaration d = d(input);
    that(d, iz(input));
    final VariableDeclarationFragment f = extract.firstVariableDeclarationFragment(d);
    that(f, notNullValue());
    final SimpleName b = f.getName();
    that(b, iz("b"));
    that(Collect.usesOf(b).in(d).size(), is(2));
  }
  @Test public void delarationAndDoLoopInMethodWithoutTheDo() {
    final String input = "void f() { int b = 3;   }";
    final MethodDeclaration d = d(input);
    that(d, iz(input));
    final VariableDeclarationFragment f = extract.firstVariableDeclarationFragment(d);
    that(f, notNullValue());
    final SimpleName b = f.getName();
    that(b, iz("b"));
    that(Collect.usesOf(b).in(d).size(), is(1));
  }
  @Test public void doLoopEmptyBody() {
    that(nCount(" do {  } while (b[i] != n);"), is(1));
  }
  @Test public void doLoopEmptyStatementBody() {
    that(nCount(" do {  } while (b[i] != n);"), is(1));
  }
  @Test public void doLoopVanilla() {
    that(nCount(" do { b[i] = 2; i++; } while (b[i] != n);"), is(1));
  }
  @Test public void fieldAccess1() {
    that(nCount("x = n.a;"), is(1));
  }
  @Test public void fieldAccess2() {
    that(nCount("x = a.n;"), is(0));
  }
  @Test public void fieldAccessDummy() {
    that(searcher().in(d("" + //
        "  public int y() {\n" + //
        "    final Z res = new Z(6);\n" + //
        "    S.out.println(res.j);\n" + //
        "    return res;\n" + //
        "  }\n" + //
        "}\n" + //
        "")).size(), is(0));
  }
  @Test public void fieldAccessReal() {
    that(searcher().in(d("" + //
        "  public int y() {\n" + //
        "    final Z n = new Z(6);\n" + //
        "    S.out.println(n.j);\n" + //
        "    return n;\n" + //
        "  }\n" + //
        "}\n" + //
        "")).size(), is(0));
  }
  @Test public void fieldAccessSimplified() {
    that(nCount("" + //
        "    S.out.println(n.j);\n" + //
        ""), is(1));
  }
  @Test public void forEnhancedAsParemeter() {
    final Statement s = s("for (int a: as) return a; ");
    final Block b = (Block) s;
    final EnhancedForStatement s2 = (EnhancedForStatement) b.statements().get(0);
    final SimpleName a = s2.getParameter().getName();
    that(a, iz("a"));
    that(Collect.usesOf(a).in(s).size(), is(2));
  }
  @Test public void forEnhancedAsParemeterInMethod() {
    final MethodDeclaration d = d("int f() { for (int a: as) return a;}");
    final Block b = d.getBody();
    final EnhancedForStatement s = (EnhancedForStatement) b.statements().get(0);
    final SimpleName a = s.getParameter().getName();
    that(a, iz("a"));
    that(Collect.usesOf(a).in(d).size(), is(2));
  }
  @Test public void forEnhancedLoop() {
    that(nCount("for (int n:ns) a= n;"), is(0));
  }
  @Test public void forLoop() {
    that(nCount("for (int a = n; a < n; a++);"), is(2));
  }
  @Test public void forLoop0() {
    that(nCount("for (int a = 2; a < 2; a++);"), is(0));
  }
  @Test public void forLoop1() {
    that(nCount("for (int a = n; a < 2; a++);"), is(1));
  }
  @Test public void forLoop1A() {
    that(nCount("for (int a = n * n + n; a < 2; a++);"), is(3));
  }
  @Test public void forLoop2() {
    that(nCount("for (int a = 1; a < n; a=1);"), is(1));
  }
  @Test public void forLoop3() {
    that(nCount("for (int a = 1; a < 2; a=n);"), is(1));
  }
  @Test public void forLoop4() {
    that(nCount("for (int a = 1; a < 2; a++) a=n;"), is(1));
  }
  @Test public void forLoop5() {
    that(nCount("for (int a = 1; a < 2; a++) a=2; a = n;"), is(1));
  }
  @Test public void forLoop6() {
    that(nCount("int a = n; for (int a = 1; a < 2; a++) a=2; a = 1;"), is(1));
  }
  @Test public void forLoop7() {
    that(nCount("int a = 2; for (int a = 1; a < 2; a++) { a=2; } a = n;"), is(1));
  }
  @Test public void forLoopEnhanced0() {
    that(nCount("for (int a: n) return n;"), is(2));
  }
  @Test public void forLoopEnhanced1() {
    that(nCount("for (int a: x) return n;"), is(1));
  }
  @Test public void forLoopEnhanced2() {
    that(nCount("for (int a: n) return 1;"), is(1));
  }
  @Test public void forLoopEnhanced3() {
    that(nCount("for (int a: as) {++n;}"), is(1));
  }
  @Test public void function() {
    that(nCount("b = n();"), is(0));
  }
  @Test public void instanceof1() {
    that(nCount("b = n instanceof x;"), is(1));
  }
  @Test public void instanceof2() {
    that(nCount("b = x instanceof n;"), is(0));
  }
  @Test public void minusMinus() {
    that(Collect.forAllOccurencesExcludingDefinitions(n).in(s("n--;")).size(), is(0));
  }
  @Test public void minusMinusPre() {
    that(Collect.forAllOccurencesExcludingDefinitions(n).in(s("--n;")).size(), is(0));
  }
  @Test public void plusPlus() {
    that(Collect.forAllOccurencesExcludingDefinitions(n).in(s("n++;")).size(), is(0));
  }
  @Test public void plusPlusPre() {
    that(Collect.forAllOccurencesExcludingDefinitions(n).in(s("++n;")).size(), is(0));
  }
  @Test public void superMethodInocation() {
    that(searcher().in(e("super.n(this)\n")).size(), is(0));
  }
  @Test public void usedAsType() {
    that(nCount("n n;"), is(0));
  }
  @Test public void usedAsType1() {
    that(nCount("n a;"), is(0));
  }
  @Test public void usedAsType2() {
    that(nCount("final n n = new n(n);"), is(0));
  }
  @Test public void vanilla() {
    final Collector findUses = searcher();
    that(findUses, notNullValue());
    that(findUses.in(s("b = n;")).size(), is(1));
  }
  @Test public void vanillaShortVersion() {
    that(nCount("b = n;"), is(1));
  }
  private int nCount(final String statement) {
    return searcher().in(s(statement)).size();
  }
  private Collector searcher() {
    return Collect.usesOf(n);
  }
}
