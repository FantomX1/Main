package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.az;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.auxilary_layer.step;
import il.org.spartan.Leonidas.plugin.Toolbox;
import il.org.spartan.Leonidas.plugin.leonidas.PreservesIterator;

/**
 * @author Oren Afek
 * @since 5/14/2017.
 */
public abstract class Quantifier extends GenericMethodCallBasedBlock {

    protected Encapsulator internal;

    public Quantifier(PsiElement e, String template, Encapsulator i) {
        super(e, template);
        internal = i;
    }

    public Quantifier(String template) {
        super(template);
    }

    @Override
    protected boolean goUpwards(Encapsulator prev, Encapsulator next) {
        return iz.generic(internal) && az.generic(internal).goUpwards(prev, next);
    }

    @Override
    public boolean generalizes(Encapsulator e) {
        return iz.conforms(internal, e);
    }

    @Override
    public int extractId(PsiElement e) {
        PsiElement ie = step.firstParameterExpression(az.methodCallExpression(e));
        return Toolbox.getInstance().getGeneric(ie).map(g -> g.extractId(ie)).orElse(null);
    }

    @Override
    public boolean isGeneric() {
        return internal.isGeneric();
    }

    @PreservesIterator
    public int getNumberOfOccurrences(Encapsulator.Iterator i) {
        return 0;
    }

}
