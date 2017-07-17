package il.org.spartan.java.cfg;

import static org.eclipse.jdt.core.dom.ASTNode.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;
import static il.org.spartan.java.cfg.CFG.Edges.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import fluent.ly.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;

/** The main class of the CFG implementation
 * @author Dor Ma'ayan
 * @author Ori Roth
 * @since 2017-06-14 */
public interface CFG {
  static void compute(final BodyDeclaration d) {
    if (d != null && beginnings.of(d).get().isEmpty())
      d.accept(new ASTVisitor() {
        Stack<ASTNode> breakTarget = new Stack<>();
        Stack<ASTNode> continueTarget = new Stack<>();
        Map<String, ASTNode> labelMap = new LinkedHashMap<>();
        Stack<ASTNode> returnTarget = anonymous.ly(() -> {
          final Stack<ASTNode> $ = new Stack<>();
          $.push(parent(d));
          return $;
        });

        @Override public void preVisit(ASTNode n) {
          if (isBreakTarget(n))
            breakTarget.push(n);
          if (isContinueTarget(n))
            continueTarget.push(n);
          if (isReturnTarget(n))
            returnTarget.push(n);
          if (iz.labeledStatement(n))
            labelMap.put(((LabeledStatement) n).getLabel().getIdentifier(), n);
        }
        @Override public void postVisit(ASTNode n) {
          if (isBreakTarget(n))
            breakTarget.pop();
          if (isContinueTarget(n))
            continueTarget.pop();
          if (isReturnTarget(n))
            returnTarget.pop();
          if (iz.labeledStatement(n))
            labelMap.remove(((LabeledStatement) n).getLabel().getIdentifier());
        }
        @Override public void endVisit(SimpleName node) {
          leaf(node);
        }
        @Override public void endVisit(InfixExpression node) {
          Expression left = node.getLeftOperand(), right = node.getRightOperand();
          delegateBeginnings(node, left);
          selfEnds(node);
          chain(left, right);
          chainShallow(right, node);
        }
        @Override public void endVisit(VariableDeclarationFragment node) {
          Expression i = step.initializer(node);
          if (i == null) {
            leaf(node);
            return;
          }
          delegateBeginnings(node, i);
          selfEnds(node);
          chainShallow(i, node);
        }
        @Override public void endVisit(VariableDeclarationStatement node) {
          List<VariableDeclarationFragment> fs = step.fragments(node);
          delegateBeginnings(node, fs.get(0));
          delegateEnds(node, fs.get(fs.size() - 1));
          chain(fs);
        }
        @Override public void endVisit(Block node) {
          List<Statement> ss = step.statements(node);
          delegateBeginnings(node, ss.get(0));
          delegateEnds(node, ss.get(ss.size() - 1));
          chain(ss);
        }
        @Override public void endVisit(MethodDeclaration node) {
          List<SingleVariableDeclaration> ps = step.parameters(node);
          Block b = step.body(node);
          if (ps == null || ps.isEmpty()) {
            chainReturn(b);
            return;
          }
          chain(ps);
          if (isEmpty(b))
            chainReturn(ps.get(ps.size() - 1));
          else {
            chain(ps.get(ps.size() - 1), b);
            chainReturn(b);
          }
        }
        private void leaf(ASTNode node) {
          if (!isIllegalLeaf(node)) {
            beginnings.of(node).add(node);
            ends.of(node).add(node);
          }
        }
        private boolean isIllegalLeaf(@SuppressWarnings("unused") ASTNode node) {
          // TOO Roth: complete
          return false;
        }
        private void delegateBeginnings(ASTNode n1, ASTNode n2) {
          if (n1 == n2)
            selfBeginnings(n1);
          else
            beginnings.of(n1).addAll(beginnings.of(n2));
        }
        private void delegateEnds(ASTNode n1, ASTNode n2) {
          if (n1 == n2)
            selfEnds(n1);
          else
            ends.of(n1).addAll(ends.of(n2));
        }
        private void selfBeginnings(ASTNode n) {
          beginnings.of(n).add(n);
        }
        private void selfEnds(ASTNode n) {
          ends.of(n).add(n);
        }
        private void chain(List<? extends ASTNode> ns) {
          for (int i = 0; i < ns.size() - 1; ++i)
            chain(ns.get(i), ns.get(i + 1));
        }
        private void chain(ASTNode n1, ASTNode n2) {
          ends.of(n1).get().stream().forEach(o -> outgoing.of(o).addAll(beginnings.of(n2)));
          beginnings.of(n2).get().stream().forEach(b -> incoming.of(b).addAll(ends.of(n1)));
        }
        private void chainShallow(ASTNode n1, ASTNode n2) {
          ends.of(n1).get().stream().forEach(o -> outgoing.of(o).add(n2));
          incoming.of(n2).addAll(ends.of(n1));
        }
        private void chainReturn(ASTNode n) {
          ends.of(n).get().stream().forEach(o -> outgoing.of(o).add(returnTarget.peek()));
        }
        private boolean isBreakTarget(ASTNode n) {
          return iz.isOneOf(n, SWITCH_STATEMENT, FOR_STATEMENT, ENHANCED_FOR_STATEMENT, WHILE_STATEMENT, DO_STATEMENT);
        }
        private boolean isContinueTarget(ASTNode n) {
          return iz.isOneOf(n, FOR_STATEMENT, ENHANCED_FOR_STATEMENT, WHILE_STATEMENT, DO_STATEMENT);
        }
        private boolean isReturnTarget(@SuppressWarnings("unused") ASTNode __) {
          return false;
        }
        private boolean isEmpty(ASTNode n) {
          return beginnings.of(n).get().isEmpty();
        }
      });
  }

  public enum Edges {
    beginnings, //
    ends, //
    incoming, //
    outgoing; //
    Of of(final ASTNode to) {
      return new Of() {
        @Override public Nodes get() {
          return Edges.this.getNodes(to);
        }
        @Override public Of add(final ASTNode what) {
          if (what != null && to != null)
            get().add(what);
          return this;
        }
        @Override public Of addAll(Collection<? extends ASTNode> what) {
          if (what != null && to != null)
            get().addAll(what);
          return this;
        }
        @Override public Of addAll(Of what) {
          if (what != null && to != null)
            get().addAll(what.get().asSet());
          return this;
        }
        @Override public Of clear() {
          if (to != null)
            get().clear();
          return this;
        }
      };
    }
    protected Nodes getNodes(ASTNode ¢) {
      return property.get(¢, getClass().getCanonicalName() + "." + this, Nodes::new);
    }
    Nodes nodes(ASTNode n) {
      if (!property.has(n, getClass().getCanonicalName() + "." + this))
        compute(az.bodyDeclaration(yieldAncestors.untilClass(BodyDeclaration.class).from(n)));
      return getNodes(n);
    }

    interface Of {
      Of add(ASTNode what);
      Of addAll(Collection<? extends ASTNode> what);
      Of addAll(Of what);
      Of clear();
      Nodes get();
      default Of set(final ASTNode what) {
        return clear().add(what);
      }
    }
  }
}
