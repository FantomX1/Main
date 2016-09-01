package il.org.spartan.refactoring.engine;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.*;

import il.org.spartan.refactoring.ast.*;

/** An empty <code><b>enum</b></code> for fluent programming. The name should
 * say it all: The name, followed by a dot, followed by a method name, should
 * read like a sentence phrase.
 * @author Yossi Gil
 * @since 2015-07-16 */
public enum Maketemp {
  /** Strategy for conversion into a compilation unit */
  COMPILATION_UNIT(ASTParser.K_COMPILATION_UNIT), //
  /** Strategy for conversion into an expression */
  EXPRESSION(ASTParser.K_EXPRESSION), //
  /** Strategy for conversion into an sequence of statements */
  STATEMENTS(ASTParser.K_STATEMENTS), //
  /** Strategy for conversion into a class body */
  CLASS_BODY_DECLARATIONS(ASTParser.K_CLASS_BODY_DECLARATIONS); //
  /** Converts the {@link MakeAST} value to its corresponding {@link Maketemp}
   * enum value
   * @param t The {@link MakeAST} type
   * @return corresponding {@link Maketemp} value to the argument */
  public static Maketemp of(final MakeAST t) {
    switch (t) {
      case STATEMENTS:
        return Maketemp.STATEMENTS;
      case EXPRESSION:
        return Maketemp.EXPRESSION;
      case COMPILATION_UNIT:
        return Maketemp.COMPILATION_UNIT;
      case CLASS_BODY_DECLARATIONS:
        return Maketemp.CLASS_BODY_DECLARATIONS;
      default:
        return null;
    }
  }

  private final int kind;

  private Maketemp(final int kind) {
    this.kind = kind;
  }

  /** Creates a no-binding parser for a given text
   * @param text what to parse
   * @return a newly created parser for the parameter */
  public ASTParser parser(final char[] text) {
    final ASTParser $ = wizard.parser(kind);
    $.setSource(text);
    return $;
  }

  /** Creates a parser for a given {@link Document}
   * @param d JD
   * @return created parser */
  public ASTParser parser(final Document d) {
    final ASTParser $ = wizard.parser(kind);
    $.setSource(d.get().toCharArray());
    return $;
  }

  /** Creates a no-binding parser for a given compilation unit
   * @param u what to parse
   * @return a newly created parser for the parameter */
  public ASTParser parser(final ICompilationUnit u) {
    final ASTParser $ = wizard.parser(kind);
    $.setSource(u);
    return $;
  }

  /** Creates a parser for a given {@link IFile}
   * @param f JD
   * @return created parser */
  public ASTParser parser(final IFile f) {
    return parser(JavaCore.createCompilationUnitFrom(f));
  }

  /** Creates a parser for a given marked text.
   * @param m JD
   * @return created parser */
  public ASTParser parser(final IMarker m) {
    return parser(MakeAST.iCompilationUnit(m));
  }

  /** Creates a no-binding parser for a given text
   * @param text what to parse
   * @return a newly created parser for the parameter */
  public ASTParser parser(final String text) {
    return parser(text.toCharArray());
  }
}