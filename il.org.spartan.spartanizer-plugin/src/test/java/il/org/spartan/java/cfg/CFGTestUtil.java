package il.org.spartan.java.cfg;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.*;
import il.org.spartan.java.cfg.CFG.*;
import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.utils.*;

/** Testing utilities for {@link CFGTest}.
 * @author Ori Roth
 * @since 2017-07-06 */
public class CFGTestUtil {
  public static IOAble cfg(final String code) {
    final CompilationUnit $ = (CompilationUnit) makeAST.COMPILATION_UNIT.from(WrapIntoComilationUnit.find(code).on(code));
    return new IOAble() {
      final IOAble self = this;

      @Override public Contains outs(String n1) {
        return new Contains(n1) {
          @Override public Nodes nodes(ASTNode ¢) {
            return CFG.out(¢);
          }
        };
      }
      @Override public Contains ins(String n1) {
        return new Contains(n1) {
          @Override public Nodes nodes(ASTNode ¢) {
            return CFG.in(¢);
          }
        };
      }

      abstract class Contains implements CFGTestUtil.Contains {
        private String n1;

        public Contains(final String n1) {
          this.n1 = n1;
        }
        public abstract Nodes nodes(ASTNode n);
        @Override public IOAble contains(String... ns2) {
          final ASTNode a1 = find($, n1);
          assert a1 != null : "\nproblem in finding node\n" + tide.clean(n1) + "\nin compilation unit\n" + tide.clean($ + "");
          final Nodes ns = nodes(a1);
          assert ns != null : "\nnull nodes of\n" + tide.clean(n1);
          for (final String n2 : ns2) {
            final ASTNode a2 = find($, n2);
            assert a1 != null : "\nproblem in finding node\n" + tide.clean(n2) + "\nin compilation unit\n" + tide.clean($ + "");
            assert ns.contains(a2) : "\nnodes of\n" + tide.clean(n1) + "\ndoes not contain\n" + tide.clean(n2);
          }
          return self;
        }
      }
    };
  }

  public interface IOAble {
    Contains outs(String s);
    Contains ins(String s);
  }

  public interface Contains {
    IOAble contains(String... ss);
  }

  static ASTNode find(final ASTNode root, final String code) {
    final String tidy = tide.clean(code);
    Wrapper<ASTNode> $ = new Wrapper<>();
    root.accept(new ASTVisitor() {
      @Override public void preVisit(ASTNode ¢) {
        if (tide.clean(¢ + "").equals(tidy))
          $.set(¢);
      }
    });
    return $.get();
  }
}
