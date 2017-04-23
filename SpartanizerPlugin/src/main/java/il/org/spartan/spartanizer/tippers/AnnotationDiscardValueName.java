package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.lisp.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.patterns.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.utils.*;

/** Removes the "value" member from annotations that only have a single member,
 * converting {@code @SuppressWarnings(value = "unchecked")} to
 * {@code @SuppressWarnings("unchecked")}
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2016-04-02 */
public final class AnnotationDiscardValueName extends NodePattern<NormalAnnotation>//
    implements TipperCategory.SyntacticBaggage {
  private static final long serialVersionUID = 0x77F6509E0062C2EDL;
  private MemberValuePair member;
  private SimpleName name;

  @Override protected ASTNode highlight() {
    return name; 
  }

  public AnnotationDiscardValueName() {
    notNil("Has only one member", () -> member = onlyOne(values(current)));
    notNil("Extract member's name", () -> name = member.getName());
    andAlso("Name equals 'value'", () -> "value".equals(name + ""));
  }

  @Override public String description() {
    return "Remove 'value' tag preserving value contents in @" + current.getTypeName().getFullyQualifiedName() + " annotation";
  }

  @Override public Examples examples() {
    return convert("@SuppressWarnings(value = \"unchecked\")")//
        .to("@SuppressWarnings(\"unchecked\")") //
    ;
  }

  @Override public ASTRewrite go(final ASTRewrite r, final TextEditGroup g) {
    final SingleMemberAnnotation a = current.getAST().newSingleMemberAnnotation();
    a.setTypeName(copy.of(current.getTypeName()));
    a.setValue(copy.of(member.getValue()));
    r.replace(current, a, g);
    return r;
  }
}
