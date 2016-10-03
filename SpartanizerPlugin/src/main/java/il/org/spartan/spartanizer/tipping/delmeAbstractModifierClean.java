package il.org.spartan.spartanizer.tipping;

import java.util.*;
import java.util.function.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.create.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.dispatch.*;

public abstract class delmeAbstractModifierClean<N extends BodyDeclaration> extends ReplaceCurrentNode<N> implements TipperCategory.SyntacticBaggage {
  @Override public String description(@SuppressWarnings("unused") final N __) {
    return "remove redundant modifier";
  }

  @Override public boolean prerequisite(final N ¢) {
    return firstBad(¢) != null;
  }

  @Override public N replacement(final N $) {
    return go(duplicate.of($));
  }

  protected abstract boolean redundant(Modifier m);

  Modifier firstThat(final N n, final Predicate<Modifier> m) {
    for (final Modifier $ : extract.modifiers(n))
      if ($.isModifier() && m.test($))
        return $;
    return null;
  }

  boolean has(final N ¢, final Predicate<Modifier> m) {
    return firstThat(¢, m) != null;
  }

  private Modifier firstBad(final N n) {
    return firstThat(n, (final Modifier ¢) -> redundant(¢));
  }

  private N go(final N $) {
    for (final Iterator<Modifier> ¢ = extract.modifiers($).iterator(); ¢.hasNext();)
      if (redundant(¢.next()))
        ¢.remove();
    return $;
  }
}