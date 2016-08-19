package il.org.spartan.refactoring.spartanizations;

import static il.org.spartan.azzert.*;
import static il.org.spartan.utils.Utils.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.*;
import org.eclipse.text.edits.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.utils.*;
import il.org.spartan.refactoring.wring.*;

/** @author Yossi Gil
 * @since 2015-07-17 */
@SuppressWarnings("javadoc") public enum TESTUtils {
  ;
  static final String WHITES = "(?m)\\s+";

  static String apply(final Trimmer t, final String from) {
    final CompilationUnit u = (CompilationUnit) MakeAST.COMPILATION_UNIT.from(from);
    azzert.notNull(u);
    final Document d = new Document(from);
    azzert.notNull(d);
    return TESTUtils.rewrite(t, u, d).get();
  }

  public static void assertNoChange(final String input) {
    assertSimilar(input, Wrap.Expression.off(apply(new Trimmer(), Wrap.Expression.on(input))));
  }

  static void assertNoOpportunity(final Spartanization s, final String from) {
    final CompilationUnit u = (CompilationUnit) MakeAST.COMPILATION_UNIT.from(from);
    azzert.that(u.toString(), TrimmerTestsUtils.countOpportunities(s, u), is(0));
  }

  static void assertNotEvenSimilar(final String expected, final String actual) {
    azzert.that(gist(actual), is(gist(expected)));
  }

  static void assertOneOpportunity(final Spartanization s, final String from) {
    final CompilationUnit u = (CompilationUnit) MakeAST.COMPILATION_UNIT.from(from);
    azzert.notNull(u);
    azzert.that(TrimmerTestsUtils.countOpportunities(s, u), greaterThanOrEqualTo(1));
  }

  /** A test to check that the actual output is similar to the actual value.
   * @param expected JD
   * @param actual JD */
  public static void assertSimilar(final String expected, final Document actual) {
    assertSimilar(expected, actual.get());
  }

  /** A test to check that the actual output is similar to the actual value.
   * @param expected JD
   * @param actual JD */
  public static void assertSimilar(final String expected, final String actual) {
    if (!expected.equals(actual))
      azzert.that(Wrap.essence(actual), is(Wrap.essence(expected)));
  }

  /** Convert a given {@link String} into an {@link Statement}, or fail the
   * current test, if such a conversion is not possible
   * @param statement a {@link String} that represents a Java statement
   * @return an {@link Statement} data structure representing the parameter. */
  public static Statement asSingle(final String statement) {
    azzert.notNull(statement);
    final ASTNode n = MakeAST.STATEMENTS.from(statement);
    azzert.notNull(n);
    return extract.singleStatement(n);
  }

  public static Document rewrite(final Spartanization s, final CompilationUnit u, final Document $) {
    try {
      s.createRewrite(u, null).rewriteAST($, null).apply($);
      return $;
    } catch (MalformedTreeException | BadLocationException e) {
      throw new AssertionError(e);
    }
  }
}
