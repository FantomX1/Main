package op.a1;

import fluent.ly.*;

/** TODO Ori Roth: document class
 * @author Ori Roth
 * @since 2017-08-28 */
abstract class Run<S extends Events.Set, Self extends Run<S, Self>> implements Selfie<Self> {
  public final Events.Delegator.Many<S> listeners = new Events.Delegator.Many<>();

  public final Self withListener(S ¢) {
    listeners.add(¢);
    return self();
  }
  public abstract void go();
}
