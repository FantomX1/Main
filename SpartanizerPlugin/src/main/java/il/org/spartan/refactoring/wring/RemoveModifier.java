package il.org.spartan.refactoring.wring;

import java.util.*;
import java.util.function.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.ast.*;

public abstract class RemoveModifier<N extends BodyDeclaration> extends Wring.ReplaceCurrentNode<N> {
  @Override String description(@SuppressWarnings("unused") final N __) {
    return "remove redundant modifier";
  }

  IExtendedModifier firstThat(final N n, final Predicate<Modifier> f) {
    for (final IExtendedModifier $ : step.modifiers(n))
      if ($.isModifier() && f.test((Modifier) $))
        return $;
    return null;
  }

  boolean has(final N ¢, final Predicate<Modifier> p) {
    return firstThat(¢, p) != null;
  }


  abstract boolean redundant(Modifier m);

  @Override N replacement(final N $) {
    return go(wizard.duplicate($));
  }

  @Override boolean scopeIncludes(final N ¢) {
    return firstBad(¢) != null;
  }


  private IExtendedModifier firstBad(final N n) {
    return firstThat(n, (final Modifier ¢) -> redundant(¢));
  }

  private N go(final N $) {
    for (final Iterator<IExtendedModifier> ¢ = step.modifiers($).iterator(); ¢.hasNext();)
      if (redundant(¢.next()))
        ¢.remove();
    return $;
  }

  private boolean redundant(final IExtendedModifier m) {
    return redundant((Modifier) m);
  }
}