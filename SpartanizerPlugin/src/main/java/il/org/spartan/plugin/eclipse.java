package il.org.spartan.plugin;

import static il.org.spartan.spartanizer.ast.wizard.*;

import java.util.*;

import javax.swing.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.wring.dispatch.*;
import il.org.spartan.utils.*;

/** Fluent API services for the plugin
 * @author Yossi Gil
 * @since 2016 */
public interface eclipse {
  static final Applicator[] safeSpartanizations = { //
      new Trimmer() };
  static final String NAME = "Spartanization";
  static final String ICON_PATH = "/src/main/icons/spartan-warrior64.gif";
  ImageIcon icon = new ImageIcon(new eclipse() {
    // TODO: Ori, why do we need this kludge?
  }.getClass().getResource(ICON_PATH));

  /** @param message What to announce
   * @return <code><b>null</b></code> */
  static Void announce(final Object message) {
    JOptionPane.showMessageDialog(null, message, NAME, JOptionPane.INFORMATION_MESSAGE, icon);
    return null;
  }

  static boolean apply(final ICompilationUnit cu) {
    return apply(cu, new Range(0, 0));
  }

  static boolean apply(final ICompilationUnit cu, final ITextSelection t) {
    for (final Applicator s : safeSpartanizations)
      try {
        s.setCompilationUnit(cu);
        s.setSelection(t.getLength() > 0 && !t.isEmpty() ? t : null);
        return s.performRule(cu, nullProgressMonitor);
      } catch (final CoreException x) {
        x.printStackTrace();
      }
    return false;
  }

  static boolean apply(final ICompilationUnit cu, final Range r) {
    return apply(cu, r == null || r.isEmpty() ? new TextSelection(0, 0) : new TextSelection(r.from, r.size()));
  }

  static ICompilationUnit compilationUnit(final IEditorPart ep) {
    return ep == null ? null : compilationUnit((IResource) resources(ep));
  }

  static ICompilationUnit compilationUnit(final IResource ¢) {
    return ¢ == null ? null : JavaCore.createCompilationUnitFrom((IFile) ¢);
  }

  /** @return List of all compilation units in the current project */
  static List<ICompilationUnit> compilationUnits() {
    try {
      return compilationUnits(currentCompilationUnit(), nullProgressMonitor);
    } catch (final JavaModelException e) {
      e.printStackTrace();
    }
    return null;
  }

  static List<ICompilationUnit> compilationUnits(final ICompilationUnit u) {
    try {
      return compilationUnits(u, nullProgressMonitor);
    } catch (final JavaModelException x) {
      x.printStackTrace();
      return null;
    }
  }

  /** @param u A compilation unit for reference - you give me an arbitrary
   *        compilation unit from the project and I'll find the root of the
   *        project and do my magic.
   * @param pm A standard {@link IProgressMonitor} - if you don't care about
   *        operation times put a "nullProgressMonitor"
   * @return List of all compilation units in the current project
   * @throws JavaModelException don't forget to catch */
  static List<ICompilationUnit> compilationUnits(final ICompilationUnit u, final IProgressMonitor pm) throws JavaModelException {
    pm.beginTask("Gathering project information...", 1);
    final List<ICompilationUnit> $ = new ArrayList<>();
    if (u == null) {
      announce("Cannot find current compilation unit " + u);
      return $;
    }
    final IJavaProject javaProject = u.getJavaProject();
    if (javaProject == null) {
      announce("Cannot find project of " + u);
      return $;
    }
    final IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
    if (packageFragmentRoots == null) {
      announce("Cannot find roots of " + javaProject);
      return $;
    }
    for (final IPackageFragmentRoot r : packageFragmentRoots)
      if (r.getKind() == IPackageFragmentRoot.K_SOURCE)
        for (final IJavaElement ¢ : r.getChildren())
          if (¢.getElementType() == IJavaElement.PACKAGE_FRAGMENT)
            $.addAll(as.list(((IPackageFragment) ¢).getCompilationUnits()));
    pm.done();
    return $;
  }

  /** Retrieves the current {@link ICompilationUnit}
   * @return current {@link ICompilationUnit} */
  static ICompilationUnit currentCompilationUnit() {
    return compilationUnit(currentWorkbenchWindow().getActivePage().getActiveEditor());
  }

  /** Retrieves the current {@link IWorkbenchWindow}
   * @return current {@link IWorkbenchWindow} */
  static IWorkbenchWindow currentWorkbenchWindow() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
  }

  static boolean isNodeOutsideMarker(final ASTNode n, final IMarker m) {
    try {
      return n.getStartPosition() < ((Integer) m.getAttribute(IMarker.CHAR_START)).intValue()
          || n.getLength() + n.getStartPosition() > ((Integer) m.getAttribute(IMarker.CHAR_END)).intValue();
    } catch (final CoreException x) {
      x.printStackTrace();
      return true;
    }
  }

  static IProgressMonitor newSubMonitor(final IProgressMonitor ¢) {
    return new SubProgressMonitor(¢, 1, SubProgressMonitor.SUPPRESS_SUBTASK_LABEL);
  }

  static Object resources(final IEditorPart ep) {
    return ep.getEditorInput().getAdapter(IResource.class);
  }

  static ITextSelection selectedText() {
    final IEditorPart ep = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    final ISelection s = ep.getEditorSite().getSelectionProvider().getSelection();
    return !(s instanceof ITextSelection) ? null : (ITextSelection) s;
  }
}
