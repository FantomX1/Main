package il.org.spartan.spartanizer.ast;

import static il.org.spartan.Utils.*;
import static org.eclipse.jdt.core.dom.ASTNode.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.step.*;

/** An empty <code><b>interface</b></code> for fluent programming. The name
 * should say it all: The name, followed by a dot, followed by a method name,
 * should read like a sentence phrase.
 * @author Yossi Gil
 * @since 2016 */
public interface hop {
  /** Retrieves the ancestors of the ASTNode, via an Iterator.
   * @param ¢ JD
   * @return an {@link Iterable} that traverses the ancestors of the ASTNode.
   *         Use case: Counting the number of Expressions among a given
   *         ASTNode's ancestors */
  static Iterable<ASTNode> ancestors(final ASTNode ¢) {
    return () -> new Iterator<ASTNode>() {
      ASTNode current = ¢;

      @Override public boolean hasNext() {
        return current != null;
      }

      @Override public ASTNode next() {
        final ASTNode $ = current;
        current = current.getParent();
        return $;
      }
    };
  }

  static CompilationUnit compilationUnit(final ASTNode ¢) {
    return (CompilationUnit) searchAncestors.forType(COMPILATION_UNIT).from(¢);
  }

  /** @param ¢ JD
   * @return ASTNode of the type if one of ¢'s parent ancestors is a container
   *         type and null otherwise */
  static ASTNode containerType(final ASTNode ¢) {
    for (final ASTNode $ : hop.ancestors(¢.getParent()))
      if (iz.is($//
          , ANONYMOUS_CLASS_DECLARATION //
          , ANNOTATION_TYPE_DECLARATION //
          , ENUM_DECLARATION //
          , TYPE_DECLARATION //
          , ENUM_CONSTANT_DECLARATION //
      ))
        return $;
    return null;
  }

  /** @param root the node whose children we return
   * @return A list containing all the nodes in the given root'¢ sub tree */
  static List<ASTNode> descendants(final ASTNode root) {
    if (root == null)
      return null;
    final List<ASTNode> $ = new ArrayList<>();
    root.accept(new ASTVisitor() {
      @Override public void preVisit(final ASTNode ¢) {
        $.add(¢);
      }
    });
    $.remove(0);
    return $;
  }

  static VariableDeclarationFragment findDefinition(final VariableDeclarationStatement s, final SimpleName n) {
    return hop.findVariableDeclarationFragment(step.fragments(s), n);
  }

  static VariableDeclarationFragment findVariableDeclarationFragment(final List<VariableDeclarationFragment> fs, final SimpleName ¢) {
    for (final VariableDeclarationFragment $ : fs)
      if (wizard.same(¢, $.getName()))
        return $;
    return null;
  }

  /** @param n the node from which to extract the proper fragment
   * @param x the name by which to look for the fragment
   * @return fragment if such with the given name exists or null otherwise (or
   *         if ¢ or name are null) */
  // TODO this seems a bug
  static VariableDeclarationFragment getDefinition(final ASTNode n, final Expression x) {
    return hasNull(n, x) || n.getNodeType() != VARIABLE_DECLARATION_STATEMENT || x.getNodeType() != SIMPLE_NAME ? null
        : findDefinition((VariableDeclarationStatement) n, (SimpleName) x);
  }

  static SimpleName lastComponent(final Name ¢) {
    return ¢ == null ? null : ¢.isSimpleName() ? (SimpleName) ¢ : ¢.isQualifiedName() ? ((QualifiedName) ¢).getName() : null;
  }

  /** Find the last statement residing under a given {@link Statement}
   * @param ¢ JD
   * @return last statement residing under a given {@link Statement}, or
   *         <code><b>null</b></code> if not such sideEffects exists. */
  static ASTNode lastStatement(final Statement ¢) {
    return last(extract.statements(¢));
  }

  /** Extract the {@link MethodDeclaration} that contains a given node.
   * @param n JD
   * @return inner most {@link MethodDeclaration} in which the parameter is
   *         nested, or <code><b>null</b></code>, if no such statement
   *         exists. */
  static MethodDeclaration methodDeclaration(final ASTNode ¢) {
    for (ASTNode $ = ¢; $ != null; $ = $.getParent())
      if (iz.methodDeclaration($))
        return az.methodDeclaration($);
    return null;
  }

  static Name name(final Type ¢) {
    return ¢.isSimpleType() ? ((SimpleType) ¢).getName()
        : ¢.isNameQualifiedType() ? ((NameQualifiedType) ¢).getName() : ¢.isQualifiedType() ? ((QualifiedType) ¢).getName() : null;
  }

  /** Makes a list of all operands of an expression, comprising the left
   * operand, the right operand, followed by extra operands when they exist.
   * @param x JD
   * @return a list of all operands of an expression */
  static List<Expression> operands(final InfixExpression ¢) {
    if (¢ == null)
      return null;
    final List<Expression> $ = new ArrayList<>();
    $.add(left(¢));
    $.add(right(¢));
    if (¢.hasExtendedOperands())
      $.addAll(step.extendedOperands(¢));
    return $;
  }

  /** Remove the last statement residing under a given {@link Statement}, if ¢
   * is empty or has only one statement return empty statement.
   * @param ¢ JD <code><b>null</b></code> if not such sideEffects exists.
   * @return Given {@link Statement} without the last inner statement, if ¢ is
   *         empty or has only one statement return empty statement. */
  static Statement removeLastStatement(final Statement $) {
    if (iz.block($)) {
      final List<Statement> l = az.block($).statements();
      if (l.size() > 0)
        l.remove(l.size() - 1);
      return $;
    }
    return $.getAST().newEmptyStatement();
  }

  static SimpleName simpleName(final Type ¢) {
    return lastComponent(hop.name(¢));
  }
}
