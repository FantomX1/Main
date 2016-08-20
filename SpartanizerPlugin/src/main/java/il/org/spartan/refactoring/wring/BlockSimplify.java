package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Funcs.*;
import static il.org.spartan.refactoring.utils.Restructure.*;
import static il.org.spartan.refactoring.utils.expose.*;

import java.util.*;
import java.util.function.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.utils.*;

/** convert
 *
 * <pre>
 * {
 *   ;
 *   ;
 *   g();
 *   {
 *   }
 *   {
 *     ;
 *     {
 *       ;
 *       {
 *         ;
 *       }
 *     }
 *     ;
 *   }
 * }
 * </pre>
 *
 * into
 *
 * <pre>
 * g();
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-07-29 */
public final class BlockSimplify extends Wring.ReplaceCurrentNode<Block> implements Kind.SyntacticBaggage {
  public static boolean hasHidings(final List<Statement> ss) {
    return new Predicate<List<Statement>>() {
      final Set<String> dictionary = new HashSet<>();

      boolean ¢(final CatchClause c) {
        return ¢(c.getException());
      }

      boolean ¢(final ForStatement ¢) {
        return ¢(expose.initializers(¢));
      }

      boolean ¢(final List<Expression> es) {
        for (final Expression e : es)
          if (e instanceof VariableDeclarationExpression && ¢((VariableDeclarationExpression) e))
            return true;
        return false;
      }

      boolean ¢(final SimpleName ¢) {
        return ¢(¢.getIdentifier());
      }

      boolean ¢(final SingleVariableDeclaration d) {
        return ¢(d.getName());
      }

      boolean ¢(final Statement ¢) {
        return ¢ instanceof VariableDeclarationStatement ? ¢((VariableDeclarationStatement) ¢) //
            : ¢ instanceof ForStatement ? ¢((ForStatement) ¢) //
                : ¢ instanceof TryStatement && ¢((TryStatement) ¢);
      }

      boolean ¢(final String ¢) {
        if (dictionary.contains(¢))
          return true;
        dictionary.add(¢);
        return false;
      }

      boolean ¢(final TryStatement s) {
        return ¢¢¢(expose.resources(s)) || ¢¢(expose.catchClauses(s));
      }

      boolean ¢(final VariableDeclarationExpression ¢) {
        return ¢¢¢¢(expose.fragments(¢));
      }

      boolean ¢(final VariableDeclarationFragment f) {
        return ¢(f.getName());
      }

      boolean ¢(final VariableDeclarationStatement s) {
        return ¢¢¢¢(fragments(s));
      }

      boolean ¢¢(final List<CatchClause> cs) {
        for (final CatchClause c : cs)
          if (¢(c))
            return true;
        return false;
      }

      boolean ¢¢¢(final List<VariableDeclarationExpression> es) {
        for (final VariableDeclarationExpression e : es)
          if (¢(e))
            return true;
        return false;
      }

      boolean ¢¢¢¢(final List<VariableDeclarationFragment> fs) {
        for (final VariableDeclarationFragment x : fs)
          if (¢(x))
            return true;
        return false;
      }

      @Override public boolean test(final List<Statement> ¢¢) {
        for (final Statement ¢ : ¢¢)
          if (¢(¢))
            return true;
        return false;
      }
    }.test(ss);
  }

  private static boolean identical(final List<Statement> os1, final List<Statement> os2) {
    if (os1.size() != os2.size())
      return false;
    for (int i = 0; i < os1.size(); ++i)
      if (os1.get(i) != os2.get(i))
        return false;
    return true;
  }

  static Statement reorganizeNestedStatement(final Statement s) {
    final List<Statement> ss = extract.statements(s);
    switch (ss.size()) {
      case 0:
        return s.getAST().newEmptyStatement();
      case 1:
        return duplicate(first(ss));
      default:
        return reorganizeStatement(s);
    }
  }

  private static Block reorganizeStatement(final Statement s) {
    final List<Statement> ss = extract.statements(s);
    final Block $ = s.getAST().newBlock();
    duplicateInto(ss, statements($));
    return $;
  }

  @Override String description(@SuppressWarnings("unused") final Block __) {
    return "Simplify block";
  }

  @Override Statement replacement(final Block b) {
    final List<Statement> ss = extract.statements(b);
    if (identical(ss, statements(b)) || hasHidings(ss))
      return null;
    final ASTNode parent = b.getParent();
    if (!(parent instanceof Statement) || parent instanceof TryStatement)
      return reorganizeStatement(b);
    switch (ss.size()) {
      case 0:
        return b.getAST().newEmptyStatement();
      case 1:
        final Statement s = first(ss);
        if (Is.blockEssential(s))
          return subject.statement(s).toBlock();
        return duplicate(s);
      default:
        return reorganizeNestedStatement(b);
    }
  }
}
