package il.org.spartan.athenizer.bloaters;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.tipping.*;

/** Add all the unecessary but legal modifiers to enums
 * @author Dor Ma'ayan
 * @since 2017-05-28 */
public class AddModifiersToEnums extends ReplaceCurrentNode<EnumDeclaration> implements TipperCategory.Bloater {
  private static final long serialVersionUID = 1L;

  @Override public String description(@SuppressWarnings("unused") EnumDeclaration n) {
    return "add all the unecessary modifiers to the enum";
  }
  @SuppressWarnings("unchecked") @Override public ASTNode replacement(EnumDeclaration n) {
    EnumDeclaration ret = copy.of(n);
    // final BodyDeclaration wrap = az.bodyDeclaration(n.getParent());
    List<Modifier> lst = extract.modifiers(n);
    if (!lst.contains(n.getAST().newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD))) {
      ret.modifiers().add(n.getAST().newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
    }
    return ret;
  }
}
