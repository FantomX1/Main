package il.org.spartan.plugin;

import static il.org.spartan.spartanizer.engine.Linguistic.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

import il.org.spartan.plugin.Listener;

/** Both {@link AbstractHandler} and {@link IMarkerResolution} implementations
 * that uses {@link Spartanizer} as its applicator.
 * @author Ori Roth
 * @since 2.6 */
public class SpartanizationHandler extends AbstractHandler implements IMarkerResolution {
  private static final String NAME = "Laconic";
  private static final int PASSES = 20;
  private static final int DIALOG_THRESHOLD = 2;

  @Override public Object execute(@SuppressWarnings("unused") final ExecutionEvent __) {
    final Spartanizer a = applicator().defaultSelection();
    a.passes(a.selection().textSelection != null ? 1 : PASSES);
    a.go();
    return null;
  }

  @Override public String getLabel() {
    return "Apply";
  }

  @Override public void run(final IMarker ¢) {
    applicator().passes(1).selection(Selection.Util.by(¢)).go();
  }

  /** Creates and configures an applicator, without configuring the selection.
   * @return applicator for this handler */
  public static Spartanizer applicator() {
    final Spartanizer $ = new Spartanizer();
    final ProgressMonitorDialog d = Dialogs.progress(false);
    $.runContext(r -> {
      try {
        d.run(true, true, __ -> r.run());
      } catch (InvocationTargetException | InterruptedException e) {
        monitor.log(e);
        e.printStackTrace();
      }
    });
    $.defaultRunAction();
    $.listener(new Listener() {
      static final int DIALOG_CREATION = 1;
      static final int DIALOG_PROCESSING = 2;
      int level;
      boolean dialogOpen;

      @Override public void tick(Object... ¢) {
        asynch(() -> {
          d.getProgressMonitor().subTask(Linguistic.merge(¢));
          d.getProgressMonitor().worked(1);
          if (d.getProgressMonitor().isCanceled())
            $.stop();
        });
      }

      @Override public void push(Object... ¢) {
        switch (++level) {
          case DIALOG_CREATION:
            if ($.selection().size() >= DIALOG_THRESHOLD)
              if (!Dialogs.ok(Dialogs.message(Linguistic.merge(¢))))
                $.stop();
              else {
                dialogOpen = true;
                asynch(() -> d.open());
              }
            break;
          case DIALOG_PROCESSING:
            if (dialogOpen)
              asynch(() -> {
                d.getProgressMonitor().beginTask(NAME, $.selection().size());
                if (d.getProgressMonitor().isCanceled())
                  $.stop();
              });
            break;
          default:
            break;
        }
      }

      /** [[SuppressWarningsSpartan]] see issue #467 */
      @Override public void pop(Object... ¢) {
        switch (level--) {
          case DIALOG_CREATION:
            if (dialogOpen)
              Dialogs.message(Linguistic.merge(¢)).open();
            break;
          case DIALOG_PROCESSING:

            break;
          default:
            break;
        }
      }
    });
    return $;
  }

  /** Run asynchronously in UI thread.
   * @param ¢ JD */
  static void asynch(final Runnable ¢) {
    Display.getDefault().asyncExec(¢);
  }

  /** Creates and configures an applicator, without configuring the selection.
   * @return applicator for this handler [[SuppressWarningsSpartan]] */
  @SuppressWarnings("deprecation") @Deprecated public static Spartanizer applicatorMapper() {
    final Spartanizer $ = new Spartanizer();
    final ProgressMonitorDialog d = Dialogs.progress(false);
    final AtomicBoolean openDialog = new AtomicBoolean(false);
    $.listener(EventMapper.empty(event.class) //
        .expand(EventMapper.recorderOf(event.visit_cu).rememberBy(WrappedCompilationUnit.class).does((__, ¢) -> {
          if (openDialog.get())
            asynch(() -> {
              d.getProgressMonitor().subTask($.selection().inner.indexOf(¢) + "/" + $.selection().size() + "\tSpartanizing " + ¢.name());
              d.getProgressMonitor().worked(1);
              if (d.getProgressMonitor().isCanceled())
                $.stop();
            });
        })) //
        .expand(EventMapper.recorderOf(event.visit_node).rememberBy(ASTNode.class)) //
        .expand(EventMapper.recorderOf(event.visit_root).rememberLast(String.class)) //
        .expand(EventMapper.recorderOf(event.run_pass).counter().does(¢ -> {
          if (openDialog.get())
            asynch(() -> {
              d.getProgressMonitor().beginTask(NAME, $.selection().size());
              if (d.getProgressMonitor().isCanceled())
                $.stop();
            });
        })) //
        .expand(EventMapper.inspectorOf(event.run_start).does(¢ -> {
          if ($.selection().size() >= DIALOG_THRESHOLD)
            if (!Dialogs.ok(Dialogs.message("Spartanizing " + nanable(¢.get(event.visit_root)))))
              $.stop();
            else {
              asynch(() -> d.open());
              openDialog.set(true);
            }
        })) //
        .expand(EventMapper.inspectorOf(event.run_finish).does(¢ -> {
          if (openDialog.get())
            asynch(() -> d.close());
        }).does(¢ -> {
          if (openDialog.get())
            Dialogs.message("Done spartanizing " + nanable(¢.get(event.visit_root)) //
                + "\nSpartanized " + nanable(¢.get(event.visit_root)) //
                + " with " + nanable((Collection<?>) ¢.get(event.visit_cu), c -> {
                  return Integer.valueOf(c.size());
                }) + " files" //
                + " in " + plurales("pass", (AtomicInteger) ¢.get(event.run_pass))).open();
        })));
    $.runContext(r -> {
      try {
        d.run(true, true, __ -> r.run());
      } catch (InvocationTargetException | InterruptedException e) {
        monitor.log(e);
        e.printStackTrace();
      }
    });
    $.defaultRunAction();
    return $;
  }
}
