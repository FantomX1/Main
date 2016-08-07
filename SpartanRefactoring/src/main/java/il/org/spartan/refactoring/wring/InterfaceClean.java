package il.org.spartan.refactoring.wring;

import org.eclipse.jdt.core.dom.*;

/** A {@link Wring} to convert
 *
 * <pre>
 * <b>abstract</b>abstract <b>interface</b> a
 * {}
 * </pre>
 *
 * to
 *
 * <pre>
 * <b>interface</b> a {}
 * </pre>
 * @author Yossi Gil
 * @since 2015-07-29 */
public final class InterfaceClean extends Wring.RemoveModifier<TypeDeclaration> {
  @Override boolean eligible(final TypeDeclaration ¢) {
    return ¢.isInterface();
  }
  @Override String description(final TypeDeclaration ¢) {
    return "Remove redundant 'abstract'/'static' modifier from interface " + ¢.getName();
  }
  @Override boolean redundant(final Modifier ¢) {
    return ¢.isAbstract() || ¢.isStatic();
  }
}
