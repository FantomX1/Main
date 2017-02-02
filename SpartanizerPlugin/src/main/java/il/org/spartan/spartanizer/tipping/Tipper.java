package il.org.spartan.spartanizer.tipping;

import static il.org.spartan.lisp.*;
import static java.lang.reflect.Modifier.*;

import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;
import static il.org.spartan.spartanizer.ast.navigate.step.fragments;
import static il.org.spartan.spartanizer.ast.navigate.step.name;

import static il.org.spartan.spartanizer.ast.navigate.extract.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A tipper is a transformation that works on an AstNode. Such a transformation
 * make a single simplification of the tree. A tipper is so small that it is
 * idempotent: Applying a tipper to the output of itself is the empty operation.
 * @param <N> type of node which triggers the transformation.
 * @author Yossi Gil
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2015-07-09 */
public abstract class Tipper<N extends ASTNode> //
    implements TipperCategory {
  /** Eliminates a {@link VariableDeclarationFragment}, with any other fragment
   * fragments which are not live in the containing
   * {@link VariabelDeclarationStatement}. If no fragments are left, then this
   * containing node is eliminated as well.
   * @param f
   * @param r
   * @param g */
  public static void eliminate(@NotNull final VariableDeclarationFragment f, @NotNull final ASTRewrite r, final TextEditGroup g) {
    final VariableDeclarationStatement parent = (VariableDeclarationStatement) f.getParent();
    final List<VariableDeclarationFragment> live = live(f, fragments(parent));
    if (live.isEmpty()) {
      r.remove(parent, g);
      return;
    }
    final VariableDeclarationStatement newParent = copy.of(parent);
    fragments(newParent).clear();
    fragments(newParent).addAll(live);
    r.replace(parent, newParent, g);
  }

  @NotNull
  protected static List<VariableDeclarationFragment> live(final VariableDeclarationFragment f, @NotNull final List<VariableDeclarationFragment> fs) {
    final List<VariableDeclarationFragment> $ = new ArrayList<>();
    fs.stream().filter(λ -> λ != f && λ.getInitializer() != null).forEach(λ -> $.add(copy.of(λ)));
    return $;
  }

  private Class<N> myOperandsClass;

  /** Determine whether the parameter is "eligible" for application of this
   * instance.
   * @param n JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is eligible for
   *         the simplification offered by this object. */
  public abstract boolean canTip(N n);

  /** Determines whether this instance can make a {@link Tip} for the parameter
   * instance.
   * @param e JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is noneligible for
   *         the simplification offered by this object.
   * @see #canTip(InfixExpression) */
  public final boolean cantTip(final N ¢) {
    return !canTip(¢);
  }

  @Nullable
  @Override public String description() {
    return getClass().getSimpleName();
  }

  @Nullable
  public abstract String description(N n);

  /** Heuristics to find the class of operands on which this class works.
   * @return a guess for the type of the node. */
  @NotNull
  public final Class<N> myAbstractOperandsClass() {
    return myOperandsClass != null ? myOperandsClass : (myOperandsClass = initializeMyOperandsClass());
  }

  @Nullable
  public Class<N> myActualOperandsClass() {
    final Class<N> $ = myAbstractOperandsClass();
    return !isAbstract($.getModifiers()) ? $ : null;
  }

  /** @return a string representing a class name. The class must inherit from
   *         Tipper. */
  @NotNull
  public String myName() {
    return getClass().getSimpleName();
  }

  /** A wrapper function without ExclusionManager.
   * @param ¢ The ASTNode object on which we deduce the tip.
   * @return a tip given for the ASTNode ¢. */
  @Nullable
  public Tip tip(final N ¢) {
    return tip(¢, null);
  }

  /** @param n an ASTNode
   * @param m exclusion manager guarantees this tip to be given only once.
   * @return a tip given for the ASTNode ¢. */
  @Nullable
  public Tip tip(final N n, @Nullable final ExclusionManager m) {
    return m != null && m.isExcluded(n) ? null : tip(n);
  }

  @NotNull
  @SuppressWarnings("unchecked") private Class<N> castClass(@NotNull final Class<?> c2) {
    return (Class<N>) c2;
  }

  @NotNull
  private Class<N> initializeMyOperandsClass() {
    Class<N> $ = null;
    for (final Method ¢ : getClass().getMethods())
      if (¢.getParameterCount() == 1 && !Modifier.isStatic(¢.getModifiers()) && isDefinedHere(¢))
        $ = lowest($, ¢.getParameterTypes()[0]);
    return $ != null ? $ : castClass(ASTNode.class);
  }

  private boolean isDefinedHere(@NotNull final Method ¢) {
    return ¢.getDeclaringClass() == getClass();
  }

  @NotNull
  private Class<N> lowest(@Nullable final Class<N> c1, @Nullable final Class<?> c2) {
    return c2 == null || !ASTNode.class.isAssignableFrom(c2) || c1 != null && !c1.isAssignableFrom(c2) ? c1 : castClass(c2);
  }

  @Override public boolean equals(@Nullable final Object ¢) {
    return ¢ != null && getClass().equals(¢.getClass());
  }

  @Override public int hashCode() {
    return super.hashCode();
  }

  public static boolean frobiddenOpOnPrimitive(@NotNull final VariableDeclarationFragment f, final Statement nextStatement) {
    if (!iz.literal(f.getInitializer()) || !iz.expressionStatement(nextStatement))
      return false;
    final ExpressionStatement x = (ExpressionStatement) nextStatement;
    if (iz.methodInvocation(x.getExpression())) {
      final Expression $ = core(expression(x.getExpression()));
      return iz.simpleName($) && ((SimpleName) $).getIdentifier().equals(f.getName().getIdentifier());
    }
    if (!iz.fieldAccess(x.getExpression()))
      return false;
    final Expression e = core(((FieldAccess) x.getExpression()).getExpression());
    return iz.simpleName(e) && ((SimpleName) e).getIdentifier().equals(f.getName().getIdentifier());
  }

  public static Expression goInfix(@NotNull final InfixExpression from, final VariableDeclarationStatement s) {
    final List<Expression> $ = hop.operands(from);
    // TODO Raviv Rachmiel: use extract.core
    $.stream().filter(λ -> iz.parenthesizedExpression(λ) && iz.assignment(az.parenthesizedExpression(λ).getExpression())).forEachOrdered(x -> {
      final Assignment a = az.assignment(az.parenthesizedExpression(x).getExpression());
      final SimpleName var = az.simpleName(left(a));
      fragments(s).stream().filter(λ -> (name(λ) + "").equals(var + "")).forEach(λ -> {
        λ.setInitializer(copy.of(right(a)));
        $.set($.indexOf(x), x.getAST().newSimpleName(var + ""));
      });
    });
    return subject.append(subject.pair(first($), $.get(1)).to(from.getOperator()), chop(chop($)));
  }
}
