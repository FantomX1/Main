package il.org.spartan.spartanizer.cmdline.good;

import static org.eclipse.jdt.core.dom.ASTNode.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.eclipse.jdt.core.dom.*;

import fluent.ly.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.cmdline.*;

public class BucketMethods {
  public static void main(final String[] args) {
    GrandVisitor.out = system.callingClassUniqueWriter();
    new GrandVisitor(args) {/**/}.visitAll(new ASTTrotter() {
      boolean interesting(final List<Statement> ¢) {
        return ¢ != null && ¢.size() >= 2 && !iz.letItBeIn(¢);
      }
      @Override boolean interesting(final MethodDeclaration ¢) {
        return !¢.isConstructor() && interesting(statements(body(¢))) && leaking(descendants.streamOf(¢));
      }
      boolean leaking(final ASTNode ¢) {
        return iz.nodeTypeIn(¢, ARRAY_CREATION, METHOD_INVOCATION, CLASS_INSTANCE_CREATION, CONSTRUCTOR_INVOCATION, ANONYMOUS_CLASS_DECLARATION,
            SUPER_CONSTRUCTOR_INVOCATION, SUPER_METHOD_INVOCATION, LAMBDA_EXPRESSION);
      }
      boolean leaking(final Stream<ASTNode> ¢) {
        return ¢.noneMatch(this::leaking);
      }
      @Override protected void record(final String summary) {
        try {
          GrandVisitor.out.write(summary);
        } catch (final IOException ¢) {
          System.err.println("Error: " + ¢.getMessage());
        }
        super.record(summary);
      }
    });
  }
}