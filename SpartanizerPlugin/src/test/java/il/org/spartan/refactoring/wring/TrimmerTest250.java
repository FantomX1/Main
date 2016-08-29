package il.org.spartan.refactoring.wring;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.utils.Funcs.*;
import static il.org.spartan.refactoring.utils.Restructure.*;
import static il.org.spartan.refactoring.utils.iz.*;
import static il.org.spartan.refactoring.wring.TrimmerTestsUtils.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.utils.*;

/** * Unit tests for the nesting class Unit test for the containing class. Note
 * our naming convention: a) test methods do not use the redundant "test"
 * prefix. b) test methods begin with the name of the method they check.
 * @author Yossi Gil
 * @since 2014-07-10 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
@SuppressWarnings({ "static-method", "javadoc" }) //
public class TrimmerTest250 {
  @Test public void issue50_Constructors1() {
    trimming("public class ClassTest {\n"//
        + "public  ClassTest(){}\n"//
        + "}").to("");
  }

  @Test public void issue50_EnumInInterface1() {
    trimming("public interface Int1 {\n"//
        + "static enum Day {\n"//
        + "SUNDAY, MONDAY\n"//
        + "}"//
        + "}")
            .to("public interface Int1 {\n"//
                + "enum Day {\n"//
                + "SUNDAY, MONDAY\n"//
                + "}" + "}");
  }

  @Test public void issue50_Enums() {
    trimming("public class ClassTest {\n"//
        + "static enum Day {\n"//
        + "SUNDAY, MONDAY\n"//
        + "}")
            .to("public class ClassTest {\n"//
                + "enum Day {\n"//
                + "SUNDAY, MONDAY\n"//
                + "}");
  }

  @Test public void issue50_EnumsOnlyRightModifierRemoved() {
    trimming("public class ClassTest {\n"//
        + "private static enum Day {\n"//
        + "SUNDAY, MONDAY\n"//
        + "}")
            .to("public class ClassTest {\n"//
                + "private enum Day {\n"//
                + "SUNDAY, MONDAY\n"//
                + "}");
  }

  @Test public void issue50_FinalClassMethods() {
    trimming("final class ClassTest {\n"//
        + "final void remove();\n"//
        + "}")
            .to("final class ClassTest {\n"//
                + "void remove();\n "//
                + "}");
  }

  @Test public void issue50_FinalClassMethodsOnlyRightModifierRemoved() {
    trimming("final class ClassTest {\n"//
        + "public final void remove();\n"//
        + "}")
            .to("final class ClassTest {\n"//
                + "public void remove();\n "//
                + "}");
  }

  @Test public void issue50_inEnumMember() {
    trimming(//
        "enum A {; final void f() {} public final void g() {} }"//
    ).to(null);
  }

  @Test public void issue50_inEnumMemberComplex() {
    trimming(//
        "enum A { a1 {{ f(); } \n" + //
            "protected final void f() {g();}  \n" + //
            "public final void g() {h();}  \n" + //
            "private final void h() {i();}   \n" + //
            "final void i() {f();}  \n" + //
            "}, a2 {{ f(); } \n" + //
            "final protected void f() {g();}  \n" + //
            "final void g() {h();}  \n" + //
            "final private void h() {i();}  \n" + //
            "final protected void i() {f();}  \n" + //
            "};\n" + //
            "protected abstract void f();\n" + //
            "protected void ia() {}\n" + //
            "void i() {}\n" + //
            "} \n"//
    ).to("enum A { a1 {{ f(); } \n" + //
        "void f() {g();}  \n" + //
        "public void g() {h();}  \n" + //
        "void h() {i();}   \n" + //
        "void i() {f();}  \n" + //
        "}, a2 {{ f(); } \n" + //
        "void f() {g();}  \n" + //
        "void g() {h();}  \n" + //
        "void h() {i();}  \n" + //
        "void i() {f();}  \n" + //
        "};\n" + //
        "abstract void f();\n" + //
        "void ia() {}\n" + //
        "void i() {}\n" + //
        "} \n"//
    );
  }

  @Test public void issue50_InterfaceMethods1() {
    trimming("public interface Int1 {\n"//
        + "public void add();\n"//
        + "void remove()\n; "//
        + "}")
            .to("public interface Int1 {\n"//
                + "void add();\n"//
                + "void remove()\n; "//
                + "}");
  }

  @Test public void issue50_InterfaceMethods2() {
    trimming("public interface Int1 {\n"//
        + "public abstract void add();\n"//
        + "abstract void remove()\n; "//
        + "}")
            .to("public interface Int1 {\n"//
                + "void add();\n"//
                + "void remove()\n; "//
                + "}");
  }

  @Test public void issue50_InterfaceMethods3() {
    trimming("public interface Int1 {\n"//
        + "abstract void add();\n"//
        + "void remove()\n; "//
        + "}")
            .to("public interface Int1 {\n"//
                + "void add();\n"//
                + "void remove()\n; "//
                + "}");
  }

  @Test public void issue50_SimpleDontWorking() {
    trimming("interface a"//
        + "{}").to("");
  }

  @Test public void issue50_SimpleWorking1() {
    trimming("abstract abstract interface a"//
        + "{}").to("interface a {}");
  }

  @Test public void issue50_SimpleWorking2() {
    trimming("abstract interface a"//
        + "{}").to("interface a {}");
  }

  @Test public void issue50a() {
    trimming("abstract interface a {}")//
        .to("interface a {}");//
  }

  @Test public void issue50b() {
    trimming("abstract static interface a {}")//
        .to("interface a {}");//
  }

  @Test public void issue50c() {
    trimming("static abstract interface a {}")//
        .to("interface a {}");//
  }

  @Test public void issue50d() {
    trimming("static interface a {}")//
        .to("interface a {}");//
  }

  @Test public void issue50e() {
    trimming("enum a {a,b}")//
        .to(null);//
  }

  @Test public void issue50e1() {
    trimming("enum a {a}")//
        .to(null);//
  }

  @Test public void issue50e2() {
    trimming("enum a {}")//
        .to(null);//
  }

  @Test public void issue50f() {
    trimming("static enum a {a, b}")//
        .to("enum a {a, b}");//
  }

  @Test public void issue50g() {
    trimming("static abstract enum a {x,y,z; void f() {}}")//
        .to("enum a {x,y,z; void f() {}}");//
  }

  @Test public void issue50h() {
    trimming("static abstract final enum a {x,y,z; void f() {}}")//
        .to("enum a {x,y,z; void f() {}}");//
  }

  @Test public void issue70_01() {
    trimming("(double)5").to("1.*5");
  }

  @Test public void issue70_02() {
    trimming("(double)4").to("1.*4");
  }

  @Test public void issue70_03() {
    trimming("(double)1.2").to("1.*1.2");
  }

  @Test public void issue70_04() {
    trimming("(double)'a'").to("1.*'a'");
  }

  @Test public void issue70_05() {
    trimming("(double)A").to("1.*A");
  }

  @Test public void issue70_06() {
    trimming("(double)a.b").to("1.*a.b");
  }

  @Test public void issue70_07() {
    trimming("(double)(double)5").to("1.*(double)5").to("1.*1.*5");
  }

  @Test public void issue70_08() {
    trimming("(double)((double)5)").to("1.*(double)5").to("1.*1.*5");
  }

  @Test public void issue70_09() {
    trimming("(double) 2. * (double)5")//
        .to("(double)5 * (double)2.") //
        .to("1. * 5  * 1. * 2.")//
        .to("10.0");
  }

  @Test public void issue70_10() {
    trimming("(double)5 - (double)3").to("1.*5-1.*3");
  }

  @Test public void issue70_11() {
    trimming("(double)f + (int)g").to("1.*f + (int)g");
  }

  @Test public void issue70_12() {
    trimming("foo((double)18)").to("foo(1.*18)");
  }

  @Test public void issue71a() {
    trimming("1*a").to("a");
  }

  @Test public void issue71b() {
    trimming("a*1").to("a");
  }

  @Test public void issue71c() {
    trimming("1*a*b").to("a*b");
  }

  @Test public void issue71d() {
    trimming("1*a*1*b").to("a*b");
  }

  @Test public void issue71e() {
    trimming("a*1*b*1").to("a*b");
  }

  @Test public void issue71f() {
    trimming("1.0*a").to(null);
  }

  @Test public void issue71g() {
    trimming("a*2").to("2*a");
  }

  @Test public void issue71h() {
    trimming("1*1").to("1");
  }

  @Test public void issue71i() {
    trimming("1*1*1").to("1");
  }

  @Test public void issue71j() {
    trimming("1*1*1*1*1.0").to("1.0");
  }

  @Test public void issue71k() {
    trimming("-1*1*1").to("-1");
  }

  @Test public void issue71l() {
    trimming("1*1*-1*-1").to("1");
  }

  @Test public void issue71m() {
    trimming("1*1*-1*-1*-1*1*-1").to("1");
  }

  @Test public void issue71n() {
    trimming("1*1").to("1");
  }

  @Test public void issue71o() {
    trimming("(1)*((a))").to("a");
  }

  @Test public void issue71p() {
    trimming("((1)*((a)))").to("(a)");
  }

  @Test public void issue71q() {
    trimming("1L*1").to("1L");
  }

  @Test public void issue71r() {
    trimming("1L*a").to("");
  }

  @Test public void issue72ma() {
    final String s = "0-x";
    final InfixExpression i = Into.i(s);
    azzert.that(i, iz(s));
    azzert.that(left(i), iz("0"));
    azzert.that(right(i), iz("x"));
    assert !i.hasExtendedOperands();
    assert isLiteralZero(left(i));
    assert !isLiteralZero(right(i));
    azzert.that(minus(left(i)), iz("0"));
    azzert.that(minus(right(i)), iz("-x"));
    trimming(s).to("-x");
  }

  @Test public void issue72mb() {
    trimming("x-0").to("x");
  }

  @Test public void issue72mc() {
    trimming("x-0-y").to("x-y").to(null);
  }

  @Test public void issue72md1() {
    trimming("0-x-0").to("-x-0").to("-x").to(null);
  }

  @Test public void issue72md2() {
    trimming("0-x-0-y").to("-x-0-y").to("-x-y").to(null);
  }

  @Test public void issue72md3() {
    trimming("0-x-0-y-0-z-0-0")//
        .to("-x-0-y-0-z-0-0")//
        .to("-x-y-0-z-0-0")//
        .to("-x-y-z-0-0")//
        .to("-x-y-z-0")//
        .to("-x-y-z")//
        .to(null);
  }

  @Test public void issue72me() {
    trimming("0-(x-0)").to("-(x-0)").to("-(x)").to(null);
  }

  @Test public void issue72me1() {
    assert !iz.negative(Into.e("0"));
  }

  @Test public void issue72me2() {
    assert iz.negative(Into.e("-1"));
    assert !iz.negative(Into.e("+1"));
    assert !iz.negative(Into.e("1"));
  }

  @Test public void issue72me3() {
    assert iz.negative(Into.e("-x"));
    assert !iz.negative(Into.e("+x"));
    assert !iz.negative(Into.e("x"));
  }

  @Test public void issue72meA() {
    trimming("(x-0)").to("(x)").to(null);
  }

  @Test public void issue72mf1() {
    trimming("0-(x-y)").to("-(x-y)").to(null);
  }

  @Test public void issue72mf1A() {
    trimming("0-(x-0)")//
        .to("-(x-0)")//
        .to("-(x)") //
        .to(null);
  }

  @Test public void issue72mf1B() {
    assert iz.isSimple(Into.e("x"));
    trimming("-(x-0)")//
        .to("-(x)")//
        .to(null);
  }

  @Test public void issue72mg() {
    trimming("(x-0)-0").to("(x)").to(null);
  }

  @Test public void issue72mg1() {
    trimming("-(x-0)-0").to("-(x)").to(null);
  }

  @Test public void issue72mh() {
    trimming("x-0-y").to("x-y").to(null);
  }

  @Test public void issue72mi() {
    trimming("0-x-0-y-0-z-0")//
        .to("-x-0-y-0-z-0")//
        .to("-x-y-0-z-0")//
        .to("-x-y-z-0")//
        .to("-x-y-z")//
        .to(null);
  }

  @Test public void issue72mj() {
    trimming("0-0").to("0");
  }

  @Test public void issue72pa() {
    trimming("x+0").to("x");
  }

  @Test public void issue72pb() {
    trimming("0+x").to("x");
  }

  @Test public void issue72pc() {
    trimming("0+x").to("x");
  }

  @Test public void issue72pd() {
    trimming("0+x+0").to("x").to(null);
  }

  @Test public void issue72pe() {
    trimming("x+0+x").to("x+x").to(null);
  }

  @Test public void issue72pf() {
    trimming("x+0+x+0+0+y+0+0+0+0+z+0+h+0").to("x+x+y+z+h").to(null);
  }

  @Test public void issue72pg() {
    trimming("0+(x+y)").to("0+x+y").to("x+y").to(null);
  }

  @Test public void issue72ph() {
    trimming("0+((x+y)+0+(z+h))+0")//
        .to("0+x+y+0+z+h+0")//
        .to("x+y+z+h")//
        .to(null);
  }

  @Test public void issue72pi() {
    trimming("0+(0+x+y+(4+0))").to("0+0+x+y+4+0").to("x+y+4").to(null);
  }
  
  @Test public void issue73_01() {
    trimming("\"\" + \"abc\"").to("\"abc\"");
  }
  
  @Test public void issue73_02() {
    trimming("\"\" + \"abc\" + \"\"").to("\"abc\"");
  }
  
  @Test public void issue73_03() {
    trimming("\"abc\" + \"\"").to("\"abc\"");
  }
  
  @Test public void issue75a() {
    trimming("int i = 0").to(null);
  }

  @Test public void issue75b() {
    trimming("int i = +1;").to("int i = 1;");
  }

  @Test public void issue75c() {
    trimming("int i = +a;").to("int i = a;");
  }

  @Test public void issue75d() {
    trimming("+ 0").to("0");
  }

  @Test public void issue75e() {
    trimming("a = +0").to("a = 0");
  }

  @Test public void issue75f() {
    trimming("a = 1+0").to("a = 1");
  }

  @Test public void issue75g() {
    trimming("i=0").to(null);
  }

  @Test public void issue75h() {
    trimming("int i; i = +0;").to("int i = +0;").to("int i=0;");
  }

  @Test public void issue75i() {
    trimming("+0").to("0");
  }

  @Test public void issue75i0() {
    trimming("-+-+2").to("--+2");
  }

  @Test public void issue75i1() {
    trimming("+0").to("0");
  }

  @Test public void issue75i2() {
    trimming("+1").to("1");
  }

  @Test public void issue75i3() {
    trimming("+-1").to("-1");
  }

  @Test public void issue75i4() {
    trimming("+1.0").to("1.0");
  }

  @Test public void issue75i5() {
    trimming("+'0'").to("'0'");
  }

  @Test public void issue75i6() {
    trimming("+1L").to("1L");
  }

  @Test public void issue75i7() {
    trimming("+0F").to("0F");
  }

  @Test public void issue75i8() {
    trimming("+0L").to("0L");
  }

  @Test public void issue75il() {
    trimming("+(a+b)").to("a+b");
  }

  @Test public void issue75j() {
    trimming("+1E3").to("1E3");
  }

  @Test public void issue75k() {
    trimming("(+(+(+x)))").to("(x)");
  }

  @Test public void issue75m() {
    trimming("+ + + i").to("i");
  }

  @Test public void issue75n() {
    trimming("(2*+(a+b))").to("(2*(a+b))");
  }

  @Ignore("Disabled: there is some bug in distributive rule") @Test public void issue76a() {
    trimming("a*b + a*c").to("a*(b+c)");
  }

  @Ignore("Disabled: there is some bug in distributive rule") @Test public void issue76b() {
    trimming("b*a + c*a").to("a*(b+c)");
  }

  @Ignore("Disabled: there is some bug in distributive rule") @Test public void issue76c() {
    trimming("b*a + c*a + d*a").to("a*(b+c+d)");
  }

  @Test public void issue76d() {
    trimming("a * (b + c)").to(null);
  }

  @Test public void issue82a() {
    trimming("(long)5").to("1L*5");
  }

  @Test public void issue82b() {
    trimming("(long)a").to("1L*a");
  }

  @Test public void issue82c() {
    trimming("(long)(long)a").to("1L*(long)a").to("1L*1L*a");
  }

  @Test public void issue82d() {
    trimming("(long)a*(long)b").to("1L*a*1L*b");
  }

  @Test public void issue82e() {
    trimming("(double)(long)a").to("1.*(long)a").to("1.*1L*a");
  }

  @Test public void issue83a() {
    trimming("if(lst.size()>=0) return a;").to("if(true) return a;");
  }

  @Test public void issue83b() {
    trimming("if(lst.size()<0) return a;").to("if(false) return a;");
  }

  @Test public void issue83c() {
    trimming("if(x.size()>0)return a;").to("if(!x.isEmpty())return a;");
  }

  @Ignore("This checks for singleton, and cannot be simplified") @Test public void issue83d() {
    trimming("if(x.size()==1) return a;").to(null);
  }

  @Ignore("This checks for singleton, and cannot be simplified") @Test public void issue83e() {
    trimming("if(x.size()==2) return a;").to(null);
  }

  @Ignore("This checks for singleton, and cannot be simplified") @Test public void issue83f() {
    trimming("if(2==lst.size()) return a;").to(null);
  }

  @Test public void issue83g() {
    trimming("if(lst.size()==4) return a;").to(null);
  }

  @Test public void issue83h() {
    trimming("if(lst.size()==0) return a;").to("if(lst.isEmpty()) return a;");
  }

  @Test public void issue83i() {
    trimming("if(es.size() >= 2) return a;").to(null);
  }

  @Test public void issue83j() {
    trimming("if(es.size() > 2) return a;").to(null);
  }

  @Test public void issue83k() {
    trimming("if(es.size() < 2) return a;").to(null);
  }

  @Test public void issue83l() {
    trimming("uses(ns).size() <= 1").to(null);
  }

  @Test public void issue83m() {
    trimming("if(a.size() >= -3) ++a;").to("if(true) ++a;");
  }

  @Test public void issue83n() {
    trimming("if(a.size() <= -9) ++a;").to("if(false) ++a;");
  }

  @Test public void issue85_86a() {
    trimming("if(true){   \n" + "x(); }   \n" + "else{   \n" + "y();   \n" + "}").to("{x();}").to("x();");
  }

  @Test public void issue85_86b() {
    trimming("if(false){   \n" + "x(); }   \n" + "else{   \n" + "y();   \n" + "}").to("{y();}").to("y();");
  }

  @Test public void issue85_86c() {
    trimming("if(false)   \n" + "x();    \n" + "else   \n" + "y();   \n").to("y();");
  }

  @Test public void issue85_86d() {
    trimming("if(false){   \n" + "x(); }   \n" + "else{   \n" + "if(false) a();   \n" + "else b();" + "}").to("{b();}").to("b();");
  }

  @Test public void issue85_86e() {
    trimming("if(false){   \n" + "x(); }   \n" + "else{   \n" + "if(true) a();   \n" + "else b();" + "}").to("{a();}").to("a();");
  }

  @Test public void issue85_86f() {
    trimming("if(true){   \n" + "if(true) a();   \n" + "else b(); }   \n" + "else{   \n" + "if(false) a();   \n" + "else b();" + "}").to("{a();}")
        .to("a();");
  }

  @Test public void issue85_86g() {
    trimming("if(z==k)   \n" + "x();    \n" + "else   \n" + "y();   \n").to(null);
  }

  @Test public void issue85_86h() {
    trimming("if(5==5)   \n" + "x();    \n" + "else   \n" + "y();   \n").to(null);
  }

  @Test public void issue85_86i() {
    trimming("if(z){   \n" + "if(true) a();   \n" + "else b(); }   \n" + "else{   \n" + "if(false) a();   \n" + "else b();" + "}")
        .to("if(z)\n" + "if(true) a();   \n" + "else b();\n" + "else\n" + "if(false) a();   \n" + "else b();")
        .to("if(z)\n" + "a(); \n" + "else \n" + "b();   \n");
  }

  @Test public void issue85_86j() {
    trimming("if(true){ \n" + "if(true) \n" + "a(); \n" + "else \n" + "b(); \n" + "} \n" + "else c();").to("{a();}").to("a();");
  }

  @Test public void issue85_86k() {
    trimming("if(false){ \n" + "if(true) \n" + "a(); \n" + "else \n" + "b(); \n" + "} \n" + "else c();").to("c();");
  }

  @Test public void issue85_86l() {
    trimming("if(false)" + "c();" + "else {\n" + "if(true) \n" + "a(); \n" + "else \n" + "b(); \n" + "} \n").to("{a();}").to("a();");
  }

  @Test public void issue87a() {
    trimming("a-b*c - (x - - - (d*e))").to("a  - b*c -x + d*e");
  }

  @Test public void issue87c() {
    trimming("a + (b-c)").to("a + b -c");
  }

  @Test public void issue87d() {
    trimming("a - (b-c)").to("a - b + c");
  }

  @Test public void issue87b() {
    trimming("a-b*c").to(null);
  }

  // @formatter:off
  enum A { a1() {{ f(); }
      public void f() {
        g();
      }
       void g() {
        h();
      }
       void i() {
        f();
      }
       void h() {
        i();
      }
    }, a2() {{ f(); }
      public void i() {
        f();
      }
      void f() {
        g();
      }
      void g() {
        h();
      }
      void h() {
        i();
      }
    }
  }
 // @formatter:on
}
