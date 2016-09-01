package il.org.spartan.refactoring.wring;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import il.org.spartan.refactoring.java.*;
import il.org.spartan.refactoring.spartanizations.*;
import il.org.spartan.refactoring.utils.*;

/** An adapter that converts the @{link Wring} protocol into that of
 * {@link Spartanization}
 * @author Yossi Gil
 * @since 2015/07/25 */
public final class AsSpartanization extends Spartanization {
  final Wring<ASTNode> inner;

  /** Instantiates this class
   * @param inner The wring we wish to convert
   * @param name The title of the refactoring */
  @SuppressWarnings("unchecked") public AsSpartanization(final Wring<? extends ASTNode> inner, final String name) {
    super(name);
    this.inner = (Wring<ASTNode>) inner;
  }

  @Override protected ASTVisitor collect(final List<Rewrite> $) {
    return new ASTVisitor() {
      <N extends ASTNode> boolean process(final N n) {
        if (!inner.scopeIncludes(n) || inner.nonEligible(n))
          return true;
        $.add(inner.make(n));
        return true;
      }

      @Override public boolean visit(final Block it) {
          return process(it);
      }
      
      @Override public boolean visit(final ConditionalExpression e) {
          return process(e);
      }

      @Override public boolean visit(final IfStatement it) {
        return process(it);
      }

      @Override public boolean visit(final InfixExpression it) {
          return process(it);
        }

      @Override public boolean visit(final PrefixExpression it) {
        return process(it);
      }

      @Override public boolean visit(final VariableDeclarationFragment it) {
        return process(it);
      }
    };
  }

  @Override protected void fillRewrite(final ASTRewrite r, final CompilationUnit u, final IMarker m) {
    u.accept(new ASTVisitor() {
      <N extends ASTNode> boolean go(final N n) {
        if (inRange(m, n))
          inner.make(n).go(r, null);
        return true;
      }

      @Override public boolean visit(final Block e) {
          return go(e);
      }
      @Override public boolean visit(final ConditionalExpression e) {
        return go(e);
    }
      @Override public boolean visit(final IfStatement s) {
        return go(s);
    }
      @Override public boolean visit(final InfixExpression e) {
        return go(e);
    }

      @Override public boolean visit(final PrefixExpression e) {
        return go(e);
    }

      @Override public boolean visit(final VariableDeclarationFragment f) {
        return go(f);
    }
    });
  }
}
