package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.expose.*;
import static il.org.spartan.refactoring.wring.Wrings.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;
import il.org.spartan.refactoring.preferences.PluginPreferencesResources.*;
import il.org.spartan.refactoring.utils.*;

/** A {@link Wring} that abbreviates the names of variables that have a generic
 * variation. The abbreviated name is the first character in the last word of
 * the variable's name.
 * @author Daniel Mittelman <tt><mittelmania [at] gmail.com></tt>
 * @since 2015/08/24 */
/* TODO This is a previous version of the MethodParameterAbbreviate wring that
 * replaces all parameter names in a method at once. If it is found to be
 * useless in the near future, delete this class. Otherwise, remove the
 * 
 * @Deprecated annotation */
@Deprecated public class MethodAbbreviateParameterNames extends Wring<MethodDeclaration> {
  @Override String description(final MethodDeclaration d) {
    return d.getName().toString();
  }
  @Override Rewrite make(final MethodDeclaration d, final ExclusionManager exclude) {
    if (d.isConstructor())
      return null;
    final List<SingleVariableDeclaration> vd = find(parameters(d));
    final Map<SimpleName, SimpleName> renameMap = new HashMap<>();
    if (vd == null)
      return null;
    for (final SingleVariableDeclaration v : vd)
      if (legal(v, d, renameMap.values()))
        renameMap.put(v.getName(), d.getAST().newSimpleName(spartan.shorten(v.getType()) + pluralVariadic(v)));
    if (renameMap.isEmpty())
      return null;
    if (exclude != null)
      exclude.exclude(d);
    return new Rewrite("Abbreviate parameters in method " + d.getName().toString(), d) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        for (final SimpleName key : renameMap.keySet())
          rename(key, renameMap.get(key), d, r, g);
      }
    };
  }
  private List<SingleVariableDeclaration> find(final List<SingleVariableDeclaration> ds) {
    final List<SingleVariableDeclaration> $ = new ArrayList<>();
    for (final SingleVariableDeclaration d : ds)
      if (suitable(d))
        $.add(d);
    return $.size() != 0 ? $ : null;
  }
  private static boolean legal(final SingleVariableDeclaration d, final MethodDeclaration m, final Collection<SimpleName> newNames) {
    if (spartan.shorten(d.getType()) == null)
      return false;
    final MethodExplorer e = new MethodExplorer(m);
    for (final SimpleName n : e.localVariables())
      if (n.getIdentifier().equals(spartan.shorten(d.getType())))
        return false;
    for (final SimpleName n : newNames)
      if (n.getIdentifier().equals(spartan.shorten(d.getType())))
        return false;
    for (final SingleVariableDeclaration n : parameters(m))
      if (n.getName().getIdentifier().equals(spartan.shorten(d.getType())))
        return false;
    return !m.getName().getIdentifier().equalsIgnoreCase(spartan.shorten(d.getType()));
  }
  private boolean suitable(final SingleVariableDeclaration d) {
    return new JavaTypeNameParser(d.getType().toString()).isGenericVariation(d.getName().getIdentifier()) && !isShort(d);
  }
  private boolean isShort(final SingleVariableDeclaration d) {
    final String n = spartan.shorten(d.getType());
    return n != null && (n + pluralVariadic(d)).equals(d.getName().getIdentifier());
  }
  @SuppressWarnings("static-method") private String pluralVariadic(final SingleVariableDeclaration d) {
    return d.isVarargs() ? "s" : "";
  }
  @Override WringGroup wringGroup() {
    return WringGroup.RENAME_PARAMETERS;
  }
}
