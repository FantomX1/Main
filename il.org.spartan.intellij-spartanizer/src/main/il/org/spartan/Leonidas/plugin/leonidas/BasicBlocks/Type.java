package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeElement;
import il.org.spartan.Leonidas.auxilary_layer.PsiRewrite;
import il.org.spartan.Leonidas.auxilary_layer.Utils;
import il.org.spartan.Leonidas.auxilary_layer.az;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.plugin.leonidas.Matcher;

import java.util.List;
import java.util.Map;

/**
 * A basic block representing a type element.
 * @author amirsagiv83, michalcohen
 * @since 29-05-2017.
 */
public class Type extends NamedElement {

    private static final String TEMPLATE = "Class";

    public Type(Encapsulator e) {
        super(e, TEMPLATE);
    }

    /**
     * For reflection use DO NOT REMOVE!
     */
    public Type() {
        super(TEMPLATE);
    }

    @Override
    protected String getName(PsiElement ¢) {
        return !iz.type(¢) ? null : az.type(¢).getText();
    }

    @Override
    public boolean conforms(PsiElement ¢) {
        return iz.type(¢) && super.conforms(¢);
    }

    @Override
    protected boolean goUpwards(Encapsulator prev, Encapsulator next) {
        return iz.type(next.getInner());
    }

    @Override
    public GenericEncapsulator create(Encapsulator e, Map<Integer, List<Matcher.Constraint>> m) {
        return new Type(e);
    }

    @Override
    public List<PsiElement> replaceByRange(List<PsiElement> es, Map<Integer, List<PsiElement>> m, PsiRewrite r) {
        if (!iz.classDeclaration(es.get(0)))
			return super.replaceByRange(es, m, r);
		PsiClass c = az.classDeclaration(es.get(0));
		PsiTypeElement $ = JavaPsiFacade.getElementFactory(Utils.getProject()).createTypeElementFromText(c.getName(),
				c);
		inner = $;
		return Utils.wrapWithList($);
    }
}

