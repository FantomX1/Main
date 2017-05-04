package il.org.spartan.Leonidas.plugin.leonidas;

import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.iz;

/**
 * @author Roey Maor & Amir Sagiv & Michal Cohen & Oren Afek
 * @since 5/3/2017.
 */
public class GenericAnyBlock extends GenericMethodCallBasedBlock {

    private static final String TEMPLATE = "anyBlock";

    public GenericAnyBlock(PsiElement e) {
        super(e, TEMPLATE);
    }

    public GenericAnyBlock(Encapsulator n) {
        super(n, TEMPLATE);
    }

    @Override
    protected boolean generalizes(PsiElement e) {
        return iz.blockStatement(e) || iz.block(e) || iz.statement(e);
    }
}
