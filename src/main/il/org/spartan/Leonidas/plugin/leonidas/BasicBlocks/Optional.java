package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.az;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.auxilary_layer.step;
import il.org.spartan.Leonidas.plugin.leonidas.Pruning;

/**
 * Created by  on 5/11/2017.
 */
public class Optional extends GenericMethodCallBasedBlock {
    private static final String TEMPLATE = "optional";
    private static int idGenerator = 0;
    Encapsulator internal;
    boolean active = true;
    private int index;

    public Optional(PsiElement e, Encapsulator i) {
        super(e, TEMPLATE);
        internal = i;
        index = idGenerator++;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean generalizes(Encapsulator e) {
        return iz.conforms(internal, e);
    }

    // !!!
    @Override
    protected boolean generalizes(PsiElement e) {
        return false;
    }

    @Override
    protected boolean goUpwards(Encapsulator prev, Encapsulator next) {
        return iz.generic(internal) && az.generic(internal).goUpwards(prev, next);
    }

    @Override
    public int extractId(PsiElement e) {
        assert iz.generic(internal);
        return az.generic(internal).extractId(step.firstParameterExpression(az.methodCallExpression(e)));
    }

    @Override
    public GenericEncapsulator create(Encapsulator e) {
        PsiElement p = step.firstParameterExpression(az.methodCallExpression(e.getInner()));
        return new Optional(e.getInner(), Pruning.prune(Encapsulator.buildTreeFromPsi(p)));
    }

    public void setActivate(boolean b) {
        active = b;
    }

    @Override
    public boolean isGeneric() {
        return internal.isGeneric();
    }
}
