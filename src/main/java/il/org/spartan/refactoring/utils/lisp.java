package il.org.spartan.refactoring.utils;

import static il.org.spartan.Utils.*;

import java.util.*;

public interface lisp {
  static <T> List<T> chop(final List<T> ts) {
    if (ts.isEmpty())
      return null;
    ts.remove(0);
    return ts;
  }

  static <T> T first(final List<T> ts) {
    return ts == null || ts.isEmpty() ? null : ts.get(0);
  }

  /** Determine if an integer can be found in a list of values
   * @param candidate what to search for
   * @param is where to search
   * @return true if the the item is found in the list */
  @SafeVarargs static boolean intIsIn(final int candidate, final int... is) {
    for (final int ¢ : is)
      if (¢ == candidate)
        return true;
    return false;
  }

  /** Retrieve next item in a list
   * @param i an index of specific item in a list
   * @param ts the indexed list
   * @return following item in the list, if such such an item exists, otherwise,
   *         the last node */
  static <T> T next(final int i, final List<T> ts) {
    return !inRange(i + 1, ts) ? last(ts) : ts.get(i + 1);
  }

  static <T> T onlyOne(final List<T> ts) {
    return ts == null || ts.size() != 1 ? null : ts.get(0);
  }

  /** Determine if an item is not included in a list of values
   * @param < T > JD
   * @param candidate what to search for
   * @param ts where to search
   * @return true if the the item is not found in the list */
  @SafeVarargs static <T> boolean out(final T candidate, final T... ts) {
    return !in(candidate, ts);
  }

  /** Retrieve previous item in a list
   * @param i an index of specific item in a list
   * @param ts the indexed list
   * @return previous item in the list, if such an item exists, otherwise, the
   *         last node */
  static <T> T prev(final int i, final List<T> ts) {
    return ts.get(i < 1 ? 0 : i - 1);
  }

  static <T> Iterable<T> rest(final Iterable<T> ts) {
    return () -> {
      return new Iterator<T>() {
        final Iterator<T> $ = ts.iterator();
        {
          $.next();
        }

        @Override public boolean hasNext() {
          return $.hasNext();
        }

        @Override public T next() {
          return $.next();
        }
      };
    };
  }

  static <T> Iterable<T> rest2(final Iterable<T> ts) {
    return rest(rest(ts));
  }

  static <T> T second(final List<T> ts) {
    return ts == null || ts.size() < 2 ? null : ts.get(1);
  }

  /** Converts a boolean into a bit value
   * @param $ JD
   * @return 1 if the parameter is <code><b>true</b></code>, 0 if it is
   *         <code><b>false</b></code> */
  static int bit(final boolean $) {
    return $ ? 1 : 0;
  }
}
