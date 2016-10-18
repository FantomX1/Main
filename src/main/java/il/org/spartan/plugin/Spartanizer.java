package il.org.spartan.plugin;

import java.util.*;
import java.util.function.*;

import org.eclipse.jdt.core.*;

import il.org.spartan.spartanizer.dispatch.*;

/** Possible events during spartanization process
 * <p>
 * Why do we need to make such a strong binding between the event generator and
 * the listener? Why should they agree on a common type of events? We should let
 * the listener take, say strings, and just record them. */
@Deprecated enum event {
  run_start, run_finish, run_pass, run_pass_done, //
  visit_root, visit_cu, visit_node, //
}

/** An {@link Applicator} suitable for eclipse GUI.
 * @author Ori Roth
 * @since 2.6 */
public class Spartanizer extends Applicator {
  /** Few passes for the applicator to conduct. */
  private static final int PASSES_FEW = 1;
  /** Many passes for the applicator to conduct. */
  private static final int PASSES_MANY = 20;

  /** Spartanization process. */
  @Override public void go() {
    if (selection() == null || listener() == null || passes() <= 0 || selection().isEmpty())
      return;
    listener().push(message.run_start.get(selection().name));
    if (!shouldRun())
      return;
    runContext().accept(() -> {
      final int l = passes();
      for (int pass = 0; pass < l; ++pass) {
        listener().push(message.run_pass.get(Integer.valueOf(pass)));
        if (!shouldRun())
          break;
        final List<WrappedCompilationUnit> selected = selection().inner;
        final List<WrappedCompilationUnit> alive = new ArrayList<>(selected);
        final List<WrappedCompilationUnit> dead = new ArrayList<>();
        for (final WrappedCompilationUnit ¢ : alive) {
          if (!runAction().apply(¢.build()).booleanValue())
            dead.add(¢);
          ¢.dispose();
          listener().tick(message.visit_cu.get(Integer.valueOf(alive.indexOf(¢)), Integer.valueOf(alive.size()), ¢.descriptor.getElementName()));
          if (!shouldRun())
            break;
        }
        listener().pop(message.run_pass_finish.get(Integer.valueOf(pass)));
        selected.removeAll(dead);
        if (selected.isEmpty() || !shouldRun())
          break;
      }
    });
    // TODO Roth: add metrics etc.
    listener().pop(message.run_finish.get(selection().name));
  }

  /** Default listener configuration of {@link Spartanizer}. Simple printing
   * to console.
   * @return this applicator */
  public Spartanizer defaultListenerNoisy() {
    listener(new Listener() {
      @Override public void tick(Object... os) {
        for (Object ¢ : os)
          System.out.print(¢ + " ");
        System.out.println();
      }
    });
    return this;
  }

  /** Default listener configuration of {@link Spartanizer}. Silent
   * listener.
   * @return this applicator */
  public Spartanizer defaultListenerSilent() {
    listener(new Listener() {
      @Override public void tick(@SuppressWarnings("unused") Object... __) {
        //
      }
    });
    return this;
  }

  /** Default selection configuration of {@link Spartanizer}. Normal eclipse
   * user selection.
   * @return this applicator */
  public Spartanizer defaultSelection() {
    selection(Selection.Util.current());
    return this;
  }

  /** Default passes configuration of {@link Spartanizer}, with few passes.
   * @return this applicator */
  public Spartanizer defaultPassesFew() {
    passes(PASSES_FEW);
    return this;
  }

  /** Default passes configuration of {@link Spartanizer}, with many passes.
   * @return this applicator */
  public Spartanizer defaultPassesMany() {
    passes(PASSES_MANY);
    return this;
  }

  /** Default run context configuration of {@link Spartanizer}. Simply runs
   * the {@link Runnable} in the current thread.
   * @return this applicator */
  public Spartanizer defaultRunContext() {
    runContext(r -> r.run());
    return this;
  }

  // TODO Roth: use Policy / replacement for Trimmer.
  /** Default run action configuration of {@link Spartanizer}. Spartanize
   * the {@link ICompilationUnit} using a {@link Trimmer}.
   * @return this applicator */
  public Spartanizer defaultRunAction() {
    final Trimmer t = new Trimmer();
    runAction(u -> Boolean.valueOf(t.apply(u, selection())));
    return this;
  }

  /** Default run action configuration of {@link Spartanizer}. Spartanize
   * the {@link ICompilationUnit} using received {@link GUI$Applicator}.
   * @param a JD
   * @return this applicator */
  public Spartanizer defaultRunAction(final GUI$Applicator a) {
    runAction(u -> Boolean.valueOf(a.apply(u, selection())));
    return this;
  }

  /** Default settings for all {@link Applicator} components.
   * @return this applicator */
  public Spartanizer defaultSettings() {
    return defaultListenerSilent().defaultPassesFew().defaultRunContext().defaultSelection().defaultRunAction();
  }

  /** Factory method.
   * @return default event applicator */
  public static Spartanizer defaultApplicator() {
    return new Spartanizer().defaultSettings();
  }
  
  /** Printing definition of events that occur during spartanization.
   * @author Ori Roth
   * @since 2.6 */
  private enum message {
    run_start(1, inp -> "Spartanizing " + printableAt(inp, 0)), //
    run_pass(1, inp -> "Pass #" + printableAt(inp, 0)), //
    run_pass_finish(1, inp -> "Pass #" + printableAt(inp, 0) + " finished"), //
    visit_cu(3, inp -> printableAt(inp, 0) + "/" + printableAt(inp, 1) + "\tSpartanizing " + printableAt(inp, 2)), //
    run_finish(1, inp -> "Done spartanizing " + printableAt(inp, 0));
    private final int inputCount;
    private final Function<Object[], String> printing;

    message(int inputCount, Function<Object[], String> printing) {
      this.inputCount = inputCount;
      this.printing = printing;
    }

    public String get(Object... ¢) {
      assert ¢.length == inputCount;
      return printing.apply(¢);
    }

    private static String printableAt(Object[] os, int index) {
      return Linguistic.nanable(os, xs -> xs[index]);
    }
  }
}
