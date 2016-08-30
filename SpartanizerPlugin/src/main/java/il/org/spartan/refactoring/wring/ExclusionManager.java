package il.org.spartan.refactoring.wring;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.utils.*;

class ExclusionManager {
  final Set<ASTNode> inner = new HashSet<>();

  void exclude(final ASTNode n) {
    inner.add(n);
  }

  boolean isExcluded(final ASTNode n) {
    for (final ASTNode ancestor : hop.ancestors(n))
      if (inner.contains(ancestor))
        return true;
    return false;
  }

  void unExclude(final ASTNode n) {
    inner.remove(n);
  }
}