package il.org.spartan.plugin;

import java.util.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.dispatch.*;

/** @author Boris van Sosin <code><boris.van.sosin [at] gmail.com></code> (v2)
 * @author Ofir Elmakias <code><elmakias [at] outlook.com></code> (original /
 *         30.05.2014) (v3)
 * @author Tomer Zeltzer <code><tomerr90 [at] gmail.com></code> (original /
 *         30.05.2014) (v3)
 * @since 2013/07/01 */
public final class Tips {
  private static GUI$Applicator[] all = { //
      new Trimmer(), //
  };
  @SuppressWarnings("synthetic-access") //
  private static final Map<String, GUI$Applicator> map = new HashMap<String, GUI$Applicator>() {
    static final long serialVersionUID = -8921699276699040030L;
    {
      for (final GUI$Applicator ¢ : all)
        put(¢.getName(), ¢);
    }
  };

  /** @return all the registered laconization refactoring objects */
  public static Iterable<GUI$Applicator> all() {
    return map.values();
  }

  /** @return Iteration over all {@link @GUIApplicator) class instances */
  public static Iterable<GUI$Applicator> allAvailablelaconizations() {
    return as.iterable(all);
  }

  /** @return all the registered laconization refactoring objects names */
  public static Set<String> allRulesNames() {
    return map.keySet();
  }

  /** @param t rule
   * @return laconization class rule instance */
  @SuppressWarnings("unchecked") //
  public static <T extends GUI$Applicator> T findInstance(final Class<? extends T> ¢) {
    for (final GUI$Applicator $ : all)
      if ($.getClass().equals(¢))
        return (T) $;
    return null;
  }

  /** @param name the name of the applicator
   * @return an instance of the class */
  public static GUI$Applicator get(final String name) {
    assert name != null;
    return map.get(name);
  }

  /** Resets the enumeration with the current values from the preferences file.
   * Letting the rules notification decisions be updated without restarting
   * eclipse. */
  public static void reset() {
    map.clear();
    for (final GUI$Applicator ¢ : all)
      map.put(¢.getName(), ¢);
  }

  private final GUI$Applicator value;

  private Tips(final GUI$Applicator value) {
    this.value = value;
  }

  /** @return ? */
  public GUI$Applicator value() {
    return value;
  }
}
