package il.org.spartan.refactoring.handlers;

import il.org.spartan.refactoring.contexts.*;
import il.org.spartan.refactoring.suggestions.*;
import il.org.spartan.refactoring.utils.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.eclipse.core.commands.*;
import org.eclipse.jdt.core.*;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.*;

import static il.org.spartan.refactoring.suggestions.DialogBoxes.*;

/**
 * A handler for {@link Spartanizations}. This handler executes all safe
 * Spartanizations on all java files in the current project.
 *
 * @author Ofir Elmakias <code><elmakias [at] outlook.com></code>
 * @since 2015/08/01
 */
public class CleanupHandler extends BaseHandler {
  static final int MAX_PASSES = 20;

  @Override public Void execute(@SuppressWarnings("unused") final ExecutionEvent __) throws ExecutionException {
    final StringBuilder message = new StringBuilder();
    final ICompilationUnit currentCompilationUnit = retrieve.currentCompilationUnit();
    final IJavaProject javaProject = currentCompilationUnit.getJavaProject();
    message.append("starting at " + currentCompilationUnit.getElementName() + "\n");
    final List<ICompilationUnit> us = retrieve.allCompilationUnits(currentCompilationUnit);
    message.append("found " + us.size() + " compilation units \n");
    final IWorkbench wb = PlatformUI.getWorkbench();
    final int initialCount = countSuggestions(currentCompilationUnit);
    message.append("with " + initialCount + " suggestions");
    if (initialCount == 0)
      return announce("No suggestions for '" + javaProject.getElementName() + "' project\n" + message);
    for (int i = 0; i < MAX_PASSES; ++i) {
      final IProgressService ps = wb.getProgressService();
      final AtomicInteger passNum = new AtomicInteger(i + 1);
      try {
        ps.busyCursorWhile(pm -> {
          pm.beginTask("Spartanizing project '" + javaProject.getElementName() + "' - " + //
              "Pass " + passNum.get() + " out of maximum of " + MAX_PASSES, us.size());
          int n = 0;
          for (final ICompilationUnit u : us) {
            ApplySpartanizationHandler.execute(u);
            pm.worked(1);
            pm.subTask(u.getElementName() + " " + ++n + "/" + us.size());
          }
          pm.done();
        });
      } catch (final InvocationTargetException x) {
        x.printStackTrace();
      } catch (final InterruptedException x) {
        x.printStackTrace();
      }
      final int finalCount = countSuggestions(currentCompilationUnit);
      if (finalCount <= 0)
        return announce("Spartanizing '" + javaProject.getElementName() + "' project \n" + //
            "Completed in " + (1 + i) + " passes. \n" + //
            "Total changes: " + (initialCount - finalCount) + "\n" + //
            "Suggestions before: " + initialCount + "\n" + //
            "Suggestions after: " + finalCount + "\n" + //
            message);
    }
    throw new ExecutionException("Too many iterations");
  }
  /**
   * Returns the number of Spartanization suggestions for this compilation unit
   *
   * @param u
   *          JD
   * @return the number of suggestions available for the compilation unit
   */
  public static int countSuggestions(final ICompilationUnit u) {
    int $ = 0;
    final CurrentAST s = new CurrentAST();
    s.setMarker(null);
    return $ += s.countSuggestions();
  }
}
