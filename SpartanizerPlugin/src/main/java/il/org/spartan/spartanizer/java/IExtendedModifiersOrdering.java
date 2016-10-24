package il.org.spartan.spartanizer.java;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

/** Maintain a canonical order of modifiers.
 * @author Yossi Gil
 * @since 2016 */
public enum IExtendedModifiersOrdering {
  Override, //
  Deprecated, //
  SuppressWarnings, //
  SafeVarargs, //
  FunctionalInterface, //
  $USER_DEFINED_ANNOTATION$, //
  PUBLIC, //
  PROTECTED, //
  PRIVATE, //
  ABSTRACT, //
  STATIC, //
  DEFAULT, //
  FINAL, //
  TRANSIENT, //
  VOLATILE, //
  SYNCHRONIZED, //
  NATIVE, //
  STRICTFP, //
  ;
  public static boolean[] bitMap() {
    final boolean[] $ = new boolean[IExtendedModifiersOrdering.size()];
    Arrays.fill($, false);
    return $;
  }

  public static int compare(final IExtendedModifier modifier1, final IExtendedModifier modifier2) {
    return compare(find(modifier1), find(modifier2));
  }

  public static int compare(final IExtendedModifier m, final IExtendedModifiersOrdering o) {
    return compare(find(m), o);
  }

  public static int compare(final String modifier1, final String modifier2) {
    return compare(find(modifier1), find(modifier2));
  }

  public static IExtendedModifiersOrdering find(final IExtendedModifier ¢) {
    return find(¢ + "");
  }

  public static boolean greaterThan(final IExtendedModifier m1, final IExtendedModifiersOrdering m2) {
    return compare(m1, m2) > 0;
  }

  public static boolean greaterThanOrEquals(final IExtendedModifier m1, final IExtendedModifiersOrdering m2) {
    return compare(m1, m2) >= 0;
  }

  public static boolean isUserDefinedAnnotation(final IExtendedModifier m1) {
    return compare(m1, $USER_DEFINED_ANNOTATION$) == 0;
  }

  public static int ordinal(final IExtendedModifier ¢) {
    return ordinal(¢ + "");
  }

  public static int userDefinedAnnotationsOrdinal() {
    return IExtendedModifiersOrdering.$USER_DEFINED_ANNOTATION$.ordinal();
  }

  static IExtendedModifiersOrdering find(final String modifier) {
    for (final IExtendedModifiersOrdering $ : IExtendedModifiersOrdering.values())
      if (modifier.equals(($ + "").toLowerCase()) || modifier.equals("@" + $))
        return $;
    return $USER_DEFINED_ANNOTATION$;
  }

  static int ordinal(final String modifier) {
    return find(modifier).ordinal();
  }

  private static int compare(final IExtendedModifiersOrdering m1, final IExtendedModifiersOrdering m2) {
    return m1.ordinal() - m2.ordinal();
  }

  private static int size() {
    return IExtendedModifiersOrdering.values().length;
  }
}