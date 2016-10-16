package il.org.spartan.plugin.revision;

import java.util.*;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;

import static il.org.spartan.spartanizer.ast.navigate.wizard.*;

import il.org.spartan.spartanizer.engine.*;

/** Couples together {@link CompilationUnit} and its {@link ICompilationUnit}.
 * @author Ori Roth
 * @since 2016 */
public class WrappedCompilationUnit {
  public ICompilationUnit descriptor;
  public CompilationUnit compilationUnit;

  /** Instantiates this class
   * @param compilationUnit JD */
  public WrappedCompilationUnit(final ICompilationUnit compilationUnit) {
    descriptor = compilationUnit;
  }

  public WrappedCompilationUnit build() {
    if (compilationUnit == null)
      compilationUnit = (CompilationUnit) Make.COMPILATION_UNIT.parser(descriptor).createAST(nullProgressMonitor);
    return this;
  }

  public WrappedCompilationUnit dispose() {
    compilationUnit = null;
    return this;
  }

  public String name() {
    return descriptor == null ? null : descriptor.getElementName();
  }

  /** Factory method
   * @param ¢ JD
   * @return an instance created by the parameter
   */
  public static WrappedCompilationUnit of(final ICompilationUnit ¢) {
    return new WrappedCompilationUnit(¢);
  }

  /** [[SuppressWarningsSpartan]] */
  public static List<WrappedCompilationUnit> of(final List<ICompilationUnit> ¢) {
    final List<WrappedCompilationUnit> $ = new ArrayList<>();
    for (final ICompilationUnit u : ¢)
      $.add(new WrappedCompilationUnit(u));
    return $;
  }
}
