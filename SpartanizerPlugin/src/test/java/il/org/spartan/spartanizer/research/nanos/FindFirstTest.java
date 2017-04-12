package il.org.spartan.spartanizer.research.nanos;

import static il.org.spartan.spartanizer.testing.TestsUtilsTrimmer.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;
import org.junit.runners.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/** Tests {@link FindFirst} and {@link ForLoop.FindFirst}
 * @author Ori Marcovitch
 * @since Jan 18, 2017 */
@SuppressWarnings("static-method")
public class FindFirstTest {
  @Test public void a() {
    trimminKof("for(Object i : is) if(i.isN()) return i; return null;")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("return is.stream().filter(i->i.isN()).findFirst().orElse(null);");
  }

  @Test public void b() {
    trimminKof("for(Object i : is) if(i.isN()) return i; throw new None();")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("return is.stream().filter(i->i.isN()).findFirst().orElseThrow(()->new None());");
  }

  @Test public void c() {
    trimminKof("for(Object i : is) if(i.isN()) {theChosen = i; break;}")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("theChosen=is.stream().filter(i->i.isN()).findFirst().orElse(theChosen);");
  }

  @Test public void d() {
    trimminKof("for (EF $ : EF) if ($.getX() == x && $.getY() == y && $.getZ() == z) return $.gI(); return 0;")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("return EF.stream().filter($->$.getX()==x&&$.getY()==y&&$.getZ()==z).map($->$.gI()).findFirst().orElse(0);");
  }

  @Test public void e() {
    trimminKof("for(Object i : is) if(i.isN()) return i; return 0;")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("return is.stream().filter(i->i.isN()).findFirst().orElse(0);");
  }

  @Test public void f() {
    trimminKof("for (final G $ : G.values()) if ($.clazz.isA(¢)) return $; return null;")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("return G.values().stream().filter($->$.clazz.isA(¢)).findFirst().orElse(null);");
  }

  @Test public void g() {
    trimminKof("for (ASTNode $ = ¢; $ != null; $ = p($)) if (iz.m($)) return az.m($); return null;")//
        .using(new ForLoop.FindFirst(), ForStatement.class)//
        .gives("return from(¢).step(($)->$!=null).to(($)->$=p($)).findFirst($->iz.m($)).map(($)->az.m($)).orElse(null);");
  }

  @Test public void h() {
    trimminKof("for (ASTNode $ = ¢; $ != null; $ = p($)) if (iz.m($)) return az.m($); return null;")//
        .using(ForStatement.class, new ForEachInRange(), new ForLoop.FindFirst())//
        .gives("return from(¢).step(($)->$!=null).to(($)->$=p($)).findFirst($->iz.m($)).map(($)->az.m($)).orElse(null);");
  }

  @Test public void i() {
    trimminKof("for(Object i : is) if(i.isN()) return i;")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("returnFirstIfAny(is).matches(i->i.isN());");
  }

  @Test public void j() {
    trimminKof("for (ASTNode $ = ¢; $ != null; $ = p($)) if (iz.m($)) return az.m($);")//
        .using(new ForLoop.FindFirst(), ForStatement.class)//
        .gives("returnFirstIfAny(from(¢).step(($)->$!=null).to(($)->$=p($))).matches($->iz.m($)).map(az.m($));");
  }

  @Test public void k() {
    trimminKof("for (EF $ : EF) if ($.getX() == x && $.getY() == y && $.getZ() == z) return $.gI();")//
        .using(new FindFirst(), EnhancedForStatement.class)//
        .gives("returnFirstIfAny(EF).matches($->$.getX()==x&&$.getY()==y&&$.getZ()==z).map($->$.gI());");
  }
}
