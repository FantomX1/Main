package op.zz;

import op.z.*;
import op.zz.OperationListener.*;

/** Operation that accepts {@link OperationListener}s.
 * @author Ori Roth
 * @since 2017-08-31 */
public class ObservableOperation<L extends OperationListener<L>> extends Observable<L> {
  @Override protected OperationListenerContainer<L> initializeContainer() {
    return new OperationListenerContainerImplementation<>();
  }
  public void go() {
    listeners().begin();
    listeners().end();
  }
}
