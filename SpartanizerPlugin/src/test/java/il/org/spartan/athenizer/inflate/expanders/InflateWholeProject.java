package il.org.spartan.athenizer.inflate.expanders;

import org.eclipse.core.commands.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import il.org.spartan.athenizer.inflate.*;
import il.org.spartan.plugin.*;

public class InflateWholeProject extends AbstractHandler {
  private static final int MAX_PASSES = 20;

  @Override public Object execute(@SuppressWarnings("unused") ExecutionEvent __) {
    final Selection s = Selection.Util.current();
    for (WrappedCompilationUnit ¢ : s.inner) {
      for (int i = 0; i < MAX_PASSES; ++i) {
        if (!SingleFlater.commitChanges(SingleFlater.in(¢.build().compilationUnit).from(new InflaterProvider()),
            ASTRewrite.create(¢.compilationUnit.getAST()), ¢, null))
          break;
        ¢.dispose();
      }
      ¢.dispose();
    }
    return null;
  }
}
