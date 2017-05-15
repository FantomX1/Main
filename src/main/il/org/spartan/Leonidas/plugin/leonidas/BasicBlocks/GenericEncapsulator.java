package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.PsiRewrite;
import il.org.spartan.Leonidas.plugin.leonidas.KeyDescriptionParameters;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


/**
 * @author Oren Afek && Michal Cohen
 * @since 03-05-2017
 */
public abstract class GenericEncapsulator extends Encapsulator {

    protected String template;


    public GenericEncapsulator(PsiElement e, String template) {
        super(e);
        children = Collections.emptyList();
        this.template = template;
    }

    public GenericEncapsulator(Encapsulator n, String template) {
        super(n);
        children = Collections.emptyList();
        this.template = template;
    }

    /**
     * For reflection use DO NOT REMOVE!
     */
    @SuppressWarnings("unused")
    protected GenericEncapsulator() {
    }

    /**
     * Do I represent a concrete PsiElement
     *
     * @param other PSI Element
     * @return true iff I'm to be switched with other.
     */
    public abstract boolean conforms(PsiElement other);

    /**
     * Extracts the Id No. of the general block from the PsiElement
     * for example:
     * statement(0) -> 0
     *
     * @param e PsiElement
     * @return the id.
     */
    public abstract int extractId(PsiElement e);

    /**
     * Prunes the irrelevant concrete PsiElements and replaces with a new Generic Encapsulator
     *
     * @param e the concrete PsiElement.
     *          for example: the methodCallExpression: statement(0).
     * @return The replacer of the syntactic generic element with the GenericEncapsulator
     */
    public Encapsulator prune(Encapsulator e) {
        assert conforms(e.getInner());
        Encapsulator upperElement = getConcreteParent(e);
        GenericEncapsulator ge = create(upperElement);
        if (isGeneric())
            ge.putUserData(KeyDescriptionParameters.ID, ge.extractId(e.getInner()));
        return ge.getParent() == null ? ge : upperElement.generalizeWith(ge);
    }

    protected abstract boolean goUpwards(Encapsulator prev, Encapsulator next);

    /**
     * Creates another one like me, with concrete PsiElement within
     *
     * @param e element within.
     * @return new <B>Specific</B> GenericEncapsulator
     */
    public abstract GenericEncapsulator create(Encapsulator e);

    /**
     * Can I generalizeWith a concrete element
     *
     * @param e concrete element
     * @return true iff I can generalizeWith e
     */
    @SuppressWarnings("InfiniteRecursion")
    public boolean generalizes(Encapsulator e) {
        return generalizes(e);
    }

    /**
     * @param newNode the concrete node that replaces the generic node.
     * @param r       rewrite
     */
    @SuppressWarnings("UnusedReturnValue")
    public void replace(Encapsulator newNode, PsiRewrite r) {
        inner = parent == null ? newNode.inner : r.replace(inner, newNode.inner);
    }

    public void replaceByRange(List<PsiElement> elements, PsiRewrite r) {
        assert parent != null;
        List<PsiElement> l = Lists.reverse(elements);
        elements.forEach(e -> inner.getParent().addAfter(inner, e));
        inner.getParent().deleteChildRange(inner, inner);
    }

    /**
     * @param n method call representing generic element.
     * @return the highest generic parent.
     */
    public Encapsulator getConcreteParent(Encapsulator n) {
        if (n.parent == null) return n;
        Encapsulator prev = n, next = n.getParent();
        while (goUpwards(prev, next)) {
            prev = next;
            next = next.getParent();
        }
        return prev;
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    public int getId() {
        return getUserData(KeyDescriptionParameters.ID);
    }

    public Encapsulator putId(int i) {
        putUserData(KeyDescriptionParameters.ID, i);
        return this;
    }

    GenericEncapsulator.EndThe is(Runnable template) {
        return EndThe.END;
    }

    EndThe is(Supplier<?> template) {
        return EndThe.END;
    }

    EndThe isNot(Runnable template) {
        return EndThe.END;
    }

    EndThe isNot(Supplier<?> template) {
        return EndThe.END;
    }

    static class EndThe {
        static final EndThe END = new GenericEncapsulator.EndThe();

        public <T> void ofType(Class<? extends T> __) {/**/}
    }
}
