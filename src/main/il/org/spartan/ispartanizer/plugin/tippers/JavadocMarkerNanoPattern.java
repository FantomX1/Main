package il.org.spartan.ispartanizer.plugin.tippers;

import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import il.org.spartan.ispartanizer.auxilary_layer.Wrapper;
import il.org.spartan.ispartanizer.auxilary_layer.az;
import il.org.spartan.ispartanizer.auxilary_layer.iz;
import il.org.spartan.ispartanizer.auxilary_layer.step;

/**
 * <p>
 * Copied and migrated to work in IntellliJ.
 * Original author is Ori Marcovitch.
 *
 * @author RoeiRaz
 * @since 03-01-2017
 *
 */
public abstract class JavadocMarkerNanoPattern extends NanoPatternTipper<PsiMethod> {
    @Override
    public boolean canTip(PsiElement ¢) {
        return iz.method(¢) && !hasTag(az.method(¢)) && prerequisites(az.method(¢));
    }

    @Override
    public final PsiElement createReplacement(PsiMethod m) {
        String docOld = step.docCommentString(m);
        String docNew = docOld + tag();
        final Wrapper<String> methodText = new Wrapper<>("");
        m.acceptChildren(new JavaElementVisitor() {
            @Override
            public void visitElement(PsiElement ¢) {
                if (!iz.javadoc(¢))
                    methodText.set(methodText.get() + ¢.getText());
                super.visitElement(¢);
            }
        });
        return JavaPsiFacade.getElementFactory(m.getProject())
                .createMethodFromText(("/**" + docNew + "*/" + methodText), m.getContext());
    }

    @Override
    public String description(PsiMethod __) {
        return "Add \"Delegator\" javadoc";
    }

    protected abstract boolean prerequisites(PsiMethod ¢);

    public final String tag() {
        return "[[" + this.getClass().getSimpleName() + "]]";
    }

    private boolean hasTag(PsiMethod ¢) {
        return step.docCommentString(¢).contains(tag());
    }
}
