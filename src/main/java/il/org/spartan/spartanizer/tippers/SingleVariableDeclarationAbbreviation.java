package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.dispatch.Wrings.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import static il.org.spartan.spartanizer.ast.step.*;

import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.tipping.*;

/** abbreviates the name of a method parameter that is a viable candidate for
 * abbreviation (meaning that its name is suitable for renaming, and isn't the
 * desired name). The abbreviated name is the first character in the last word
 * of the variable's name.
 * <p>
 * This tipper is applied to all methods in the code, excluding constructors.
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2015/09/24 */
public final class SingleVariableDeclarationAbbreviation extends EagerWring<SingleVariableDeclaration> implements Kind.Abbreviation {
  private static String getExtraDimensions(final SingleVariableDeclaration d) {
    String $ = "";
    for (int ¢ = d.getExtraDimensions(); ¢ > 0; --¢)
      $ += "s";
    return $;
  }

  private static boolean isShort(final SingleVariableDeclaration d) {
    final String n = spartan.shorten(d.getType());
    return n != null && (n + pluralVariadic(d)).equals(d.getName().getIdentifier());
  }

  private static boolean legal(final SingleVariableDeclaration d, final MethodDeclaration m) {
    if (spartan.shorten(d.getType()) == null)
      return false;
    final MethodExplorer e = new MethodExplorer(m);
    for (final SimpleName ¢ : e.localVariables())
      if (¢.getIdentifier().equals(spartan.shorten(d.getType()) + pluralVariadic(d)))
        return false;
    for (final SingleVariableDeclaration ¢ : parameters(m))
      if (¢.getName().getIdentifier().equals(spartan.shorten(d.getType()) + pluralVariadic(d)))
        return false;
    return !m.getName().getIdentifier().equalsIgnoreCase(spartan.shorten(d.getType()) + pluralVariadic(d));
  }

  private static String pluralVariadic(final SingleVariableDeclaration ¢) {
    return ¢.isVarargs() ? "s" : getExtraDimensions(¢);
  }

  private static boolean suitable(final SingleVariableDeclaration ¢) {
    return new JavaTypeNameParser(¢.getType() + "").isGenericVariation(¢.getName().getIdentifier()) && !isShort(¢);
  }

  @Override public String description(final SingleVariableDeclaration ¢) {
    return ¢.getName() + "";
  }

  @Override public Tip suggest(final SingleVariableDeclaration d, final ExclusionManager exclude) {
    final ASTNode parent = d.getParent();
    if (parent == null || !(parent instanceof MethodDeclaration))
      return null;
    final MethodDeclaration m = (MethodDeclaration) parent;
    if (m.isConstructor() || !suitable(d) || isShort(d) || !legal(d, m))
      return null;
    if (exclude != null)
      exclude.exclude(m);
    final SimpleName oldName = d.getName();
    final String newName = spartan.shorten(d.getType()) + pluralVariadic(d);
    return new Tip("Rename parameter " + oldName + " to " + newName + " in method " + m.getName().getIdentifier(), d) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        rename(oldName, d.getAST().newSimpleName(newName), m, r, g);
        fixJavadoc(m, oldName, newName, r, g);
      }
    };
  }
  
  static void fixJavadoc(final MethodDeclaration m, final SimpleName oldName, final String newName,
      final ASTRewrite r, final TextEditGroup g) {
    final Javadoc j = m.getJavadoc();
    if (j == null)
      return;
    final List<TagElement> ts = step.tags(j);
    if (ts != null)
      for (final TagElement t : ts) {
        if (!TagElement.TAG_PARAM.equals(t.getTagName()))
          continue;
        for (final Object ¢ : t.fragments())
          if (¢ instanceof SimpleName && wizard.same((SimpleName) ¢, oldName)) {
            r.replace((SimpleName) ¢, m.getAST().newSimpleName(newName), g);
            return;
          }
      }
  }
}
