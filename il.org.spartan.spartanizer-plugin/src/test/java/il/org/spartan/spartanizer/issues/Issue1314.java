package il.org.spartan.spartanizer.issues;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import il.org.spartan.spartanizer.testing.*;
import il.org.spartan.spartanizer.tippers.*;
import il.org.spartan.spartanizer.tipping.*;

/** TODO Niv Shalmon: document class 
 * 
 * @author Niv Shalmon
 * @since 2017-05-26 */
public class Issue1314 extends TipperTest<VariableDeclarationFragment>{

  @Override public Tipper<VariableDeclarationFragment> tipper() {
    return new LocalInitializedNewAddAll();
  }

  @Override public Class<VariableDeclarationFragment> tipsOn() {
    return VariableDeclarationFragment.class;
  }
  
  @Test public void test0(){
    trimmingOf("List<T> x = new ArrayList<>(); x.addAll(ys);").gives("List<T> x = new ArrayList<>(ys);");
  }
  
}
