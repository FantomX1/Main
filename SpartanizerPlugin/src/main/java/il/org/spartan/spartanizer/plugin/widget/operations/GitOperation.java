package il.org.spartan.spartanizer.plugin.widget.operations;

import java.io.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.storage.file.*;

import il.org.spartan.spartanizer.plugin.*;
import il.org.spartan.spartanizer.plugin.widget.*;

/** Git pull command.
 * @author Ori Roth
 * @since 2017-04-24 */
public abstract class GitOperation extends WidgetOperation {
  private static final long serialVersionUID = 0xB18576ECF23E9C3L;
  public static final String POPUP = "popup";
  private Boolean popup = Boolean.TRUE;

  @Override public String[][] configurationComponents() {
    return new String[][] { //
        { POPUP, "Boolean", "true", "false", "REQUIRED" } //
    };
  }
  @Override public boolean register(final Map<?, ?> configuration) {
    return (popup = ((Boolean) configuration.get(POPUP))) != null;
  }
  protected abstract void gitOperation(Git g) throws Throwable;
  @Override @SuppressWarnings("unused") public void onMouseUp(final WidgetContext c) throws Throwable {
    if (c.project == null || !c.project.exists()) {
      Dialogs.message("Git Error: Project doesn't exist").open();
      return;
    }
    final IPath p = c.project.getLocation();
    if (p == null) {
      Dialogs.message("Git Error: No path can be determined").open();
      return;
    }
    final File f = p.toFile();
    if (f == null || !f.exists()) {
      Dialogs.message("Git Error: Path to file can not be determined").open();
      return;
    }
    final FileRepositoryBuilder builder = new FileRepositoryBuilder().findGitDir(f);
    if (builder != null)
      try (Repository repo = builder.build()) {
        if (repo != null)
          try (Git git = new Git(repo)) {
            gitOperation(git);
          }
//          } catch (Throwable e) {
//            Dialogs.message("Git Error: ...").open();
//            return;
//          }
      } catch (Throwable e) {
        Dialogs.message("Git Error: No git directory was found").open();
        return;
      }
  }
  protected void displayMessage(String ¢) {
    if (popup.booleanValue())
      Dialogs.message(¢).open();
  }
}
