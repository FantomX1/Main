package il.org.spartan.spartanizer.issues;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import fluent.ly.*;
import il.org.spartan.athenizer.bloaters.*;
import il.org.spartan.spartanizer.meta.*;
import il.org.spartan.spartanizer.testing.*;
import il.org.spartan.spartanizer.tipping.*;

/** Tests for {@link LocalInitializedCollection} see github issue thus numbered
 * for more info
 * @author Niv Shalmon
 * @since 2017-05-30 */
public class Issue1421 extends BloaterTest<VariableDeclarationFragment> {
  @Override public Tipper<VariableDeclarationFragment> bloater() {
    return new LocalInitializedCollection();
  }
  @Override public Class<VariableDeclarationFragment> tipsOn() {
    return VariableDeclarationFragment.class;
  }
  @Test public void test00() {
    bloatingOf(new testUtils())//
        .givesWithBinding(
            "public void from0(){"//
                + " List<Integer> x = new ArrayList<>();"//
                + " x.addAll(ys);"//
                + "}"//
            , "from0")//
        .staysWithBinding();
  }
  @Ignore("issue 1453")
  @Test public void test01() {
    bloatingOf(new testUtils())//
        .staysWithBinding();
  }
  @Ignore("issue 1453")
  @Test public void test02() {
    bloatingOf(new testUtils())//
        .staysWithBinding();
  }
  @Test public void test03() {
    bloatingOf(new testUtils())//
        .givesWithBinding(
            "public void from3(){ "//
                + "HashSet<Integer> x = new HashSet<>();"//
                + "x.addAll(zs);"//
                + "}"//
            , "from3")//
        .staysWithBinding();
  }

  @SuppressWarnings("unused")
  class testUtils extends MetaFixture {
    List<Integer> ys = as.ilist(1, 2, 3, 4, 5);
    ArrayList<Integer> zs = new ArrayList<>();

    public testUtils() {}
    public testUtils(Collection<?> __) {
      //
    }
    public void from0() {
      new ArrayList<>(ys);
    }
    public void from1() {
      new testUtils(ys);
    }
    public void from2() {
      new HashSet<Integer>(zs);
    }
    public void from3() {
      new HashSet<>(zs);
    }
    public void f(Integer x){
      //
    }
    public void f(String x){
      //
    }
    public void f(testUtils x){
      //
    }
  }
}
