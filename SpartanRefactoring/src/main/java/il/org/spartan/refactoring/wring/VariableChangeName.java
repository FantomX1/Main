package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.extract.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;
import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.Wring.*;

/** A parent wring for changing variables names TODO Ori: check safety of
 * Collect.usesOf(n.getName()).in(p)
 * @author Ori Roth
 * @since 2016/05/08
 * @param <N> either SingleVariableDeclaration or VariableDeclarationFragment */
public abstract class VariableChangeName<N extends VariableDeclaration> extends MultipleReplaceCurrentNode<N> {
  abstract boolean change(N n);
  @Override public ASTRewrite go(final ASTRewrite r, final N n, @SuppressWarnings("unused") final TextEditGroup __, final List<ASTNode> uses,
      final List<ASTNode> replacement) {
    if (!change(n))
      return null;
    uses.addAll(Collect.usesOf(n.getName()).in(containerType(n)));
    replacement.add(replacement(n));
    return r;
  }
  abstract SimpleName replacement(N n);
}
