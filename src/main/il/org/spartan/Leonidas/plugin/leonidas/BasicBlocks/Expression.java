package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import il.org.spartan.Leonidas.auxilary_layer.iz;

/**
 * @author Oren Afek
 * @since 5/3/2017.
 */
public class Expression extends GenericMethodCallBasedBlock {

    //FIXME @michalcohen @orenafek change to expression
    private static final String TEMPLATE = "booleanExpression";
    PsiType type;

    public Expression(PsiElement e, PsiType type) {
        super(e, TEMPLATE);
        this.type = type;
    }

    public Expression(PsiElement e) {
        super(e, TEMPLATE);
    }

    public Expression(Encapsulator n) {
        super(n, TEMPLATE);
    }

    /**
     * For reflection use DO NOT REMOVE!
     */
    @SuppressWarnings("unused")
    protected Expression() {
        super(TEMPLATE);
    }

    @Override
    public boolean generalizes(Encapsulator e) {
        return iz.expression(e.getInner());
    }

    @Override
    protected boolean goUpwards(Encapsulator prev, Encapsulator next) {
        return next != null && prev.getText().equals(next.getText());
    }

    @Override
    public GenericEncapsulator create(Encapsulator e) {
        return new Expression(e);
    }

    public PsiType evaluationType() {
        return type;
    }


}
